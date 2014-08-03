package fm.strength.worm.sql;

import android.net.Uri;

import java.util.List;

import fm.strength.worm.SqlBuilderHelper;

public class Expression {

    public static final Object NULL = new Object();


    // nesting
    public List<Expression> $or;
    public List<Expression> $and;

    // aggregate functions
    public Expression $avg;
    public Expression $count;
    public Expression $max;
    public Expression $min;
    public Expression $sum;
    public Expression $total;

    // functions
    public Expression $abs;
    public Expression $lower;
    public Expression $upper;

    // value
    public Uri uri;
    public String field;
    public Object value;

    // operators
    public Expression $is;
    public Expression $not;
    public Expression $ne;
    public Expression $lt;
    public Expression $lte;
    public Expression $gt;
    public Expression $gte;
    public Expression $like;
    public Expression $nlike;
    public List<Object> $in;
    public List<Object> $nin;

    // alias
    public String $as;


    public Expression() {
        // default constructor
    }
    public Expression(Object value) {
        this.value = value;
    }
    public Expression(Uri uri, String field) {
        this.uri = uri;
        this.field = field;
    }

    public void apply(SqlBuilderHelper helper, StringBuilder sb) {
        if($and != null) {
            sb.append('(');
            for(int i = 0; i < $and.size(); i++) {
                if(i > 0) sb.append(" AND ");
                $and.get(i).apply(helper, sb);
            }
            sb.append(')');
        }
        else if($or != null) {
            sb.append('(');
            for(int i = 0; i < $or.size(); i++) {
                if(i > 0) sb.append(" OR ");
                $or.get(i).apply(helper, sb);
            }
            sb.append(')');
        }
        else {
            // left
            if(value != null) {
                if(value.equals("")) {
                    sb.append("''");
                } else if(value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append('?');
                    helper.addArg(value);
                }
            }
            else if(field != null) {
                Character alias = helper.getAlias(uri);
                if(alias == null) sb.append(field);
                else sb.append(alias).append('.').append(field);
            }
            // right
            // aggregate functions
            if($avg != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("AVG(");
                $avg.apply(helper, sb);
                sb.append(')');
            }
            else if($count != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("COUNT(");
                $count.apply(helper, sb);
                sb.append(')');
            }
            else if($max != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("MAX(");
                $max.apply(helper, sb);
                sb.append(')');
            }
            else if($min != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("MIN(");
                $min.apply(helper, sb);
                sb.append(')');
            }
            else if($sum != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("SUM(");
                $sum.apply(helper, sb);
                sb.append(')');
            }
            else if($total != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("TOTAL(");
                $total.apply(helper, sb);
                sb.append(')');
            }
            // functions
            else if($abs != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("ABS(");
                $abs.apply(helper, sb);
                sb.append(')');
            }
            else if($lower != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("LOWER(");
                $lower.apply(helper, sb);
                sb.append(')');
            }
            else if($upper != null) {
                if(sb.length() > 0) sb.append(' ');
                sb.append("UPPER(");
                $upper.apply(helper, sb);
                sb.append(')');
            }
            // operators
            if($is != null) {
                if($is.value == Expression.NULL) {
                    sb.append(" IS NULL");
                } else {
                    sb.append("=");
                    $is.apply(helper, sb);
                }
            }
            else if($not != null) {
                if($not.value == Expression.NULL) {
                    sb.append(" IS NOT NULL");
                } else {
                    sb.append("!");
                    $not.apply(helper, sb);
                }
            }
            else if($ne != null) {
                sb.append("!=");
                $ne.apply(helper, sb);
            }
            else if($lt != null) {
                sb.append("<");
                $lt.apply(helper, sb);
            }
            else if($lte != null) {
                sb.append("<=");
                $lte.apply(helper, sb);
            }
            else if($gt != null) {
                sb.append(">");
                $gt.apply(helper, sb);
            }
            else if($gte != null) {
                sb.append(">=");
                $gte.apply(helper, sb);
            }
            else if($like != null) {
                sb.append(" LIKE ");
                $like.apply(helper, sb);
            }
            else if($nlike != null) {
                sb.append(" NOT LIKE ");
                $nlike.apply(helper, sb);
            }
            else if($in != null) {
                sb.append(" IN (");
                for(int i = 0; i < $in.size(); i++) {
                    if(i > 0) sb.append(',');
                    sb.append($in.get(i));
                }
                sb.append(')');
            }
            else if($nin != null) {
                sb.append(" NOT IN (");
                for(int i = 0; i < $nin.size(); i++) {
                    if(i > 0) sb.append(',');
                    sb.append($nin.get(i));
                }
                sb.append(')');
            }
            // alias
            if($as != null) {
                sb.append(" AS ").append($as);
            }
        }
    }
}
