package com.example.vending.server;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class ServerRequest {

    final String BASE_URL = "https://raw.githubusercontent.com/ivailo-zankov/VendingMachineJava/master/files/json/";

    public InputStream getResponse(String requestUrl, Map<String, String> headers, Map<String, String> params) {
        String requestMethod;

        HttpsURLConnection connection;

        try {
            URL url = new URL(BASE_URL + requestUrl);
            connection = (HttpsURLConnection) url.openConnection();

            int code = connection.getResponseCode();
            if (code !=  200) {
                throw new IOException("Invalid response from server: " + code);
            }

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (params != null) {
                requestMethod = RequestMethod.POST.toString();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            } else {
                requestMethod = RequestMethod.GET.toString();
            }
            connection.setRequestMethod(requestMethod);
            connection.connect();
            return new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
        }
    }

    public InputStream getResponse(String requestUrl, Map<String, String> headers) {
        return this.getResponse(requestUrl, headers, null);
    }

    public InputStream getResponse(String requestUrl) {
        return this.getResponse(requestUrl, null, null);
    }
}
