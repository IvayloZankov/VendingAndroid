package com.example.vending.backend;

import java.util.Map;

public class VM {

    private static ItemData selectedProduct;

    private static Storage<ItemData> productsStorage;
    private static Storage<ItemData> coinsStorageMachine;
    private static Storage<ItemData> coinsStorageUser;

    public VM() {
    }

    public ItemData getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(ItemData product) {
        selectedProduct = product;
    }

    public void loadProduct(String name, double price, int quantity) {
        productsStorage.loadItem(productsStorage.getSize(), new ItemData(name, price, quantity));
//        new StateSaver().updateProducts(this.products);
    }

    public void loadCoin(String name, double price, int quantity) {
        coinsStorageMachine.loadItem(coinsStorageMachine.getSize() + 1, new ItemData(name, price, quantity));
//        new StateSaver().updateCoins(this.coins);
    }

    public void removeProduct(int position) {
        productsStorage.removeItem(position);
//        new StateSaver().updateProducts(products);
    }

    public void removeCoin(int position) {
        coinsStorageMachine.removeItem(position);
//        new StateSaver().updateCoins(this.coins);
    }

    public void increaseProductQuantity(int productIndex, int quantity) {
        productsStorage.getItem(productIndex).increaseQuantity(quantity);
//        new StateSaver().updateProducts(this.products);
    }

    public void increaseCoinQuantity(int coinIndex, int quantity) {
        coinsStorageMachine.getItem(coinIndex).increaseQuantity(quantity);
//        new StateSaver().updateCoins(this.coins);
    }

    public void decreaseProductQuantity(ItemData product) {
        for (int i = 0; i < productsStorage.getSize(); i++) {
            String name = productsStorage.getItem(i).getName();
            if (name.equalsIgnoreCase(product.getName())) {
                productsStorage.getItem(i).decreaseQuantity();
                break;
            }
        }
    }

    public Storage<ItemData> getProducts() {
        return productsStorage;
    }

    public Map<Integer, ItemData> getProductsStorage() {
        return productsStorage.getStorage();
    }

    public int getProductsSize() {
        return productsStorage.getSize();
    }

    public Storage<ItemData> getMachineCoins() {
        return coinsStorageMachine;
    }

    public Storage<ItemData> getUserCoins() {
        return coinsStorageUser;
    }

    public Map<Integer, ItemData> getCoinsStorage() {
        return coinsStorageMachine.getStorage();
    }

    public int getCoinsSize() {
        return coinsStorageMachine.getSize();
    }

    /**
     * @return - ten cents coins quantity
     */
    public boolean canReturnChange() {
        return coinsStorageMachine.getStorage().get(0).getQuantity() > 40;
    }

    public void initProductsStorage() {
        productsStorage = new Storage<>();
    }

    public void initUserCoinsStorage() {
        coinsStorageUser = new Storage<>();
    }

    public void loadProductsToStorage() {
        initProductsStorage();
        productsStorage.loadItem(0, new ItemData("Coca-Cola", 0.60, 3));
        productsStorage.loadItem(1, new ItemData("Mars", 1.10, 3));
        productsStorage.loadItem(2, new ItemData("Water", 0.60, 3));
        productsStorage.loadItem(3, new ItemData("Croissant", 0.50, 3));
        productsStorage.loadItem(4, new ItemData("Snickers", 0.60, 3));
        productsStorage.loadItem(5, new ItemData("Orange Juice", 0.40, 3));
        productsStorage.loadItem(6, new ItemData("Almonds", 1.40, 3));
        productsStorage.loadItem(7, new ItemData("Peanuts", 0.90, 3));
        productsStorage.loadItem(8, new ItemData("Chips", 0.70, 3));
        productsStorage.loadItem(9, new ItemData("Chocolate", 1.20, 3));
    }

    public void loadCoinsToStorage() {
        coinsStorageMachine = new Storage<>();
        coinsStorageMachine.loadItem(0, new ItemData("five_cents", 0.05, 60));
        coinsStorageMachine.loadItem(1, new ItemData("ten_cents", 0.10, 40));
        coinsStorageMachine.loadItem(2, new ItemData("twenty_cents", 0.20, 20));
        coinsStorageMachine.loadItem(3, new ItemData("fifty_cents", 0.50, 10));
        coinsStorageMachine.loadItem(4, new ItemData("one_eur", 1.00, 5));
        coinsStorageMachine.loadItem(5, new ItemData("two_eur", 2.00, 0));
    }
}
