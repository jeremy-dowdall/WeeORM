package fm.strength.testapps.base;

import android.net.Uri;

import fm.strength.worm.Contracts.Contract.Model;
import fm.strength.worm.Contracts.Contract.SortedModel;

public class TestAppContract {

    public static final int VERSION = 3;

    public static final String AUTHORITY = "TestContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Users implements Model {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TestAppContract.CONTENT_URI, "users");
        public static final String TABLE = "users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_DETAILS = "details";
    }

    public static final class Workouts implements SortedModel {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TestAppContract.CONTENT_URI, "workouts");
        public static final String TABLE = "workouts";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_ID = "user_id";

        public static final class Sections implements SortedModel {

            public static final Uri CONTENT_URI = Uri.withAppendedPath(Workouts.CONTENT_URI, "sections");
            public static final String TABLE = "workout_sections";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_USER_ID = "user_id";
            public static final String COLUMN_WORKOUT_ID = "workout_id";
            public static final String COLUMN_PLAYLIST = "playlist";

            public static final class Exercises implements SortedModel {

                public static final Uri CONTENT_URI = Uri.withAppendedPath(Sections.CONTENT_URI, "exercises");
                public static final String TABLE = "workout_section_exercises";
                public static final String COLUMN_SECTION_ID = "section_id";
                public static final String COLUMN_NAME = "name";
            }
        }
    }
}
