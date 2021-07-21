package com.example.vending.server;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class ServerRequestRx {

    final String BASE_URL = "https://raw.githubusercontent.com/ivailo-zankov/VendingMachineJava/master/files/json/";

    public RxRequest getInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RxRequest.class);
    }

    public interface RxRequest {

        @GET
        Observable<ResponseBody> getHeaders(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @GET
        Flowable<ResponseBody> get(
                @Url String url
        );

        @POST
        Observable<Response<ResponseBody>> post(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @POST
        @FormUrlEncoded
        Observable<Response<ResponseBody>> postParams(
                @Url String url,
                @HeaderMap Map<String, String> headers,
                @FieldMap Map<String, String> params
        );
    }
}
