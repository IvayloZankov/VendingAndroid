package com.example.vending.utils;

import com.example.vending.server.ResponseModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the insert and returning coins with successful or cancel order
 */
public class CoinsCounter {

    /**
     * Insert coins method
     */
    public void insertCoin(ResponseModel.Item coin, List<ResponseModel.Item> storage) {
        String selectedCoinName = coin.getName();
        double selectedCoinValue = coin.getPrice();
        boolean isPresent = false;

        int size = storage.size();

        for (int i = 0; i < size; i++) {
            String name = storage.get(i).getName();
            if (coin.getName().equals(name)) {
                storage.get(i).increaseQuantity(1);
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            storage.add(storage.size(), new ResponseModel.Item(selectedCoinName, selectedCoinValue, 1));
        }
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
    public List<ResponseModel.Item> getReturningCoins(String sumInserted, String productPrice, List<ResponseModel.Item> storage) {
        String change = calculateChange(sumInserted, productPrice);
        List<ResponseModel.Item> coinsAsChange = new ArrayList<>();
        BigDecimal calculatedChangeDecimal = new BigDecimal(change);

        while (Double.parseDouble(calculatedChangeDecimal.toString()) > 0.00) {
            int coinIndex = 0;
            for (int i = 0; i < storage.size(); i++) {
                int quantity = storage.get(i).getQuantity();
                double price = storage.get(i).getPrice();
                double changeDouble = Double.parseDouble(calculatedChangeDecimal.toString());
                if (quantity > 0 &&
                        price <= changeDouble &&
                        price > storage.get(coinIndex).getPrice()){
                    coinIndex = i;
                }
            }
            while (storage.get(coinIndex).getPrice() <= Double.parseDouble(calculatedChangeDecimal.toString()) &&
                    storage.get(coinIndex).getQuantity() > 0) {
                storage.get(coinIndex).decreaseQuantity();
                insertCoin(storage.get(coinIndex), coinsAsChange);
                BigDecimal coinValueDecimal = BigDecimal.valueOf(storage.get(coinIndex).getPrice());
                calculatedChangeDecimal = calculatedChangeDecimal.subtract(coinValueDecimal);
            }
        }
        return coinsAsChange;
    }

    /**
     * Adds inserted coins to the coins in the machine
     */
    public void addCoinsToStorage(List<ResponseModel.Item> storageUser, List<ResponseModel.Item> storageMachine) {
        for (int i = 0; i < storageUser.size(); i++) {
            ResponseModel.Item userCoin = storageUser.get(i);
            String tempCoinName = userCoin.getName();
            for (int j = 0; j < storageMachine.size(); j++) {
                ResponseModel.Item coinMachine = storageMachine.get(j);
                String storageCoinName = coinMachine.getName();
                if (tempCoinName.equalsIgnoreCase(storageCoinName)) {
                    coinMachine.increaseQuantity(userCoin.getQuantity());
                    break;
                }
            }
        }
    }
}
