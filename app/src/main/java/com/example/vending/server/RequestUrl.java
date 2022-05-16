package com.example.vending.server;

public class RequestUrl {
    private static final String PATH = "/vending/";
    static final String GET_PRODUCTS = PATH + "getProducts";
    static final String DECREASE_PRODUCT = PATH + "decreaseProduct";
    static final String RESET_PRODUCTS = PATH + "resetProducts";
    static final String GET_COINS = PATH + "getCoins";
    static final String RESET_COINS = PATH + "resetCoins";
    static final String UPDATE_COINS = PATH + "updateCoins";
}
