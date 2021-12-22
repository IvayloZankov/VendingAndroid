package com.example.vending;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.example.vending.server.ResponseModel;
import com.example.vending.server.VendingClient;
import com.example.vending.utils.CoinsCounter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

public class VendingViewModel extends AndroidViewModel {
    private static final String TAG = VendingViewModel.class.getSimpleName();

    private final MutableLiveData<List<ResponseModel.Item>> mLiveDataProducts = new MutableLiveData<>();
    private final MutableLiveData<List<ResponseModel.Item>> mLiveDataCoins = new MutableLiveData<>();
    private final MutableLiveData<String> mLiveDataInsertedAmount = new MutableLiveData<>();
    private List<ResponseModel.Item> mListCoinsUser;
    private List<ResponseModel.Item> mListCoinsForReturn;

    private final CoinsCounter mCoinsCounter = new CoinsCounter();
    private final VendingClient mClient = new VendingClient();
    private final CompositeDisposable mBag = new CompositeDisposable();
    private int selectedProduct;
    private boolean areProductsLoaded;
    private boolean areCoinsLoaded;

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

    public void updateLiveDataProducts(List<ResponseModel.Item> list) {
        mLiveDataProducts.postValue(list);
    }

    public void updateLiveDataCoins(List<ResponseModel.Item> list) {
        mLiveDataCoins.postValue(list);
    }

    public LiveData<ResponseModel> makeProductsRequest() {
        return LiveDataReactiveStreams.fromPublisher(
                mClient.getProducts().onErrorReturn(ResponseModel::new)
        );
    }

    public LiveData<ResponseModel> makeCoinsRequest() {
        return LiveDataReactiveStreams.fromPublisher(
                mClient.getCoins().onErrorReturn(ResponseModel::new)
        );
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

    public void removeProduct() {
        List<ResponseModel.Item> value = mLiveDataProducts.getValue();
        if (value != null) value.get(selectedProduct).decreaseQuantity();
    }

    public void addUserCoinsToMachine() {
        List<ResponseModel.Item> listCoins = mLiveDataCoins.getValue();
        mCoinsCounter.addCoinsToStorage(mListCoinsUser, listCoins);
        mListCoinsUser = new ArrayList<>();
        mLiveDataCoins.postValue(listCoins);
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
        mLiveDataCoins.postValue(listCoins);
    }

    public List<ResponseModel.Item> getCoinsForReturn() {
        return mListCoinsForReturn;
    }

    public void setProductsLoaded(boolean areProductsLoaded) {
        this.areProductsLoaded = areProductsLoaded;
    }

    public void setCoinsLoaded(boolean areCoinsLoaded) {
        this.areCoinsLoaded = areCoinsLoaded;
    }

    public boolean isDataFetched() {
        return areProductsLoaded && areCoinsLoaded;
    }

    @Override
    protected void onCleared() {
        mBag.clear();
        super.onCleared();
    }
}
