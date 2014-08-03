package fm.strength.worm;

import android.net.Uri;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import fm.strength.worm.data.Detail;
import fm.strength.worm.data.Select;
import fm.strength.worm.data.Task;
import fm.strength.worm.data.Value;
import fm.strength.worm.data.Where;

public class Async {

    private final Data data;

    Async(Data data) {
        this.data = data;
    }

    public Task<Data.Results> query(final Uri contentUri, final Detail...details) {
        return Task.create(new Callable<Data.Results>() {
            public Data.Results call() {
                return data.query(contentUri, details);
            }
        });
    }


    public <T> Task<T> find(final Class<T> type, final Detail...details) {
        return Task.create(new Callable<T>() {
            public T call() {
                return data.find(type, details);
            }
        });
    }

    public <T> Task<List<T>> findAll(final Class<T> type, final Detail...details) {
        return Task.create(new Callable<List<T>>() {
            public List<T> call() {
                return data.findAll(type, details);
            }
        });
    }


    public Task<Integer> count(final Uri contentUri, final Where...where) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.count(contentUri, where);
            }
        });
    }

    public Task<Integer> max(final Uri contentUri, final Select select, final Where...where) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.max(contentUri, select, where);
            }
        });
    }

    public Task<Integer> min(final Uri contentUri, final Select select, final Where...where) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.min(contentUri, select, where);
            }
        });
    }


    public Task<Long> create(final Uri contentUri, final Value...values) {
        return Task.create(new Callable<Long>() {
            public Long call() {
                return data.create(contentUri, values);
            }
        });
    }

    public Task<Long> create(final Object object) {
        return Task.create(new Callable<Long>() {
            public Long call() throws Exception {
                return data.create(object);
            }
        });
    }

    public Task<Integer> createAll(final Collection<?> objects) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.createAll(objects);
            }
        });
    }


    public Task<Integer> update(final Uri contentUri, final Detail...details) {
        return Task.create(new Callable<Integer>() {
            public Integer call() {
                return data.update(contentUri, details);
            }
        });
    }

    public Task<Integer> update(final Object object, final String...columns) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.update(object, columns);
            }
        });
    }

    public Task<Integer> updateAll(final Collection<?> objects, final String...columns) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.updateAll(objects, columns);
            }
        });
    }


    public Task<Long> save(final Uri contentUri, final long id, final Value...values) {
        return Task.create(new Callable<Long>() {
            public Long call() throws Exception {
                return data.save(contentUri, id, values);
            }
        });
    }

    public Task<Long> save(final Object object) {
        return Task.create(new Callable<Long>() {
            public Long call() throws Exception {
                return data.save(object);
            }
        });
    }


    public Task<Integer> destroy(final Uri contentUri, final Detail...details) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.update(contentUri, details);
            }
        });
    }

    public Task<Integer> destroy(final Object object) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.destroy(object);
            }
        });
    }

    public Task<Integer> destroyAll(final Collection<?> objects) {
        return Task.create(new Callable<Integer>() {
            public Integer call() throws Exception {
                return data.destroyAll(objects);
            }
        });
    }


    public Batch.Async batch(String authority) {
        return new Batch.Async(data.context, authority);
    }

}
