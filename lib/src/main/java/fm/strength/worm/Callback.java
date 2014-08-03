package fm.strength.worm;

import fm.strength.worm.Data.Results;
import fm.strength.worm.util.Log;

public class Callback<T> {

    public void onSuccess(T result) {
        // subclasses to override
    }

    public void onFailure(Exception error) {
        Log.d(error, error.getLocalizedMessage());
    }

    public void onComplete(T results, Exception error) {
        // subclasses to override
    }

}
