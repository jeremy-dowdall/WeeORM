package fm.strength.worm;

import android.test.ProviderTestCase2;

import java.util.List;

import fm.strength.sloppyj.Jay;
import fm.strength.testapps.base.TestAppContentProvider;
import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Users;
import fm.strength.testapps.base.TestAppContract.Workouts;
import fm.strength.worm.Data.Model;
import fm.strength.worm.Data.Model.Contract;
import fm.strength.worm.Data.Model.JSON;
import fm.strength.worm.Data.Model.Where;

import static fm.strength.worm.Data.select;
import static fm.strength.worm.Data.value;
import static fm.strength.worm.Data.where;
import static org.fest.assertions.api.Assertions.assertThat;


public class ModelWhereTests extends ProviderTestCase2<TestAppContentProvider> {

    private Data db;

    public ModelWhereTests() {
        super(TestAppContentProvider.class, TestAppContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = Data.sync(getMockContext());
    }


    @Contract(Users.class)
    public static class User {
        public String name;
        @Where(field = Workouts.COLUMN_NAME, is = Model.NULL)
        public List<Workout> workouts;
        @Contract(Workouts.class)
        public static class Workout {
            public long id;
        }
    }

    public void testFindAll_whereNameIsNull() throws Exception {
        long id = db.create(Users.CONTENT_URI, value(Users.COLUMN_NAME, "user 1"));
        db.create(Workouts.CONTENT_URI, value(Workouts.COLUMN_NAME, "workout 2"), value(Workouts.COLUMN_USER_ID, id));
        long wid = db.create(Workouts.CONTENT_URI, value(Workouts.COLUMN_USER_ID, id));
        db.create(Workouts.CONTENT_URI, value(Workouts.COLUMN_NAME, "workout 3"), value(Workouts.COLUMN_USER_ID, id));

        User user = db.find(User.class);

        assertThat(user).isNotNull();
        assertThat(user.workouts).hasSize(1);
        assertThat(user.workouts.get(0).id).isEqualTo(wid);
    }

}
