package fm.strength.worm;

import java.util.HashMap;
import java.util.Map;

import fm.strength.worm.Data.Model.JSON;

public class Types {

    private final static Map<Class<?>, Class<?>> p2w;
    private final static Map<Class<?>, Object> p2n;
    private final static Map<Class<?>, Class<?>> w2p;
    static {
        p2w = new HashMap<Class<?>, Class<?>>();
        p2w.put(int.class, Integer.class);
        p2w.put(long.class, Long.class);
        p2w.put(byte.class, Byte.class);
        p2w.put(short.class, Short.class);
        p2w.put(float.class, Float.class);
        p2w.put(double.class, Double.class);
        p2w.put(char.class, Character.class);
        p2w.put(boolean.class, Boolean.class);
        p2n = new HashMap<Class<?>, Object>();
        p2n.put(int.class, 0);
        p2n.put(long.class, 0l);
        p2n.put(byte.class, (byte) 0);
        p2n.put(short.class, (short) 0);
        p2n.put(float.class, 0f);
        p2n.put(double.class, 0d);
        p2n.put(char.class, (char) 0);
        p2n.put(boolean.class, false);
        w2p = new HashMap<Class<?>, Class<?>>();
        w2p.put(Integer.class, int.class);
        w2p.put(Long.class, long.class);
        w2p.put(Byte.class, byte.class);
        w2p.put(Short.class, short.class);
        w2p.put(Float.class, float.class);
        w2p.put(Double.class, double.class);
        w2p.put(Character.class, char.class);
        w2p.put(Boolean.class, boolean.class);
    }

    public static <T> Class<T> wrapper(Class<T> type) {
        //noinspection unchecked
        return type.isPrimitive() ? (Class<T>) p2w.get(type) : type;
    }

    public static <T> T wrappedNull(Class<T> type) {
        //noinspection unchecked
        return type.isPrimitive() ? (T) p2n.get(type) : null;
    }

    public static boolean isJSON(Class<?> type) {
        return type.getAnnotation(JSON.class) != null;
    }

    public static boolean isSimple(Class<?> type) {
        return (type == String.class) || type.isPrimitive() || Types.isWrapper(type);
    }

    public static boolean isWrapper(Class<?> type) {
        return w2p.containsKey(type);
    }

}
