package com.example.dagna.together.helpers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.dagna.together.ProfileActivity;
import com.example.dagna.together.R;
import com.example.dagna.together.onlineDatabase.GetUserByLogin;

/**
 * Created by gapsa on 25.04.2016.
 */
public class GeneralHelpers {
    public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationAvailable(LocationManager locationManager)
    {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }else{
            return false;
        }
    }

    public static void displayToast(Context context, int val)
    {
        switch(val)
        {
            case 1:
                Toast.makeText(context, R.string.offline_mode,
                        Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(context, R.string.gps_off,
                        Toast.LENGTH_LONG).show();
                break;
            default: break;

        }
    }

    public static void createNetErrorDialog(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need a network connection to perform this action. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                context.startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayToast(context, 1);
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void createGpsErrorDialog(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need a location enabled to perform this action. Please turn on location in Settings.")
                .setTitle("Unable to localize")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayToast(context, 2);
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }



}
