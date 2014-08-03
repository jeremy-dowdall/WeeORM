package fm.strength.worm;

import android.test.ProviderTestCase2;

import java.util.Arrays;
import java.util.List;

import fm.strength.testapps.base.TestAppContentProvider;
import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Users;
import fm.strength.testapps.base.TestAppContract.Workouts;
import fm.strength.testapps.base.TestAppContract.Workouts.Sections;
import fm.strength.testapps.base.TestAppContract.Workouts.Sections.Exercises;
import fm.strength.worm.Data.Model.Contract;
import fm.strength.worm.Data.Model.NotNull;
import fm.strength.worm.Data.Model.Order;
import fm.strength.worm.Data.Model.X;
import fm.strength.worm.util.Err;

import static fm.strength.worm.Data.select;
import static fm.strength.worm.Data.value;
import static fm.strength.worm.Data.where;
import static org.fest.assertions.api.Assertions.assertThat;
import static fm.strength.worm.util.TestHelper.*;


public class ModelTests extends ProviderTestCase2<TestAppContentProvider> {

    public static class Nested {
        @Contract(Workouts.class)
        public static class Workout {
            String name;
            List<Section> sections;
        }
        @Contract(Sections.class)
        public static class Section {
            String name;
            List<Exercise> exercises;
        }
        @Contract(Exercises.class)
        public static class Exercise {
            long id;
            int ix;
            String name;
            int sectionId;

            public Exercise() { }

            public Exercise(long id, int ix) {
                this.id = id;
                this.ix = ix;
            }
            public Exercise(long id, String name) {
                this.id = id;
                this.name = name;
            }
        }
    }
    public static class NestedWithExplicitSort {
        @Contract(Workouts.class)
        public static class Workout {
            String name;
            @Order(Sections.COLUMN_NAME+" DESC")
            List<Section> sections;
        }
        @Contract(Sections.class)
        public static class Section {
            String name;
            @Order(Exercises.COLUMN_NAME+" DESC")
            List<Exercise> exercises;
        }
        @Contract(Exercises.class)
        public static class Exercise {
            long id;
            String name;
            int sectionId;

            public Exercise() { }

            public Exercise(long id, String name) {
                this.id = id;
                this.name = name;
            }
        }
    }
    public static class SingleJoined {
        @Contract(Users.class)
        public static class User {
            String name;
        }
        @Contract(Workouts.class)
        public static class Workout {
            String name;
        }
        @Contract(Sections.class)
        public static class Section {
            String name;
            Workout workout;
        }
    }
    public static class SiblingJoined {
        @Contract(Users.class)
        public static class User {
            String name;
        }
        @Contract(Workouts.class)
        public static class Workout {
            String name;
        }
        @Contract(Sections.class)
        public static class Section {
            String name;
            User user;
            Workout workout;
        }
    }
    public static class IdsSiblingJoined {
        @Contract(Users.class)
        public static class User {
            long id;
            String name;
        }
        @Contract(Workouts.class)
        public static class Workout {
            long id;
            String name;
        }
        @Contract(Sections.class)
        public static class Section {
            long id;
            String name;
            User user;
            Workout workout;
        }
    }
    public static class Invalid {
        @Contract(Exercises.class)
        public static class Exercise {
            long id;
            int ix;
            String name;
            String crash;
            public Exercise(long id, int ix) {
                this.id = id; this.ix = ix;
            }
        }
    }
    public static class Excluded {
        @Contract(Exercises.class)
        public static class Exercise {
            long id;
            int ix;
            String name;
            @X String crash;
            public Exercise(long id, int ix) {
                this.id = id; this.ix = ix;
            }
        }
    }
    public static class NoNull {
        @Contract(Workouts.class)
        public static class Workout {
            String name;
        }
        @Contract(Sections.class)
        public static class Section {
            String name;
            @NotNull Workout workout;
        }
    }

    private Data db;

    public ModelTests() {
        super(TestAppContentProvider.class, TestAppContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = Data.sync(getMockContext());
    }



    public void testCreate_withOutIdField() throws Exception {
        Nested.Workout workout = new Nested.Workout();
        workout.name = "test 1";

        long id = db.create(workout);

        assertThat(id).isGreaterThan(0);
    }

    public void testCreate_withIdField() throws Exception {
        Nested.Exercise exercise = new Nested.Exercise();
        exercise.name = "test 1";

        long id = db.create(exercise);

        assertThat(id).isGreaterThan(0);
        assertThat(exercise.id).isEqualTo(id);
    }

    public void testCreate_withIdField_inBatch() throws Exception {
        Nested.Exercise e1 = new Nested.Exercise();
        e1.name = "test 1";
        Nested.Exercise e2 = new Nested.Exercise();
        e2.name = "test 2";

        boolean success = db.batch(TestAppContract.AUTHORITY)
                .create(e1)
                .create(e2)
        .execute();

        assertThat(success).isTrue();
        assertThat(e1.id).isGreaterThan(0);
        assertThat(e2.id).isGreaterThan(0);
    }

    public void testCreateAll() throws Exception {
        List<Nested.Exercise> exercises = Arrays.asList(
                new Nested.Exercise(0, "test 1"),
                new Nested.Exercise(0, "test 2"),
                new Nested.Exercise(0, "test 3")
        );

        int count = db.createAll(exercises);

        assertThat(count).isEqualTo(3);
    }

    public void testCreate_withJoined() throws Exception {
        IdsSiblingJoined.Workout workout = new IdsSiblingJoined.Workout();
        workout.id = 123;
        workout.name = "workout 1";
        IdsSiblingJoined.Section section = new IdsSiblingJoined.Section();
        section.name = "section 1";
        section.workout = workout;

        long sid = db.create(section);

        assertThat(sid).isGreaterThan(0);
        assertThat(db.query(Sections.CONTENT_URI, select(Sections.COLUMN_NAME), where(Sections.CONTENT_URI).hasId(sid)).as(String.class)).isEqualTo(section.name);
        assertThat(db.query(Sections.CONTENT_URI, select(Sections.COLUMN_WORKOUT_ID), where(Sections.CONTENT_URI).hasId(sid)).as(long.class)).isEqualTo(section.workout.id);
    }

    public void testUpdate_objectWithNoIdField() throws Exception {
        try {
            db.update(new Nested.Workout());
            fail("expected exception");
        } catch(IllegalArgumentException e) {
            // TODO
        }
    }

    public void testUpdate_objectIdIsZero() throws Exception {
        Nested.Exercise exercise = new Nested.Exercise();
        try {
            db.update(exercise);
            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(Err.ERR_CANNOT_UPDATE_NEW_OBJECT, exercise).getMessage());
        }
    }

    public void testUpdate() throws Exception {
        long id = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));

        Nested.Exercise exercise = new Nested.Exercise();
        exercise.id = id;
        exercise.name = "bob";

        int count = db.update(exercise);

        assertThat(count).isEqualTo(1);
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id)).as(Nested.Exercise.class).name).isEqualTo(exercise.name);
    }

    public void testUpdateAll() throws Exception {
        long id1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));
        long id2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:0,_ix:0"));
        long id3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:0,_ix:0"));

        List<Nested.Exercise> exercises = Arrays.asList(
                new Nested.Exercise(id2, "name 1"),
                new Nested.Exercise(id2, "name 2"),
                new Nested.Exercise(id2, "name 3")
        );

        int count = db.updateAll(exercises);

        assertThat(count).isEqualTo(3);
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id1)).as(Nested.Exercise.class).name).isEqualTo("test exercise 1");
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id2)).as(Nested.Exercise.class).name).isEqualTo("name 3");
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id3)).as(Nested.Exercise.class).name).isEqualTo("test exercise 3");
    }

    public void testUpdateAll_withField() throws Exception {
        long id1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));
        long id2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:0,_ix:0"));
        long id3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:0,_ix:0"));
        List<Nested.Exercise> exercises = Arrays.asList(
                new Nested.Exercise(id1, 3),
                new Nested.Exercise(id2, 2),
                new Nested.Exercise(id3, 1)
        );

        int count = db.updateAll(exercises, Exercises.COLUMN_IX);

        assertThat(count).isEqualTo(3);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id1)).as(int.class)).isEqualTo(3);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id2)).as(int.class)).isEqualTo(2);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id3)).as(int.class)).isEqualTo(1);
    }

    public void testUpdateAll_withField_skippingInvalidField() throws Exception {
        long id1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));
        long id2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:0,_ix:0"));
        long id3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:0,_ix:0"));
        List<Invalid.Exercise> exercises = Arrays.asList(
                new Invalid.Exercise(id1, 3),
                new Invalid.Exercise(id2, 2),
                new Invalid.Exercise(id3, 1)
        );

        int count = db.updateAll(exercises, Exercises.COLUMN_IX);

        assertThat(count).isEqualTo(3);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id1)).as(int.class)).isEqualTo(3);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id2)).as(int.class)).isEqualTo(2);
        assertThat(db.query(Exercises.CONTENT_URI, select(Exercises.COLUMN_IX), where(Exercises.COLUMN_ID).isEqualTo(id3)).as(int.class)).isEqualTo(1);
    }

    public void testDestroy_objectWithNoIdField() throws Exception {
        try {
            db.destroy(new Nested.Workout());
            fail("expected exception");
        } catch(IllegalArgumentException e) {
            // TODO
        }
    }

    public void testDestroy_objectIdIsZero() throws Exception {
        Nested.Exercise exercise = new Nested.Exercise();
        try {
            db.destroy(exercise);
            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(Err.ERR_CANNOT_DESTROY_NEW_OBJECT, exercise).getMessage());
        }
    }

    public void testDestroy() throws Exception {
        long id = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));

        Nested.Exercise exercise = new Nested.Exercise();
        exercise.id = id;
        exercise.name = "bob";

        int count = db.destroy(exercise);

        assertThat(count).isEqualTo(1);
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id)).as(Nested.Exercise.class)).isNull();
    }

    public void testDestroyAll() throws Exception {
        long id1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));
        long id2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:0,_ix:0"));
        long id3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:0,_ix:0"));

        List<Nested.Exercise> exercises = Arrays.asList(
                new Nested.Exercise(id1, "name 1"),
                new Nested.Exercise(id2, "name 2"),
                new Nested.Exercise(id2, "name 3")
        );

        int count = db.destroyAll(exercises);

        assertThat(count).isEqualTo(2);
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id1)).as(Nested.Exercise.class)).isNull();
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id2)).as(Nested.Exercise.class)).isNull();
        assertThat(db.query(Exercises.CONTENT_URI, where(Exercises.COLUMN_ID).isEqualTo(id3)).as(Nested.Exercise.class).name).isEqualTo("test exercise 3");
    }

    public void testQueryObject() throws Exception {
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:0"));

        Nested.Exercise exercise = db.query(Exercises.CONTENT_URI).as(Nested.Exercise.class);

        assertThat(exercise).isNotNull();
        assertThat(exercise.id).isEqualTo(eid1);
        assertThat(exercise.name).isEqualTo("test exercise 1");
    }

    public void test_find_byName() throws Exception {
        db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        db.create(Workouts.CONTENT_URI, values("name:'test workout 2'"));

        Nested.Workout workout = db.find(Nested.Workout.class, where(Workouts.CONTENT_URI, Workouts.COLUMN_NAME).isEqualTo("test workout 2"));

        assertThat(workout).isNotNull();
        assertThat(workout.name).isEqualTo("test workout 2");
    }

    public void test_find_hasMany() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long wid2 = db.create(Workouts.CONTENT_URI, values("name:'test workout 2'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:1", wid1));
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?,_ix:0", sid1));
        long eid2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:?,_ix:1", sid1));
        long eid3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:?,_ix:0", sid2));
        long eid4 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 4',section_id:?,_ix:1", sid2));

        Nested.Workout workout = db.find(Nested.Workout.class);

        assertThat(workout).isNotNull();
        assertThat(workout.name).isEqualTo("test workout 1");
        assertThat(workout.sections).hasSize(2);
        assertThat(workout.sections.get(0).name).isEqualTo("test section 1");
        assertThat(workout.sections.get(0).exercises).hasSize(2);
        assertThat(workout.sections.get(0).exercises.get(0).id).isEqualTo(eid1);
        assertThat(workout.sections.get(0).exercises.get(0).name).isEqualTo("test exercise 1");
        assertThat(workout.sections.get(0).exercises.get(1).id).isEqualTo(eid2);
        assertThat(workout.sections.get(0).exercises.get(1).name).isEqualTo("test exercise 2");
        assertThat(workout.sections.get(1).name).isEqualTo("test section 2");
        assertThat(workout.sections.get(1).exercises).hasSize(2);
        assertThat(workout.sections.get(1).exercises.get(0).id).isEqualTo(eid3);
        assertThat(workout.sections.get(1).exercises.get(0).name).isEqualTo("test exercise 3");
        assertThat(workout.sections.get(1).exercises.get(1).id).isEqualTo(eid4);
        assertThat(workout.sections.get(1).exercises.get(1).name).isEqualTo("test exercise 4");
    }

    public void test_find_hasMany_outOfModelOrder() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1',_ix:1"));
        long wid2 = db.create(Workouts.CONTENT_URI, values("name:'test workout 2',_ix:0"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:1", wid2));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:0", wid2));
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?,_ix:0", sid1));
        long eid2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:?,_ix:1", sid1));
        long eid3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:?,_ix:0", sid2));
        long eid4 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 4',section_id:?,_ix:1", sid2));

        Nested.Workout workout = db.find(Nested.Workout.class);

        assertThat(workout).isNotNull();
        assertThat(workout.name).isEqualTo("test workout 2");
        assertThat(workout.sections).hasSize(2);
        assertThat(workout.sections.get(0).name).isEqualTo("test section 2");
        assertThat(workout.sections.get(0).exercises).hasSize(2);
        assertThat(workout.sections.get(0).exercises.get(0).id).isEqualTo(eid3);
        assertThat(workout.sections.get(0).exercises.get(0).name).isEqualTo("test exercise 3");
        assertThat(workout.sections.get(0).exercises.get(1).id).isEqualTo(eid4);
        assertThat(workout.sections.get(0).exercises.get(1).name).isEqualTo("test exercise 4");
        assertThat(workout.sections.get(1).name).isEqualTo("test section 1");
        assertThat(workout.sections.get(1).exercises).hasSize(2);
        assertThat(workout.sections.get(1).exercises.get(0).id).isEqualTo(eid1);
        assertThat(workout.sections.get(1).exercises.get(0).name).isEqualTo("test exercise 1");
        assertThat(workout.sections.get(1).exercises.get(1).id).isEqualTo(eid2);
        assertThat(workout.sections.get(1).exercises.get(1).name).isEqualTo("test exercise 2");
    }

    public void test_find_hasMany_outOfExplicitOrder() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long wid2 = db.create(Workouts.CONTENT_URI, values("name:'test workout 2'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?", wid1));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?", wid1));
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?", sid1));
        long eid2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:?", sid1));
        long eid3 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:?", sid2));
        long eid4 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 4',section_id:?", sid2));

        NestedWithExplicitSort.Workout workout = db.find(NestedWithExplicitSort.Workout.class);

        assertThat(workout).isNotNull();
        assertThat(workout.name).isEqualTo("test workout 1");
        assertThat(workout.sections).hasSize(2);
        assertThat(workout.sections.get(0).exercises).hasSize(2);
        assertThat(workout.sections.get(1).exercises).hasSize(2);
        assertThat(workout.sections.get(0).name).isEqualTo("test section 2");
        assertThat(workout.sections.get(0).exercises.get(0).name).isEqualTo("test exercise 4");
        assertThat(workout.sections.get(0).exercises.get(1).name).isEqualTo("test exercise 3");
        assertThat(workout.sections.get(1).name).isEqualTo("test section 1");
        assertThat(workout.sections.get(1).exercises.get(0).name).isEqualTo("test exercise 2");
        assertThat(workout.sections.get(1).exercises.get(1).name).isEqualTo("test exercise 1");
    }

    public void test_find_hasMany_withId() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long wid2 = db.create(Workouts.CONTENT_URI, values("name:'test workout 2'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:1", wid1));
        db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?,_ix:0", sid1));
        db.create(Exercises.CONTENT_URI, values("name:'test exercise 2',section_id:?,_ix:1", sid1));
        db.create(Exercises.CONTENT_URI, values("name:'test exercise 3',section_id:?,_ix:0", sid2));
        db.create(Exercises.CONTENT_URI, values("name:'test exercise 4',section_id:?,_ix:1", sid2));

        Nested.Workout workout = db.find(Nested.Workout.class, where(Workouts.CONTENT_URI).hasId(wid1));

        assertThat(workout).isNotNull();
        assertThat(workout.name).isEqualTo("test workout 1");
        assertThat(workout.sections).hasSize(2);
        assertThat(workout.sections.get(0).name).isEqualTo("test section 1");
        assertThat(workout.sections.get(0).exercises).hasSize(2);
        assertThat(workout.sections.get(0).exercises.get(0).name).isEqualTo("test exercise 1");
        assertThat(workout.sections.get(0).exercises.get(1).name).isEqualTo("test exercise 2");
        assertThat(workout.sections.get(1).name).isEqualTo("test section 2");
        assertThat(workout.sections.get(1).exercises).hasSize(2);
        assertThat(workout.sections.get(1).exercises.get(0).name).isEqualTo("test exercise 3");
        assertThat(workout.sections.get(1).exercises.get(1).name).isEqualTo("test exercise 4");
    }

    public void test_findAll_hasMany() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long wid2 = db.create(Workouts.CONTENT_URI, values("name:'test workout 2'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid2));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:1", wid2));

        List<Nested.Workout> workouts = db.findAll(Nested.Workout.class);

        assertThat(workouts).hasSize(2);
        assertThat(workouts.get(0).name).isEqualTo("test workout 1");
        assertThat(workouts.get(0).sections).isEmpty();
        assertThat(workouts.get(1).name).isEqualTo("test workout 2");
        assertThat(workouts.get(1).sections).hasSize(2);
        assertThat(workouts.get(1).sections.get(0).name).isEqualTo("test section 1");
        assertThat(workouts.get(1).sections.get(1).name).isEqualTo("test section 2");
    }

    public void test_find_hasMany_withSort() throws Exception {
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:0,_ix:0"));
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?,_ix:1", sid1));
        long eid2 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:?,_ix:0", sid1));

        Nested.Section section = db.find(Nested.Section.class);

        assertThat(section).isNotNull();
        assertThat(section.exercises).hasSize(2);
        assertThat(section.exercises.get(0).id).isEqualTo(eid2);
        assertThat(section.exercises.get(1).id).isEqualTo(eid1);
    }

    public void test_find_hasOne_withSingleJoin_joinIsNull() throws Exception {
        db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", 0));

        SingleJoined.Section section = db.find(SingleJoined.Section.class);

        assertThat(section).isNotNull();
        assertThat(section.name).isEqualTo("test section 1");
        assertThat(section.workout).isNull();
    }

    public void test_find_hasOne_withSingleJoin_joinIsNotNull() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));

        SingleJoined.Section section = db.find(SingleJoined.Section.class);

        assertThat(section).isNotNull();
        assertThat(section.name).isEqualTo("test section 1");
        assertThat(section.workout).isNotNull();
        assertThat(section.workout.name).isEqualTo("test workout 1");
    }

    public void test_findAll_joinedChildrenAreSameObject() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));
        db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:0", wid1));

        List<SingleJoined.Section> sections = db.findAll(SingleJoined.Section.class);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).name).isNotEqualTo(sections.get(1).name);
        assertThat(sections.get(0).workout).isEqualTo(sections.get(1).workout);
    }

    public void test_findAll_joinedChildren_areNotNull_andNotEqual() throws Exception {
        db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:0,_ix:0"));
        db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:0,_ix:0"));

        List<NoNull.Section> sections = db.findAll(NoNull.Section.class);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).name).isNotEqualTo(sections.get(1).name);
        assertThat(sections.get(0).workout).isNotNull();
        assertThat(sections.get(0).workout.name).isNull();
        assertThat(sections.get(1).workout).isNotNull();
        assertThat(sections.get(1).workout.name).isNull();
        assertThat(sections.get(0).workout).isNotEqualTo(sections.get(1).workout);
    }

    public void test_findAll_whereNameIsNull() throws Exception {
        db.create(Exercises.CONTENT_URI, values("name:'exercise 1',section_id:0"));
        long id = db.create(Exercises.CONTENT_URI, values("section_id:0"));

        List<Nested.Exercise> exercises = db.findAll(Nested.Exercise.class, where(Exercises.COLUMN_NAME).isNull());

        assertThat(exercises).hasSize(1);
        assertThat(exercises.get(0).id).isEqualTo(id);
        assertThat(exercises.get(0).name).isNull();
    }

    public void test_findAll_whereNameIsNotNull() throws Exception {
        long id = db.create(Exercises.CONTENT_URI, values("name:'exercise 1',section_id:0"));
        db.create(Exercises.CONTENT_URI, values("section_id:0"));

        List<Nested.Exercise> exercises = db.findAll(Nested.Exercise.class, where(Exercises.COLUMN_NAME).isNotNull());

        assertThat(exercises).hasSize(1);
        assertThat(exercises.get(0).id).isEqualTo(id);
        assertThat(exercises.get(0).name).isEqualTo("exercise 1");
    }

    public void test_find_hasOne_withTwoSiblingJoins() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));

        SiblingJoined.Section section = db.find(SiblingJoined.Section.class);

        assertThat(section).isNotNull();
        assertThat(section.name).isEqualTo("test section 1");
        assertThat(section.user).isNull();
        assertThat(section.workout).isNotNull();
        assertThat(section.workout.name).isEqualTo("test workout 1");
    }

    public void test_find_hasOne_withId_andTwoSiblingJoins() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:0", wid1));

        SiblingJoined.Section section = db.find(SiblingJoined.Section.class, where(Sections.CONTENT_URI).hasId(sid2));

        assertThat(section).isNotNull();
        assertThat(section.name).isEqualTo("test section 2");
        assertThat(section.user).isNull();
        assertThat(section.workout).isNotNull();
        assertThat(section.workout.name).isEqualTo("test workout 1");
    }

    public void test_find_hasOne_withWhere_andTwoSiblingJoins() throws Exception {
        long wid1 = db.create(Workouts.CONTENT_URI, values("name:'test workout 1'"));
        long sid1 = db.create(Sections.CONTENT_URI, values("name:'test section 1',workout_id:?,_ix:0", wid1));
        long sid2 = db.create(Sections.CONTENT_URI, values("name:'test section 2',workout_id:?,_ix:0", wid1));

        SiblingJoined.Section section = db.find(SiblingJoined.Section.class, where(Sections.CONTENT_URI, Sections.COLUMN_ID).isEqualTo(sid2));

        assertThat(section).isNotNull();
        assertThat(section.name).isEqualTo("test section 2");
        assertThat(section.user).isNull();
        assertThat(section.workout).isNotNull();
        assertThat(section.workout.name).isEqualTo("test workout 1");
    }

    public void test_find_withExcludedColumn() throws Exception {
        long eid1 = db.create(Exercises.CONTENT_URI, values("name:'test exercise 1',section_id:0,_ix:1"));

        Excluded.Exercise exercise = db.find(Excluded.Exercise.class);

        assertThat(exercise).isNotNull();
        assertThat(exercise.id).isEqualTo(eid1);
    }

    public static class TestBoolean {
        @Contract(Users.class)
        public static class User {
            long    id;
            String  name;
            boolean active;
        }
    }
    public void test_find_withBoolean() throws Exception {
        long uid1 = db.create(Users.CONTENT_URI, value("name", "user 1"), value("active", true));
        long uid2 = db.create(Users.CONTENT_URI, value("name", "user 2"), value("active", false));

        List<TestBoolean.User> users = db.findAll(TestBoolean.User.class);

        assertThat(users).hasSize(2);
        assertThat(users.get(0).id).isEqualTo(uid1);
        assertThat(users.get(0).name).isEqualTo("user 1");
        assertThat(users.get(0).active).isTrue();
        assertThat(users.get(1).id).isEqualTo(uid2);
        assertThat(users.get(1).name).isEqualTo("user 2");
        assertThat(users.get(1).active).isFalse();
    }
    public void test_find_withBooleanTrue() throws Exception {
        long uid1 = db.create(Users.CONTENT_URI, value("name", "user 1"), value("active", true));
        long uid2 = db.create(Users.CONTENT_URI, value("name", "user 2"), value("active", false));

        List<TestBoolean.User> users = db.findAll(TestBoolean.User.class, where(Users.COLUMN_ACTIVE).isTrue());

        assertThat(users).hasSize(1);
        assertThat(users.get(0).id).isEqualTo(uid1);
        assertThat(users.get(0).name).isEqualTo("user 1");
    }
    public void test_find_withBooleanFalse() throws Exception {
        long uid1 = db.create(Users.CONTENT_URI, value("name", "user 1"), value("active", true));
        long uid2 = db.create(Users.CONTENT_URI, value("name", "user 2"), value("active", false));

        List<TestBoolean.User> users = db.findAll(TestBoolean.User.class, where(Users.COLUMN_ACTIVE).isFalse());

        assertThat(users).hasSize(1);
        assertThat(users.get(0).id).isEqualTo(uid2);
        assertThat(users.get(0).name).isEqualTo("user 2");
    }
    public void test_update_booleanFromTrueToFalse() throws Exception {
        long uid1 = db.create(Users.CONTENT_URI, value("name", "user 1"), value("active", true));
        TestBoolean.User user = new TestBoolean.User();
        user.id = uid1;
        user.active = false;

        int count = db.update(user, Users.COLUMN_ACTIVE);

        assertThat(count).isEqualTo(1);
        assertThat(db.query(Users.CONTENT_URI, select(Users.COLUMN_ACTIVE), where(Users.COLUMN_ID).isEqualTo(uid1)).as(boolean.class)).isFalse();
    }
    public void test_update_booleanFromFalseToTrue() throws Exception {
        long uid1 = db.create(Users.CONTENT_URI, value("name", "user 1"), value("active", false));
        TestBoolean.User user = new TestBoolean.User();
        user.id = uid1;
        user.active = true;

        int count = db.update(user, Users.COLUMN_ACTIVE);

        assertThat(count).isEqualTo(1);
        assertThat(db.query(Users.CONTENT_URI, select(Users.COLUMN_ACTIVE), where(Users.COLUMN_ID).isEqualTo(uid1)).as(boolean.class)).isTrue();
    }

}
