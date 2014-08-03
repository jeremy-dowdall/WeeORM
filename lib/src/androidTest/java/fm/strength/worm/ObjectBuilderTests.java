package fm.strength.worm;

import android.database.MatrixCursor;

import junit.framework.TestCase;

import java.util.AbstractList;
import java.util.List;

import fm.strength.worm.util.Err;

import static org.fest.assertions.api.Assertions.assertThat;

public class ObjectBuilderTests extends TestCase {

    public void testCreateInterfaceFails() throws Exception {
        try {
            ObjectBuilder.create(List.class);

            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ObjectBuilder.ERR_INTERFACE, List.class).getMessage());
        }
    }

    public void testCreateAbstractClassFails() throws Exception {
        try {
            ObjectBuilder.create(AbstractList.class);

            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ObjectBuilder.ERR_ABSTRACT_CLASS, AbstractList.class).getMessage());
        }
    }

    public void testCreateInnerClassFails() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col" });
        mc.addRow(new String[] { "1" });

        try {
            ObjectBuilder.create(NonStaticInnerClass.class).withData(mc).build();

            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ObjectBuilder.ERR_INNER_CLASS, NonStaticInnerClass.class).getMessage());
        }
    }

    public void testCreateClassWithEnumFieldFails() throws Exception {
        MatrixCursor mc = new MatrixCursor(new String[] { "col" });
        mc.addRow(new String[] { "1" });

        try {
            ObjectBuilder.create(ClassWithEnumField.class).withData(mc).build();

            fail("expected exception");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ObjectBuilder.ERR_ENUM_FIELD, "testEnum", ClassWithEnumField.class).getMessage());
        }
    }


    enum TestEnum {
        // empty test enum
    }

    public static class ClassWithEnumField {
        TestEnum testEnum;
    }
    public class NonStaticInnerClass {
        String name;
    }

}
