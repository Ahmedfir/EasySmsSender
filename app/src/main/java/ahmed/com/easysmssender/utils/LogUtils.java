package ahmed.com.easysmssender.utils;

import android.text.TextUtils;
import android.util.Log;

import ahmed.com.easysmssender.BuildConfig;

/**
 * utility class for log outputs.
 *
 * Created by ahmed on 11/15/15.
 */
public final class LogUtils {

    /**
     * Internal constructor; not to be called as this class provides static
     * utilities only.
     */
    private LogUtils() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.i(tag, message);
        }
    }

    public static void i(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.i(tag, message, t);
        }
    }

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.v(tag, message);
        }
    }

    public static void v(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.v(tag, message, t);
        }
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.d(tag, message);
        }
    }

    public static void d(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.d(tag, message, t);
        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.e(tag, message, t);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.w(tag, message);
        }
    }

    public static void w(String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.w(tag, message, t);
        }
    }
}
