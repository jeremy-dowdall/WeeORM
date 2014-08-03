package fm.strength.worm;

import android.net.Uri;

import junit.framework.TestCase;

import static fm.strength.worm.Data.select;
import static org.fest.assertions.api.Assertions.assertThat;

public class DetailSelectTest extends TestCase {
    
    private StringBuilder sb;
    private SqlBuilderHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sb = new StringBuilder();
        helper = new SqlBuilderHelper() {
            public void addArg(Object arg) {
            }
            public Character getAlias(Uri uri) {
                return null;
            }
        };
    }

    public void test_selectSingleField() throws Exception {
        select("a").expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a");
    }

    public void test_selectSingleField_asSomething() throws Exception {
        select("a").withAlias("something").expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a AS something");
    }

    public void test_selectSingleField_asCount() throws Exception {
        select("a").asCount().expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("COUNT(a)");
    }

    public void test_selectSingleField_asMax() throws Exception {
        select("a").asMax().expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("MAX(a)");
    }

    public void test_selectSingleField_asMin() throws Exception {
        select("a").asMin().expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("MIN(a)");
    }

    public void test_selectSingleField_asCount_andAsSomething() throws Exception {
        select("a").asCount().withAlias("something").expression.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("COUNT(a) AS something");
    }

}
