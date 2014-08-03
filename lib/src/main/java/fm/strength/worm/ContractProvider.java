package fm.strength.worm;

import android.content.UriMatcher;
import android.database.sqlite.SQLiteOpenHelper;

public interface ContractProvider {

    /**
     * Retrieve the authority for the ContentProvider, as defined in the AndroidManifest.
     * @return the ContentProvider's authority; never null
     */
    String getAuthority();

    /**
     * Retrieve the Contract class used by the ContentProvider to define its data / models.
     * @return the Contract class; never null
     */
    Class<?> getContract();

    /**
     * Allows subclasses to obtain and add custom types to this ContentProvider's matcher.
     * @return the matcher used by this ContentProvider; only valid after #onCreate
     */
    UriMatcher getMatcher();

}
