package fm.strength.worm;

import android.content.ContentValues;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import fm.strength.sloppyj.Jay;
import fm.strength.worm.Contracts.Contract;
import fm.strength.worm.Data.Model.JSON;
import fm.strength.worm.Data.Model.X;
import fm.strength.worm.data.Value;
import fm.strength.worm.util.Err;
import fm.strength.worm.util.Log;

import static fm.strength.worm.Types.isSimple;
import static fm.strength.worm.util.Err.*;

public class ObjectParser {

    public static ObjectParser create(Object object) throws IllegalArgumentException {
        return new ObjectParser(object);
    }

    private static boolean includes(String[] columns, String column) {
        if(columns == null || columns.length == 0) {
            return true;
        }
        if(column == null) {
            return false;
        }
        for(int i = 0; i < columns.length; i++) {
            if(column.equals(columns[i])) {
                return true;
            }
        }
        return false;
    }


    private final Object object;

    private ObjectParser(Object object) {
        this.object = object;
    }

    public ContentValues getContentValues() {
        return getContentValues(null);
    }
    public ContentValues getContentValues(String[] columns) {
        Class<?> type = object.getClass();
        Contract contract = Contracts.getContract(type);
        if(contract == null) {
            throw Err.get(ERR_NO_CONTRACT, type);
        }
        ContentValues values = new ContentValues();
        for(Field field : type.getDeclaredFields()) {
            if(field.getAnnotation(X.class) == null) {
                Class<?> fieldType = field.getType();
                Contract fieldContract = Contracts.getContract(fieldType);
                boolean isJson = fieldType.getAnnotation(JSON.class) != null;
                if(fieldContract != null || isJson || include(field)) {
                    try {
                        String column = contract.getColumn(field);
                        if(includes(columns, column)) {
                            field.setAccessible(true);
                            Object value = field.get(object);
                            if(fieldContract != null && value != null) {
                                values.put(column, ObjectParser.create(value).getId());
                            } else if(isJson && value != null) {
                                values.put(column, Jay.get(value).asJson());
                            } else {
                                Value.add(values, column, value);
                            }
                        }
                    } catch(IllegalArgumentException e) {
                        if(columns == null || columns.length == 0) Log.w(e, e.getMessage());
                    } catch(IllegalAccessException e) {
                        Log.wtf(e, e.getMessage());
                    }
                }
            }
        }
        return values;
    }

    public long getId() {
        try {
            Field field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return field.getLong(object);
        } catch(NoSuchFieldException e) {
            throw Err.get("object does not contain an 'id' field: %s", object);
        } catch(IllegalAccessException e) {
            throw Err.get("could not access object's 'id' field: %s", object);
        }
    }

    private boolean include(Field field) {
        if("id".equals(field.getName()))            return false;
        if(field.isSynthetic())                     return false;
        if(Modifier.isStatic(field.getModifiers())) return false;

        Class<?> fieldType = field.getType();
        if(fieldType.isEnum())                      return false;

        return isSimple(fieldType);
    }

    public boolean setId(long id) {
        try {
            Field field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(object, id);
            return true;
        } catch(IllegalAccessException e) {
            throw Err.get("could not access object's 'id' field: %s", object);
        } catch(NoSuchFieldException e) {
            // fall through
        }
        return false;
    }

}
