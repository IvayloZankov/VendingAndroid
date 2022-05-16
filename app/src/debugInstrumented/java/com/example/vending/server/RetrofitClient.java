package com.example.vending.server;

import static com.example.vending.server.RequestUrl.*;

import com.example.vending.BuildConfig;
import com.example.vending.server.response.ResponseModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    final static String BASE_URL = "https://zankov.dev/vending/";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new FakeInterceptor()).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static class FakeInterceptor implements Interceptor {
        @NotNull
        @Override
        public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
            okhttp3.Response response;
            if (BuildConfig.DEBUG) {
                final URI uri = chain.request().url().uri();
                final String requestUrl = uri.getPath();
                response = new Response.Builder()
                        .code(200)
                        .message("ok")
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(
                                FakeResponse.getFakeResponse(requestUrl).getBytes(StandardCharsets.UTF_8),
                                MediaType.parse("application/json")))
                        .addHeader("content-type", "application/json")
                        .build();
            }
            else {
                response = chain.proceed(chain.request());
            }
            return response;
        }
    }

    private static class FakeResponse {

        public static String getFakeResponse(String request) {
            ArrayList<ResponseModel.Item> items = new ArrayList<>();
            switch (request) {
                case GET_PRODUCTS:
                    items.add(new ResponseModel.Item(0, "Water", 0.9, 0));
                    items.add(new ResponseModel.Item(1, "Coke", 1.1, 1));
                    items.add(new ResponseModel.Item(2, "Croissant", 0.7, 1));
                    items.add(new ResponseModel.Item(3, "Mars", 0.5, 1));
                    String json = new Gson().toJson(new ResponseModel(true, "ok", items));
                    return json;
                case GET_COINS:
                    items.add(new ResponseModel.Item(0, "five_cents", 0.05, 39));
                    items.add(new ResponseModel.Item(1, "ten_cents", 0.1, 10));
                    items.add(new ResponseModel.Item(2, "twenty_cents", 0.2, 8));
                    items.add(new ResponseModel.Item(3, "fifty_cents", 0.5, 5));
                    items.add(new ResponseModel.Item(4, "one_eur", 1, 2));
                    items.add(new ResponseModel.Item(5, "two_eur", 2, 0));
                    return new Gson().toJson(new ResponseModel(true, "ok", items));
                default:
                    return "";
            }
        }
    }
}
