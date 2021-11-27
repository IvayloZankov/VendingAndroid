package com.example.vending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.example.vending.server.ResponseModel;
import com.example.vending.server.VendingClient;
import com.example.vending.server.VendingObserver;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class SplashActivity extends AppCompatActivity {

    private final VendingClient client = new VendingClient();
    private final CompositeDisposable bag = new CompositeDisposable();

    private long mLastClickTime = 0;

    private List<ResponseModel.Item> productsList;
    private List<ResponseModel.Item> coinsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.getProducts().subscribe(initProductsObserver());
    }

    @Override
    protected void onDestroy() {
        bag.clear();
        super.onDestroy();
    }

    private VendingObserver<ResponseModel> initProductsObserver() {
        return new VendingObserver<ResponseModel>(bag, this) {
            @Override
            public void onSuccess(@NonNull ResponseModel responseModel) {
                productsList = responseModel.getItems();
                client.getCoins().subscribe(initCoinsObserver());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                setNoConnectionLayout();
            }
        };
    }

    private VendingObserver<ResponseModel> initCoinsObserver() {
        return new VendingObserver<ResponseModel>(bag, this) {
            @Override
            public void onSuccess(@NonNull ResponseModel responseModel) {
                coinsList = responseModel.getItems();
                Intent intent = new Intent(SplashActivity.this, VendingActivity.class);
                intent.putExtra(getString(R.string.products_prefix), new Gson().toJson(productsList));
                intent.putExtra(getString(R.string.coins_prefix), new Gson().toJson(coinsList));
                startActivity(intent);
                finish();
            }
        };
    }

    private void setNoConnectionLayout() {
        ConstraintLayout noConnectionLayout = findViewById(R.id.layoutNoConnection);
        Button retryButton = findViewById(R.id.button_retry);
        noConnectionLayout.setVisibility(View.VISIBLE);
        retryButton.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> v.animate().scaleX(1).scaleY(1).setDuration(100));
            client.getProducts().subscribe(initProductsObserver());
            retryButton.setOnClickListener(null);
        });
    }
}