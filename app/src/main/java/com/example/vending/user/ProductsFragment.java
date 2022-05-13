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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.base.BaseFragment;
import com.example.vending.utils.SoundManager;
import com.example.vending.VendingViewModel;
import com.example.vending.server.ResponseModel;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends BaseFragment<VendingViewModel> implements ProductsAdapter.ProductListener {

    private static final String TAG = ProductsFragment.class.getSimpleName();

    private long mLastClickTime = 0;
    boolean doubleBackToExitPressedOnce = false;
    private ProductsAdapter mAdapter;
    private List<ResponseModel.Item> mListProducts;
    private List<ResponseModel.Item> mListCoins;
    private AlertDialog alertOutOfOrder;
    private ConstraintLayout layoutNoConnection;
    private RecyclerView mProductsRecycler;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListProducts = new ArrayList<>();
        initViewModel();
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleBackButton();
        initRecyclerView(view);
        initNoConnLayout(view);
        mViewModel.initProductsRequest();
        mViewModel.initCoinsRequest();
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(VendingViewModel.class);
        mViewModel.getLiveDataLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProductsRecycler.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mProductsRecycler.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getLiveDataProducts().observe(getViewLifecycleOwner(), items -> {
            mListProducts.clear();
            mListProducts.addAll(items);
            mAdapter.notifyDataSetChanged();
        });
        mViewModel.getLiveDataCoins().observe(getViewLifecycleOwner(), items -> {
            mListCoins = items;
            checkIfCoinsToOperate();
        });
    }

    private void initNoConnLayout(View view) {
        layoutNoConnection = view.findViewById(R.id.layoutNoConnection);
        Button retryButton = view.findViewById(R.id.button_retry);
        retryButton.setOnClickListener(v -> {
            layoutNoConnection.setVisibility(View.GONE);
            fetchDataFromServer();
        });
    }

    private void initRecyclerView(View view) {
        mProgressBar = view.findViewById(R.id.progressBar);
        mProductsRecycler = view.findViewById(R.id.products_recycler);
        mAdapter = new ProductsAdapter(mListProducts, this);
        mProductsRecycler.setAdapter(mAdapter);
    }

    private void fetchDataFromServer() {
        mViewModel.initProductsRequest();
        mViewModel.initCoinsRequest();
    }

    private void checkIfCoinsToOperate() {
        if (mListCoins.size() > 0) {
            ResponseModel.Item item = mListCoins.get(0);
            int quantity = item.getQuantity();
            if (quantity < 40) {
                showOutOfOrderAlert();
            }
        } else showOutOfOrderAlert();
    }

    private void showOutOfOrderAlert() {
        if (alertOutOfOrder == null || !alertOutOfOrder.isShowing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(R.string.alert_title_out_of_order);
            alertDialogBuilder.setMessage(R.string.alert_text_enter_maintenance);
            alertDialogBuilder.setPositiveButton(R.string.alert_out_of_order_reset, (dialog, which) -> {
                        SoundManager.getInstance().playClick();
                        Fragment parentFragment = getParentFragment();
                        if (parentFragment != null)
                            NavHostFragment.findNavController(parentFragment)
                                    .navigate(R.id.action_maintenance);
                    })
                    .setCancelable(false);
            alertOutOfOrder = alertDialogBuilder.create();
            SoundManager.getInstance().playError();
            alertOutOfOrder.show();
        }
    }

    @Override
    public void onProductClick(int position, View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mViewModel.setSelectedProduct(position);
        mLastClickTime = SystemClock.elapsedRealtime();
        View viewButton = v.findViewById(R.id.product_button);
        if (mListProducts.get(position).getQuantity() > 0) {
            viewButton.setBackgroundResource(R.drawable.button_round_background_pressed);
            ResponseModel.Item itemData = mListProducts.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.item_name_key), itemData.getName());
            bundle.putDouble(getString(R.string.item_price_key), itemData.getPrice());
            bundle.putInt(getString(R.string.item_position_key), position);
            NavHostFragment.findNavController(ProductsFragment.this)
                    .navigate(R.id.action_insert_coins, bundle);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (alertOutOfOrder != null) alertOutOfOrder.dismiss();
        super.onSaveInstanceState(outState);
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(getContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        doubleBackToExitPressedOnce = false, 2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}