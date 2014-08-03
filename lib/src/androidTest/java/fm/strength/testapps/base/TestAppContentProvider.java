package fm.strength.testapps.base;

import android.database.sqlite.SQLiteOpenHelper;

import fm.strength.worm.ContractContentProvider;

public class TestAppContentProvider extends ContractContentProvider {

    @Override
    public String getAuthority() {
        return TestAppContract.AUTHORITY;
    }

    @Override
    protected String getBaseMimeType() {
        return "com.test";
    }

    @Override
    public Class<?> getContract() {
        return TestAppContract.class;
    }

    @Override
    protected SQLiteOpenHelper getSQLiteOpenHelper() {
        return new TestAppSqliteOpenHelper(getContext());
    }

}
