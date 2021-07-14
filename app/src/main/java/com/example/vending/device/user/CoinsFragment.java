package com.example.vending.device.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vending.R;
import com.example.vending.backend.CoinsCounter;
import com.example.vending.backend.ItemData;
import com.example.vending.backend.Storage;
import com.example.vending.backend.VM;

import java.math.BigDecimal;
import java.util.Locale;

public class CoinsFragment extends Fragment {

    ViewGroup coinsList;
    TextView productName;
    TextView coinsAmount;
    TextView productPrice;
    String insertedAmount;

    VM vm;
    CoinsCounter coinsCounter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBackButton();

        vm = new VM();
        Storage<ItemData> coinsMachine = vm.getMachineCoins();
        coinsCounter = new CoinsCounter();

        final ItemData selectedProduct = vm.getSelectedProduct();
        productName = view.findViewById(R.id.product_name);
        coinsList = view.findViewById(R.id.coins_list);
        coinsAmount = view.findViewById(R.id.coins_inserted_amount);
        productName.setText(selectedProduct.getName());
        insertedAmount = "0.00";
        coinsAmount.setText(insertedAmount);
        productPrice = view.findViewById(R.id.product_price);
        String selectedProductPrice = String.format(Locale.CANADA, "%.2f", selectedProduct.getPrice());
        productPrice.setText(selectedProductPrice);

        for (int i = 0; i < coinsMachine.getSize(); i++) {
            Button button = new Button(view.getContext());
            button.setBackgroundResource(R.drawable.coin_button_circle_background);
            button.setTextColor(getResources().getColor(R.color.white));
            String nominal = String.format(Locale.CANADA, "%.2f", coinsMachine.getItem(i).getPrice());
            button.setText(nominal);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            coinsList.addView(button);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    updateInsertedAmount(coinsMachine.getItem(finalI));
                    coinsCounter.insertCoin(coinsMachine.getItem(finalI), vm.getUserCoins());
                    coinsAmount.setText(insertedAmount);
                    if (Double.parseDouble(insertedAmount) >= Double.parseDouble(productPrice.getText().toString())) {
                        disableButtonsInView(coinsList);
                        button.setEnabled(false);
                        coinsCounter.addCoinsToStorage(vm.getUserCoins(), vm.getMachineCoins());
                        Storage<ItemData> coinsForReturn = coinsCounter.calculateReturningCoins(insertedAmount, selectedProductPrice, vm.getMachineCoins());
                        vm.decreaseProductQuantity(selectedProduct);
                        showGetCoinsAlert(coinsForReturn, false);
                        vm.initUserCoinsStorage();

                    }
                }
            });
        }

        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGetCoinsAlert(vm.getUserCoins(), true);
                vm.initUserCoinsStorage();
            }
        });
    }

    private void updateInsertedAmount(ItemData item) {
        insertedAmount = new BigDecimal(insertedAmount).add(BigDecimal.valueOf(item.getPrice())).toString();
    }

    private void showGetCoinsAlert(Storage<ItemData> coinsForReturn, boolean isCancel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        View alertLayout = getLayoutInflater().inflate(R.layout.dialog_get_product, null);
        if (!isCancel) {
            alertDialogBuilder.setTitle(R.string.alert_title_thank_you);
            alertDialogBuilder.setMessage(R.string.alert_text_get_product);
        } else {
            alertDialogBuilder.setTitle(R.string.alert_title_order_canceled);
        }
        if (coinsForReturn.getSize() > 0) {
            if (!isCancel) {
                String msg = getString(R.string.alert_text_get_product) + "\n"+ getString(R.string.alert_text_get_change);
                alertDialogBuilder.setMessage(msg);
            } else {
                alertDialogBuilder.setMessage(R.string.alert_text_order_canceled);
            }
            ViewGroup layout = alertLayout.findViewById(R.id.coins_change_list);
            for (int i = 0; i < coinsForReturn.getSize(); i++) {
                ItemData coinItem = coinsForReturn.getItem(i);
                int quantity = coinItem.getQuantity();
                layout.addView(createReturnCoinView(coinItem, quantity));
            }
            alertDialogBuilder.setView(layout);
        }
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NavHostFragment.findNavController(CoinsFragment.this)
                        .navigate(R.id.action_CoinsFragment_to_ProductsFragment);
            }
        })
                .setCancelable(false)
                .show();
    }

    private TextView createReturnCoinView(ItemData coinItem, int quantity) {
        TextView coinView = new TextView(getContext());
        coinView.setBackgroundResource(R.drawable.coin_button_circle_background_small);
        coinView.setTextColor(getResources().getColor(R.color.white));
        String price = String.format(Locale.CANADA, "%.2f", (coinItem.getPrice()));
        coinView.setText(getString(R.string.alert_text_order_coins_holder, String.valueOf(quantity), price));
        coinView.setGravity(Gravity.CENTER);
        coinView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        return coinView;
    }

    private void disableButtonsInView(View view) {
        ViewGroup group = (ViewGroup)view;

        for ( int i = 0 ; i < group.getChildCount() ; i++ ) {
            group.getChildAt(i).setEnabled(false);
        }

    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showGetCoinsAlert(vm.getUserCoins(), true);
                vm.initUserCoinsStorage();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}