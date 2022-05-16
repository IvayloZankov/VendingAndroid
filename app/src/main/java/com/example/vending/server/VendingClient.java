package com.example.vending.server;

import com.example.vending.server.response.ResponseModel;

import java.util.HashMap;
import java.util.List;

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

    public Single<ResponseModel> decreaseProducts(HashMap<String, String> params) {
        return subscribe(vendingApi.decreaseProducts(params));
    }

    public Single<ResponseModel> resetProducts() {
        return subscribe(vendingApi.resetProducts());
    }

    public Single<ResponseModel> getCoins() {
        return subscribe(vendingApi.getCoins());
    }

    public Single<ResponseModel> updateCoins(List<ResponseModel.Item> list) {
        return subscribe(vendingApi.updateCoins(list));
    }

    public Single<ResponseModel> resetCoins() {
        return subscribe(vendingApi.resetCoins());
    }

    private <T> Single<T> subscribe(Single<T> single) {
        return single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
