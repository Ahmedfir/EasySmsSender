package ahmed.com.easysmssender.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * The softKeyboard works a bit randomly from a device to another, and also in different API levels.
 * This class utilities should force the hide and show of the keyboard.
 * <p/>
 * <p/>
 * Created by ahmed on 5/15/15.
 */
public final class SoftKeyboardUtils {

    /**
     * Internal constructor; not to be called as this class provides static
     * utilities only.
     */
    private SoftKeyboardUtils() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Force show softKeyboard.
     */
    public static void forceShow(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Force hide softKeyboard.
     */
    public static boolean forceHide(@NonNull Activity activity, @NonNull EditText editText) {
        if (activity.getCurrentFocus() == null || !(activity.getCurrentFocus() instanceof EditText)) {
            editText.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return imm.isActive();
    }

}
