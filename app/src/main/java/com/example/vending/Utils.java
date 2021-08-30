package com.example.vending;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static JSONArray extractJsonArray(JSONObject object, String arrayKey) {
        if (object != null) {
            try {
                return object.getJSONArray(arrayKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void showNoInternetDialog(Context context) {
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

    public static void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(
                () -> view.animate().scaleX(1).scaleY(1).setDuration(100));
    }

    public static void playSound(Context context, int resID) {
        MediaPlayer mp = MediaPlayer.create(context, resID);
        mp.start();
        mp.setOnCompletionListener(MediaPlayer::release);
    }
}
