package ahmed.com.easysmssender.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

/**
 * Created by ahmed on 11/15/15.
 */
public final class DeviceUtils {

    /**
     * Internal constructor; not to be called as this class provides static
     * utilities only.
     */
    private DeviceUtils() {
        throw new UnsupportedOperationException("No instances permitted");
    }


    public static String getCurrentUserPhoneNumber(@NonNull Context context){
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }
}
