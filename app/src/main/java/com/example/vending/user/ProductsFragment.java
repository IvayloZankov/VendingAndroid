package com.example.vending.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.Utils;
import com.example.vending.VendingActivity;
import com.example.vending.server.ResponseModel;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements ProductsAdapter.ProductListener {

    private long mLastClickTime = 0;
    private List<ResponseModel.Item> products;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VendingActivity activity = (VendingActivity) getActivity();
        products = activity != null ? activity.getProducts() : new ArrayList<>();

        handleBackButton();


        initRecyclerView(view);

        checkIfCoinsToOperate();
    }

    private void initRecyclerView(View view) {
        RecyclerView productsList = view.findViewById(R.id.products_list);
        ProductsAdapter adapter = new ProductsAdapter(products, this);
        productsList.setAdapter(adapter);
    }

    private void checkIfCoinsToOperate() {
        VendingActivity activity = (VendingActivity) getActivity();
        List<ResponseModel.Item> coins = activity != null ? activity.getCoins() : new ArrayList<>();
        if (coins.size() > 0) {
            ResponseModel.Item item = coins.get(0);
            int quantity = item.getQuantity();
            if (quantity < 40) {
                showOutOfOrderAlert();
            }
        } else showOutOfOrderAlert();
    }

    private void showOutOfOrderAlert() {
        Utils.playSound(getContext(), R.raw.out_of_order);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_title_out_of_order);
        alertDialogBuilder.setMessage(R.string.alert_text_enter_maintenance);
        alertDialogBuilder.setPositiveButton(R.string.alert_out_of_order_reset, (dialog, which) -> {
                Utils.playSound(getContext(), R.raw.click_default);
            Fragment parentFragment = getParentFragment();
            if (parentFragment != null)
            NavHostFragment.findNavController(parentFragment)
                        .navigate(R.id.action_MaintenanceFragment);
        })
        .setCancelable(false).show();
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) activity.finish();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(getContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onProductClick(int position, View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        View viewButton = v.findViewById(R.id.product_button);
        if (products.get(position).getQuantity() > 0) {
            Utils.playSound(getContext(), R.raw.click_default);
            Utils.animateClick(viewButton);
            viewButton.setBackgroundResource(R.drawable.button_round_background_pressed);
            ResponseModel.Item itemData = products.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.item_name_key), itemData.getName());
            bundle.putDouble(getString(R.string.item_price_key), itemData.getPrice());
            bundle.putInt(getString(R.string.item_position_key), position);
            NavHostFragment.findNavController(ProductsFragment.this)
                    .navigate(R.id.action_ProductsFragment_to_CoinsFragment, bundle);
        } else {
            Utils.playSound(getContext(), R.raw.click_default);
            Utils.animateClick(viewButton);
        }
    }
}