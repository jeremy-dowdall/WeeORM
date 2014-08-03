package fm.strength.worm;

import static fm.strength.worm.util.StringUtils.path;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.ArrayList;

import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.Contracts.Contract.Model;
import fm.strength.worm.SqlBuilder.Statement;
import fm.strength.worm.util.Err;
import fm.strength.worm.util.Log;

public abstract class ContractContentProvider extends ContentProvider implements ContractProvider, QueryValidator {

    public static final String ERR_UNSUPPORTED_URI = "Unsupported URI: %s";
    public static final String ERR_LOADING_CONTRACT = "failed to load contract";
    public static final String ERR_INSERT_FAILED = "Problem with insert for URI: %s";

    public static final int MODE_SAFE = 0;
    public static final int MODE_WARN = 1;


    private SQLiteOpenHelper dbHelper;
    private UriMatcher matcher;
    private QueryValidator validator;

    private int mode = MODE_WARN;
    private final ThreadLocal<Boolean> batchMode = new ThreadLocal<Boolean>();

    /**
     * Retrieve the helper used by the ContentProvider to access the database.
     * @return the SQLiteOpenHelper; never null
     */
    protected abstract SQLiteOpenHelper getSQLiteOpenHelper();

    @Override
    public String checkAlias(String alias) throws IllegalArgumentException {
        if(validator == null) validator = new QueryValidator.Default();
        return validator.checkAlias(alias);
    }

    @Override
    public String checkColumn(String column, int...ixs) throws IllegalArgumentException {
        if(validator == null) validator = new QueryValidator.Default();
        return validator.checkColumn(column, ixs);
    }

    /**
     * Subclasses can override, but MUST call through to super
     * @return true, unless the contract failed to load
     */
    @Override
    public boolean onCreate() {
        dbHelper = getSQLiteOpenHelper();
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // TODO defer loading to first use?
        try {
            ContractsLoader.load(getBaseMimeType(), getContract());
            String authority = getAuthority();
            for(int i = 0; i < Contracts.size(); i++) {
                Contract contract = Contracts.getContract(i);
                if(contract.isItem) {
                    matcher.addURI(authority, path(contract.uri.getPath(), "#"), i);
                } else {
                    matcher.addURI(authority, path(contract.uri.getPath()), i);
                }
            }
        } catch(IllegalArgumentException e) {
            throw Err.get(e, ERR_LOADING_CONTRACT);
        }

        return true;
    }

    protected void resetDatabase() {
        if(dbHelper != null) {
            dbHelper.close();
        }
        dbHelper = getSQLiteOpenHelper();
    }

    protected String getBaseMimeType() {
        return getContext().getApplicationContext().getPackageName();
    }

    @Override
    public UriMatcher getMatcher() {
        return matcher;
    }

    protected int getIndex(Uri uri) {
        int ix = matcher.match(uri);
        if(ix < 0) {
            throw Err.get(ERR_UNSUPPORTED_URI, uri);
        }
        return ix;
    }

    @Override
    public String getType(Uri uri) {
        int ix = getIndex(uri);
        if(ix < Contracts.size()) {
            return Contracts.getContract(ix).mime;
        }
        return null; // subclasses to provide
    }

    private boolean isBatchMode() {
        return batchMode.get() != null && batchMode.get();
    }

    Cursor rawQuery(String sql, String[] selectionArgs) {
        return dbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    protected Cursor safeQuery(int ix, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Contracts.Contract contract = Contracts.getContract(ix);
        builder.setTables(contract.table);
        if(contract.isItem) {
            builder.appendWhere(Model.COLUMN_ID + "=" + ContentUris.parseId(uri));
        }
        try {
            Cursor cursor = builder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            Log.d("queried: %s", uri);
            return cursor;
        } catch(SQLiteException e) {
            Log.d(e, "query failed: %s, %s, %s, %s, %s", uri, projection, selection, selectionArgs, sortOrder);
            throw Err.get(e, "query failed: %s", uri);
        }
    }

    protected int safeUpdate(int ix, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            Contract contract = Contracts.getContract(ix);
            if(contract.isItem) {
                selection = Model.COLUMN_ID + "=" + ContentUris.parseId(uri);
            }
            return dbHelper.getWritableDatabase().update(contract.table, values, selection, selectionArgs);
        } catch(SQLiteException e) {
            Log.d(e, "update failed: %s, %s, %s, %s", uri, values, selection, selectionArgs);
            throw Err.get(e, "update failed: %s", uri);
        }
    }

    protected int safeDelete(int ix, Uri uri, String selection, String[] selectionArgs) {
        try {
            Contract contract = Contracts.getContract(ix);
            if(contract.isItem) {
                selection = Model.COLUMN_ID + "=" + ContentUris.parseId(uri);
            }
            return dbHelper.getWritableDatabase().delete(contract.table, selection, selectionArgs);
        } catch(SQLiteException e) {
            Log.d(e, "delete failed: %s, %s, %s", uri, selection, selectionArgs);
            throw Err.get(e, "delete failed: %s", uri);
        }
    }


    protected Cursor rawQuery(int ix, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(mode == MODE_SAFE) {
            throw Err.get("direct query not permitted in safe mode");
        }
        if(mode == MODE_WARN) {
            Log.w("allowing direct query: %s, %s, %s, %s, %s", uri, projection, selection, selectionArgs, sortOrder);
        }
        return safeQuery(ix, uri, projection, selection, selectionArgs, sortOrder);
    }

    protected int rawUpdate(int ix, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(mode == MODE_SAFE) {
            throw Err.get("direct update not permitted in safe mode");
        }
        if(mode == MODE_WARN) {
            Log.w("allowing direct update: %s, %s, %s, %s", uri, values, selection, selectionArgs);
        }
        return safeUpdate(ix, uri, values, selection, selectionArgs);
    }

    protected int rawDelete(int ix, Uri uri, String selection, String[] selectionArgs) {
        if(mode == MODE_SAFE) {
            throw Err.get("direct delete not permitted in safe mode");
        }
        if(mode == MODE_WARN) {
            Log.w("allowing direct delete: %s, %s, %s", uri, selection, selectionArgs);
        }
        return safeDelete(ix, uri, selection, selectionArgs);
    }


    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        batchMode.set(true);
        try {
            db.beginTransaction();
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            batchMode.remove();
            if(db != null) db.endTransaction();
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String q = uri.getQueryParameter("q");
        if(q != null) {
            Query query = Query.fromJson(q);
            return QueryRunner.run(this, query);
        } else {
            int ix = getIndex(uri);
            return rawQuery(ix, uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int ix = getIndex(uri);
        if(ix < Contracts.size()) {
            long id = dbHelper.getWritableDatabase().insert(Contracts.getContract(ix).table, null, values);
            if(id > 0) {
                Uri itemUri = ContentUris.withAppendedId(uri, id);
                notifyChange(ix, itemUri);
                Log.d("created %s in %s", itemUri, uri);
                return itemUri;
            }
            throw Err.get(ERR_INSERT_FAILED, uri);
        }
        return null; // subclasses to provide...
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int ix = getIndex(uri);
        if(ix < Contracts.size()) {
            int count = 0;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for(ContentValues v : values) {
                    long id = db.insert(Contracts.getContract(ix).table, null, v);
                    if(id > 0) {
                        Log.d("build insert %d of %d", count, values.length);
                        count++;
                    } else {
                        Log.d("bulk insert failed at %d of %d", count, values.length);
                        break;
                    }
                }
                if(count == values.length) {
                    db.setTransactionSuccessful();
                    notifyChange(ix, uri);
                    return count;
                }
            } finally {
                if(db != null) db.endTransaction();
            }
        }
        return 0; // subclasses to provide...
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        int ix = getIndex(uri);
        String q = uri.getQueryParameter("q");
        if(q == null) {
            count = rawUpdate(ix, uri, values, selection, selectionArgs);
        } else {
            Statement stmt = SqlBuilder.create()
                    .withQuery(Query.fromJson(q))
            .buildSelection();
            count = safeUpdate(ix, uri, values, stmt.sql, stmt.args);
        }
        if(count == 0) {
            Log.d("nothing updated at %s", uri);
        } else {
            notifyChange(ix, uri);
            Log.d("updated %,d at %s", count, uri);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        int ix = getIndex(uri);
        String q = uri.getQueryParameter("q");
        if(q == null) {
            count = rawDelete(ix, uri, selection, selectionArgs);
        } else {
            Statement stmt = SqlBuilder.create()
                    .withQuery(Query.fromJson(q))
            .buildSelection();
            count = safeDelete(ix, uri, stmt.sql, stmt.args);
        }
        if (count == 0) {
            Log.d("nothing deleted at %s", uri);
        } else {
            notifyChange(ix, uri);
            Log.d("deleted %,d at %s", count, uri);
        }
        return count;
    }


    private void notifyChange(int ix, Uri uri) {
        if(!isBatchMode()) {
            notifyChange(ix, uri, getContext().getContentResolver());
        }
    }

    protected void notifyChange(int ix, Uri uri, ContentResolver resolver) {
        resolver.notifyChange(uri, null);
    }

}
