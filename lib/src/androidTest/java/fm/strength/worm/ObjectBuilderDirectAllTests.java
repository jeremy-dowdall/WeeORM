package fm.strength.worm;

import android.database.MatrixCursor;

import junit.framework.TestCase;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectBuilderDirectAllTests extends TestCase {

    public void testBuild_withNoData() throws Exception {
        List<String> results = ObjectBuilder.create(String.class).build();

        assertThat(results).isEmpty();
    }

    public void testBuild_withEmptyData() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });

        List<String> results = ObjectBuilder.create(String.class).withData(mc).build();

        assertThat(results).isEmpty();
    }

    public void testBuildPrimitiveInt() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Integer> results = ObjectBuilder.create(int.class).withData(mc).build();

        assertThat(results).containsExactly(123, 321);
    }

    public void testBuildInteger() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Integer> results = ObjectBuilder.create(Integer.class).withData(mc).build();

        assertThat(results).containsExactly(123, 321);
    }

    public void testBuildPrimitiveLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Long> results = ObjectBuilder.create(long.class).withData(mc).build();

        assertThat(results).containsExactly(123l, 321l);
    }

    public void testBuildLong() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Long> results = ObjectBuilder.create(Long.class).withData(mc).build();

        assertThat(results).containsExactly(123l, 321l);
    }

    public void testBuildPrimitiveByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Byte> results = ObjectBuilder.create(byte.class).withData(mc).build();

        assertThat(results).containsExactly((byte) 123, (byte) 321);
    }

    public void testBuildByte() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Byte> results = ObjectBuilder.create(Byte.class).withData(mc).build();

        assertThat(results).containsExactly((byte) 123, (byte) 321);
    }

    public void testBuildPrimitiveShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Short> results = ObjectBuilder.create(short.class).withData(mc).build();

        assertThat(results).containsExactly((short) 123, (short) 321);
    }

    public void testBuildShort() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Short> results = ObjectBuilder.create(Short.class).withData(mc).build();

        assertThat(results).containsExactly((short) 123, (short) 321);
    }

    public void testBuildPrimitiveFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Float> results = ObjectBuilder.create(float.class).withData(mc).build();

        assertThat(results).containsExactly(123f, 321f);
    }

    public void testBuildFloat() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Float> results = ObjectBuilder.create(Float.class).withData(mc).build();

        assertThat(results).containsExactly(123f, 321f);
    }

    public void testBuildPrimitiveDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Double> results = ObjectBuilder.create(double.class).withData(mc).build();

        assertThat(results).containsExactly(123d, 321d);
    }

    public void testBuildDouble() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Double> results = ObjectBuilder.create(Double.class).withData(mc).build();

        assertThat(results).containsExactly(123d, 321d);
    }

    public void testBuildPrimitiveCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Character> results = ObjectBuilder.create(char.class).withData(mc).build();

        assertThat(results).containsExactly((char) 123, (char) 321);
    }

    public void testBuildCharacter() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "123" });
        mc.addRow(new String[] { "321" });

        List<Character> results = ObjectBuilder.create(Character.class).withData(mc).build();

        assertThat(results).containsExactly((char) 123, (char) 321);
    }

    public void testBuildPrimitiveBoolean() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "1" });
        mc.addRow(new String[] { "0" });
        mc.addRow(new String[] { "321" });

        List<Boolean> results = ObjectBuilder.create(boolean.class).withData(mc).build();

        assertThat(results).containsExactly(true, false, true);
    }

    public void testBuildBoolean_asTrue() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "1" });
        mc.addRow(new String[] { "0" });
        mc.addRow(new String[] { "321" });

        List<Boolean> results = ObjectBuilder.create(Boolean.class).withData(mc).build();

        assertThat(results).containsExactly(true, false, true);
    }

    public void testBuildString() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "data" });
        mc.addRow(new String[] { "bob" });
        mc.addRow(new String[] { "joe" });

        List<String> results = ObjectBuilder.create(String.class).withData(mc).build();

        assertThat(results).containsExactly("bob", "joe");
    }

}
