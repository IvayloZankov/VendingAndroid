package com.example.vending.server;

public enum RequestUrl {
    GET_PRODUCTS("products.txt"),
    GET_COINS("coins.txt");

    private String string;

    RequestUrl(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
