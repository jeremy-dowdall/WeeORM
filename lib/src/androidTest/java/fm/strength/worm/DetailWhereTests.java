package fm.strength.worm;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fm.strength.worm.Data.where;
import static org.fest.assertions.api.Assertions.assertThat;

public class DetailWhereTests extends TestCase {

    private static class Helper implements SqlBuilderHelper {
        public Character alias;
        public List<Object> args;
        public void addArg(Object arg) {
            if(args == null) args = new ArrayList<Object>();
            args.add(arg);
        }
        public Character getAlias(Uri uri) {
            return alias;
        }
    }


    private StringBuilder sb;
    private Helper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sb = new StringBuilder();
        helper = new Helper();
    }

    public void test_isEqualTo() throws Exception {
        where("a").isEqualTo("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a=?");
        assertThat(helper.args).containsExactly("b");
    }

    public void test_isEqualTo_withNull() throws Exception {
        where("a").isEqualTo(null).condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IS NULL");
    }

    public void test_isEqualTo_withNumber() throws Exception {
        where("a").isEqualTo(123).condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a=123");
    }

    public void test_isNull() throws Exception {
        where("a").isNull().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IS NULL");
    }

    public void test_isNotEqualTo() throws Exception {
        where("a").isNotEqualTo("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a!=?");
        assertThat(helper.args).containsExactly("b");
    }

    public void test_isNotEqualTo_withNull() throws Exception {
        where("a").isNotEqualTo(null).condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IS NOT NULL");
    }

    public void test_isNotNull() throws Exception {
        where("a").isNotNull().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IS NOT NULL");
    }

    public void test_isTrue() throws Exception {
        where("a").isTrue().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a=1");
    }

    public void test_isNotTrue() throws Exception {
        where("a").isNotTrue().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a!=1");
    }

    public void test_isIn_array() throws Exception {
        where("a").isIn("a", "b", "c").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IN (a,b,c)");
    }

    public void test_isIn_collection() throws Exception {
        where("a").isIn(Arrays.asList("a", "b", "c")).condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a IN (a,b,c)");
    }

    public void test_isNotIn_array() throws Exception {
        where("a").isNotIn("a", "b", "c").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a NOT IN (a,b,c)");
    }

    public void test_isNotIn_collection() throws Exception {
        where("a").isNotIn(Arrays.asList("a", "b", "c")).condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a NOT IN (a,b,c)");
    }

    public void test_isBlank() throws Exception {
        where("a").isBlank().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("(a IS NULL OR a='')");
    }

    public void test_isNotBlank() throws Exception {
        where("a").isNotBlank().condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("(a IS NOT NULL AND a!='')");
    }

    public void test_isGreaterThan_field() throws Exception {
        where("a").isGreaterThan("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a>?");
        assertThat(helper.args).containsExactly("b");
    }

    public void test_isLessThan_field() throws Exception {
        where("a").isLessThan("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a<?");
        assertThat(helper.args).containsExactly("b");
    }

    public void test_isLike() throws Exception {
        where("a").isLike("%b%").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a LIKE ?");
        assertThat(helper.args).containsExactly("%b%");
    }

    public void test_isNotLike() throws Exception {
        where("a").isNotLike("%b%").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("a NOT LIKE ?");
        assertThat(helper.args).containsExactly("%b%");
    }

    public void test_asLower_isEqualTo() throws Exception {
        where("a").asLowerCase().isEqualTo("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("LOWER(a)=?");
        assertThat(helper.args).containsExactly("b");
    }

    public void test_asUpper_isEqualTo() throws Exception {
        where("a").asUpperCase().isEqualTo("b").condition.apply(helper, sb);
        assertThat(sb.toString()).isEqualTo("UPPER(a)=?");
        assertThat(helper.args).containsExactly("b");
    }

}
