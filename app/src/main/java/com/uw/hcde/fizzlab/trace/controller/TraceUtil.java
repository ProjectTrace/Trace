package com.uw.hcde.fizzlab.trace.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Contains utils for Trace project.
 *
 * @author tianchi
 */
public class TraceUtil {
    public static final int TOAST_MESSAGE_TIME = 2000;

    private static final int ALERT_MESSAGE_OFFSET = 20;
    private static Boolean sIsShowingToastMessage = false;
    private static Handler sHandler = new Handler();

    /**
     * Returns if toast message is being shown.
     *
     * @return
     */
    public static boolean isShowingToast() {
        return sIsShowingToastMessage;
    }

    /**
     * Displays toast message on screen.
     *
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
        }, TOAST_MESSAGE_TIME);
    }

    /**
     * Checks network status (Wifi or Cell), show settings if necessary
     *
     * @param context
     * @return true if available
     */
    public static boolean checkNetworkStatus(final Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean enabled = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Dialog));
            builder.setTitle(context.getString(R.string.network_service_disabled));

            // Sets up message window
            TextView text = new TextView(context);
            text.setText(context.getString(R.string.enable_network));
            text.setPadding(ALERT_MESSAGE_OFFSET, ALERT_MESSAGE_OFFSET, 0, ALERT_MESSAGE_OFFSET);
            builder.setView(text);

            // Negative button
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Nothing
                }
            });

            // Positive button
            builder.setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    context.startActivity(intent);
                }
            });
            builder.create().show();
        }
        return enabled;
    }

    /**
     * Checks GPS status, show settings if necessary
     *
     * @param context
     * @return true if available
     */
    public static boolean checkGPSStatus(final Context context) {
        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.location_service_disabled));

            // Sets up message window
            TextView text = new TextView(context);
            text.setText(context.getString(R.string.enable_gps));
            text.setPadding(ALERT_MESSAGE_OFFSET, ALERT_MESSAGE_OFFSET, 0, ALERT_MESSAGE_OFFSET);
            builder.setView(text);

            // Negative button
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Nothing
                }
            });

            // Positive button
            builder.setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            builder.create().show();
        }
        return enabled;
    }
}
