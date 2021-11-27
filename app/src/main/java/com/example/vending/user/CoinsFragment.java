package com.example.vending.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.CoinsCounter;
import com.example.vending.Utils;
import com.example.vending.VendingActivity;
import com.example.vending.server.ResponseModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoinsFragment extends Fragment implements CoinsAdapter.CoinListener {

    private TextView coinsAmount;
    private TextView productPrice;
    private String insertedAmount;

    private List<ResponseModel.Item> coinsMachine;
    private CoinsCounter coinsCounter;
    private String selectedProductPrice;

    VendingActivity activity;
    private List<ResponseModel.Item> coinsUser;

    boolean isClickable;

    private int pPos;
    private String pName;
    private Double pPrice;

    private long mLastClickTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            pPos = arguments.getInt(getString(R.string.item_position_key));
            pName = arguments.getString(getString(R.string.item_name_key));
            pPrice = arguments.getDouble(getString(R.string.item_price_key));
        }
        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBackButton();

        initCoinsAmount(view);
        initProductName(view);
        initProductPrice(view);
        initRecyclerView(view);
        initCancelButton(view);
        isClickable = true;
    }

    private void initCoinsAmount(View view) {
        coinsUser = new ArrayList<>();
        coinsMachine = activity.getCoins();
        coinsCounter = new CoinsCounter();
        insertedAmount = getString(R.string.zero_amount);
        coinsAmount = view.findViewById(R.id.coins_inserted_amount);
        coinsAmount.setText(insertedAmount);
    }

    private void initProductName(View view) {
        TextView productName = view.findViewById(R.id.product_name);
        productName.setText(pName);
    }

    private void initProductPrice(View view) {
        productPrice = view.findViewById(R.id.product_price);
        selectedProductPrice = String.format(Locale.CANADA, "%.2f", pPrice);
        productPrice.setText(selectedProductPrice);
    }

    private void initRecyclerView(View view) {
        VendingActivity activity = (VendingActivity) getActivity();
        coinsMachine = activity != null ? activity.getCoins() : new ArrayList<>();
        RecyclerView coinsListRecycler = view.findViewById(R.id.coins_list_recycler);
        CoinsAdapter adapter = new CoinsAdapter(coinsMachine, this);
        coinsListRecycler.setAdapter(adapter);
    }

    private void initCancelButton(View view) {
        Button buttonCancel = view.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(viewButtonCancel -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Utils.playSound(getContext(), R.raw.click_default);
            Utils.animateClick(viewButtonCancel);
            showGetCoinsAlert(coinsUser, true);
        });
    }

    private void updateInsertedAmount(ResponseModel.Item item) {
        insertedAmount = new BigDecimal(insertedAmount).add(BigDecimal.valueOf(item.getPrice())).toString();
    }

    private void showGetCoinsAlert(List<ResponseModel.Item> coinsForReturn, boolean isCancel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        View alertLayout = getLayoutInflater().inflate(R.layout.dialog_get_product, null);
        if (!isCancel) {
            alertDialogBuilder.setTitle(R.string.alert_title_thank_you);
            alertDialogBuilder.setMessage(R.string.alert_text_get_product);
        } else {
            alertDialogBuilder.setTitle(R.string.alert_title_order_canceled);
        }
        if (coinsForReturn.size() > 0) {
            if (!isCancel) {
                String msg = getString(R.string.alert_text_get_product) + "\n"+ getString(R.string.alert_text_get_change);
                alertDialogBuilder.setMessage(msg);
            } else {
                alertDialogBuilder.setMessage(R.string.alert_text_order_canceled);
            }
            ViewGroup layout = alertLayout.findViewById(R.id.coins_change_list);
            for (int i = 0; i < coinsForReturn.size(); i++) {
                ResponseModel.Item coinItem = coinsForReturn.get(i);
                int quantity = coinItem.getQuantity();
                layout.addView(createReturnCoinView(coinItem, quantity));
            }
            alertDialogBuilder.setView(layout);
        }
        alertDialogBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
            NavHostFragment.findNavController(CoinsFragment.this)
                    .navigate(R.id.action_CoinsFragment_to_ProductsFragment);
        })
                .setCancelable(false)
                .show();
    }

    private View createReturnCoinView(ResponseModel.Item coinItem, int quantity) {
        View view = getLayoutInflater().inflate(R.layout.item_coin_small, null);
        String price = String.format(Locale.CANADA, "%.2f", (coinItem.getPrice()));
        TextView amountView = view.findViewById(R.id.return_coin_amount);
        TextView nominalView = view.findViewById(R.id.return_coin_nominal);
        amountView.setText(String.valueOf(quantity));
        nominalView.setText(price);
        return view;
    }

    @Override
    public void onCoinClick(int position, View v) {
        if (isClickable) {
            registerCoinClick(position, v);
        }
    }

    public void registerCoinClick(int position, View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Utils.playSound(getContext(), R.raw.click_coin);
        Utils.animateClick(v);
        updateInsertedAmount(coinsMachine.get(position));
        coinsCounter.insertCoin(coinsMachine.get(position), coinsUser);
        coinsAmount.setText(insertedAmount);
        if (Double.parseDouble(insertedAmount) >= Double.parseDouble(productPrice.getText().toString())) {
            isClickable = false;
            addAndReturnCoins();
        }
    }

    private void addAndReturnCoins() {
        coinsCounter.addCoinsToStorage(coinsUser, coinsMachine);
        List<ResponseModel.Item> coinsForReturn = coinsCounter.calculateReturningCoins(insertedAmount, selectedProductPrice, coinsMachine);
        VendingActivity activity = (VendingActivity) getActivity();
        if (activity != null) activity.decreaseProductQuantity(pPos);
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> showGetCoinsAlert(coinsForReturn, false), 300);
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showGetCoinsAlert(coinsUser, true);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}