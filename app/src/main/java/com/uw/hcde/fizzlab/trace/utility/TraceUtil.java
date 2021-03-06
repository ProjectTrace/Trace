package com.uw.hcde.fizzlab.trace.utility;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseLog;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Contains utils for Trace project.
 *
 * @author tianchi
 */
public class TraceUtil {
    public static final int TOAST_MESSAGE_TIME = 500;
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
     * Checks network status (Wifi or Cell), shows settings and forces action.
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
            final MaterialDialog dialog = new MaterialDialog(context);
            dialog.setTitle(R.string.network_service_disabled);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(R.string.enable_network);

            // Negative button
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Positive button
            dialog.setPositiveButton(R.string.settings, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    context.startActivity(intent);
                }
            });

            dialog.show();
        }
        return enabled;
    }

    /**
     * Checks GPS status, show settings and forces action.
     *
     * @param context
     * @return true if available
     */
    public static boolean checkGPSStatus(final Context context) {
        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            final MaterialDialog dialog = new MaterialDialog(context);
            dialog.setTitle(R.string.location_service_disabled);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(R.string.enable_gps);

            // Negative button
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Positive button
            dialog.setPositiveButton(R.string.settings, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            dialog.show();
        }
        return enabled;
    }

    /**
     * Checks if google play service is available.
     * TODO: prompts user to install latest version.
     *
     * @return true if available
     */
    public static boolean checkGooglePlayService(final Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            final MaterialDialog dialog = new MaterialDialog(context);
            dialog.setTitle(R.string.google_play_required);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(R.string.enable_google_play);

            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
    }

    public static void showTutorialDialog(final Context context, int res) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.tutorial);
        dialog.setMessage(context.getString(res));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.report_problem, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showReportDialog(context);
            }
        });
        dialog.show();
    }

    public static void showReportDialog(final Context context) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.report_problem);

        // Set up dialog view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_report_problem, null);
        final EditText input = (EditText) view.findViewById(R.id.report_content);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report = input.getText().toString();
                if (report.length() != 0) {
                    ParseLog log = new ParseLog();
                    log.setUser(ParseUser.getCurrentUser());
                    log.setMessage(report);
                    log.saveInBackground();
                }
                dialog.dismiss();
                TraceUtil.showToast(context, context.getString(R.string.thanks));
            }
        });
        dialog.show();
    }


}
