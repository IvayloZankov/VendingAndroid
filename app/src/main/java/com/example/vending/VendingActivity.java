package com.example.vending;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.server.ModelData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VendingActivity extends AppCompatActivity {

    public List<ModelData.Item> productsStorage;
    public List<ModelData.Item> coinsStorage;

    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsStorage = new ArrayList<>();
        coinsStorage = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loadProductsToStorage(extras.getString(getString(R.string.products_prefix)));
            loadCoinsToStorage(extras.getString(getString(R.string.coins_prefix)));
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_maintenance) {
            Utils.playSound(this, R.raw.click_default);
            //TODO button at maintenance fragment
            Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            NavHostFragment.findNavController(primaryNavigationFragment)
                    .navigate(R.id.action_MaintenanceFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(VendingActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.loading_products));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public void loadProductsToStorage(String products) {
        Type typeToken = new TypeToken<ArrayList<ModelData.Item>>() {}.getType();
        productsStorage = new Gson().fromJson(products, typeToken);
    }

    public void loadCoinsToStorage(String coins) {
        Type typeToken = new TypeToken<ArrayList<ModelData.Item>>() {}.getType();
        coinsStorage = new Gson().fromJson(coins, typeToken);
    }

    public void decreaseProductQuantity(int pos) {
        productsStorage.get(pos).decreaseQuantity();
    }

    public List<ModelData.Item> getProducts() {
        return productsStorage;
    }

    public List<ModelData.Item> getCoins() {
        return coinsStorage;
    }
}