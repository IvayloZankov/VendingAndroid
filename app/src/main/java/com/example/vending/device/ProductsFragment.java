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

import java.util.Locale;

public class ProductsFragment extends Fragment {

    ViewGroup productsList;
    VM vm;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBackButton();

        productsList = view.findViewById(R.id.products_list);
        vm = new VM();

        if (!vm.canReturnChange()) {
            showOutOfOrderAlert();
        }

        Storage<ItemData> products = vm.getProducts();

        for (int i = 0; i < products.getSize(); i++) {
            createItemButton(getView(), products.getItem(i));
        }
        view.findViewById(R.id.button_reset_products).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkHandler network = new NetworkHandler(getContext());
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

    private void createItemButton(View view, ItemData product) {
        Button button = new Button(view.getContext());
        setButtonsStyle(button);
        this.productsList.addView(button);
        if (product.getQuantity() > 0) {
            String price = String.format(Locale.CANADA, "%.2f", (product.getPrice()));
            button.setText(getResources().getString(R.string.product_button_format, product.getName(), price));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new VM().setSelectedProduct(product);
                    NavHostFragment.findNavController(ProductsFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
            });
        } else {
            button.setText(getResources().getString(R.string.product_no_quantity, product.getName()));
        }
    }

    private void setButtonsStyle(Button button) {
        button.setBackgroundResource(R.drawable.product_button_round_background);
        button.setTextColor(getResources().getColor(R.color.design_default_color_secondary_variant));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
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

            productsList.removeAllViews();

            Storage<ItemData> products = vm.getProducts();

            for (int i = 0; i < products.getSize(); i++) {
                createItemButton(getView(), products.getItem(i));
            }

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