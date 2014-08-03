package fm.strength.worm;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import java.util.ArrayList;
import java.util.List;

import fm.strength.worm.Data.Model;
import fm.strength.worm.data.Detail;

public class ObjectLoader<T> extends ModelLoader<List<T>> {

    private final Class<T> type;
    private final Detail[] details;

    public ObjectLoader(Context context, Class<T> type, Detail...details) {
        super(context);
        this.type = type;
        this.details = details;
    }

    @Override
    protected List<T> load() {
        return loadAll(type, details);
    }
}
