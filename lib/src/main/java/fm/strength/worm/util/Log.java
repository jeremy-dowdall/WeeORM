package fm.strength.worm.util;

import static fm.strength.worm.util.StringUtils.format;

public class Log {

    public static final String TAG = "fm.strength.worm";

    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG   = android.util.Log.DEBUG;
    public static final int INFO    = android.util.Log.INFO;
    public static final int WARN    = android.util.Log.WARN;
    public static final int ERROR   = android.util.Log.ERROR;
    public static final int ASSERT  = android.util.Log.ASSERT;


    public static boolean isLoggable(int level) {
        return android.util.Log.isLoggable(TAG, level);
    }

    public static void v(String msg, Object...args) {
        if(isLoggable(VERBOSE)) android.util.Log.v(TAG, format(msg, args));
    }

    public static void v(Throwable err, String msg, Object...args) {
        if(isLoggable(VERBOSE)) android.util.Log.v(TAG, format(msg, args), err);
    }

    public static void d(String msg, Object...args) {
        if(isLoggable(DEBUG)) android.util.Log.d(TAG, format(msg, args));
    }

    public static void d(Throwable err, String msg, Object...args) {
        if(isLoggable(DEBUG)) android.util.Log.d(TAG, format(msg, args), err);
    }

    public static void i(String msg, Object...args) {
        if(isLoggable(INFO)) android.util.Log.i(TAG, format(msg, args));
    }

    public static void i(Throwable err, String msg, Object...args) {
        if(isLoggable(INFO)) android.util.Log.i(TAG, format(msg, args), err);
    }

    public static void w(String msg, Object...args) {
        if(isLoggable(WARN)) android.util.Log.w(TAG, format(msg, args));
    }

    public static void w(Throwable err, String msg, Object...args) {
        if(isLoggable(WARN)) android.util.Log.w(TAG, format(msg, args), err);
    }

    public static void e(String msg, Object...args) {
        if(isLoggable(ERROR)) android.util.Log.e(TAG, format(msg, args));
    }

    public static void e(Throwable err, String msg, Object...args) {
        if(isLoggable(ERROR)) android.util.Log.e(TAG, format(msg, args), err);
    }

    public static void wtf(String msg, Object...args) {
        if(isLoggable(ASSERT)) android.util.Log.wtf(TAG, format(msg, args));
    }

    public static void wtf(Throwable err, String msg, Object...args) {
        if(isLoggable(ASSERT)) android.util.Log.wtf(TAG, format(msg, args), err);
    }

    
    private Log() {
        // private constructor
    }

}
