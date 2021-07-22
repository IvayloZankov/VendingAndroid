package com.example.vending.server;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class ClientRx {

    final static String BASE_URL = "https://raw.githubusercontent.com/ivailo-zankov/VendingMachineJava/master/files/json/";
    final Request rxApi = null;

    private Request getInstance() {
        if (rxApi == null) {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(Request.class);
        } else return rxApi;
    }

    private interface Request {

        @GET
        Flowable<ResponseBody> getHeaders(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @GET
        Flowable<ResponseBody> get(
                @Url String url
        );

        @POST
        Flowable<ResponseBody> post(
                @Url String url,
                @HeaderMap Map<String, String> headers
        );

        @POST
        @FormUrlEncoded
        Flowable<ResponseBody> postParams(
                @Url String url,
                @HeaderMap Map<String, String> headers,
                @FieldMap Map<String, String> params
        );
    }

    public void observe(String url, Observer<ResponseBody> observer) {
        getInstance().get(url).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
