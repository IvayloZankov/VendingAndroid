package com.example.vending.backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VM {

    private static ItemData selectedProduct;

    private static List<ItemData> productsStorage;
    private static List<ItemData> coinsStorage;
    private static Storage<ItemData> coinsStorageUser;

    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String QUANTITY = "quantity";

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
        coinsStorage.add(coinsStorage.size(), new ItemData(name, price, quantity));
//        new StateSaver().updateCoins(this.coins);
    }

    public void removeProduct(int position) {
        productsStorage.remove(position);
//        new StateSaver().updateProducts(products);
    }

    public void removeCoin(int position) {
        coinsStorage.remove(position);
//        new StateSaver().updateCoins(this.coins);
    }

    public void increaseProductQuantity(int productIndex, int quantity) {
        productsStorage.get(productIndex).increaseQuantity(quantity);
//        new StateSaver().updateProducts(this.products);
    }

    public void increaseCoinQuantity(int coinIndex, int quantity) {
        coinsStorage.get(coinIndex).increaseQuantity(quantity);
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

    public List<ItemData> getCoins() {
        return coinsStorage;
    }

    public Storage<ItemData> getCoinsUser() {
        return coinsStorageUser;
    }

    public List<ItemData> getCoinsStorage() {
        return coinsStorage;
    }

    public int getCoinsSize() {
        return coinsStorage.size();
    }

    /**
     * @return - ten cents coins quantity
     */
    public boolean canReturnChange() {
        return coinsStorage.get(0).getQuantity() > 40;
    }

    public void initProductsStorage() {
        productsStorage = new ArrayList<>();
    }

    public void initCoinsStorage() {
        coinsStorage = new ArrayList<>();
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
//                Log.e("JSON", item.toString());
                loadProduct(
                        item.getString(NAME),
                        Double.parseDouble(item.getString(PRICE)),
                        Integer.parseInt(item.getString(QUANTITY))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadCoinsToStorage(JSONArray jsonArray) {
        initCoinsStorage();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item;
            try {
                item = jsonArray.getJSONObject(i);
//                Log.e("JSON", item.toString());
                loadCoin(
                        item.getString(NAME),
                        Double.parseDouble(item.getString(PRICE)),
                        Integer.parseInt(item.getString(QUANTITY))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<MaintenanceOption> getMaintenanceOptions() {
        return new ArrayList<>(Arrays.asList(MaintenanceOption.values()));
    }
}
