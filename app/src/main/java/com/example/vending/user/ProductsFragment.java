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

public class ProductsFragment extends BaseFragment<VendingViewModel> implements ProductsAdapter.ProductClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = ProductsFragment.class.getSimpleName();

    private long mLastClickTime = 0;
    boolean doubleBackToExitPressedOnce = false;
    private ProductsAdapter mAdapter;
    private AlertDialog alertOutOfOrder;
    private ConstraintLayout mLayoutNoConnection;
    private RecyclerView mRecyclerProducts;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViewModel();
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleBackButton();
        initRecyclerView(view);
        initNoConnLayout(view);
        fetchDataFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getLiveDataOutOfOrderAlert().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) showOutOfOrderAlert();
            else hideOutOfOrderAlert();
        });
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(VendingViewModel.class);
        mViewModel.getLiveDataLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerProducts.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerProducts.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getLiveDataProducts().observe(getViewLifecycleOwner(), items ->
            mAdapter.setProducts(items));
        mViewModel.getLiveDataCoins().observe(getViewLifecycleOwner(), items ->
            mViewModel.checkIfCoinsToOperate());
        mViewModel.getLiveDataNoConnection().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                mLayoutNoConnection.setVisibility(View.VISIBLE);
                mRecyclerProducts.setVisibility(View.GONE);
            }
            else {
                mLayoutNoConnection.setVisibility(View.GONE);
                mRecyclerProducts.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initNoConnLayout(View view) {
        mLayoutNoConnection = view.findViewById(R.id.layoutNoConnection);
        Button retryButton = view.findViewById(R.id.button_retry);
        retryButton.setOnClickListener(v -> {
            mLayoutNoConnection.setVisibility(View.GONE);
            mViewModel.setNoConnection(false);
            fetchDataFromServer();
        });
    }

    private void initRecyclerView(View view) {
        mProgressBar = view.findViewById(R.id.progressBar);
        mRecyclerProducts = view.findViewById(R.id.products_recycler);
        mAdapter = new ProductsAdapter(this);
        mRecyclerProducts.setAdapter(mAdapter);
    }

    private void fetchDataFromServer() {
        mViewModel.initProductsRequest();
        mViewModel.initCoinsRequest();
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

    private void hideOutOfOrderAlert() {
        if (alertOutOfOrder != null && alertOutOfOrder.isShowing()) {
            alertOutOfOrder.dismiss();
        }
    }

    @Override
    public void onProductClick(int position, View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) return;

        mViewModel.setSelectedProduct(position);
        mLastClickTime = SystemClock.elapsedRealtime();
        View viewButton = v.findViewById(R.id.product_button);
        if (mViewModel.selectedProductHasQuantity()) {
            viewButton.setBackgroundResource(R.drawable.button_round_background_pressed);
            NavHostFragment.findNavController(ProductsFragment.this)
                    .navigate(R.id.action_insert_coins);
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