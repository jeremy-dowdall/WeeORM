package fm.strength.worm.data;

import android.os.AsyncTask;

import java.util.concurrent.Callable;

import fm.strength.worm.Callback;

public class Task<T> extends AsyncTask<Void, Void, Object> {

    public static <T> Task<T> create(Callable<T> callable) {
        Task<T> task = new Task<T>(callable);
        task.execute();
        return task;
    }


    private final Callable callable;
    private Callback<T> callback;

    public Task(Callable callable) {
        this.callable = callable;
    }

    @Override
    protected Object doInBackground(Void...params) {
        try {
            return callable.call();
        } catch(Exception e) {
            cancel(true);
            return e;
        }
    }

    @Override
    protected void onCancelled(Object data) {
        if(callback != null) {
            if(data instanceof Exception) {
                Exception error = (Exception) data;
                callback.onFailure(error);
                callback.onComplete(null, error);
            } else {
                callback.onComplete(null, null);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Object results) {
        if(callback != null) {
            callback.onSuccess((T) results);
            callback.onComplete((T) results, null);
        }
    }

    public void then(Callback<T> callback) {
        this.callback = callback;
    }

    public Task<T> start() {
        execute();
        return this;
    }

}
