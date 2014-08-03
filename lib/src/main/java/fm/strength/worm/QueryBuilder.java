package fm.strength.worm;

import android.net.Uri;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.Contracts.Contract.SortedModel;
import fm.strength.worm.Data.Model;
import fm.strength.worm.Data.Model.Join;
import fm.strength.worm.Data.Model.X;
import fm.strength.worm.data.Limit;
import fm.strength.worm.data.Order;
import fm.strength.worm.data.Select;
import fm.strength.worm.data.Where;
import fm.strength.worm.sql.Expression;
import fm.strength.worm.Query.Table;
import fm.strength.worm.data.Detail;
import fm.strength.worm.util.Err;

import static fm.strength.worm.Types.isJSON;
import static fm.strength.worm.Types.isSimple;
import static fm.strength.worm.util.Err.ERR_ILLEGAL_ACCESS;
import static fm.strength.worm.util.Err.ERR_NO_CONTRACT;
import static fm.strength.worm.util.Err.ERR_NO_SUCH_FIELD;
import static fm.strength.worm.util.StringUtils.columnName;

public class QueryBuilder {

    public static QueryBuilder create(Uri uri) throws IllegalArgumentException {
        return new QueryBuilder(uri);
    }
    public static QueryBuilder create(Class<?> type) throws IllegalArgumentException {
        return new QueryBuilder(type);
    }


    private final Uri uri;
    private final Class type;
    private Contract[] contracts;

    private Query query;
    private String joinColumn;
    private Detail[] details;

    private Map<Uri, Table> tableMap;

    private QueryBuilder(Class type) {
        this.uri = null;
        this.type = type;
    }
    private QueryBuilder(Uri uri) {
        this.uri = uri;
        this.type = null;
    }

    public QueryBuilder withDetails(Detail...details) {
        this.details = details;
        return this;
    }

    public Query build() {
        contracts = (type != null) ? Contracts.getContracts(type) : new Contract[0];
        if(type != null) {
            if(contracts.length == 0) throw Err.get(ERR_NO_CONTRACT, type);
            query = new Query(table(null, contracts[0].uri, null, joinColumn));
            build(query.table, type);
        } else {
            query = new Query(table(null, uri, null, joinColumn));
        }
        if(details != null) {
            for(Detail detail : details) {
                switch(detail.type) {
                    case Detail.SELECT:
                        if(query.select == null) query.select = new ArrayList<Expression>();
                        query.select.add(((Select) detail).expression);
                        break;
                    case Detail.WHERE:
                        if(query.where == null) query.where = new ArrayList<Expression>();
                        query.where.add(((Where) detail).condition);
                        break;
                    case Detail.ORDER:
                        if(query.order == null) query.order = new ArrayList<fm.strength.worm.data.Order>();
                        query.order.add((Order) detail);
                        break;
                    case Detail.LIMIT:
                        query.limit = (Limit) detail;
                }
            }
        }
        if(query.order == null && contracts.length > 0 && contracts[0].isSorted()) {
            query.order = new ArrayList<fm.strength.worm.data.Order>();
            query.order.add(new fm.strength.worm.data.Order(SortedModel.COLUMN_IX));
        }
        return query;
    }

    private void build(Table table, Class type) {
        Contract[] contracts = Contracts.getContracts(type);
        if(contracts.length == 0) {
            throw Err.get(ERR_NO_CONTRACT);
        }
        Field[] fields = type.getDeclaredFields();
        // TODO one loop?
        for(int i = 0; i < fields.length; i++) {
            if(fields[i].getAnnotation(X.class) != null) {
                fields[i] = null;
            }
        }
        for(int i = 0; i < fields.length; i++) {
            if(fields[i] != null && (isSimple(fields[i].getType()) || isJSON(fields[i].getType()))) {
                buildDirect(table, fields[i], contracts);
                fields[i] = null;
            }
        }
        for(int i = 0; i < fields.length; i++) {
            if(fields[i] != null && !List.class.isAssignableFrom(fields[i].getType())) {
                buildJoin(table, fields[i]);
                fields[i] = null;
            }
        }
        for(int i = 0; i < fields.length; i++) {
            if(fields[i] != null) {
                buildInclude(table, fields[i]);
            }
        }
    }

    private void buildDirect(Table table, Field field, Contract[] contracts) {
        String column = null;
        Contract contract = contracts[0];
        for(int i = 0; i < contracts.length; i++) {
            contract = contracts[i];
            String columnFieldName = "COLUMN_" + columnName(field.getName()).toUpperCase();
            try {
                Field columnField = contract.type.getField(columnFieldName);
                column = (String) columnField.get(field.getType());
                break;
            } catch(NoSuchFieldException e) {
                if(i == contracts.length - 1) throw Err.get(ERR_NO_SUCH_FIELD, type, columnFieldName);
            } catch(IllegalAccessException e) {
                throw Err.get(ERR_ILLEGAL_ACCESS, columnFieldName, type);
            }
        }
        if(contract != contracts[0]) {
            table = table(table, contract.uri, table.field, contracts[0].getMergeId(contract));
        }
        table.add(column);
    }

    private void buildJoin(Table table, Field field) {
        String fieldName = field.getName();
        Class fieldType = field.getType();
        String joinColumn = null;
        Contract parent = Contracts.getContract(table.uri);
        while(parent != null && joinColumn == null) {
            try {
                joinColumn = parent.getJoinColumn(fieldName);
            } catch(IllegalArgumentException e) {
                // TODO make smarter...
                Class parentType = parent.type.getDeclaringClass();
                parent = Contracts.getContract(parentType);
                if(parent == null) throw e;
                String throughColumn = parent.getMergeId(parentType);
                table = table(table, parent.uri, null, throughColumn);
            }
        }
        Contract contract = Contracts.getContract(fieldType);
        table = table(table, contract.uri, fieldName, joinColumn);
        build(table, fieldType);
        table.add("_id");
    }

    // List<ModelType> models
    private void buildInclude(Table table, Field field) {
        Class<?> includeType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        Contract includeContract = Contracts.getContract(includeType);
        if(includeContract == null) {
            throw Err.get(ERR_NO_CONTRACT, includeType);
        }

        QueryBuilder builder = new QueryBuilder(includeType);

        Join join = field.getAnnotation(Join.class);
        int ix = setIncludeJoinColumn(includeContract, builder, join);
        if(ix > 0) {
            // this requires a direct path from the join contract to the base (0-index) contract... allow gaps?
            for(int i = 0; i < ix; i++) {
                String throughColumn = contracts[i].getMergeId(contracts[i + 1]);
                table = table(table, contracts[i + 1].uri, null, throughColumn);
            }
        }

        builder.details = Detail.merge(builder.details, buildIncludeWhere(field, includeContract.uri));
        builder.details = Detail.merge(builder.details, buildIncludeOrder(field));

        Query include = builder.build();
        include.parentIx = ix;
        include.parentColumn = (join != null) ? join.to() : "_id";
        include.parentUri = contracts[ix].uri;

        if(query.includes == null) query.includes = new ArrayList<Query>();
        query.includes.add(include);
    }

    private int setIncludeJoinColumn(Contract includeContract, QueryBuilder builder, Join join) {
        int ix = contracts.length;
        while(builder.joinColumn == null && --ix >= 0) {
            if(ix == 0 || getParent(includeContract) == contracts[ix]) {
                try {
                    if(join == null) {
                        builder.joinColumn = includeContract.getMergeId(contracts[ix]);
                    } else if(contracts[ix].columns.contains(join.to())) {
                        builder.joinColumn = join.from();
                    }
                } catch(IllegalArgumentException e) {
                    if(ix == 0) throw e;
                }
            }
        }
        if(builder.joinColumn == null) {
            if(joinColumn == null) {
                throw Err.get("could not find join from %s to %s", includeContract.type, type);
            } else {
                throw Err.get("could not find join from %s to %s using explicit join column %s", includeContract.type, type, joinColumn);
            }
        }
        return ix;
    }

    private Contract getParent(Contract includeContract) {
        Class parentType = includeContract.type.getDeclaringClass();
        return Contracts.getContract(parentType);
    }

    private Detail[] buildIncludeWhere(Field field, Uri uri) {
        Model.Where whereAnnotation = field.getAnnotation(Model.Where.class);
        if(whereAnnotation != null) {
            Where where = new Where(uri, whereAnnotation.field());
            String s;
            if((s = whereAnnotation.is()).length() > 0) {
                if(Model.NULL.equals(s))       where.isNull();
                else if(Model.TRUE.equals(s))  where.isTrue();
                else if(Model.FALSE.equals(s)) where.isFalse();
                else                           where.isEqualTo(uri, s);
            }
            else if((s = whereAnnotation.isNot()).length() > 0) {
                if(Model.NULL.equals(s))       where.isNotNull();
                else if(Model.TRUE.equals(s))  where.isNotTrue();
                else if(Model.FALSE.equals(s)) where.isNotFalse();
                else                           where.isNotEqualTo(uri, s);
            }
            else if((s = whereAnnotation.lt()).length() > 0) {
                try {
                    where.isLessThan(Integer.parseInt(s));
                } catch(NumberFormatException e) {
                    where.isLessThan(uri, s);
                }
            }
            else if((s = whereAnnotation.lte()).length() > 0) {
                try {
                    where.isLessThanOrEqualTo(Integer.parseInt(s));
                } catch(NumberFormatException e) {
                    where.isLessThanOrEqualTo(uri, s);
                }
            }
            else if((s = whereAnnotation.gt()).length() > 0) {
                try {
                    where.isGreaterThan(Integer.parseInt(s));
                } catch(NumberFormatException e) {
                    where.isGreaterThan(uri, s);
                }
            }
            else if((s = whereAnnotation.gte()).length() > 0) {
                try {
                    where.isGreaterThanOrEqualTo(Integer.parseInt(s));
                } catch(NumberFormatException e) {
                    where.isGreaterThanOrEqualTo(uri, s);
                }
            }
            return new Detail[] { where };
        }
        return null;
    }

    private Detail[] buildIncludeOrder(Field field) {
        Model.Order orderAnnotation = field.getAnnotation(Model.Order.class);
        if(orderAnnotation != null) {
            String[] values = orderAnnotation.value();
            Detail[] details = new Detail[values.length];
            for(int i = 0; i < values.length; i++) {
                String[] sa = values[i].split("\\s+");
                Order order = new Order(sa[0]);
                if(sa.length > 1) {
                    if(sa[1].equalsIgnoreCase("D") || sa[1].equalsIgnoreCase("DESC")) order.DESC();
                }
                details[i] = order;
            }
            return details;
        }
        return null;
    }


    private Table table(Table parent, Uri uri, String field, String joinColumn) {
        if(tableMap == null) tableMap = new LinkedHashMap<Uri, Table>();
        Table table = tableMap.get(uri);
        if(table == null) {
            tableMap.put(uri, table = new Table(uri, field, joinColumn));
            if(parent != null) {
                parent.add(table);
            }
        }
        if(table.joinColumn == null || TextUtils.equals(joinColumn, table.joinColumn)) {
            if(field == null || TextUtils.equals(field, table.field)) {
                return table;
            }
            if(table.field == null) {
                table.field = field;
                return table;
            }
        }
        throw Err.get("not yet supported...");
    }


}
