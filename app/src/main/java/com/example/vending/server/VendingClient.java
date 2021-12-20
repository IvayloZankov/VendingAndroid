package com.example.vending.server;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class VendingClient {

    private final VendingApi vendingApi;

    public VendingClient() {
        this.vendingApi = RetrofitClient.getInstance().create(VendingApi.class);
    }

    public Flowable<ResponseModel> getProducts() {
        return subscribe(vendingApi.getProducts());
    }

    public Flowable<ResponseModel> getCoins() {
        return subscribe(vendingApi.getCoins());
    }

    private <T> Flowable<T> subscribe(Flowable<T> flowable) {
        return flowable.subscribeOn(Schedulers.io());
    }
}
