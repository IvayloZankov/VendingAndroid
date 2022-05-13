package com.example.vending;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vending.base.BaseViewModel;
import com.example.vending.server.ResponseModel;
import com.example.vending.utils.CoinsCounter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VendingViewModel extends BaseViewModel {
    private static final String TAG = VendingViewModel.class.getSimpleName();

    private final MutableLiveData<List<ResponseModel.Item>> mLiveDataProducts = new MutableLiveData<>();
    private final MutableLiveData<List<ResponseModel.Item>> mLiveDataCoins = new MutableLiveData<>();
    private final MutableLiveData<String> mLiveDataInsertedAmount = new MutableLiveData<>();
    private List<ResponseModel.Item> mListCoinsUser;
    private List<ResponseModel.Item> mListCoinsForReturn;

    private final CoinsCounter mCoinsCounter = new CoinsCounter();
    private int selectedProduct;

    public VendingViewModel(@NonNull Application application) {
        super(application);
        mLiveDataInsertedAmount.postValue(application.getString(R.string.zero_amount));
        mListCoinsUser = new ArrayList<>();
    }

    public LiveData<List<ResponseModel.Item>> getLiveDataProducts() {
        return mLiveDataProducts;
    }

    public LiveData<List<ResponseModel.Item>> getLiveDataCoins() {
        return mLiveDataCoins;
    }

    public void initProductsRequest() {
        mClient.getProducts().subscribe(new VendingObserver<ResponseModel>() {
            @Override
            public void onSuccess(@NonNull ResponseModel responseModel) {
                super.onSuccess(responseModel);
                handleResponse(responseModel, response ->
                    mLiveDataProducts.setValue(responseModel.getItems()));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public void initCoinsRequest() {
        mClient.getCoins().subscribe(new VendingObserver<ResponseModel>() {
            @Override
            public void onSuccess(@NonNull ResponseModel responseModel) {
                handleResponse(responseModel, response ->
                    mLiveDataCoins.setValue(responseModel.getItems()));
                Log.e(TAG, String.valueOf(responseModel.getItems()));
            }
        });
    }

    public void initUpdateCoinsRequest() {
        mClient.updateCoins(mLiveDataCoins.getValue()).subscribe(new VendingObserver<ResponseModel>() {});
    }

    public void initResetCoinsRequest() {
        mClient.resetCoins().subscribe(new VendingObserver<ResponseModel>() {});
    }

    public void initRemoveProductRequest() {
        HashMap<String, String> params = new HashMap<String, String>(){{
            put("id", String.valueOf(selectedProduct));
        }};
        Log.e(TAG, String.valueOf(params));
        mClient.decreaseProducts(params).subscribe(new VendingObserver<ResponseModel>() {
            @Override
            public void onSuccess(@NonNull ResponseModel responseModel) {
                handleResponse(responseModel, response ->
                    mLiveDataProducts.setValue(responseModel.getItems()));
            }
        });
    }

    public void initResetProductsRequest() {
        mClient.resetProducts().subscribe(new VendingObserver<ResponseModel>() {});
    }

    public ResponseModel.Item getSelectedProduct() {
        return mLiveDataProducts.getValue() != null ? mLiveDataProducts.getValue().get(selectedProduct) : null;
    }

    public void setSelectedProduct(int selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public LiveData<String> getInsertedAmount() {
        return mLiveDataInsertedAmount;
    }

    public void updateInsertedAmount(ResponseModel.Item item) {
        String value = mLiveDataInsertedAmount.getValue();
        value = new BigDecimal(value).add(BigDecimal.valueOf(item.getPrice())).toString();
        mLiveDataInsertedAmount.postValue(value);
        mCoinsCounter.insertCoin(item, mListCoinsUser);
    }

    public void addUserCoinsToMachine() {
        List<ResponseModel.Item> listCoins = mLiveDataCoins.getValue();
        mCoinsCounter.addCoinsToStorage(mListCoinsUser, listCoins);
        mListCoinsUser = new ArrayList<>();
        mLiveDataCoins.setValue(listCoins);
    }

    public List<ResponseModel.Item> getUserCoins() {
        return mListCoinsUser;
    }

    public void resetUserCoins() {
        mListCoinsUser = new ArrayList<>();
        mListCoinsForReturn = new ArrayList<>();
        mLiveDataInsertedAmount.postValue(getApplication().getString(R.string.zero_amount));
    }

    public void prepareReturningCoins() {
        List<ResponseModel.Item> listCoins = mLiveDataCoins.getValue();
        String productPrice = String.format(Locale.CANADA, "%.2f", getSelectedProduct().getPrice());
        mListCoinsForReturn = mCoinsCounter.getReturningCoins(mLiveDataInsertedAmount.getValue(), productPrice, listCoins);
        mLiveDataCoins.setValue(listCoins);
    }

    public List<ResponseModel.Item> getCoinsForReturn() {
        return mListCoinsForReturn;
    }
}
