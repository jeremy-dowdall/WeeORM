package fm.strength.worm;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import fm.strength.sloppyj.Adapter;
import fm.strength.sloppyj.Jay;
import fm.strength.worm.data.Limit;
import fm.strength.worm.data.Order;
import fm.strength.worm.sql.Expression;

public class Query {

    private static final Adapter adapter = new Adapter() {
        public Object fromJson(Class<?> type, Object json) {
            if(json == null)         return null;
            if(type == Uri.class)    return Uri.parse((String)json);
            if(json.equals("$null")) return Expression.NULL;
            return json;
        }
        public Object toJson(Object object) {
            if(object == Expression.NULL) return "$null";
            return object;
        }
    };

    public static Query fromJson(String q, Object...args) {
        return Jay.get(q).withAdapter(adapter).withArgs(args).as(Query.class);
    }

    public String toJson() {
        return Jay.get(this).withAdapter(adapter).asJson();
    }


    Table table;
    int parentIx;
    String parentColumn;
    Uri parentUri;
    List<Expression> select;
    List<Expression> where;
    List<String> groupBy;
    List<Expression> having;

    List<Order> order;
    Limit limit;

    List<Query> includes;

    public Query(Table table) {
        this.table = table;
    }


    public static class Table {
        Uri    uri;
        String field;
        String joinColumn;
        List<String> projection;
        List<Table> tables;

        public Table(Uri uri, String field, String joinColumn) {
            this.uri = uri;
            this.field = field;
            this.joinColumn = joinColumn;
        }
        public void add(String column) {
            if(projection == null) projection = new ArrayList<String>();
            if(!projection.contains(column)) {
                projection.add(column);
            }
        }
        public void add(Table table) {
            if(tables == null) tables = new ArrayList<Table>();
            tables.add(table);
        }
    }

}
