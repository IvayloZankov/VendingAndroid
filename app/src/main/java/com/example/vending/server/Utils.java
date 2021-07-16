package com.example.vending.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
