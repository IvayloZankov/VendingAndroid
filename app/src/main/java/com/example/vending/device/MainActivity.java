package com.example.vending.device;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.R;
import com.example.vending.backend.VM;
import com.example.vending.server.JsonUtil;
import com.example.vending.server.RequestMethod;
import com.example.vending.server.RequestUrl;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    VM vm;
    NetworkHandler network;

    ProgressDialog loadingDialog;

    ExecutorService executor;

    int counts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ExecutorService executorService = Executors.newFixedThreadPool(2);

        vm = new VM();
        network = new NetworkHandler(getApplicationContext());
        if (network.isNetworkAvailable()) {
            executor = Executors.newSingleThreadExecutor();
//            initLoadingDialog();
            serverRequest(getString(R.string.request_products));
            serverRequest(getString(R.string.request_coins));
        } else {
            vm.initProductsStorage();
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            network.showNoInternetDialog(this);
        }
        vm.initUserCoinsStorage();
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
                JSONArray jsonArray = JsonUtil.convertToArray(json, getString(R.string.request_data));
                if (jsonArray != null) {
                    if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                        vm.loadProductsToStorage(jsonArray);
                    } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                        vm.loadCoinsToStorage(jsonArray);
                        setContentView(R.layout.activity_main);
                        Toolbar toolbar = findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);
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
}