package com.example.vending;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.vending.R;

public class NetworkHandler {

    Context context;

    public NetworkHandler(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = manager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(activeNetwork);

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    public void showNoInternetDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.no_internet);
        alertDialogBuilder.setMessage(R.string.check_connection);
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setCancelable(false).show();
    }
}
