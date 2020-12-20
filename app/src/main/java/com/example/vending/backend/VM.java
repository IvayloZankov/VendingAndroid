package com.example.vending.backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VM {

    private static ItemData selectedProduct;

    private static List<ItemData> productsStorage;
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
        productsStorage.add(productsStorage.size(), new ItemData(name, price, quantity));
//        new StateSaver().updateProducts(this.products);
    }

    public void loadCoin(String name, double price, int quantity) {
        coinsStorageMachine.loadItem(coinsStorageMachine.getSize() + 1, new ItemData(name, price, quantity));
//        new StateSaver().updateCoins(this.coins);
    }

    public void removeProduct(int position) {
        productsStorage.remove(position);
//        new StateSaver().updateProducts(products);
    }

    public void removeCoin(int position) {
        coinsStorageMachine.removeItem(position);
//        new StateSaver().updateCoins(this.coins);
    }

    public void increaseProductQuantity(int productIndex, int quantity) {
        productsStorage.get(productIndex).increaseQuantity(quantity);
//        new StateSaver().updateProducts(this.products);
    }

    public void increaseCoinQuantity(int coinIndex, int quantity) {
        coinsStorageMachine.getItem(coinIndex).increaseQuantity(quantity);
//        new StateSaver().updateCoins(this.coins);
    }

    public void decreaseProductQuantity(ItemData product) {
        for (int i = 0; i < productsStorage.size(); i++) {
            String name = productsStorage.get(i).getName();
            if (name.equalsIgnoreCase(product.getName())) {
                productsStorage.get(i).decreaseQuantity();
                break;
            }
        }
    }

    public List<ItemData> getProducts() {
        return productsStorage;
    }

    public List<ItemData> getProductsStorage() {
        return productsStorage;
    }

    public int getProductsSize() {
        return productsStorage.size();
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
        productsStorage = new ArrayList<>();
    }

    public void initUserCoinsStorage() {
        coinsStorageUser = new Storage<>();
    }

    public void loadProductsToStorage(JSONArray jsonArray) {
        initProductsStorage();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item;
            try {
                item = jsonArray.getJSONObject(i);
                loadProduct(
                        item.getString("name"),
                        Double.parseDouble(item.getString("price")),
                        Integer.parseInt(item.getString("quantity"))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
