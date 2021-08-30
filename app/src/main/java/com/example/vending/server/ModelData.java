package com.example.vending.server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelData {

    @SerializedName("data")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private String name;
        private double price;
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
    }
}
