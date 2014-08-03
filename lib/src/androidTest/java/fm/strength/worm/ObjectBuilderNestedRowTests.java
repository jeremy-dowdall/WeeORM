package fm.strength.worm;

import android.database.Cursor;

import junit.framework.TestCase;

import java.util.List;

import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Workouts;
import fm.strength.testapps.base.TestAppContract.Workouts.Sections;
import fm.strength.testapps.base.TestAppContract.Workouts.Sections.Exercises;
import fm.strength.worm.Data.Model.Contract;
import fm.strength.worm.util.Err;

import static org.fest.assertions.api.Assertions.assertThat;
import static fm.strength.worm.ObjectBuilder.ERR_MISSING_QCOL;
import static fm.strength.worm.ObjectBuilder.ERR_MISSING_PCOL;
import static fm.strength.worm.ObjectBuilder.Q;
import static fm.strength.worm.ObjectBuilder.P;

import static fm.strength.worm.util.TestHelper.*;

public class ObjectBuilderNestedRowTests extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContractsLoader.load("com.test", TestAppContract.class);
    }

    public void testBuild_withOutQueryColumn() throws Exception {
        Cursor mc = cursor(row(P, "name"), row("", "workout 1"));
        try {
            ObjectBuilder.create(Workout.class).withData(mc).build(0);
            fail("expected exception");
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ERR_MISSING_QCOL, Workout.class).getMessage());
        }
    }

    public void testBuild_withOutParentIdColumn() throws Exception {
        Cursor mc = cursor(row(Q, "name"), row("", "workout 1"));
        try {
            ObjectBuilder.create(Workout.class).withData(mc).build(0);
            fail("expected exception");
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ERR_MISSING_PCOL, Workout.class).getMessage());
        }
    }

    public void testBuild_withOutQueryColumn_inNestedClass() throws Exception {
        Cursor mc = cursor(
                cursor(row(Q, P, "name"), row("0", "0", "workout 1")),
                cursor(row(P, "name"),    row("1", "section 1"))
        );
        try {
            ObjectBuilder.create(Workout.class).withData(mc).build(0);
            fail("expected exception");
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ERR_MISSING_QCOL, Workout.Section.class).getMessage());
        }
    }

    public void testBuild_withOutParentIdColumn_inNestedClass() throws Exception {
        Cursor mc = cursor(
                cursor(row(Q, P, "name"), row("0", "0", "workout 1")),
                cursor(row(Q, "name"),    row("1", "section 1"))
        );
        try {
            ObjectBuilder.create(Workout.class).withData(mc).build(0);
            fail("expected exception");
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ERR_MISSING_PCOL, Workout.Section.class).getMessage());
        }
    }


    @Contract(Workouts.class)
    public static class Workout {
        public final String name = null;
        public final List<Section> sections = null;

        @Contract(Sections.class)
        public static class Section {
            public final String name = null;
            public final String playlist = null;
            public final List<Exercise> exercises = null;

            @Contract(Exercises.class)
            public static class Exercise {
                public long id;
                public final String name = null;
            }
        }
    }

}
