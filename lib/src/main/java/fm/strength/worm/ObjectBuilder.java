package fm.strength.worm;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.strength.sloppyj.Jay;
import fm.strength.sloppyj.Kreator;
import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.Contracts.Contract.Model;
import fm.strength.worm.Data.Model.JSON;
import fm.strength.worm.Data.Model.NotNull;
import fm.strength.worm.Data.Model.X;
import fm.strength.worm.util.Err;

import static fm.strength.worm.Types.isJSON;
import static fm.strength.worm.Types.isSimple;
import static fm.strength.worm.util.Err.ERR_NO_SUCH_FIELD;
import static fm.strength.worm.util.StringUtils.columnName;

public class ObjectBuilder<T> {

    public static final String ERR_NOT_DIRECT_TYPE = "requested type is not a direct data type";
    public static final String ERR_MISSING_QCOL = "missing the 'query' column in data for nested type: %s";
    public static final String ERR_MISSING_PCOL = "missing the 'parent id' column in data for nested type: %s";
    public static final String ERR_INVALID_QCOL = "couldn't parse 'query' column: %s";
    public static final String ERR_ILLEGAL_ACCESS = "field should have been set to accessible...";
    public static final String ERR_ENUM_FIELD = "enum fields not supported: field '%s' in %s";
    public static final String ERR_SYNTHETIC_FIELD = "synthetic fields not supported: field '%s' in %s";
    public static final String ERR_INNER_CLASS = "non-static inner classes not supported: %s";
    public static final String ERR_ABSTRACT_CLASS = "building abstract classes not supported: %s";
    public static final String ERR_INTERFACE = "building interfaces not supported: %s";

    public static final String Q = "__q__";
    public static final String P = "__p__";

    private static final int QCOL = 0;
    private static final int PCOL = 1;

    private static final int SKIP = -1; // could also mean 'not found'
    private static final int NESTED = -2;
    private static final int JOINED = -3;


    public static <T> ObjectBuilder<T> create(Class<T> type) {
        assertValidType(type);
        return new ObjectBuilder<T>(type);
    }

    private static void assertHasIdColumns(Class<?> type, Cursor cursor) {
        if(!Q.equals(cursor.getColumnName(QCOL))) throw Err.get(ERR_MISSING_QCOL, type);
        if(!P.equals(cursor.getColumnName(PCOL))) throw Err.get(ERR_MISSING_PCOL, type);
    }

    private static void assertValidType(Class<?> type) {
        if(!type.isPrimitive()) {
            int m = type.getModifiers();
            if(Modifier.isInterface(m)) throw Err.get(ERR_INTERFACE, type);
            if(Modifier.isAbstract(m)) throw Err.get(ERR_ABSTRACT_CLASS, type);
            if(type.isMemberClass() && !Modifier.isStatic(m)) throw Err.get(ERR_INNER_CLASS, type);
        }
    }


    private final Class<T> type;
    private Map<String, BuildDetails<?>> buildDetails;
    private Cursor cursor;
    private boolean nested;

    private ObjectBuilder(Class<T> type) {
        this.type = type;
    }

    public ObjectBuilder<T> withData(Cursor cursor) {
        this.cursor = cursor;
        return this;
    }

    public List<T> build() {
        if(cursor != null && cursor.moveToFirst()) {
            if(isSimple(type)) {
                return buildSimpleObjects();
            } else {
                BuildDetails<T> details = getBuildDetails(type);
                nested = details.nested;
                if(nested) {
                    return buildNestedObjects(details);
                }
                return buildObjects(details);
            }
        } else {
            return new ArrayList<T>(0);
        }
    }

    public T build(int row) {
        if(cursor != null && row >= 0) {
            if(cursor.moveToPosition(row)) {
                if(isSimple(type)) {
                    return buildSimpleObject();
                } else {
                    BuildDetails<T> details = getBuildDetails(type);
                    nested = details.nested;
                    if(nested) {
                        return buildNestedObject(details);
                    }
                    return buildObject(details);
                }
            }
        }
        return Types.wrappedNull(type);
    }

    public T build(int row, int col) {
        if(cursor != null && row >= 0 && col >= 0) {
            if(cursor.moveToPosition(row) && col < cursor.getColumnCount()) {
                return get(type, col);
            }
        }
        return Types.wrappedNull(type);
    }


    private T buildSimpleObject() {
        return get(type, 0);
    }

    private List<T> buildSimpleObjects() {
        List<T> results = new ArrayList<T>();
        do results.add(get(type, 0)); while(cursor.moveToNext());
        return results;
    }

    private T buildObject(BuildDetails<T> details) {
        T object = Kreator.newInstance(details.buildType);
        for(int i = 0; i < details.columns.length; i++) {
            Field field = details.fields[i];
            int column = details.columns[i];
            if(column >= 0) {
                if(isJSON(field.getType())) {
                    if(cursor.isNull(column)) {
                        if(field.getAnnotation(NotNull.class) != null) {
                            set(object, field, Kreator.newInstance(field.getType()));
                        }
                    } else {
                        String json = cursor.getString(column);
                        Object value = Jay.get(json).as(field.getType());
                        set(object, field, value);
                    }
                } else {
                    set(object, field, column);
                }
            } else if(column == JOINED) {
                String alias = field.getName();
                Class<?> joinType = field.getType();
                String idColumnName = "_" + alias + "__id";
                int joinIdColumn = cursor.getColumnIndex(idColumnName);
                if(joinIdColumn == -1) throw Err.get("cannot build %s without joinIdColumn %s", joinType.getSimpleName(), idColumnName);
                long id = cursor.getLong(joinIdColumn);
                if(id > 0) {
                    BuildDetails joinDetails = getBuildDetails(joinType, alias);
                    if(joinDetails.cache == null) {
                        joinDetails.cache = new HashMap<Long, T>();
                    }
                    Object joinObject = joinDetails.cache.get(id);
                    if(joinObject == null) {
                        joinDetails.cache.put(id, joinObject = buildObject(joinDetails));
                    }
                    set(object, field, joinObject);
                }
                else if(field.getAnnotation(NotNull.class) != null) {
                    set(object, field, Kreator.newInstance(joinType));
                }
            }
        }
        return object;
    }

    private List<T> buildObjects(BuildDetails<T> details) {
        List<T> objects = new ArrayList<T>();

        do {
            objects.add(buildObject(details));
        } while(cursor.moveToNext());

        return objects;
    }

    private T buildNestedObject(BuildDetails<T> details) {
        List<Model> models = buildModels(details);
        return models.isEmpty() ? null : type.cast(models.get(0).instance);
    }

    private List<T> buildNestedObjects(BuildDetails<T> details) {
        List<Model> models = buildModels(details);
        List<T> objects = new ArrayList<T>(models.size());
        for(Model model : models) {
            objects.add(type.cast(model.instance));
        }
        return objects;
    }

    private List<Model> buildModels(BuildDetails details) {
        assertHasIdColumns(details.buildType, cursor);
        List<Model> models = new ArrayList<Model>();

        int q0 = getQCount();
        int q1;
        do {
            Object object = buildObject(details);
            Model model = new Model(cursor.getPosition(), cursor.getLong(1), object);
            models.add(model);
            q1 = cursor.moveToNext() ? getQCount() : -1;
        } while(q0 == q1);

        if(details.nested && models.size() > 0) {
            for(int i = 0; i < details.fields.length; i++) {
                if(details.columns[i] == NESTED) {
                    if(q1 == q0 + 1) {
                        linkModels(models, details.fields[i]);
                        q0 = cursor.moveToPrevious() ? getQCount() : -1;
                        q1 = cursor.moveToNext() ? getQCount() : -1;
                    } else {
                        for(Model model : models) {
                            set(model.instance, details.fields[i], new ArrayList(0));
                        }
                        q0++;
                    }
                }
            }
        }

        return models;
    }

    private int getQCount() {
        if(nested) {
            try {
                return cursor.getInt(QCOL);
            } catch(NumberFormatException e) {
                throw Err.get(e, ERR_INVALID_QCOL, e.getMessage());
            }
        }
        return 0;
    }

    private void linkModels(List<Model> models, Field field) {
        Class<?> includeType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        BuildDetails<?> includeDetails = getBuildDetails(includeType);

        List<Model> includes = buildModels(includeDetails);

        Map<Long, List<Object>> map = new HashMap<Long, List<Object>>();
        for(Model child : includes) {
            List<Object> children = map.get(child.pid);
            if(children == null) {
                map.put(child.pid, children = new ArrayList<Object>());
            }
            children.add(child.instance);
        }

        // TODO
        String parentId = null;
        Contract contract = Contracts.getContract(includeType);
        Contract[] contracts = Contracts.getContracts(models.get(0).instance.getClass());
        for(int j = contracts.length - 1; j >= 0 && parentId == null; j--) {
            try {
                contract.getMergeId(contracts[j]);
                parentId = "_" + j + "__id";
            } catch(IllegalArgumentException e) {
                if(j == 0) throw e;
            }
        }


        int includeEndPosition = cursor.getPosition();

        cursor.moveToPosition(models.get(0).pos);
        int ix = cursor.getColumnIndex(parentId);
        if(ix == -1) {
            ix = cursor.getColumnIndex("_id"); // TODO
        }

        for(Model model : models) {
            cursor.moveToPosition(model.pos);
            long id = cursor.getLong(ix);
            List<Object> children = map.get(id);
            if(children == null) {
                children = new ArrayList<Object>(0);
            }
            set(model.instance, field, children);
        }

        cursor.moveToPosition(includeEndPosition);
    }

    private <E> E get(Class<E> type, int col) {
        Class<E> wrapperType = Types.wrapper(type);
        if(wrapperType == String.class)    return wrapperType.cast(cursor.getString(col));
        if(wrapperType == Integer.class)   return wrapperType.cast(cursor.getInt(col));
        if(wrapperType == Long.class)      return wrapperType.cast(cursor.getLong(col));
        if(wrapperType == Byte.class)      return wrapperType.cast((byte) cursor.getInt(col));
        if(wrapperType == Short.class)     return wrapperType.cast(cursor.getShort(col));
        if(wrapperType == Float.class)     return wrapperType.cast(cursor.getFloat(col));
        if(wrapperType == Double.class)    return wrapperType.cast(cursor.getDouble(col));
        if(wrapperType == Character.class) return wrapperType.cast((char) cursor.getInt(col));
        if(wrapperType == Boolean.class)   return wrapperType.cast(cursor.getInt(col) != 0);
        if(wrapperType == Date.class)      return wrapperType.cast(new Date(cursor.getLong(col)));
        throw Err.get(ERR_NOT_DIRECT_TYPE);
    }

    private void set(Object object, Field field, int col) {
        set(object, field, get(field.getType(), col));
    }

    private void set(Object object, Field field, Object value) {
        try {
            field.set(object, value);
        } catch(IllegalAccessException e) {
            throw Err.get(e, ERR_ILLEGAL_ACCESS, e);
        }
    }


    private <BT> BuildDetails<BT> getBuildDetails(Class<BT> buildType) {
        return getBuildDetails(buildType, null);
    }
    private <BT> BuildDetails<BT> getBuildDetails(Class<BT> buildType, String alias) {
        if(buildDetails == null) {
            buildDetails = new HashMap<String, BuildDetails<?>>();
        }
        String key = getQCount() + "::" + buildType + "::" + alias;
        BuildDetails bd = buildDetails.get(key);
        if(bd == null) {
            buildDetails.put(key, bd = new BuildDetails(buildType, alias));
        }
        return bd;
    }


    private class BuildDetails<BT> {
        final Class<BT> buildType;
        final Field[] fields;
        final int[] columns;
        final boolean nested;
        final String alias;
        public Map<Long, BT> cache;
        private BuildDetails(Class<BT> type, String alias) {
            this.buildType = type;
            this.alias = alias;
            this.fields = type.getDeclaredFields();
            this.columns = new int[fields.length];
            this.nested = initColumns();
        }
        private boolean initColumns() {
            boolean nested = false;
            Contract[] contracts = Contracts.getContracts(buildType);
            for(int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(field.getAnnotation(X.class) != null || Modifier.isStatic(field.getModifiers())) {
                    columns[i] = SKIP;
                } else {
                    String fieldName = field.getName();
                    Class<?> fieldType = field.getType();
                    if(field.isSynthetic()) throw Err.get(ERR_SYNTHETIC_FIELD, fieldName, type);
                    if(fieldType.isEnum()) throw Err.get(ERR_ENUM_FIELD, fieldName, type);
                    field.setAccessible(true); // used later, when building
                    if(contracts.length == 0) {
                        if(fieldName.equals("id")) fieldName = Contract.Model.COLUMN_ID;
                        columns[i] = cursor.getColumnIndex(columnName(fieldName));
                    } else {
                        if(isSimple(fieldType) || isJSON(fieldType)) {
                            String column = null;
                            for(int j = 0; j < contracts.length; j++) {
                                Contract contract = contracts[j];
                                String columnFieldName = "COLUMN_" + columnName(fieldName).toUpperCase();
                                try {
                                    Field columnField = contract.type.getField(columnFieldName);
                                    column = (String) columnField.get(fieldType);
                                    break;
                                } catch(NoSuchFieldException e) {
                                    if(j == contracts.length - 1) throw Err.get(ERR_NO_SUCH_FIELD, type, columnFieldName);
                                } catch(IllegalAccessException e) {
                                    throw Err.get(ERR_ILLEGAL_ACCESS, columnFieldName, type);
                                }
                            }
                            if(alias != null) column = "_" + alias + "_" + column;
                            columns[i] = cursor.getColumnIndex(column);
                        }
                        else if(List.class.isAssignableFrom(fieldType)) {
                            columns[i] = NESTED;
                            nested = true;
                        } else {
                            columns[i] = JOINED;
                        }
                    }
                }
            }
            return nested;
        }
    }


    private static class Model {
        final int  pos;
        final long pid;
        final Object instance;
        Model(int pos, long pid, Object instance) {
            this.pos = pos; this.pid = pid; this.instance = instance;
        }
    }

}
