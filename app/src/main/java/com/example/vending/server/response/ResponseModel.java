package com.example.vending.server.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ResponseModel {

    @SerializedName("success")
    public boolean success;
    @SerializedName("message")
    public String message = "";
    @SerializedName("data")
    public List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public ResponseModel(boolean success, String message, List<Item> items) {
        this.success = success;
        this.message = message;
        this.items = items;
    }

    public static class Item {
        private final int id;
        private final String name;
        private final double price;
        private int quantity;

        public Item(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void decreaseQuantity() {
            this.quantity--;
        }

        public void increaseQuantity(int quantity) {
            this.quantity += quantity;
        }

        @NonNull
        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseModel{" +
                "items=" + items +
                '}';
    }
}
