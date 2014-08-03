package fm.strength.worm;

import android.test.ProviderTestCase2;

import fm.strength.sloppyj.Jay;
import fm.strength.testapps.base.TestAppContentProvider;
import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Users;
import fm.strength.worm.Data.Model.Contract;
import fm.strength.worm.Data.Model.JSON;

import static fm.strength.worm.Data.select;
import static fm.strength.worm.Data.value;
import static fm.strength.worm.Data.where;
import static org.fest.assertions.api.Assertions.assertThat;


public class ModelJsonTests extends ProviderTestCase2<TestAppContentProvider> {

    private Data db;

    public ModelJsonTests() {
        super(TestAppContentProvider.class, TestAppContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = Data.sync(getMockContext());
    }


    @Contract(Users.class)
    public static class User {
        public long id;
        public String name;
        public Details details;
        @JSON
        public static class Details {
            public long aLong;
            public String aString;
        }
    }
    public void testFind_withJsonField() throws Exception {
        User.Details details = new User.Details();
        details.aLong = 123L;
        details.aString = "hi!";
        long id = db.create(Users.CONTENT_URI, value(Users.COLUMN_DETAILS, Jay.get(details).asJson()));

        User user = db.find(User.class);

        assertThat(user).isNotNull();
        assertThat(user.id).isEqualTo(id);
        assertThat(user.details).isNotNull();
        assertThat(user.details.aLong).isEqualTo(details.aLong);
        assertThat(user.details.aString).isEqualTo(details.aString);
    }

    public void testCreate_withJsonField() throws Exception {
        User.Details details = new User.Details();
        details.aLong = 123L;
        details.aString = "hi!";
        User user = new User();
        user.details = details;

        long id = db.create(user);

        assertThat(id).isGreaterThan(0);
        String json = Jay.get(details).asJson();
        assertThat(db.query(Users.CONTENT_URI, select(Users.COLUMN_DETAILS), where(Users.COLUMN_ID).isEqualTo(id)).as(String.class)).isEqualTo(json);
    }

    public void testUpdate_withJsonField() throws Exception {
        User.Details details = new User.Details();
        details.aLong = 123L;
        details.aString = "hi!";
        long id = db.create(Users.CONTENT_URI, value(Users.COLUMN_DETAILS, Jay.get(details).asJson()));
        User user = new User();
        user.id = id;
        user.details = details;
        user.details.aString = "bye!";

        int count = db.update(user);

        assertThat(count).isEqualTo(1);
        String json = Jay.get(details).asJson();
        assertThat(db.query(Users.CONTENT_URI, select(Users.COLUMN_DETAILS), where(Users.COLUMN_ID).isEqualTo(id)).as(String.class)).isEqualTo(json);
    }
}
