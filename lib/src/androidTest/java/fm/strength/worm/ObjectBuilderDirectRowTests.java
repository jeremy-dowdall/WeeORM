package fm.strength.worm;

import android.database.MatrixCursor;

import junit.framework.TestCase;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectBuilderDirectRowTests extends TestCase {

    public void testBuildCell_outsideOfData() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });

        Object result = ObjectBuilder.create(String.class).withData(mc).build(0);

        assertThat(result).isNull();
    }

    public void testBuildPrimitiveInt() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        int result = ObjectBuilder.create(int.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildInteger() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Integer result = ObjectBuilder.create(Integer.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildPrimitiveLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        long result = ObjectBuilder.create(long.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Long result = ObjectBuilder.create(Long.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildPrimitiveByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        byte result = ObjectBuilder.create(byte.class).withData(mc).build(0);

        assertThat(result).isEqualTo((byte) 123);
    }

    public void testBuildByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Byte result = ObjectBuilder.create(Byte.class).withData(mc).build(0);

        assertThat(result).isEqualTo((byte) 123);
    }

    public void testBuildPrimitiveShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        short result = ObjectBuilder.create(short.class).withData(mc).build(0);

        assertThat(result).isEqualTo((short) 123);
    }

    public void testBuildShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Short result = ObjectBuilder.create(Short.class).withData(mc).build(0);

        assertThat(result).isEqualTo((short) 123);
    }

    public void testBuildPrimitiveFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        float result = ObjectBuilder.create(float.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123f);
    }

    public void testBuildFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Float result = ObjectBuilder.create(Float.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123f);
    }

    public void testBuildPrimitiveDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        double result = ObjectBuilder.create(double.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123d);
    }

    public void testBuildDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Double result = ObjectBuilder.create(Double.class).withData(mc).build(0);

        assertThat(result).isEqualTo(123d);
    }

    public void testBuildPrimitiveCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        char result = ObjectBuilder.create(char.class).withData(mc).build(0);

        assertThat(result).isEqualTo((char) 123);
    }

    public void testBuildCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Character result = ObjectBuilder.create(Character.class).withData(mc).build(0);

        assertThat(result).isEqualTo((char) 123);
    }

    public void testBuildPrimitiveBoolean_asFalse() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "0", "FAIL" });

        boolean result = ObjectBuilder.create(boolean.class).withData(mc).build(0);

        assertThat(result).isFalse();
    }

    public void testBuildBoolean_asFalse() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "0", "FAIL" });

        Boolean result = ObjectBuilder.create(Boolean.class).withData(mc).build(0);

        assertThat(result).isFalse();
    }

    public void testBuildPrimitiveBoolean_asTrue() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        boolean result = ObjectBuilder.create(boolean.class).withData(mc).build(0);

        assertThat(result).isTrue();
    }

    public void testBuildBoolean_asTrue() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "123", "FAIL" });

        Boolean result = ObjectBuilder.create(Boolean.class).withData(mc).build(0);

        assertThat(result).isTrue();
    }

    public void testBuildString() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col1", "col2" });
        mc.addRow(new String[] { "bob", "FAIL" });

        String result = ObjectBuilder.create(String.class).withData(mc).build(0);

        assertThat(result).isEqualTo("bob");
    }

}
