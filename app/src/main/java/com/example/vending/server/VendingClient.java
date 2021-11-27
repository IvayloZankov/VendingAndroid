package com.example.vending.server;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VendingClient {

    private final VendingApi vendingApi;

    public VendingClient() {
        this.vendingApi = RetrofitClient.getInstance().create(VendingApi.class);
    }

    public Single<ResponseModel> getProducts() {
        return subscribe(vendingApi.getProducts());
    }

    public Single<ResponseModel> getCoins() {
        return subscribe(vendingApi.getCoins());
    }

    private <T> Single<T> subscribe(Single<T> single) {
        return single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
