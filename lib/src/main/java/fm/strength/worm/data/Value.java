package fm.strength.worm.data;

import android.content.ContentValues;

public class Value extends Detail {

    public static ContentValues compile(Detail...details) {
        ContentValues contentValues = new ContentValues();
        for(Detail detail : details) {
            if(detail.type == Detail.VALUE) {
                Value value = (Value) detail;
                add(contentValues, value);
            }
        }
        return contentValues;
    }

    public static ContentValues compile(Value...values) {
        ContentValues contentValues = new ContentValues(values.length);
        for(Value value : values) {
            add(contentValues, value);
        }
        return contentValues;
    }

    public static void add(ContentValues values, Value value) {
        add(values, value.key, value.value);
    }

    public static void add(ContentValues values, String key, Object value) {
        if(value == null) values.putNull(key);
        else if(value instanceof Boolean) values.put(key, (Boolean) value);
        else if(value instanceof Byte)    values.put(key, (Byte)    value);
        else if(value instanceof byte[])  values.put(key, (byte[])  value);
        else if(value instanceof Double)  values.put(key, (Double)  value);
        else if(value instanceof Float)   values.put(key, (Float)   value);
        else if(value instanceof Integer) values.put(key, (Integer) value);
        else if(value instanceof Long)    values.put(key, (Long)    value);
        else if(value instanceof Short)   values.put(key, (Short)   value);
        else values.put(key, String.valueOf(value));
    }


    private final String key;
    private final Object value;

    public Value(String key, Object value) {
        super(Detail.VALUE);
        this.key = key; this.value = value;
    }

}
