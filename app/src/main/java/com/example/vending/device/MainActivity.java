package com.example.vending.device;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.R;
import com.example.vending.backend.VM;
import com.example.vending.server.JsonUtil;
import com.example.vending.server.RequestUrl;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    NetworkHandler network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VM vm = new VM();
        network = new NetworkHandler(getApplicationContext());
        if (network.isNetworkAvailable()) {
            new InitProducts().execute();
        } else {
            vm.initProductsStorage();
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            network.showNoInternetDialog(this);
        }

        vm.loadCoinsToStorage();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class InitProducts extends AsyncTask<String, String, JSONObject> {
        ProgressDialog loadingDialog;


        @Override
        protected void onPreExecute() {
            loadingDialog = new ProgressDialog(MainActivity.this);
            loadingDialog.setMessage(getResources().getString(R.string.loading_products));
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            return new JsonUtil().getObject(new ServerRequest().getResponse(RequestUrl.GET_PRODUCTS.toString()));
        }

        @Override
        protected void onPostExecute(final JSONObject json) {
            //dismiss dialog
            VM vm = new VM();
            vm.initProductsStorage();
            JSONArray jsonArray = JsonUtil.convertToArray(json, "data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = null;
                try {
                    item = jsonArray.getJSONObject(i);
                    vm.loadProduct(
                            item.getString("name"),
                            Double.parseDouble(item.getString("price")),
                            Integer.parseInt(item.getString("quantity"))
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            loadingDialog.dismiss();
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }
}