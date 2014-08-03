package fm.strength.worm;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.data.Detail;
import fm.strength.worm.data.Limit;
import fm.strength.worm.data.Order;
import fm.strength.worm.data.Select;
import fm.strength.worm.data.Value;
import fm.strength.worm.data.Where;
import fm.strength.worm.util.Err;

public class Data {

    public static class Model {

        public static final String TRUE = "true";
        public static final String FALSE = "false";
        public static final String NULL = "null";

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        public @interface Contract {
            Class[] value();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        public @interface JSON {
            // marker interface
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Column {
            String value() default "";
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Join {
            String from();
            String to();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Where {
            String field();
            String is() default "";
            String isNot() default "";
            String lt() default "";
            String lte() default "";
            String gt() default "";
            String gte() default "";
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Order {
            String[] value() default "";
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface NotNull {
            // marker interface
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface X {
            // marker interface
        }

    }


    public static Async async(Context context) {
        return new Async(new Data(context));
    }

    public static Data sync(Context context) {
        return new Data(context);
    }


    final Context context;
    public final Async async;

    private Data(Context context) {
        this.context = context;
        this.async = new Async(this);
    }

    @TargetApi(16)
    private Results runQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        Cursor cursor;
        if(Build.VERSION.SDK_INT < 16) {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        } else {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
        }
        return new Results(cursor);
    }


    public Results query(Uri contentUri, Detail...details) {
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        contentUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(contentUri, null, null, null, null, null);
    }


    public <T> T find(Class<T> type, Detail...details) throws IllegalArgumentException {
        details = Detail.append(details, limit(1));
        Query query = QueryBuilder.create(type).withDetails(details).build();
        Uri requestUri = Contracts.getContentUri(type).buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(requestUri, null, null, null, null, null).get(0).as(type);
    }

    public <T> List<T> findAll(Class<T> type, Detail...details) throws IllegalArgumentException {
        return findAll(type, null, details);
    }

    <T> List<T> findAll(Class<T> type, CancellationSignal cancellationSignal, Detail...details) throws IllegalArgumentException {
        Query query = QueryBuilder.create(type).withDetails(details).build();
        Uri requestUri = Contracts.getContentUri(type).buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(requestUri, null, null, null, null, cancellationSignal).asListOf(type);
    }


    public int count(Uri contentUri, Where...where) {
        Detail[] details = Detail.prepend(select("*").asCount(), where);
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        Uri requestUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(requestUri, null, null, null, null, null).get(0, 0).as(int.class);
    }

    public int max(Uri contentUri, Select select, Where...where) {
        Detail[] details = Detail.prepend(select.asMax(), where);
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        Uri requestUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(requestUri, null, null, null, null, null).get(0, 0).as(int.class);
    }

    public int min(Uri contentUri, Select select, Where...where) {
        Detail[] details = Detail.prepend(select.asMin(), where);
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        Uri requestUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return runQuery(requestUri, null, null, null, null, null).get(0, 0).as(int.class);
    }


    public long create(Uri contentUri, Value...values) {
        Uri uri = context.getContentResolver().insert(contentUri, Value.compile(values));
        return Long.parseLong(uri.getLastPathSegment());
    }

    public long create(Object object) {
        if(object instanceof Uri) {
            return create((Uri) object, new Value[0]);
        }
        ObjectParser objectParser = ObjectParser.create(object);
        Uri contentUri = Contracts.getContentUri(object.getClass());
        ContentValues contentValues = objectParser.getContentValues();
        Uri uri = context.getContentResolver().insert(contentUri, contentValues);
        long id = Long.parseLong(uri.getLastPathSegment());
        objectParser.setId(id);
        return id;
    }

    public int createAll(Collection<?> objects) {
        int ix = 0;
        Uri contentUri = null;
        ContentValues[] values = new ContentValues[objects.size()];
        for(Object object : objects) {
            if(contentUri == null) contentUri = Contracts.getContentUri(object.getClass());
            values[ix++] = ObjectParser.create(object).getContentValues();
        }
        return (contentUri == null) ? 0 : context.getContentResolver().bulkInsert(contentUri, values);
    }


    public int update(Uri contentUri, Detail...details) {
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        contentUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return context.getContentResolver().update(contentUri, Value.compile(details), null, null);
    }

    public int update(Object object, String...columns) {
        ObjectParser parser = ObjectParser.create(object);
        long id = parser.getId();
        if(id == 0) {
            throw Err.get(Err.ERR_CANNOT_UPDATE_NEW_OBJECT, object);
        }
        Uri contentUri = ContentUris.withAppendedId(Contracts.getContentUri(object.getClass()), id);
        ContentValues contentValues = parser.getContentValues(columns);
        return context.getContentResolver().update(contentUri, contentValues, null, null);
    }

    public int updateAll(Collection<?> objects, String...columns) {
        int count = 0;
        for(Object object : objects) {
            count += update(object, columns);
        }
        return count;
    }


    public long save(Uri contentUri, long id, Value... values) {
        if(id > 0) {
            update(ContentUris.withAppendedId(contentUri, id), values);
            return id;
        } else {
            return create(contentUri, values);
        }
    }

    public long save(Object object) {
        ObjectParser parser = ObjectParser.create(object);
        long id = parser.getId();
        Uri contentUri = Contracts.getContentUri(object.getClass());
        ContentValues contentValues = parser.getContentValues();
        if(id > 0) {
            contentUri = ContentUris.withAppendedId(contentUri, id);
            context.getContentResolver().update(contentUri, contentValues, null, null);
            return id;
        } else {
            Uri uri = context.getContentResolver().insert(contentUri, contentValues);
            return Long.parseLong(uri.getLastPathSegment());
        }
    }


    public int destroy(Uri contentUri, Detail...details) {
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        contentUri = contentUri.buildUpon().appendQueryParameter("q", query.toJson()).build();
        return context.getContentResolver().delete(contentUri, null, null);
    }

    public int destroy(Object object) {
        if(object instanceof Uri) {
            return destroy((Uri) object, new Detail[0]);
        }
        ObjectParser parser = ObjectParser.create(object);
        long id = parser.getId();
        if(id == 0) throw Err.get(Err.ERR_CANNOT_DESTROY_NEW_OBJECT, object);
        Uri contentUri = ContentUris.withAppendedId(Contracts.getContentUri(object.getClass()), id);
        return context.getContentResolver().delete(contentUri, null, null);
    }

    public int destroyAll(Collection<?> objects) {
        Uri uri = null;
        List<Long> ids = new ArrayList<Long>();
        for(Object object : objects) {
            ObjectParser parser = ObjectParser.create(object);
            long id = parser.getId();
            if(id == 0) throw Err.get(Err.ERR_CANNOT_DESTROY_NEW_OBJECT, object);
            if(uri == null) {
                Contracts.Contract contract = Contracts.getContract(object.getClass());
                uri = contract.uri;
            }
            ids.add(id);
        }
        if(uri != null) {
            return destroy(uri, where(Contract.Model.COLUMN_ID).isIn(ids));
        }
        return 0;
    }


    public static Value value(String key, Object value) {
        return new Value(key, value);
    }

    public static Select select(String field) {
        return new Select(null, field);
    }

    public static Where where(Uri contentUri, String field) {
        return new Where(contentUri, field);
    }

    public static Where where(Uri contentUri) {
        return new Where(contentUri, null);
    }

    public static Where where(String field) {
        return new Where(null, field);
    }

    public static Order order(String field) {
        return new Order(field);
    }

    public static Limit limit(int limit) {
        return new Limit(limit);
    }


    public Batch.Sync batch(String authority) {
        return new Batch.Sync(context, authority);
    }


    public static class Results {
        private final Cursor cursor;
        public Results(Cursor cursor) {
            this.cursor = cursor;
        }
        public Cursor getCursor() {
            return cursor;
        }
        public void close() {
            if(cursor != null) cursor.close();
        }
        private <E> E close(E e) {
            try {
                return e;
            } finally {
                close();
            }
        }
        public <T> T as(Class<T> type) {
            return close(ObjectBuilder.create(type).withData(cursor).build(0));
        }
        public <T> List<T> asListOf(Class<T> type) {
            return close(ObjectBuilder.create(type).withData(cursor).build());
        }
        public String[][] asStrings() {
            try {
                String[][] data = new String[cursor.getCount()][cursor.getColumnCount()];
                for(int r = 0; r < data.length; r++) {
                    cursor.moveToPosition(r);
                    for(int c = 0; c < data[r].length; c++) {
                        data[r][c] = cursor.getString(c);
                    }
                }
                return data;
            } finally {
                close();
            }
        }
        public Row get(int row) {
            return new Row(this, row);
        }
        public Cell get(int row, int column) {
            return new Cell(this, row, column);
        }


        public static class Row {
            private final Results results;
            private final int r;
            Row(Results results, int r) {
                this.results = results;
                this.r = r;
            }
            public <T> T as(Class<T> type) {
                return results.close(ObjectBuilder.create(type).withData(results.cursor).build(r));
            }
            public String[] asStrings() {
                try {
                    if(results.cursor.moveToPosition(r)) {
                        String[] data = new String[results.cursor.getColumnCount()];
                        for(int c = 0; c < data.length; c++) {
                            data[c] = results.cursor.getString(c);
                        }
                        return data;
                    }
                    return new String[0];
                } finally {
                    results.close();
                }
            }
        }

        public static class Cell {
            private final Results results;
            private final int r, c;
            Cell(Results results, int r, int c) {
                this.results = results;
                this.r = r;
                this.c = c;
            }
            public <T> T as(Class<T> type) {
                return results.close(ObjectBuilder.create(type).withData(results.cursor).build(r, c));
            }
        }
    }

}
