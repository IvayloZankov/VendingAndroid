package com.example.vending.server;

import static com.example.vending.server.RequestUrl.*;

import com.example.vending.server.response.ResponseModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface VendingApi {

    @GET(GET_PRODUCTS)
    Single<ResponseModel> getProducts();

    @FormUrlEncoded
    @POST(DECREASE_PRODUCT)
    Single<ResponseModel> decreaseProducts(@FieldMap Map<String, String> params);

    @GET(RESET_PRODUCTS)
    Single<ResponseModel> resetProducts();

    @GET(GET_COINS)
    Single<ResponseModel> getCoins();

    @POST(UPDATE_COINS)
    Single<ResponseModel> updateCoins(@Body List<ResponseModel.Item> list);

    @GET(RESET_COINS)
    Single<ResponseModel> resetCoins();
}
