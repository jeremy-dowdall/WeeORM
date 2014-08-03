package fm.strength.worm;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import fm.strength.sloppyj.Jay;
import fm.strength.worm.data.Detail;
import fm.strength.worm.data.Task;
import fm.strength.worm.data.Value;
import fm.strength.worm.util.Err;

@SuppressWarnings("unchecked")
public abstract class Batch<T> {

    final Context context;
    final String authority;
    final ArrayList<ContentProviderOperation> operations;
    final ArrayList<Object> objects;

    public Batch(Context context, String authority) {
        this.context = context;
        this.authority = authority;
        this.operations = new ArrayList<ContentProviderOperation>();
        this.objects = new ArrayList<Object>();
    }


    public T create(Uri contentUri, Value...values) {
        add(ContentProviderOperation.newInsert(contentUri).withValues(Value.compile(values)).build(), null);
        return (T) this;
    }

    public T create(Object object) {
        Uri uri = Contracts.getContentUri(object.getClass());
        ContentValues contentValues = ObjectParser.create(object).getContentValues();
        add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build(), object);
        return (T) this;
    }

    public T createAll(Collection<?> objects) {
        for(Object object : objects) {
            create(object);
        }
        return (T) this;
    }

    public T createAll(Object...objects) {
        return createAll(Arrays.asList(objects));
    }


    public T update(Uri contentUri, Detail...details) {
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        Uri requestUri = contentUri.buildUpon().appendQueryParameter("q", Jay.get(query).asJson()).build();
        add(ContentProviderOperation.newUpdate(requestUri).withValues(Value.compile(details)).build(), null);
        return (T) this;
    }

    public T update(Object object, String...columns) {
        ObjectParser parser = ObjectParser.create(object);
        long id = parser.getId();
        if(id == 0) throw Err.get(Err.ERR_CANNOT_UPDATE_NEW_OBJECT, object);
        Uri uri = ContentUris.withAppendedId(Contracts.getContentUri(object.getClass()), id);
        ContentValues values = parser.getContentValues(columns);
        add(ContentProviderOperation.newUpdate(uri).withValues(values).build(), null);
        return (T) this;
    }

    public T updateAll(Collection<?> objects, String...columns) {
        for(Object object : objects) {
            update(object, columns);
        }
        return (T) this;
    }


    public T save(Uri contentUri, long id, Value... values) {
        if(id > 0) {
            return update(ContentUris.withAppendedId(contentUri, id), values);
        } else {
            return create(contentUri, values);
        }
    }

    public T save(Object object) {
        ObjectParser parser = ObjectParser.create(object);
        long id = parser.getId();
        Uri uri = Contracts.getContentUri(object.getClass());
        ContentValues values = parser.getContentValues();
        if(id > 0) {
            uri = ContentUris.withAppendedId(uri, id);
            add(ContentProviderOperation.newUpdate(uri).withValues(values).build(), null);
        } else {
            add(ContentProviderOperation.newInsert(uri).withValues(values).build(), object);
        }
        return (T) this;
    }

    public T saveAll(Collection<?> objects) {
        for(Object object : objects) {
            save(object);
        }
        return (T) this;
    }


    public T destroy(Uri contentUri, Detail...details) {
        Query query = QueryBuilder.create(contentUri).withDetails(details).build();
        Uri uri = contentUri.buildUpon().appendQueryParameter("q", Jay.get(query).asJson()).build();
        add(ContentProviderOperation.newDelete(uri).build(), null);
        return (T) this;
    }

    public T destroy(Object object) {
        if(object instanceof Uri) {
            return destroy((Uri) object, new Detail[0]);
        } else {
            ObjectParser parser = ObjectParser.create(object);
            long id = parser.getId();
            if(id == 0) throw Err.get(Err.ERR_CANNOT_DESTROY_NEW_OBJECT, object);
            Uri uri = ContentUris.withAppendedId(Contracts.getContentUri(object.getClass()), id);
            add(ContentProviderOperation.newDelete(uri).build(), null);
            return (T) this;
        }
    }

    public T destroyAll(Collection<?> objects) {
        for(Object object : objects) {
            destroy(object);
        }
        return (T) this;
    }


    private void add(ContentProviderOperation operation, Object object) {
        operations.add(operation);
        objects.add(object);
    }

    private boolean execute() {
        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(authority, operations);
            for(int i = 0; i < results.length; i++) {
                Object object = objects.get(i);
                if(object != null) {
                    long id = ContentUris.parseId(results[i].uri);
                    ObjectParser.create(object).setId(id);
                }
            }
            return results.length == operations.size();
        } catch(RemoteException e) {
            e.printStackTrace();
        } catch(OperationApplicationException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static class Async extends Batch<Async> {

        public Async(Context context, String authority) {
            super(context, authority);
        }

        public void execute() {
            Task.create(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return Async.super.execute();
                }
            });
        }

        public void then(Callback<Boolean> callback) {
            Task.create(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return Async.super.execute();
                }
            }).then(callback);
        }

        public void thenNotify(final Uri uri) {
            Task.create(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return Async.super.execute();
                }
            }).then(new Callback<Boolean>() {
                public void onSuccess(Boolean result) {
                    context.getContentResolver().notifyChange(uri, null);
                }
            });
        }

    }


    public static class Sync extends Batch<Sync> {

        public Sync(Context context, String authority) {
            super(context, authority);
        }

        public boolean execute() {
            return super.execute();
        }

    }

}
