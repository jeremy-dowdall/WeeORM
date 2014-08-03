package fm.strength.worm;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import java.util.List;

import fm.strength.worm.data.Detail;

public abstract class ModelLoader<T> extends AsyncTaskLoader<T> {

    private final Data db;
    private final ForceLoadContentObserver observer;

    private T result;
    private CancellationSignal cancellationSignal;

    public ModelLoader(Context context) {
        super(context);
        this.db = Data.sync(context);
        this.observer = new ForceLoadContentObserver();
    }

    protected abstract T load();

    protected <QT> QT load(Class<QT> type, Detail...details) {
        register(type);
        return db.find(type, details);
    }

    protected <QT> List<QT> loadAll(Class<QT> type, Detail...details) {
        register(type);
        return db.findAll(type, details);
    }

    private void register(Class<?> type) {
        ContentResolver cr = getContext().getContentResolver();
        for(Uri contentUri : Contracts.getContentUris(type)) {
            cr.registerContentObserver(contentUri, true, observer);
        }
    }

    protected T onError(Exception e) {
        if(e != null) e.printStackTrace();
        return null;
    }

    /* Runs on a worker thread */
    @Override
    @TargetApi(16)
    public T loadInBackground() {
        if(Build.VERSION.SDK_INT >= 16) {
            synchronized(this) {
                if(isLoadInBackgroundCanceled()) {
                    throw new OperationCanceledException();
                }
                cancellationSignal = new CancellationSignal();
            }
            try {
                result = load();
            } catch(Exception e) {
                return result = onError(e);
            } finally {
                synchronized(this) {
                    cancellationSignal = null;
                }
            }
        } else {
            result = load();
        }

        return result;
    }

    @Override
    @TargetApi(16)
    public void cancelLoadInBackground() {
        if(Build.VERSION.SDK_INT >= 16) {
            super.cancelLoadInBackground();
            synchronized(this) {
                if(cancellationSignal != null) {
                    cancellationSignal.cancel();
                }
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(T result) {
        if(isReset()) {
            return;
        }

        if(isStarted()) {
            super.deliverResult(result);
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if(result != null) {
            deliverResult(result);
        }
        if(takeContentChanged() || result == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(T result) {
        getContext().getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        getContext().getContentResolver().unregisterContentObserver(observer);
        result = null;
    }

}
