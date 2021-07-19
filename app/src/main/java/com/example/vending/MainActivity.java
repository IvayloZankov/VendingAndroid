package com.example.vending;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.server.Utils;
import com.example.vending.server.RequestMethod;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public List<ItemData> productsStorage;
    public List<ItemData> coinsStorage;
    NetworkHandler network;

    ProgressDialog loadingDialog;

    ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        productsStorage = new ArrayList<>();
        coinsStorage = new ArrayList<>();

//        ExecutorService executorService = Executors.newFixedThreadPool(2);

        network = new NetworkHandler(getApplicationContext());
        if (network.isNetworkAvailable()) {
            executor = Executors.newSingleThreadExecutor();
//            initLoadingDialog();
            serverRequest(getString(R.string.request_products));
        } else {
            network.showNoInternetDialog(this);
        }
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

    private void serverRequest(String request) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(getString(R.string.request_items));

                JSONObject json = null;
                try {
                    json = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = Utils.extractJsonArray(json, getString(R.string.request_data));
                if (jsonArray != null) {
                    if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                        loadProductsToStorage(jsonArray);
                        serverRequest(getString(R.string.request_coins));
                    } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                        loadCoinsToStorage(jsonArray);
                        Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                        NavHostFragment.findNavController(primaryNavigationFragment)
                                .navigate(R.id.action_SplashFragment_to_ProductsFragment);
                        executor.shutdown();
                    }
                }
            }
        };

        Runnable runnable = new Runnable() {
            public void run() {
                JSONObject json = null;
                if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                    json = new ServerRequest().getResponse(RequestMethod.GET, request);
                } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                    json = new ServerRequest().getResponse(RequestMethod.GET, request);
                }
                if (json != null) {
                    String jsonString = json.toString();
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.request_items), jsonString);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        };
        executor.submit(runnable);
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
//                Log.e("JSON", item.toString());
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
//                Log.e("JSON", item.toString());
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