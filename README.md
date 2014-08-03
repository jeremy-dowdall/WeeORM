WeeOrm
=============
A wee lil' ORM for Android.

Status
------
WeeORM is currently more of an exploration than an implementation and should be received as such.

This is not production ready. Not even Beta or Alpha - WeeORM can and will change randomly and without notice. This README will be updated from time-to-time as time permits. The proof however, as they say, is in the pudding; or, in this matter, the code. Please let that be the basis of discussion, along with some insights into my [initial purpose](http://jtdowdall.blogspot.com/2014/04/contextual-data-models.html). 

Quick Start
-----------

clone the sample project... (TODO: create a sample project)

Getting Started
---------------

####Install - Gradle
```java
repositories {
    maven { url 'http://github.com/jeremy-dowdall/mvn-repo/raw/master' }
}

dependencies {
    compile 'me.licious:weeorm:+@aar'
}
```

####Create an object model
```java
public static class MyModel {
    String name;
}
```

####Save it to the Database
```java
long id = Data.save(MyModel.class, Data.value("name", "Hello World!!!");
```

####Read it back
```java
Data.find(MyModel.class, Data.withId(id));
```

API - Synchronous
-------------

####Get an instance to work with:
```java
Data    <- Data.sync(Context);
```

Note that all other methods throw IllegalArgumentException if something goes awry

####Retrieve:

```java
Results <- query(Uri, Detail...)

T       <- find(Class<T>, Detail...)    // T has @Contract
List<T> <- findAll(Class<T>, Detail...) // T has @Contract

int     <- count(Uri, Where...)
int     <- max(Uri, Select, Where...)
int     <- min(Uri, Select, Where...)

Results Object:
    Cursor   <- getCursor()
    T        <- as(Class<T>)
    List<T>  <- asListOf(Class<T>)
    String[] <- asStrings()
    T        <- get(int).as(Class<T>)
    String[] <- get(int).asStrings()
    T        <- get(int, int).as(Class<T>)
```

####Create:

```java
long    <- create(Uri, Value...)
long    <- create(T)
int     <- createAll(Collection<T>)
int     <- createAll(T...)
```

####Update:

```java
int     <- update(Uri, Detail...)     // must contain at least 1 Value
int     <- update(T)
int     <- updateAll(Collection<T>)
int     <- updateAll(T...)
```

####Save:

```java
long    <- save(Uri, long, Value...)
long    <- save(T)
int     <- saveAll(Collection<T>)
int     <- saveAll(T...)
```

####Destroy:

```java
int     <- destroy(Uri, Where...)
int     <- destroy(T)
int     <- destroyAll(Collection<T>)
int     <- destroyAll(T...)
```

API - Asynchronous
----------------
####Get an instance to work with:
```java
Async   <- Data.async(Context);
```

The async API is identical to that of the synchronous one, with the exception of the return values since, due to the nature of async calls, there won't be anything to return right away. Instead, each async method returns an instance of Task that can be used to register a callback:
```java
Data.async(Context)
    .find(Class<T>, Where...)
    .then(new Callback<T>() {
        public void onSuccess(T object) {
            // do stuff
        }
        public void onFailure(Exception error) {
            // do stuff
        }
        public void onComplete(T object, Exception error) {
            // do stuff
        }
    });
```
If you're already working with a synchronous instance, it already has an async instance ready for you:
```java
Data data = Data.sync(Context);

data.async.find(MyModel.class).then(new Callback<MyModel>() {
    public void onSuccess(MyModel model) {
        // do stuff
    }
});
```
Asynchronous operations are built upon the AsyncTask class, and the callback methods will be called on the application's UI thread.

Note that the '.then' method is optional - if you don't need the callback, just skip it.

API - Batch
---------
All methods can also be used in a batch, either synchronously:
```java
Data.sync(Context)
    .batch(AUTHORITY)
        .destroy(Class<T>)
        .create(T...)
    .execute();
```
or asynchronously:
```java
Data.async(Context)
    .batch(AUTHORITY)
        .destroy(Class<T>)
        .create(T...)
    .then(new Callback<Integer>() {
        public void onSuccess(Integer count) {
            // do stuff
        }
        public void onFailure(Exception error) {
            // do stuff
        }
        public void onComplete(Integer count, Exception error) {
            // do stuff
        }
    });
```
All batches need to be terminated - the sync version has a single no-args method to handle this: execute(). The async version shown above is using another method: then(Callback). If you don't need a callback however, then you can also use execute() similar to sync batches:
```java
Data.async(Context)
    .batch(AUTHORITY)
        .destroy(Class<T>)
        .create(T...)
    .execute();
```

ObjectBuilder:
-------------
```java
List<T> <- ObjectBuilder.create(T).withData(Cursor).build()         // build all rows
T       <- ObjectBuilder.create(T).withData(Cursor).build(int)      // build a single row
T       <- ObjectBuilder.create(T).withData(Cursor).build(int, int) // build a single cell
```

####Type of objects built

* Direct - a single cursor cell (primitives, wrappers, Strings)
* Simple - a single cursor row
* Nested - multiple cursor rows (requires special columns: level, parent id, and id)

####Annotations

* @Contract - specify the Contract Class for an object

####Contracts


