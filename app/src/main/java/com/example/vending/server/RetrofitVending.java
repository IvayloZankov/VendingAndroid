package com.example.vending.server;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitVending {

    final static String BASE_URL = "https://raw.githubusercontent.com/ivailo-zankov/VendingMachineJava/master/files/json/";
    private static Retrofit rxApi;

    public Retrofit getInstance() {
        if (rxApi == null) {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return rxApi;
    }
}
