package com.example.vending.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtil {

    public JsonUtil() {
    }

    public JSONObject getObject(InputStream stream) {
        try {
            return new JSONObject(readStream(stream));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    public static JSONArray convertToArray(JSONObject object, String arrayKey) {
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
