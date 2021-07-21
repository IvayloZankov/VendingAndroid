package com.example.vending.server;

import android.database.Observable;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class ServerRequest {

    final String BASE_URL = "https://raw.githubusercontent.com/ivailo-zankov/VendingMachineJava/master/files/json/";

    public JSONObject getResponse(
            RequestMethod reqMethod,
            String url,
            Map<String, String> headers,
            Map<String, String> params) {

        try {
            String reqUrl = BASE_URL + url;

            Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ResponseBody body;
            if (reqMethod == RequestMethod.POST) {
                if (params != null) {
                    body = retrofit.create(Request.class).postParams(reqUrl, headers, params).execute().body();
                } else {
                    body = retrofit.create(Request.class).post(reqUrl, headers).execute().body();
                }
            } else {
                if (headers != null) {
                    body = retrofit.create(Request.class).getHeaders(reqUrl, headers).execute().body();
                } else {
                    body = retrofit.create(Request.class).get(reqUrl).execute().body();
                }
            }
            if (body != null)
                return new JSONObject(body.string());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public JSONObject getResponse(RequestMethod reqMethod, String requestUrl, Map<String, String> headers) {
        return this.getResponse(reqMethod, requestUrl, headers, null);
    }

    public JSONObject getResponse(RequestMethod reqMethod, String requestUrl) {
        return this.getResponse(reqMethod, requestUrl, null, null);
    }

    private interface Request {
        @GET
        Call<ResponseBody> getHeaders(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @GET
        Call<ResponseBody> get(
                @Url String url
        );

        @POST
        Call<ResponseBody> post(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @POST
        @FormUrlEncoded
        Call<ResponseBody> postParams(
                @Url String url,
                @HeaderMap Map<String, String> headers,
                @FieldMap Map<String, String> params
        );
    }
}
