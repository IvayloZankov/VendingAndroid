package com.example.vending.machine;

public class ItemData {
    private String name;
    private double price;
    private int quantity;

    /**
     * Init product
     * @param name product name
     * @param price product price
     */
    public ItemData(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
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
