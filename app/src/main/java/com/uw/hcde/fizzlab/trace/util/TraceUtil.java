package com.uw.hcde.fizzlab.trace.util;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Contains utils for Trace project.
 *
 * @author tianchi
 */
public class TraceUtil {
    private static Boolean sIsShowingToastMessage = false;
    private static Handler sHandler = new Handler();

    /**
     * Returns if toast message is being shown.
     * @return
     */
    public static boolean isShowingToast() {
        return sIsShowingToastMessage;
    }

    /**
     * Displays toast message on screen.
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (sIsShowingToastMessage) {
            return;
        }

        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        sIsShowingToastMessage = true;
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sIsShowingToastMessage = false;
            }
        }, 2000);
    }
}
