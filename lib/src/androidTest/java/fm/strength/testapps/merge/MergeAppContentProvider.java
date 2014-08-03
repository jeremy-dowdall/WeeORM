package fm.strength.testapps.merge;

import android.database.sqlite.SQLiteOpenHelper;

import fm.strength.worm.ContractContentProvider;

public class MergeAppContentProvider extends ContractContentProvider {

    @Override
    public String getAuthority() {
        return MergeAppContract.AUTHORITY;
    }

    @Override
    protected String getBaseMimeType() {
        return "com.test";
    }

    @Override
    public Class<?> getContract() {
        return MergeAppContract.class;
    }

    @Override
    protected SQLiteOpenHelper getSQLiteOpenHelper() {
        return new MergeAppSqliteOpenHelper(getContext());
    }

}
