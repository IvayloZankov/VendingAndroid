package com.example.vending.device;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.backend.ItemData;
import com.example.vending.backend.Storage;
import com.example.vending.backend.VM;
import com.example.vending.server.JsonUtil;
import com.example.vending.server.RequestUrl;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class ProductsFragment extends Fragment {

    RecyclerView productsList;
    ProductsRecyclerViewAdapter adapter;

    VM vm;
    NetworkHandler network;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new VM();
        network = new NetworkHandler(getContext());

        handleBackButton();

        productsList = view.findViewById(R.id.products_list);
        adapter = new ProductsRecyclerViewAdapter(vm.getProducts());
        productsList.setAdapter(adapter);

        if (!vm.canReturnChange()) {
            showOutOfOrderAlert();
        }

        view.findViewById(R.id.button_reset_products).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (network.isNetworkAvailable()) {
                    new ReloadProducts().execute();
                } else {
                    network.showNoInternetDialog(getContext());
                }
            }
        });
    }

    private void showOutOfOrderAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_title_out_of_order);
        alertDialogBuilder.setMessage(R.string.alert_text_enter_maintenance);
        alertDialogBuilder.setPositiveButton(R.string.alert_out_of_order_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new VM().loadCoinsToStorage();
            }
                })
        .setCancelable(false).show();
    }

    private class ReloadProducts extends AsyncTask<String, String, JSONObject> {

        ProgressDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = new ProgressDialog(getContext());
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
            vm.initProductsStorage();
            JSONArray jsonArray = JsonUtil.convertToArray(json, "data");
            vm.loadProductsToStorage(jsonArray);
            adapter.refreshScreen(vm.getProducts());
            loadingDialog.dismiss();
        }
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    getActivity().finish();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(getContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}