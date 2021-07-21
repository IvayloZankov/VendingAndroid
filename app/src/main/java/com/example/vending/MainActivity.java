package com.example.vending;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<ItemData> productsStorage;
    public List<ItemData> coinsStorage;

    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        productsStorage = new ArrayList<>();
        coinsStorage = new ArrayList<>();
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
            //TODO button at maintenance fragment
            Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            NavHostFragment.findNavController(primaryNavigationFragment)
                    .navigate(R.id.action_MaintenanceFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.loading_products));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public void loadProductsToStorage(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item;
            try {
                item = jsonArray.getJSONObject(i);
                productsStorage.add(
                        productsStorage.size(),
                        new ItemData(item.getString(getString(R.string.item_name_key)),
                        Double.parseDouble(item.getString(getString(R.string.item_price_key))),
                        Integer.parseInt(item.getString(getString(R.string.item_quantity_key))))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadCoinsToStorage(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item;
            try {
                item = jsonArray.getJSONObject(i);
                coinsStorage.add(
                        coinsStorage.size(),
                        new ItemData(item.getString(getString(R.string.item_name_key)),
                        Double.parseDouble(item.getString(getString(R.string.item_price_key))),
                        Integer.parseInt(item.getString(getString(R.string.item_quantity_key))))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void decreaseProductQuantity(int pos) {
        productsStorage.get(pos).decreaseQuantity();
    }

    public List<ItemData> getProducts() {
        return productsStorage;
    }

    public List<ItemData> getCoins() {
        return coinsStorage;
    }
}