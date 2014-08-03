package fm.strength.testapps.merge;

import android.net.Uri;

import fm.strength.testapps.merge.MergeAppContract.Stores;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;

public class MergeAppContract {

    public static final int VERSION = 1;

    public static final String AUTHORITY = "MergeContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Trips {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MergeAppContract.CONTENT_URI, "trips");
        public static final String TABLE = "trips";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";

        public static final class Lists {

            public static final Uri CONTENT_URI = Uri.withAppendedPath(Trips.CONTENT_URI, "lists");
            public static final String TABLE = "trip_lists";
            public static final String COLUMN_ID = "_id";
            public static final String COLUMN_TRIP_ID = "trip_id";
            public static final String COLUMN_LIST_ID = "list_id";

        }
    }

    public static final class Lists {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MergeAppContract.CONTENT_URI, "lists");
        public static final String TABLE = "lists";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";

        public static final class Stores {

            public static final Uri CONTENT_URI = Uri.withAppendedPath(Lists.CONTENT_URI, "stores");
            public static final String TABLE = "list_stores";
            public static final String COLUMN_ID = "_id";
            public static final String COLUMN_LIST_ID = "list_id";
            public static final String COLUMN_STORE_ID = "store_id";

            public static final class Categories {

                public static final Uri CONTENT_URI = Uri.withAppendedPath(Stores.CONTENT_URI, "categories");
                public static final String TABLE = "list_store_categories";
                public static final String COLUMN_ID = "_id";
                public static final String COLUMN_STORE_ID = "store_id";
                public static final String COLUMN_CATEGORY_ID = "category_id";

                public static final class Products {

                    public static final Uri CONTENT_URI = Uri.withAppendedPath(Categories.CONTENT_URI, "products");
                    public static final String TABLE = "list_store_category_products";
                    public static final String COLUMN_ID = "_id";
                    public static final String COLUMN_CATEGORY_ID = "category_id";
                    public static final String COLUMN_PRODUCT_ID = "product_id";
                    public static final String COLUMN_CLEARED_AT = "cleared_at";

                }
            }
        }
    }

    public static final class Stores {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MergeAppContract.CONTENT_URI, "stores");
        public static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/vnd.stores";
        public static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/vnd.store";
        public static final String TABLE = "stores";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";

    }

    public static final class Categories {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MergeAppContract.CONTENT_URI, "categories");
        public static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/vnd.categories";
        public static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/vnd.category";
        public static final String TABLE = "categories";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";

    }

    public static final class Products {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MergeAppContract.CONTENT_URI, "products");
        public static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/vnd.products";
        public static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/vnd.product";
        public static final String TABLE = "products";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNITS = "units";

    }

}
