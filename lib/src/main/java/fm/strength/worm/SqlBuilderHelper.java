package fm.strength.worm;

import android.net.Uri;

public interface SqlBuilderHelper {

    void addArg(Object arg);

    Character getAlias(Uri uri);

}
