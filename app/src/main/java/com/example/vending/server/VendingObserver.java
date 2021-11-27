package com.example.vending.server;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.vending.Utils;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class VendingObserver<T> implements SingleObserver<T> {

    private final CompositeDisposable mBag;
    private final Context mContext;

    public VendingObserver(CompositeDisposable bag, Context context) {
        mBag = bag;
        mContext = context;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mBag.add(d);
    }

    @Override
    public void onSuccess(@NonNull T t) {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        Utils.showNoInternetDialog(mContext);
    }
}
