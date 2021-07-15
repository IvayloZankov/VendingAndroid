package com.example.vending.device.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.backend.ItemData;
import com.example.vending.backend.VM;
import com.example.vending.device.NetworkHandler;

import java.util.List;

public class ProductsFragment extends Fragment implements ProductsAdapter.ProductListener {

    private RecyclerView productsList;
    private ProductsAdapter adapter;
    private long mLastClickTime = 0;

    private VM vm;
    private NetworkHandler network;
    private List<ItemData> products;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new VM();
        network = new NetworkHandler(getContext());
        products = vm.getProducts();

        handleBackButton();

        productsList = view.findViewById(R.id.products_list);
        adapter = new ProductsAdapter(products, this);
        productsList.setAdapter(adapter);

        if (!vm.canReturnChange()) {
            showOutOfOrderAlert();
        }
    }

    private void showOutOfOrderAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_title_out_of_order);
        alertDialogBuilder.setMessage(R.string.alert_text_enter_maintenance);
        alertDialogBuilder.setPositiveButton(R.string.alert_out_of_order_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NavHostFragment.findNavController(getParentFragment())
                        .navigate(R.id.action_MaintenanceFragment);
            }
                })
        .setCancelable(false).show();
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

    @Override
    public void onProductClick(int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (products.get(position).getQuantity() > 0) {
            new VM().setSelectedProduct(products.get(position));
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_ProductsFragment_to_CoinsFragment);
        }
    }
}