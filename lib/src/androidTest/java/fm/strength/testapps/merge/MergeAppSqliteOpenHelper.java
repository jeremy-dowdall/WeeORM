package fm.strength.testapps.merge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fm.strength.testapps.merge.MergeAppContract.Categories;
import fm.strength.testapps.merge.MergeAppContract.Lists;
import fm.strength.testapps.merge.MergeAppContract.Products;
import fm.strength.testapps.merge.MergeAppContract.Stores;
import fm.strength.testapps.merge.MergeAppContract.Trips;

import static fm.strength.testapps.merge.MergeAppContract.VERSION;

public class MergeAppSqliteOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "testapp.db";


    private static final String CREATE_TRIPS =
            "create table "
                    + Trips.TABLE
                    + "("
                    + Trips.COLUMN_ID + " integer primary key autoincrement, "
                    + Trips.COLUMN_NAME + " text not null"
                    + ");"
            ;
    private static final String CREATE_TRIP_LISTS =
            "create table "
                    + Trips.Lists.TABLE
                    + "("
                    + Trips.Lists.COLUMN_ID + " integer primary key autoincrement, "
                    + Trips.Lists.COLUMN_TRIP_ID + " integer not null, "
                    + Trips.Lists.COLUMN_LIST_ID + " integer not null"
                    + ");"
            ;
    private static final String CREATE_LISTS =
            "create table "
                    + Lists.TABLE
                    + "("
                    + Lists.COLUMN_ID + " integer primary key autoincrement, "
                    + Lists.COLUMN_NAME + " text not null"
                    + ");"
            ;
    private static final String CREATE_LIST_STORES =
            "create table "
                    + Lists.Stores.TABLE
                    + "("
                    + Lists.Stores.COLUMN_ID + " integer primary key autoincrement, "
                    + Lists.Stores.COLUMN_LIST_ID + " integer not null, "
                    + Lists.Stores.COLUMN_STORE_ID + " integer not null"
                    + ");"
            ;
    private static final String CREATE_LIST_STORE_CATEGORIES =
            "create table "
                    + Lists.Stores.Categories.TABLE
                    + "("
                    + Lists.Stores.Categories.COLUMN_ID + " integer primary key autoincrement, "
                    + Lists.Stores.Categories.COLUMN_STORE_ID + " integer not null, "
                    + Lists.Stores.Categories.COLUMN_CATEGORY_ID + " integer not null"
                    + ");"
            ;
    private static final String CREATE_LIST_STORE_CATEGORY_PRODUCTS =
            "create table "
                    + Lists.Stores.Categories.Products.TABLE
                    + "("
                    + Lists.Stores.Categories.Products.COLUMN_ID + " integer primary key autoincrement, "
                    + Lists.Stores.Categories.Products.COLUMN_CATEGORY_ID + " integer not null, "
                    + Lists.Stores.Categories.Products.COLUMN_PRODUCT_ID + " integer not null, "
                    + Lists.Stores.Categories.Products.COLUMN_CLEARED_AT + " text"
                    + ");"
            ;
    private static final String CREATE_STORES =
            "create table "
                    + Stores.TABLE
                    + "("
                    + Stores.COLUMN_ID + " integer primary key autoincrement, "
                    + Stores.COLUMN_NAME + " text not null"
                    + ");"
            ;
    private static final String CREATE_CATEGORIES =
            "create table "
                    + Categories.TABLE
                    + "("
                    + Categories.COLUMN_ID + " integer primary key autoincrement, "
                    + Categories.COLUMN_NAME + " text not null"
                    + ");"
            ;
    private static final String CREATE_PRODUCTS =
            "create table "
                    + Products.TABLE
                    + "("
                    + Products.COLUMN_ID + " integer primary key autoincrement, "
                    + Products.COLUMN_NAME + " text not null, "
                    + Products.COLUMN_QUANTITY + " text, "
                    + Products.COLUMN_UNITS + " text"
                    + ");"
            ;


    public MergeAppSqliteOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRIPS);
        db.execSQL(CREATE_TRIP_LISTS);
        db.execSQL(CREATE_LISTS);
        db.execSQL(CREATE_LIST_STORES);
        db.execSQL(CREATE_LIST_STORE_CATEGORIES);
        db.execSQL(CREATE_LIST_STORE_CATEGORY_PRODUCTS);
        db.execSQL(CREATE_STORES);
        db.execSQL(CREATE_CATEGORIES);
        db.execSQL(CREATE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
