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
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.backend.CoinsCounter;
import com.example.vending.backend.ItemData;
import com.example.vending.backend.Storage;
import com.example.vending.backend.VM;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class CoinsFragment extends Fragment implements CoinsAdapter.CoinListener {

    private TextView coinsAmount;
    private TextView productPrice;
    private String insertedAmount;

    private  VM vm;
    private List<ItemData> coinsMachine;
    private CoinsCounter coinsCounter;
    private String selectedProductPrice;
    private ItemData selectedProduct;

    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBackButton();

        vm = new VM();
        coinsMachine = vm.getCoins();
        coinsCounter = new CoinsCounter();
        CoinsAdapter adapter = new CoinsAdapter(coinsMachine, this);

        selectedProduct = vm.getSelectedProduct();
        TextView productName = view.findViewById(R.id.product_name);
        coinsAmount = view.findViewById(R.id.coins_inserted_amount);
        productName.setText(selectedProduct.getName());
        insertedAmount = getString(R.string.zero_amount);
        coinsAmount.setText(insertedAmount);
        productPrice = view.findViewById(R.id.product_price);
        RecyclerView coinsListRecycler = view.findViewById(R.id.coins_list_recycler);
        selectedProductPrice = String.format(Locale.CANADA, "%.2f", selectedProduct.getPrice());
        productPrice.setText(selectedProductPrice);
        coinsListRecycler.setAdapter(adapter);

        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGetCoinsAlert(vm.getCoinsUser(), true);
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
    public void onCoinClick(int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        updateInsertedAmount(coinsMachine.get(position));
        coinsCounter.insertCoin(coinsMachine.get(position), vm.getCoinsUser());
        coinsAmount.setText(insertedAmount);
        if (Double.parseDouble(insertedAmount) >= Double.parseDouble(productPrice.getText().toString())) {
            coinsCounter.addCoinsToStorage(vm.getCoinsUser(), vm.getCoins());
            Storage<ItemData> coinsForReturn = coinsCounter.calculateReturningCoins(insertedAmount, selectedProductPrice, vm.getCoins());
            vm.decreaseProductQuantity(selectedProduct);
            showGetCoinsAlert(coinsForReturn, false);
            vm.initUserCoinsStorage();
        }
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showGetCoinsAlert(vm.getCoinsUser(), true);
                vm.initUserCoinsStorage();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

}