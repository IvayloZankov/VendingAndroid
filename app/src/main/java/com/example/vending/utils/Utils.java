package com.example.vending.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import com.example.vending.R;

public class Utils {

    public static AlertDialog buildNoInternetDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.no_internet);
        alertDialogBuilder.setMessage(R.string.check_connection);
        alertDialogBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.cancel());
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

    public static void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(
                () -> view.animate().scaleX(1).scaleY(1).setDuration(100));
    }
}
