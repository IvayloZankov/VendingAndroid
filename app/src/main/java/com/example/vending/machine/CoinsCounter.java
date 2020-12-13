package com.example.vending.machine;


import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Class that handles the insert and returning coins with successful or cancel order
 */
public class CoinsCounter {

    public CoinsCounter() {
    }

//    private double insertedCoinsAmountTemp = 0.00;
//    private ArrayList<ItemData> coinsTemp = new ArrayList<>();

    /**
     * Insert coins method
     */
    public void insertCoin(ItemData coin, Storage<ItemData> storage) {
        String selectedCoinName = coin.getName();
        double selectedCoinValue = coin.getPrice();
        boolean isPresent = false;

        int size = storage.getSize();

        for (int i = 0; i < size; i++) {
            String name = storage.getItem(i).getName();
            if (coin.getName().equals(name)) {
                storage.getItem(i).increaseQuantity(1);
                isPresent = true;
                break;
            }
        }
//        for (Map<Integer, ItemData> coinTemp : storage) {
//            if (coin.getName().equals(selectedCoinName)) {
//                coin.increaseQuantity(1);
//                isPresent = true;
//            }
//        }
        if (!isPresent) {
//            int coinPlace = 0;
//            if (storage.getSize() > 0) {
//                coinPlace = storage.getSize() + 1;
//            }
            storage.loadItem(storage.getSize(), new ItemData(selectedCoinName, selectedCoinValue, 1));
        }
//        insertedCoinsAmountTemp = getSumValue(insertedCoinsAmountTemp, selectedCoinValue);
//        return insertedCoinsAmountTemp;
    }

    /**
     * Method adding the new coin value to the coins amount
     *
     * @param insertedCoinsAmountTemp - the amount of coins
     * @param selectedCoinValue   - the coin value in string format
     * @return - the new coins amount
     */
    private double getSumValue(double insertedCoinsAmountTemp, double selectedCoinValue) {
        BigDecimal sumDeci = new BigDecimal(String.valueOf(insertedCoinsAmountTemp));
        BigDecimal coinValueDeci = new BigDecimal(String.valueOf(selectedCoinValue));
        BigDecimal bSum = sumDeci.add(coinValueDeci);
        return Double.parseDouble(bSum.toString());
    }

    /**
     * Calculates the change
     *
     * @param sumCoinsInserted - the sum of the inserted coins
     * @param productPrice     - the product price
     * @return - the change amount
     */
    public String calculateChange(String sumCoinsInserted, String productPrice) {
        return new BigDecimal(sumCoinsInserted).subtract(new BigDecimal(productPrice)).toString();
    }

    /**
     * Method to calculate the coins needed for change in descending order
     * @return - returned coins as String
     */
    public Storage<ItemData> calculateReturningCoins(String sumInserted, String productPrice, Storage<ItemData> storage) {
        String change = calculateChange(sumInserted, productPrice);
        Storage<ItemData> coinsAsChange = new Storage<>();
        BigDecimal calculatedChangeDecimal = new BigDecimal(change);

        while (Double.parseDouble(calculatedChangeDecimal.toString()) > 0.00) {
//            getHighCoinValueAvailable();
            int coinIndex = 0;
            for (int i = 0; i < storage.getSize(); i++) {
                int quantity = storage.getItem(i).getQuantity();
                double price = storage.getItem(i).getPrice();
                double changeDouble = Double.parseDouble(calculatedChangeDecimal.toString());
                if (quantity > 0 &&
                        price <= changeDouble &&
                        price > storage.getItem(coinIndex).getPrice()){
                    coinIndex = i;
                }
            }
//            int quantityTemp = 0;
            while (storage.getItem(coinIndex).getPrice() <= Double.parseDouble(calculatedChangeDecimal.toString()) &&
                    storage.getItem(coinIndex).getQuantity() > 0) {
                storage.getItem(coinIndex).decreaseQuantity();
                insertCoin(storage.getItem(coinIndex), coinsAsChange);
//                quantityTemp ++;
                BigDecimal coinValueDecimal = BigDecimal.valueOf(storage.getItem(coinIndex).getPrice());
                calculatedChangeDecimal = calculatedChangeDecimal.subtract(coinValueDecimal);
            }
        }
        return coinsAsChange;
//        new StateSaver().updateCoins(coins);
    }

//    private ItemData getHighCoinValueAvailable(Storage<ItemData> storage) {
//        ItemData item
//        for (int i = 0; i < storage.getSize(); i++) {
//            if (storage.getItem(i).get)
//        }
//    }

    /**
     * Adds inserted coins to the coins in the machine
     */
    public void addCoinsToStorage(Storage<ItemData> storageUser, Storage<ItemData> storageMachine) {
        for (int i = 0; i < storageUser.getSize(); i++) {
            String tempCoinName = storageUser.getItem(i).getName();
            for (int j = 0; j < storageMachine.getSize(); j++) {
                String storageCoinName = storageMachine.getItem(i).getName();
                if (tempCoinName.equalsIgnoreCase(storageCoinName)) {
                    storageMachine.getItem(i).increaseQuantity(1);
                    break;
                }
            }
        }
//        new StateSaver().updateCoins(coins);

//        storageMachine = new Storage<>();
    }

//    /**
//     * Returns inserted coins to the user and reset
//     */
//    public void returnInsertedCoins() {
//        if (coinsTemp.getSize() > 0) {
//            //update to visual
//
//
////            for (CoinsData coin : coinsTemp) {
////                System.out.print("(" + coin.getQuantity() + ")" + " " + coin.getName() + " ");
////            }
////            System.out.println();
//            coinsTemp = new Storage<>();
//        }
//    }
}
