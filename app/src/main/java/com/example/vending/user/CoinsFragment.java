package com.example.vending.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.CoinsCounter;
import com.example.vending.ItemData;
import com.example.vending.MainActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoinsFragment extends Fragment implements CoinsAdapter.CoinListener {

    private TextView coinsAmount;
    private TextView productPrice;
    private String insertedAmount;

    private List<ItemData> coinsMachine;
    private CoinsCounter coinsCounter;
    private String selectedProductPrice;

    MainActivity activity;
    private List<ItemData> coinsUser;

    private int pPos;
    private String pName;
    private Double pPrice;

    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        pPos = arguments.getInt(getString(R.string.item_position_key));
        pName = arguments.getString(getString(R.string.item_name_key));
        pPrice = arguments.getDouble(getString(R.string.item_price_key));
        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBackButton();

        activity = (MainActivity) getActivity();

        coinsUser = new ArrayList<>();
        coinsMachine = activity.getCoins();
        coinsCounter = new CoinsCounter();
        CoinsAdapter adapter = new CoinsAdapter(coinsMachine, this);

        TextView productName = view.findViewById(R.id.product_name);
        coinsAmount = view.findViewById(R.id.coins_inserted_amount);
        productName.setText(pName);
        insertedAmount = getString(R.string.zero_amount);
        coinsAmount.setText(insertedAmount);
        productPrice = view.findViewById(R.id.product_price);
        RecyclerView coinsListRecycler = view.findViewById(R.id.coins_list_recycler);
        selectedProductPrice = String.format(Locale.CANADA, "%.2f", pPrice);
        productPrice.setText(selectedProductPrice);
        coinsListRecycler.setAdapter(adapter);

        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGetCoinsAlert(coinsUser, true);
            }
        });
    }

    private void updateInsertedAmount(ItemData item) {
        insertedAmount = new BigDecimal(insertedAmount).add(BigDecimal.valueOf(item.getPrice())).toString();
    }

    private void showGetCoinsAlert(List<ItemData> coinsForReturn, boolean isCancel) {
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
                ItemData coinItem = coinsForReturn.get(i);
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

    private View createReturnCoinView(ItemData coinItem, int quantity) {
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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                v.animate().scaleX(1).scaleY(1).setDuration(100);
            }
        });
        updateInsertedAmount(coinsMachine.get(position));
        coinsCounter.insertCoin(coinsMachine.get(position), coinsUser);
        coinsAmount.setText(insertedAmount);
        if (Double.parseDouble(insertedAmount) >= Double.parseDouble(productPrice.getText().toString())) {
            coinsCounter.addCoinsToStorage(coinsUser, coinsMachine);
            List<ItemData> coinsForReturn = coinsCounter.calculateReturningCoins(insertedAmount, selectedProductPrice, coinsMachine);
            ((MainActivity) getActivity()).decreaseProductQuantity(pPos);
            showGetCoinsAlert(coinsForReturn, false);
        }
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