package fm.strength.worm;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.sql.Expression;
import fm.strength.worm.Query.Table;
import fm.strength.worm.data.Order;

public class SqlBuilder implements SqlBuilderHelper {

    public static SqlBuilder create() {
        return new SqlBuilder();
    }


    private Query query;
    private int count;
    private long[] parents;
    private Map<Uri, Character> aliasMap;
    private List<String> args;

    public SqlBuilder() {
    }

    public SqlBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public SqlBuilder withParents(long...parents) {
        this.parents = parents;
        return this;
    }

    public SqlBuilder withQuery(Query query) {
        this.query = query;
        return this;
    }


    public Statement buildSelection() {
        Statement s = new Statement();
        s.sql = buildWhere();
        if(args != null) s.args = args.toArray(new String[args.size()]);
        return s;
    }

    public Statement buildSql() {
        StringBuilder sb = new StringBuilder();

        buildAliasMap();
        String select = buildSelect();
        String tables = buildTables();
        String where  = buildWhere();
        String group  = buildGroup();
        String having = buildHaving();
        String order  = buildOrder();
        String limit  = buildLimit();

        sb.append("SELECT ").append(select);
        sb.append(" FROM ").append(tables);
        append(sb, " WHERE ", where);
        append(sb, " GROUP BY ", group);
        append(sb, " HAVING ", having);
        append(sb, " ORDER BY ", order);
        append(sb, " LIMIT ", limit);

        Statement s = new Statement();
        s.sql = sb.toString();
        if(args != null) s.args = args.toArray(new String[args.size()]);

        return s;
    }


    @Override
    public void addArg(Object value) {
        if(args == null) args = new ArrayList<String>();
        args.add(value.toString());
    }

    @Override
    public Character getAlias(Uri uri) {
        if(aliasMap != null) {
            if(uri == null && query != null && query.table != null) {
                uri = query.table.uri;
            }
            return aliasMap.get(uri);
        }
        return null;
    }


    private StringBuilder alias(StringBuilder sb) {
        Character alias = (query.table.tables != null) ? alias(query.table.uri) : null;
        if(alias != null) sb.append(alias).append('.');
        return sb;
    }

    private Character alias(Uri uri) {
        return (aliasMap != null) ? aliasMap.get(uri) : null;
    }

    private void buildAliasMap() {
        if(query != null && query.table != null && query.table.tables != null) {
            buildAliasMap(query.table);
        }
    }

    private void buildAliasMap(Table table) {
        if(aliasMap == null) {
            aliasMap = new HashMap<Uri, Character>();
            aliasMap.put(table.uri, 'a');
        } else {
            if(!aliasMap.containsKey(table.uri)) {
                aliasMap.put(table.uri, (char) ('a' + aliasMap.size()));
            }
        }
        if(table.tables != null) {
            for(Table t : table.tables) {
                buildAliasMap(t);
            }
        }
    }


    private String buildSelect() {
        StringBuilder sb = new StringBuilder();

        if(count > 0) {
            sb.append('\'').append(count).append('\'').append(" AS ").append(ObjectBuilder.Q).append(", ");
            alias(sb).append(query.table.joinColumn).append(" AS ").append(ObjectBuilder.P);
        }
        else if(query.includes != null) {
            sb.append("'0' AS ").append(ObjectBuilder.Q).append(", ");
            sb.append("'0' AS ").append(ObjectBuilder.P);
        }

        if(query.table.tables != null) {
            buildSelect(sb, query.table);
        }
        else if(query.table.projection != null) {
            for(String column : query.table.projection) {
                if(sb.length() > 0) sb.append(',').append(' ');
                sb.append(column);
            }
        }
        else if(query.select != null) {
            for(Expression select : query.select) {
                if(sb.length() > 0) sb.append(',').append(' ');
                select.apply(this, sb);
            }
        }

        if(query.includes != null) {
            for(Query include : query.includes) {
                if(sb.length() > 0) sb.append(',').append(' ');
                Character alias = alias(include.parentUri);
                if(alias != null) sb.append(alias).append('.');
//                sb.append("_id AS _").append(include.parentIx).append("__id");
                sb.append(include.parentColumn).append(" AS _").append(include.parentIx).append("__id");
            }
        }

        return (sb.length() > 0) ? sb.toString() : "*";
    }

    private void buildSelect(StringBuilder sb, Table table) {
        if(table.projection != null) {
            char alias = alias(table.uri);
            for(String column : table.projection) {
                if(sb.length() > 0) sb.append(',').append(' ');
                sb.append(alias).append('.').append(column);
                if(table.field == null) sb.append(" AS ").append(column);
                else sb.append(" AS _").append(table.field).append('_').append(column);
            }
        }
        if(table.tables != null) {
            for(Table t : table.tables) {
                buildSelect(sb, t);
            }
        }
    }

    private String buildTables() {
        if(query.table.tables != null) {
            StringBuilder sb = new StringBuilder();
            buildTables(sb, null, query.table);
            return sb.toString();
        }
        return Contracts.getContract(query.table.uri).table;
    }


    private void buildTables(StringBuilder sb, Table parent, Table table) {
        char alias = alias(table.uri);
        Contract contract = Contracts.getContract(table.uri);
        if(alias == 'a') {
            sb.append(contract.table).append(" AS ").append(alias);
        } else {
            String id = Contract.Model.COLUMN_ID;
            String pid = table.joinColumn;
            sb.append(" LEFT JOIN ").append(contract.table).append(" AS ").append(alias);
            sb.append(" ON ").append(alias).append('.').append(id).append('=').append(alias(parent.uri)).append('.').append(pid);
        }
        if(table.tables != null) {
            for(Table t : table.tables) {
                buildTables(sb, table, t);
            }
        }
    }

    private String buildWhere() {
        StringBuilder sb = new StringBuilder();
        if(query.where != null) {
            for(Expression expression : query.where) {
                if(sb.length() > 0) sb.append(" AND ");
                expression.apply(this, sb);
            }
        }
        if(count > 0) {
            if(sb.length() > 0) sb.append(" AND ");
            alias(sb).append(query.table.joinColumn).append(" IN (");
            for(long id : parents) {
                sb.append(id).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
        }
        return (sb.length() > 0) ? sb.toString() : null;
    }

    private String buildGroup() {
        if(query.groupBy != null) {
            StringBuilder sb = new StringBuilder();
            for(String group : query.groupBy) {
                if(sb.length() > 0) sb.append(',').append(' ');
                alias(sb).append(group);
            }
            return sb.toString();
        }
        return null;
    }

    private String buildHaving() {
        if(query.having != null) {
            StringBuilder sb = new StringBuilder();
            for(Expression expression : query.having) {
                expression.apply(this, sb);
            }
            return sb.toString();
        }
        return null;
    }

    private String buildOrder() {
        if(query.order != null) {
            StringBuilder sb = new StringBuilder();
            for(Order order : query.order) {
                if(sb.length() > 0) sb.append(',').append(' ');
                alias(sb).append(order.col);
                if("D".equalsIgnoreCase(order.dir) || "DESC".equalsIgnoreCase(order.dir)) {
                    sb.append(" DESC");
                }
            }
            return sb.toString();
        }
        return null;
    }

    private String buildLimit() {
        if(query.limit != null) {
            if(query.limit.o == 0) {
                return String.valueOf(query.limit.l);
            }
            return query.limit.l + " OFFSET " + query.limit.o;
        }
        return null;
    }


    private void append(StringBuilder sb, String name, String clause) {
        if(clause != null && clause.length() > 0) sb.append(name).append(clause);
    }


    public class Statement {
        String sql;
        String[] args;
    }

}
