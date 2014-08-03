package fm.strength.testapps.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fm.strength.testapps.base.TestAppContract.Users;
import fm.strength.testapps.base.TestAppContract.Workouts;

import static fm.strength.testapps.base.TestAppContract.VERSION;

public class TestAppSqliteOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "testapp.db";


    private static final String CREATE_USERS =
            "create table "
                    + Users.TABLE
                    + "("
                    + Users.COLUMN_ID + " integer primary key autoincrement, "
                    + Users.COLUMN_NAME + " text, "
                    + Users.COLUMN_ACTIVE + " integer default 1, "
                    + Users.COLUMN_DETAILS + " text"
                    + ");"
            ;
    private static final String CREATE_WORKOUTS =
            "create table "
                    + Workouts.TABLE
                    + "("
                    + Workouts.COLUMN_ID + " integer primary key autoincrement, "
                    + Workouts.COLUMN_IX + " integer default 0, "
                    + Workouts.COLUMN_USER_ID + " integer, "
                    + Workouts.COLUMN_NAME + " text"
                    + ");"
            ;
    private static final String CREATE_WORKOUT_SECTIONS =
            "create table "
                    + Workouts.Sections.TABLE
                    + "("
                    + Workouts.Sections.COLUMN_ID + " integer primary key autoincrement, "
                    + Workouts.Sections.COLUMN_IX + " integer not null default 0, "
                    + Workouts.Sections.COLUMN_USER_ID + " integer, "
                    + Workouts.Sections.COLUMN_WORKOUT_ID + " integer not null, "
                    + Workouts.Sections.COLUMN_NAME + " text not null, "
                    + Workouts.Sections.COLUMN_PLAYLIST + " text"
                    + ");"
            ;
    private static final String CREATE_WORKOUT_SECTION_EXERCISES =
            "create table "
                    + Workouts.Sections.Exercises.TABLE
                    + "("
                    + Workouts.Sections.Exercises.COLUMN_ID + " integer primary key autoincrement, "
                    + Workouts.Sections.Exercises.COLUMN_IX + " integer not null default 0, "
                    + Workouts.Sections.Exercises.COLUMN_SECTION_ID + " integer not null, "
                    + Workouts.Sections.Exercises.COLUMN_NAME + " text"
                    + ");"
            ;


    public TestAppSqliteOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_WORKOUTS);
        db.execSQL(CREATE_WORKOUT_SECTIONS);
        db.execSQL(CREATE_WORKOUT_SECTION_EXERCISES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
