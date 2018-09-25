package com.vritti.yard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Admin-3 on 11/10/2016.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(Context parent) {
        boolean haveConnectedMobile = false;
        boolean haveConnectedWifi = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) parent
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo network : networkInfo) {
            if (network.getTypeName().equalsIgnoreCase("WIFI")) {
                if (network.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            if (network.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (network.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        // Log.i("isNetworkAvailable", ":"
        // + (haveConnectedWifi || haveConnectedMobile));
        return haveConnectedWifi || haveConnectedMobile;
    }
}
