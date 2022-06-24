package com.idialogics.autofobuser.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.idialogics.autofobuser.Utils.App;


@SuppressWarnings("deprecation")
public class NetworkChangeReceiver extends BroadcastReceiver {

    Context context1;

    public NetworkChangeReceiver(App myApp) {

        context1 = myApp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


    }


    public static boolean isOnline(Context context) {


        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());

        } catch (NullPointerException e) {

            e.printStackTrace();
            return false;
        }

    }


}