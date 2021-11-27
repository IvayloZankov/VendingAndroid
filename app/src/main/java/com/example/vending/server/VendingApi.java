package com.example.vending.server;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface VendingApi {

    String PRODUCTS_URL = "products.txt";
    String COINS_URL = "coins.txt";

    @GET(PRODUCTS_URL)
    Single<ResponseModel> getProducts();

    @GET(COINS_URL)
    Single<ResponseModel> getCoins();

    @POST
    Single<ResponseBody> post(
            @Url String url,
            @HeaderMap Map<String, String> headers
    );

    @POST
    @FormUrlEncoded
    Single<ResponseBody> postParams(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @FieldMap Map<String, String> params
    );
}
