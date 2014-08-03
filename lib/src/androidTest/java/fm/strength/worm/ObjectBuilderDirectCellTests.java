package fm.strength.worm;

import android.database.MatrixCursor;

import junit.framework.TestCase;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectBuilderDirectCellTests extends TestCase {

    public void testBuildCell_outsideOfData() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });

        Object result = ObjectBuilder.create(String.class).withData(mc).build(0, 0);

        assertThat(result).isNull();
    }

    public void testBuildPrimitiveInt() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        int result = ObjectBuilder.create(int.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildInteger() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Integer result = ObjectBuilder.create(Integer.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildPrimitiveLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        long result = ObjectBuilder.create(long.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildPrimitiveLongNull() throws Exception {
        long result = ObjectBuilder.create(long.class).build(0, 0);

        assertThat(result).isEqualTo(0l);
    }

    public void testBuildLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Long result = ObjectBuilder.create(Long.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123);
    }

    public void testBuildLongNull() throws Exception {
        Long result = ObjectBuilder.create(Long.class).build(0, 0);

        assertThat(result).isNull();
    }

    public void testBuildPrimitiveByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        byte result = ObjectBuilder.create(byte.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((byte) 123);
    }

    public void testBuildByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Byte result = ObjectBuilder.create(Byte.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((byte) 123);
    }

    public void testBuildPrimitiveShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        short result = ObjectBuilder.create(short.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((short) 123);
    }

    public void testBuildShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Short result = ObjectBuilder.create(Short.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((short) 123);
    }

    public void testBuildPrimitiveFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        float result = ObjectBuilder.create(float.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123f);
    }

    public void testBuildFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Float result = ObjectBuilder.create(Float.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123f);
    }

    public void testBuildPrimitiveDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        double result = ObjectBuilder.create(double.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123d);
    }

    public void testBuildDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Double result = ObjectBuilder.create(Double.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(123d);
    }

    public void testBuildPrimitiveCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        char result = ObjectBuilder.create(char.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((char) 123);
    }

    public void testBuildCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Character result = ObjectBuilder.create(Character.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo((char) 123);
    }

    public void testBuildPrimitiveBoolean_asFalse() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "0" });

        boolean result = ObjectBuilder.create(boolean.class).withData(mc).build(0, 0);

        assertThat(result).isFalse();
    }

    public void testBuildBoolean_asFalse() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "0" });

        Boolean result = ObjectBuilder.create(Boolean.class).withData(mc).build(0, 0);

        assertThat(result).isFalse();
    }

    public void testBuildPrimitiveBoolean_asTrue() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        boolean result = ObjectBuilder.create(boolean.class).withData(mc).build(0, 0);

        assertThat(result).isTrue();
    }

    public void testBuildBoolean_asTrue() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });

        Boolean result = ObjectBuilder.create(Boolean.class).withData(mc).build(0, 0);

        assertThat(result).isTrue();
    }

    public void testBuildString() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "bob" });

        String result = ObjectBuilder.create(String.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo("bob");
    }

    public void testBuildDate() throws Exception {
        Date date = new Date();
        MatrixCursor mc = new MatrixCursor(new String[] { "created_at" });
        mc.addRow(new String[] { String.valueOf(date.getTime()) });

        Date result = ObjectBuilder.create(Date.class).withData(mc).build(0, 0);

        assertThat(result).isEqualTo(date);
    }

}
