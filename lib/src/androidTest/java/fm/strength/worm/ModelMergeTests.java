package fm.strength.worm;

import android.test.ProviderTestCase2;

import java.util.List;

import fm.strength.testapps.merge.MergeAppContentProvider;
import fm.strength.testapps.merge.MergeAppContract;
import fm.strength.testapps.merge.MergeAppContract.Categories;
import fm.strength.testapps.merge.MergeAppContract.Lists;
import fm.strength.testapps.merge.MergeAppContract.Products;
import fm.strength.testapps.merge.MergeAppContract.Stores;
import fm.strength.testapps.merge.MergeAppContract.Trips;
import fm.strength.worm.Contracts.Contract.Model;
import fm.strength.worm.Data.Model.Contract;
import fm.strength.worm.Data.Model.Join;

import static fm.strength.worm.Data.where;
import static fm.strength.worm.util.TestHelper.values;
import static org.fest.assertions.api.Assertions.assertThat;

public class ModelMergeTests extends ProviderTestCase2<MergeAppContentProvider> {

    public static class Test1 {
        @Contract({ Lists.Stores.Categories.Products.class, Products.class })
        public static class Product {
            long id;
            String name;
            Category category;
            Store store;
        }
        @Contract({ Lists.Stores.Categories.class, Categories.class })
        public static class Category {
            long id;
            String name;
        }
        @Contract({ Lists.Stores.class, Stores.class })
        public static class Store {
            long id;
            String name;
        }
    }
    public static class Test2 {
        @Contract({ Lists.Stores.Categories.Products.class, Products.class })
        public static class Product {
            long id;
            String name;
            String units;
            Category category;
            Store store;
            List list;
        }
        @Contract({ Lists.Stores.Categories.class, Categories.class })
        public static class Category {
            long id;
            String name;
        }
        @Contract({ Lists.Stores.class, Stores.class })
        public static class Store {
            long id;
            String name;
        }
        @Contract(Lists.class)
        public static class List {
            long id;
            String name;
        }
    }
    public static class Test3 {
        @Contract({ Lists.Stores.Categories.class, Categories.class })
        public static class Category {
            public long id;
            public String name;
            public List<Product> products;
            @Contract({ Lists.Stores.Categories.Products.class, Products.class })
            public static class Product {
                public long categoryId;
                public long id;
                public String name;
            }
        }
    }
    public static class Test4 {
        @Contract({Lists.Stores.class, Stores.class})
        public static class Store {
            public long   id;
            public String name;
        }
    }
    public static class Test5 {
        @Contract({ Trips.Lists.class, Lists.class })
        public static class List {
            public java.util.List<Store> stores;
        }
        @Contract({Lists.Stores.class, Stores.class})
        public static class Store {
            long   id;
            String name;
        }
    }
    public static class Test6 {
        @Contract({ Trips.Lists.class, Lists.class })
        public static class List {
            public long   id;
            public String name;
            @Join(from=Lists.Stores.COLUMN_LIST_ID, to=Lists.COLUMN_ID)
            public java.util.List<Store> stores;
        }
        @Contract({Lists.Stores.class, Stores.class})
        public static class Store {
            long   id;
            String name;
        }
    }
    public static class Test7 {
        @Contract({ Trips.Lists.class, Lists.class })
        public static class List {
            public java.util.List<Item> items;
        }
        @Contract(Lists.Stores.class)
        public static class Item {
            long   id;
            Store  store;
        }
        @Contract(Stores.class)
        public static class Store {
            long   id;
            String name;
        }
    }


    private Data data;

    public ModelMergeTests() {
        super(MergeAppContentProvider.class, MergeAppContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        data = Data.sync(getMockContext());
    }


    public void testFindTest1() throws Exception {
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long categoryId = data.create(Categories.CONTENT_URI, values("name:'category 1'"));
        long productId = data.create(Products.CONTENT_URI, values("name:'product 1"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:0,store_id:?", storeId));
        long listCategoryId = data.create(Lists.Stores.Categories.CONTENT_URI, values("store_id:?,category_id:?", listStoreId, categoryId));
        long listProductId = data.create(Lists.Stores.Categories.Products.CONTENT_URI, values("category_id:?,product_id:?", listCategoryId, productId));

        List<Test1.Product> products = data.findAll(Test1.Product.class,
                where(Lists.Stores.Categories.Products.CONTENT_URI, Lists.Stores.Categories.Products.COLUMN_ID).isEqualTo(listProductId)
        );

        assertThat(products).hasSize(1);
        assertThat(products.get(0).id).isEqualTo(listProductId);
        assertThat(products.get(0).name).isEqualTo("product 1");
        assertThat(products.get(0).category).isNotNull();
        assertThat(products.get(0).category.id).isEqualTo(listCategoryId);
        assertThat(products.get(0).category.name).isEqualTo("category 1");
        assertThat(products.get(0).store).isNotNull();
        assertThat(products.get(0).store.id).isEqualTo(listStoreId);
        assertThat(products.get(0).store.name).isEqualTo("store 1");
    }

    public void testFindTest2() throws Exception {
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long categoryId = data.create(Categories.CONTENT_URI, values("name:'category 1'"));
        long productId = data.create(Products.CONTENT_URI, values("name:'product 1"));
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));
        long listCategoryId = data.create(Lists.Stores.Categories.CONTENT_URI, values("store_id:?,category_id:?", listStoreId, categoryId));
        long listProductId = data.create(Lists.Stores.Categories.Products.CONTENT_URI, values("category_id:?,product_id:?", listCategoryId, productId));

        List<Test2.Product> products = data.findAll(Test2.Product.class,
                where(Lists.Stores.Categories.Products.CONTENT_URI, Lists.Stores.Categories.Products.COLUMN_ID).isEqualTo(listProductId)
        );

        assertThat(products).hasSize(1);
        assertThat(products.get(0).id).isEqualTo(listProductId);
        assertThat(products.get(0).name).isEqualTo("product 1");
        assertThat(products.get(0).category).isNotNull();
        assertThat(products.get(0).category.id).isEqualTo(listCategoryId);
        assertThat(products.get(0).category.name).isEqualTo("category 1");
        assertThat(products.get(0).store).isNotNull();
        assertThat(products.get(0).store.id).isEqualTo(listStoreId);
        assertThat(products.get(0).store.name).isEqualTo("store 1");
        assertThat(products.get(0).list).isNotNull();
        assertThat(products.get(0).list.id).isEqualTo(listId);
        assertThat(products.get(0).list.name).isEqualTo("list 1");
    }

    public void testFindTest2_withDefaultUri() throws Exception {
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long categoryId = data.create(Categories.CONTENT_URI, values("name:'category 1'"));
        long productId = data.create(Products.CONTENT_URI, values("name:'product 1"));
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));
        long listCategoryId = data.create(Lists.Stores.Categories.CONTENT_URI, values("store_id:?,category_id:?", listStoreId, categoryId));
        long listProductId = data.create(Lists.Stores.Categories.Products.CONTENT_URI, values("category_id:?,product_id:?", listCategoryId, productId));

        List<Test2.Product> products = data.findAll(Test2.Product.class,
                where(Lists.Stores.Categories.Products.COLUMN_ID).isEqualTo(listProductId)
        );

        assertThat(products).hasSize(1);
        assertThat(products.get(0).id).isEqualTo(listProductId);
        assertThat(products.get(0).name).isEqualTo("product 1");
        assertThat(products.get(0).category).isNotNull();
        assertThat(products.get(0).category.id).isEqualTo(listCategoryId);
        assertThat(products.get(0).category.name).isEqualTo("category 1");
        assertThat(products.get(0).store).isNotNull();
        assertThat(products.get(0).store.id).isEqualTo(listStoreId);
        assertThat(products.get(0).store.name).isEqualTo("store 1");
        assertThat(products.get(0).list).isNotNull();
        assertThat(products.get(0).list.id).isEqualTo(listId);
        assertThat(products.get(0).list.name).isEqualTo("list 1");
    }

    public void testFindTest3() throws Exception {
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long categoryId = data.create(Categories.CONTENT_URI, values("name:'category 1'"));
        long productId = data.create(Products.CONTENT_URI, values("name:'product 1"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:0,store_id:?", storeId));
        data.create(Lists.Stores.Categories.CONTENT_URI, values("store_id:0,category_id:0"));
        long listCategoryId = data.create(Lists.Stores.Categories.CONTENT_URI, values("store_id:?,category_id:?", listStoreId, categoryId));
        long listProductId = data.create(Lists.Stores.Categories.Products.CONTENT_URI, values("category_id:?,product_id:?", listCategoryId, productId));

        Test3.Category category = data.find(Test3.Category.class, where(Model.COLUMN_ID).isEqualTo(listCategoryId));

        assertThat(category).isNotNull();
        assertThat(category.id).isEqualTo(listCategoryId);
        assertThat(category.name).isEqualTo("category 1");
        assertThat(category.products).hasSize(1);
        assertThat(category.products.get(0).categoryId).isEqualTo(listCategoryId);
        assertThat(category.products.get(0).id).isEqualTo(listProductId);
        assertThat(category.products.get(0).name).isEqualTo("product 1");
    }

    public void testFindMerged() throws Exception {
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long linkId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));

        List<Test4.Store> stores = data.findAll(Test4.Store.class);

        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).id).isEqualTo(linkId);
        assertThat(stores.get(0).name).isEqualTo("store 1");
    }

    public void testFindSplitMerge() throws Exception {
        long tripId = data.create(Trips.CONTENT_URI, values("name:'trip 1"));
        data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 2'"));
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        data.create(Lists.Stores.CONTENT_URI, values("list_id:0,store_id:0"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));
        long tripListId = data.create(Trips.Lists.CONTENT_URI, values("trip_id:?,list_id:?", tripId, listId));

        List<Test5.List> lists = data.findAll(Test5.List.class);

        assertThat(lists).hasSize(1);
        assertThat(lists.get(0).stores).hasSize(1);
        assertThat(lists.get(0).stores.get(0).id).isEqualTo(listStoreId);
        assertThat(lists.get(0).stores.get(0).name).isEqualTo("store 1");
    }

    public void testFindSplitMergeBoth() throws Exception {
        long tripId = data.create(Trips.CONTENT_URI, values("name:'trip 1"));
        data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 2'"));
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        data.create(Lists.Stores.CONTENT_URI, values("list_id:0,store_id:0"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));
        long tripListId = data.create(Trips.Lists.CONTENT_URI, values("trip_id:?,list_id:?", tripId, listId));

        List<Test6.List> lists = data.findAll(Test6.List.class);

        assertThat(lists).hasSize(1);
        assertThat(lists.get(0).id).isEqualTo(tripListId);
        assertThat(lists.get(0).name).isEqualTo("list 2");
        assertThat(lists.get(0).stores).hasSize(1);
        assertThat(lists.get(0).stores.get(0).id).isEqualTo(listStoreId);
        assertThat(lists.get(0).stores.get(0).name).isEqualTo("store 1");
    }

    public void testFindSplitJoin() throws Exception {
        long tripId = data.create(Trips.CONTENT_URI, values("name:'trip 1"));
        data.create(Lists.CONTENT_URI, values("name:'list 1'"));
        long listId = data.create(Lists.CONTENT_URI, values("name:'list 2'"));
        long storeId = data.create(Stores.CONTENT_URI, values("name:'store 1'"));
        long listStoreId = data.create(Lists.Stores.CONTENT_URI, values("list_id:?,store_id:?", listId, storeId));
        long tripListId = data.create(Trips.Lists.CONTENT_URI, values("trip_id:?,list_id:?", tripId, listId));

        List<Test7.List> lists = data.findAll(Test7.List.class);

        assertThat(lists).hasSize(1);
        assertThat(lists.get(0).items).hasSize(1);
        assertThat(lists.get(0).items.get(0).id).isEqualTo(listStoreId);
        assertThat(lists.get(0).items.get(0).store).isNotNull();
        assertThat(lists.get(0).items.get(0).store.id).isEqualTo(storeId);
        assertThat(lists.get(0).items.get(0).store.name).isEqualTo("store 1");
    }

}
