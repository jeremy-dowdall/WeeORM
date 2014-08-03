package fm.strength.worm;

import android.net.Uri;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fm.strength.worm.Data.Model.Column;
import fm.strength.worm.Data.Model.X;
import fm.strength.worm.util.Err;
import fm.strength.worm.util.StringUtils;

import static fm.strength.worm.util.Err.ERR_ILLEGAL_ACCESS;
import static fm.strength.worm.util.Err.ERR_NEED_CONTRACT_FOR_COLUMN;
import static fm.strength.worm.util.Err.ERR_NO_SUCH_FIELD;
import static fm.strength.worm.util.StringUtils.columnName;

public class Contracts {

    private static Contracts instance = new Contracts();


    public static Uri getContentUri(Class<?> type) {
        if(instance == null) return null; // TODO
        Contract m = getContract(type);
        return (m != null) ? m.uri : null;
    }

    public static List<Uri> getContentUris(Class<?> type) {
        List<Uri> uris = new ArrayList<Uri>();
        addContentUris(type, uris);
        return uris;
    }

    private static void addContentUris(Class<?> type, List<Uri> uris) {
        uris.add(getContentUri(type));
        for(Field field : type.getDeclaredFields()) {
            if(field.getAnnotation(X.class) == null) {
                Class<?> fieldType = field.getType();
                if(List.class.isAssignableFrom(fieldType)) {
                    fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                }
                Data.Model.Contract ann = fieldType.getAnnotation(Data.Model.Contract.class);
                if(ann != null) {
                    addContentUris(fieldType, uris);
                }
            }
        }
    }

    public static Contract getContract(Class<?> type) {
        Contract m = instance.map.get(type);
        if(m != null) return m;
        Data.Model.Contract ann = type.getAnnotation(Data.Model.Contract.class);
        if(ann != null && ann.value().length > 0) {
            return instance.map.get(ann.value()[0]);
        }
        return null;
    }

    public static Contract getContract(int index) {
        return instance.list.get(index);
    }

    public static Contract getContract(Uri uri) {
        // TODO
        for(Contract contract : instance.list) {
            if(uri.equals(contract.uri)) return contract;
        }
        return null;
    }

    public static Contract[] getContracts(Class<?> type) {
        Contract m = instance.map.get(type);
        if(m != null) return new Contract[] { m };
        Data.Model.Contract ann = type.getAnnotation(Data.Model.Contract.class);
        if(ann != null && ann.value().length > 0) {
            Contract[] contracts = new Contract[ann.value().length];
            for(int i = 0; i < contracts.length; i++) {
                contracts[i] = instance.map.get(ann.value()[i]);
            }
            return contracts;
        }
        return new Contract[0];
    }

    public static boolean hasContract(Class<?> type) {
        return getContract(type) != null;
    }

    public static int size() {
        return instance.list.size();
    }


    static void addModel(Class<?> type, Uri contentUri, String contentType, String table, Set<String> columns, boolean isItem) {
        Contract contract = new Contract(type, contentUri, contentType, table, columns, isItem);
        instance.list.add(contract);
        instance.map.put(type, contract);
    }


    private final Map<Class<?>, Contract> map;
    private final List<Contract> list;

    private Contracts() {
        this.map = new HashMap<Class<?>, Contract>();
        this.list = new ArrayList<Contract>();
    }


    public static class Contract {

        public interface Model {
            String COLUMN_ID = "_id";
        }
        public interface NamedModel extends Model {
            String COLUMN_NAME = "name";
        }
        public interface SortedModel extends Model {
            String COLUMN_IX = "_ix";
        }

        public final Class<?> type;
        public final Uri uri;
        final String mime;
        final String table;
        final Set<String> columns;
        final boolean isItem;

        Contract(Class<?> type, Uri uri, String mime, String table, Set<String> columns, boolean isItem) {
            this.type = type;
            this.uri = uri;
            this.mime = mime;
            this.table = table;
            this.columns = Collections.unmodifiableSet(columns);
            this.isItem = isItem;
        }

        public String getColumn(Field field) throws IllegalArgumentException {
            Column columnAnnotation = field.getAnnotation(Column.class);
            String column = (columnAnnotation != null) ? columnAnnotation.value() : null;
            if(column == null || column.isEmpty()) {
                if(type == null) {
                    throw Err.get(ERR_NEED_CONTRACT_FOR_COLUMN);
                }
                String fieldName = "COLUMN_" + columnName(field.getName()).toUpperCase();
                if(hasContract(field.getType())) fieldName += "_ID";
                try {
                    column = (String) type.getField(fieldName).get(type);
                } catch(NoSuchFieldException e) {
                    throw Err.get(ERR_NO_SUCH_FIELD, type, fieldName);
                } catch(IllegalAccessException e) {
                    throw Err.get(ERR_ILLEGAL_ACCESS, fieldName, type);
                }
            }
            return column;
        }

        public String getJoinColumn(String fieldName) {
            return getString("COLUMN_" + columnName(fieldName).toUpperCase() + "_ID");
        }

        public String getMergeId(Contract contract) {
            return getString("COLUMN_" + StringUtils.singular(contract.type.getSimpleName()).toUpperCase() + "_ID");
        }

        public String getMergeId(Class type) {
            return getMergeId(getContract(type));
//            return getString("COLUMN_" + StringUtils.singular(type.getSimpleName()).toUpperCase() + "_ID");
        }

        public String getString(String fieldName) throws IllegalArgumentException {
            try {
                Object value = type.getField(fieldName).get(type);
                return (value != null) ? value.toString() : null;
            } catch(NoSuchFieldException e) {
                throw Err.get(Err.ERR_NO_SUCH_FIELD, type, fieldName);
            } catch(IllegalAccessException e) {
                throw Err.get(Err.ERR_ILLEGAL_ACCESS, fieldName, type);
            }
        }

        public boolean isSorted() {
            return columns.contains(SortedModel.COLUMN_IX);
        }

        @Override
        public String toString() {
            return uri.toString();
        }
    }
}
