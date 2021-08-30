package com.example.vending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.example.vending.server.ModelData;
import com.example.vending.server.RequestApi;
import com.example.vending.server.RetrofitVending;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    private final CompositeDisposable bag = new CompositeDisposable();

    private ConstraintLayout noConnectionLayout;
    private Button retryButton;
    private long mLastClickTime = 0;

    private List<ModelData.Item> productsList;
    private List<ModelData.Item> coinsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        noConnectionLayout = findViewById(R.id.layoutNoConnection);
        retryButton = findViewById(R.id.button_retry);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRequest(getString(R.string.request_products));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bag.clear();
    }

    private void initRequest(String request) {
        Retrofit retrofit = new RetrofitVending().getInstance();
        Single<ModelData> single = null;
        Consumer<ModelData> onSuccess = null;
        Consumer<Throwable> onError = throwable -> {
            noConnectionLayout.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> v.animate().scaleX(1).scaleY(1).setDuration(100));
                initRequest(getString(R.string.request_products));
                retryButton.setOnClickListener(null);
            });
            Utils.showNoInternetDialog(SplashActivity.this);
        };
        if (request.equalsIgnoreCase(getString(R.string.request_products))) {
            onSuccess = response -> {
                productsList = response.getItems();
                initRequest(getString(R.string.request_coins));
            };
            single = retrofit.create(RequestApi.class).getProducts();
        } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
            onSuccess = response -> {
                coinsList = response.getItems();
                Intent intent = new Intent(SplashActivity.this, VendingActivity.class);
                intent.putExtra(getString(R.string.products_prefix), new Gson().toJson(productsList));
                intent.putExtra(getString(R.string.coins_prefix), new Gson().toJson(coinsList));
                startActivity(intent);
                finish();
            };
            single = retrofit.create(RequestApi.class).getCoins();
        }
        if (single != null)
        bag.add(single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError));
    }
}