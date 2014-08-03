package fm.strength.worm;

import android.database.MatrixCursor;

import junit.framework.TestCase;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectBuilderSimpleRowTests extends TestCase {

    /** public, non-final fields */
    public static class Class00 {
        public String name;
        public int weight;
    }
    public void testBuild_withPublicFields() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class00 result = ObjectBuilder.create(Class00.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("test 1");
        assertThat(result.weight).isEqualTo(150);
    }

    /** private, non-final fields */
    public static class Class01 {
        private String name;
        private int weight;
    }
    public void testBuild_withPrivateFields() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class01 result = ObjectBuilder.create(Class01.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("test 1");
        assertThat(result.weight).isEqualTo(150);
    }

    /** public final fields, initialized inline */
    public static class Class02 {
        public final String name = null;
        public final int weight = -1;
    }
    public void testBuild_withPublicFinalFields_initializedInline() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class02 result = ObjectBuilder.create(Class02.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("test 1"); // name can be written because it was initialized as null
        assertThat(result.weight).isEqualTo(-1);     // weight cannot be written - it was inlined by the complier
    }

    /** public final fields, initialized inline */
    public static class Class03 {
        public final String name = "bob";
        public final int weight = 100;
    }
    public void testBuild03() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class03 result = ObjectBuilder.create(Class03.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("bob"); // name cannot be written - it was inlined by the compiler
        assertThat(result.weight).isEqualTo(100);     // weight cannot be written - it was inlined by the compiler
    }

    /** public final fields, initialized in a public no-args constructor */
    public static class Class04 {
        public final String name;
        public final int weight;
        public Class04() { this.name = "ERROR!!!"; this.weight = -1; }
    }
    public void testBuild_withPublicFinalFields_initializedInPublicNoArgsConstructor() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class04 result = ObjectBuilder.create(Class04.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("test 1");
        assertThat(result.weight).isEqualTo(150);
    }

    /** public final fields, initialized in a private no-args constructor */
    public static class Class05 {
        public final String name;
        public final int weight;
        private Class05() { this.name = null; this.weight = 0; }
    }
    public void testBuild_withPublicFinalFields_initializedInPrivateNoArgsConstructor() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "name", "weight" });
        mc.addRow(new Object[] { "test 1", 150 });

        Class05 result = ObjectBuilder.create(Class05.class).withData(mc).build(0);

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("test 1");
        assertThat(result.weight).isEqualTo(150);
    }

}
