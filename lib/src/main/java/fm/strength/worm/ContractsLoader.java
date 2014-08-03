package fm.strength.worm;

import android.net.Uri;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import fm.strength.worm.util.Err;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;

public class ContractsLoader {

    public static final String ERR_CLASS_CAST = "field '%s' must be of type %s to use %s as model";
    public static final String ERR_ILLEGAL_ACCESS = "field '%s' must be public to use %s as a model";
    public static final String ERR_NO_SUCH_FIELD = "%s must contain a public field '%s' to be used as a model";
    public static final String ERR_NOT_STATIC = "'%s' must be a static field";
    public static final String ERR_NOT_FINAL = "'%s' must be declared final field";
    public static final String ERR_NOT_HIERARCHICAL = "'%s' is not a hierarchical uri";


    public static void load(String baseType, Class<?> rootContract) throws IllegalArgumentException {
        ContractsLoader loader = new ContractsLoader();
        loader.loadContracts("/vnd." + baseType, rootContract);
    }


    private ContractsLoader() {
        // private constructor
    }

    private void loadContracts(String baseType, Class<?> rootContract) throws IllegalArgumentException {
        for(Class<?> modelType : rootContract.getDeclaredClasses()) {
            loadContract(baseType, modelType);
        }
    }

    private void loadContract(String baseType, Class<?> contract) throws IllegalArgumentException {
        Uri contentUri = getContentUri(contract);

        if (contentUri != null) {
            String path = contentUri.getPath();
            if(path == null) {
                throw Err.get(ERR_NOT_HIERARCHICAL, contentUri);
            }

            String contentPath = path.replace('/', '.');
            String contentType = CURSOR_DIR_BASE_TYPE + baseType + contentPath;
            String contentItemType = CURSOR_ITEM_BASE_TYPE + baseType + contentPath;

            String table = getTable(contract);
            Set<String> columns = getColumns(contract);

            Contracts.addModel(contract, contentUri, contentType, table, columns, false);
            Contracts.addModel(contract, contentUri, contentItemType, table, columns, true);

            for (Class<?> childType : contract.getDeclaredClasses()) {
                loadContract(baseType, childType);
            }
        }
    }


    private void checkModifiers(Field field) throws IllegalArgumentException {
        int modifiers = field.getModifiers();
        if(!Modifier.isStatic(modifiers)) throw Err.get(ERR_NOT_STATIC, field.getName());
        if(!Modifier.isFinal(modifiers))  throw Err.get(ERR_NOT_FINAL, field.getName());
    }

    private Set<String> getColumns(Class<?> type) throws IllegalArgumentException {
        Set<String> columns = new HashSet<String>();
        for(Field field : type.getFields()) {
            String name = field.getName();
            try {
                if(name.startsWith("COLUMN_")) {
                    checkModifiers(field);
                    columns.add((String) field.get(type));
                }
            } catch(ClassCastException e) {
                throw Err.get(ERR_CLASS_CAST, name, "String", type.getSimpleName());
            } catch(IllegalAccessException e) {
                throw Err.get(ERR_ILLEGAL_ACCESS, name, type.getSimpleName());
            }
        }
        return columns;
    }

    private Uri getContentUri(Class<?> type) throws IllegalArgumentException {
        try {
            Field field = type.getField("CONTENT_URI");
            checkModifiers(field);
            return Uri.class.cast(field.get(type));
        } catch (ClassCastException e) {
            throw Err.get(ERR_CLASS_CAST, "CONTENT_URI", "Uri", type.getSimpleName());
        } catch (IllegalAccessException e) {
            throw Err.get(ERR_ILLEGAL_ACCESS, "CONTENT_URI", type.getSimpleName());
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private String getTable(Class<?> type) throws IllegalArgumentException {
        try {
            Field field = type.getDeclaredField("TABLE");
            checkModifiers(field);
            field.setAccessible(true);
            return (String) field.get(type);
        } catch (ClassCastException e) {
            throw Err.get(ERR_CLASS_CAST, "TABLE", "String", type.getSimpleName());
        } catch (IllegalAccessException e) {
            throw Err.get(ERR_ILLEGAL_ACCESS, "TABLE", type.getSimpleName());
        } catch (NoSuchFieldException e) {
            throw Err.get(ERR_NO_SUCH_FIELD, type.getSimpleName(), "TABLE");
        }
    }

}
