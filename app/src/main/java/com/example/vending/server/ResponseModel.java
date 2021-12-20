package com.example.vending.server;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {

    @SerializedName("data")
    private List<Item> items;

    private final Throwable error;

    public ResponseModel(Throwable error) {
        this.error = error;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private final String name;
        private final double price;
        private int quantity;

        public Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
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

    public Throwable getError() {
        return error;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseModel{" +
                "items=" + items +
                '}';
    }
}
