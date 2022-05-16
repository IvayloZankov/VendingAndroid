package com.example.vending.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vending.server.ResponseEvent;
import com.example.vending.server.response.ResponseModel;
import com.example.vending.server.VendingClient;

import java.net.ConnectException;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel {

    protected MutableLiveData<ResponseEvent<Throwable>> mLiveDataOnError = new MutableLiveData<>();
    protected MutableLiveData<Boolean> mLiveDataNoConnection = new MutableLiveData<>();
    protected MutableLiveData<ResponseEvent<ResponseModel>> mLiveDataServerError = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> mLiveDataLoading = new MutableLiveData<>();

    protected final VendingClient mClient = new VendingClient();
    protected final CompositeDisposable mBag = new CompositeDisposable();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    protected LiveData<ResponseEvent<Throwable>> getOnErrorLiveData() {
        return mLiveDataOnError;
    }

    public LiveData<ResponseEvent<ResponseModel>> getServerErrorLiveData() {
        return mLiveDataServerError;
    }

    public LiveData<Boolean> getLiveDataLoading() {
        return mLiveDataLoading;
    }

    public LiveData<Boolean> getLiveDataNoConnection() {
        return mLiveDataNoConnection;
    }

    public void setNoConnection(boolean noConnection) {
        mLiveDataNoConnection.setValue(noConnection);
    }

    /**
     * Method to handle server response with error body
     * @param response server response
     * @param funcSuccess function if server response is OK
     * @param funcError function if there is an error
     * @param <R> extended response class.
     */
    protected <R extends ResponseModel> void handleResponse(
            @Nullable R response,
            @Nullable Consumer<R> funcSuccess,
            @Nullable Consumer<R> funcError) {
        if (response != null) {
            if (response.success) {
                if (funcSuccess != null)
                    funcSuccess.accept(response);
            } else {
                if (funcError != null)
                    funcError.accept(response);
            }
        }
    }

    /**
     * Handles server response. Shows default server error alert.
     * @param response server response
     * @param funcSuccess function if server response is OK
     * @param <R> extended response class.
     */
    public <R extends ResponseModel> void handleResponse(
            @Nullable R response,
            @Nullable Consumer<R> funcSuccess) {
        this.handleResponse(response, funcSuccess, responseModel ->
                mLiveDataServerError.setValue(new ResponseEvent<>(responseModel)));
    }

    @Override
    protected void onCleared() {
        mBag.clear();
        super.onCleared();
    }

    /**
     * Main DskSmart observer class. Override onSubscribe and onError for custom behaviour.
     * @param <R> extended response class.
     */
    public abstract class VendingObserver<R extends ResponseModel> implements SingleObserver<R> {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            mLiveDataLoading.setValue(true);
            mBag.add(d);
        }

        @Override
        public void onSuccess(@NonNull R r) {
            mLiveDataLoading.setValue(false);
        }

        @Override
        public void onError(Throwable e) {
            mLiveDataLoading.setValue(false);
            e.printStackTrace();
            if (!(e instanceof ConnectException)) {
                mLiveDataOnError.setValue(new ResponseEvent<>(e));
            } else {
                mLiveDataNoConnection.setValue(true);
            }
        }
    }
}
