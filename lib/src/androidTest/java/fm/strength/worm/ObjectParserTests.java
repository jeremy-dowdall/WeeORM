package fm.strength.worm;

import android.content.ContentValues;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import fm.strength.worm.Data.Model.Contract;
import fm.strength.testapps.base.TestAppContract.Workouts;
import fm.strength.testapps.base.TestAppContract.Workouts.Sections;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectParserTests extends TestCase {

    @Contract(Sections.class)
    public static class Class01 {
        public long id;
        public String name;
        public long workoutId;
    }

    @Contract(Workouts.class)
    public static class Class02 {
        public String name;
        public List<String> names;
    }


    public void test_getContentValues_fromSimpleObject() throws Exception {
        Class01 object = new Class01();
        object.name = "bob";
        object.workoutId = 175;

        ContentValues contentValues = ObjectParser.create(object).getContentValues();

        assertThat(contentValues).isNotNull();
        assertThat(contentValues.size()).isEqualTo(2);
        assertThat(contentValues.get("name")).isEqualTo("bob");
        assertThat(contentValues.get("workout_id")).isEqualTo(175L);
    }

    public void test_getContentValues_fromObject_withNonDirectField() throws Exception {
        Class02 object = new Class02();
        object.name = "joe";
        object.names = new ArrayList<String>(0);

        ContentValues contentValues = ObjectParser.create(object).getContentValues();

        assertThat(contentValues).isNotNull();
        assertThat(contentValues.size()).isEqualTo(1);
        assertThat(contentValues.get("name")).isEqualTo("joe");
    }

}
