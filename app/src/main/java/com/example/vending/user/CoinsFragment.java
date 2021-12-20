package com.example.vending.user;

import static com.example.vending.utils.Constant.IS_ORDER_CANCELED;
import static com.example.vending.utils.Constant.IS_PRODUCT_TAKEN;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.utils.Utils;
import com.example.vending.VendingViewModel;
import com.example.vending.server.ResponseModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoinsFragment extends Fragment implements CoinsAdapter.CoinListener {

    private static final String TAG = CoinsFragment.class.getSimpleName();

    private TextView mViewCoinsAmount;
    private String mSelectedProductPrice;
    private CoinsAdapter mAdapter;
    private VendingViewModel mViewModel;
    private List<ResponseModel.Item> mListCoinsMachine;
    private ResponseModel.Item mSelectedProduct;
    boolean isClickable, isProductTaken, isOrderCanceled;
    private long mLastClickTime = 0;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isProductTaken = savedInstanceState.getBoolean(IS_PRODUCT_TAKEN);
            isOrderCanceled = savedInstanceState.getBoolean(IS_ORDER_CANCELED);
        }
        isClickable = true;
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_coins, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleBackButton();
        initViewModel();
        initCoinsAmount(view);
        initProductViews(view);
        initRecyclerView(view);
        initCancelButton(view);
        checkOrderCancelled();
    }

    private void checkOrderCancelled() {
        if (isOrderCanceled) {
            showGetCoinsAlert(mViewModel.getUserCoins(), true);
        }
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(VendingViewModel.class);
        mViewModel.getLiveDataCoins().observe(getViewLifecycleOwner(), items -> {
            mListCoinsMachine.clear();
            mListCoinsMachine.addAll(items);
            mAdapter.notifyDataSetChanged();
//            Log.e(TAG, items.toString());
        });
        mViewModel.getInsertedAmount().observe(getViewLifecycleOwner(), amount -> {
            mViewCoinsAmount.setText(amount);
            if (Double.parseDouble(amount) >= Double.parseDouble(mSelectedProductPrice)) {
                isClickable = false;
                addUserCoinsAndReturnChange();
            }
        });
        mSelectedProduct = mViewModel.getSelectedProduct();
    }

    private void initProductViews(View view) {
        if (mSelectedProduct != null) {
            TextView viewProductName = view.findViewById(R.id.productName);
            viewProductName.setText(mSelectedProduct.getName());
            TextView viewProductPrice = view.findViewById(R.id.textProductPrice);
            mSelectedProductPrice = String.format(Locale.CANADA, "%.2f", mSelectedProduct.getPrice());
            viewProductPrice.setText(mSelectedProductPrice);
        }
    }

    private void initCoinsAmount(View view) {
        mViewCoinsAmount = view.findViewById(R.id.coinsInsertedAmount);
    }

    private void initRecyclerView(View view) {
        mListCoinsMachine = new ArrayList<>();
        RecyclerView coinsListRecycler = view.findViewById(R.id.recyclerCoins);
        mAdapter = new CoinsAdapter(mListCoinsMachine, this);
        coinsListRecycler.setAdapter(mAdapter);
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
            isOrderCanceled = true;
            showGetCoinsAlert(mViewModel.getUserCoins(), true);
        });
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
            mViewModel.resetUserCoins();
            NavHostFragment.findNavController(CoinsFragment.this)
                    .navigate(R.id.action_products);
        })
                .setCancelable(false);
        alertDialog = alertDialogBuilder.show();
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
        mViewModel.updateInsertedAmount(mListCoinsMachine.get(position));
    }

    private void addUserCoinsAndReturnChange() {
        if (!isProductTaken) {
            isProductTaken = true;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        mViewModel.addUserCoinsToMachine();
                        mViewModel.removeProduct();
                        mViewModel.prepareReturningCoins();
                        showGetCoinsAlert(mViewModel.getCoinsForReturn(), false);
                    }, 300);
        } else showGetCoinsAlert(mViewModel.getCoinsForReturn(), false);
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                isOrderCanceled = true;
                showGetCoinsAlert(mViewModel.getUserCoins(), true);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (alertDialog != null) alertDialog.dismiss();
        outState.putBoolean(IS_PRODUCT_TAKEN, isProductTaken);
        outState.putBoolean(IS_ORDER_CANCELED, isOrderCanceled);
        super.onSaveInstanceState(outState);
    }
}