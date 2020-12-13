package com.example.vending.screen;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vending.R;
import com.example.vending.machine.CoinsCounter;
import com.example.vending.machine.ItemData;
import com.example.vending.machine.Storage;
import com.example.vending.machine.VM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.Locale;

public class CoinsFragment extends Fragment {

    ViewGroup coinsList;
    TextView coinsAmount;
    TextView productPrice;
    String insertedAmount;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VM vm = new VM();
        Storage<ItemData> coinsMachine = vm.getMachineCoins();
        CoinsCounter coinsCounter = new CoinsCounter();


        coinsList = (ViewGroup) view.findViewById(R.id.coins_list);
        coinsAmount = (TextView) view.findViewById(R.id.coins_inserted_amount);
        insertedAmount = "0.00";
        coinsAmount.setText(insertedAmount);
        productPrice = (TextView) view.findViewById(R.id.product_price);
        String selectedProductPrice = String.format(Locale.CANADA, "%.2f", vm.getSelectedProduct().getPrice());
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
//                    BigDecimal decimal = new BigDecimal(coinsAmount.getText().toString());
//                    coinsAmount.setText(decimal.add(new BigDecimal("0.10")).toString());
                    if (Double.parseDouble(insertedAmount) >= Double.parseDouble(productPrice.getText().toString())) {
                        button.setEnabled(false);
                        Storage<ItemData> coinsForReturn = coinsCounter.calculateReturningCoins(insertedAmount, selectedProductPrice, vm.getMachineCoins());
                        vm.decreaseProductQuantity(vm.getSelectedProduct());
                        showGetProductAlert(coinsForReturn);
                    }
                }
            });
            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    coinsCounter.returnInsertedCoins();
                    NavHostFragment.findNavController(CoinsFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }
            });
        }

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CoinsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    private void updateInsertedAmount(ItemData item) {
        insertedAmount = new BigDecimal(insertedAmount).add(BigDecimal.valueOf(item.getPrice())).toString();
    }

    private void showGetProductAlert(Storage<ItemData> coinsForReturn) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        View alertLayout = getLayoutInflater().inflate(R.layout.dialog_get_product, null);
        ViewGroup layout = (ViewGroup) alertLayout.findViewById(R.id.coins_change_list);

        for (int i = 0; i < coinsForReturn.getSize(); i++) {
            for (int j = 0; j < coinsForReturn.getItem(i).getQuantity(); j++) {
                TextView coin = new TextView(getContext());
                coin.setBackgroundResource(R.drawable.coin_button_circle_background_small);
                coin.setTextColor(getResources().getColor(R.color.white));
                String price = String.format(Locale.CANADA, "%.2f", (coinsForReturn.getItem(i).getPrice()));
                coin.setText(price);
                coin.setGravity(Gravity.CENTER);
                coin.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                layout.addView(coin);
            }
        }
        alertDialogBuilder.setTitle(R.string.alert_title_get_product);
        alertDialogBuilder.setMessage(R.string.alert_text_get_product);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NavHostFragment.findNavController(CoinsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        })
                .setCancelable(false)
                .show();
    }
}