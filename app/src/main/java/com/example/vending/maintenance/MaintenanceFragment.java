package com.example.vending.maintenance;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.example.vending.VendingActivity;
import com.example.vending.Utils;
import com.example.vending.server.ModelData;
import com.example.vending.server.RequestApi;
import com.example.vending.server.RetrofitVending;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MaintenanceFragment extends Fragment implements OptionsRecyclerViewAdapter.OptionListener {

    private final CompositeDisposable bag = new CompositeDisposable();

    private RecyclerView optionsList;
    private OptionsRecyclerViewAdapter mAdapter;
    private List<MaintenanceOption> mOptions;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOptions = new ArrayList<>(Arrays.asList(MaintenanceOption.values()));

        handleBackButton();

        optionsList = view.findViewById(R.id.options_list);
        mAdapter = new OptionsRecyclerViewAdapter(mOptions, this);
        optionsList.setAdapter(mAdapter);

        view.findViewById(R.id.button_exit).setOnClickListener(view1 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Utils.playSound(getContext(), R.raw.click_default);
            Utils.animateClick(view1);
            showQuitAlert();
        });
    }

    @Override
    public void onOptionClicked(int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Utils.playSound(getContext(), R.raw.click_default);
        Utils.animateClick(optionsList.getChildAt(position));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        if (mOptions.get(position) == MaintenanceOption.RESET_PRODUCTS) {
            alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_products);
            alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
            alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
                Utils.playSound(getContext(), R.raw.click_default);
                serverRequest(getString(R.string.request_products));
            });

        } else if (mOptions.get(position) == MaintenanceOption.RESET_COINS) {
            alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_coins);
            alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
            alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
                Utils.playSound(getContext(), R.raw.click_default);
                serverRequest(getString(R.string.request_coins));
            });
        }
        alertDialogBuilder.setNegativeButton(R.string.alert_quit_maintenance_cancel, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
        });
        alertDialogBuilder.setCancelable(false).show();
    }

    private void serverRequest(String request) {
        VendingActivity vendingActivity = (VendingActivity) getActivity();
        Consumer<Throwable> onError = throwable -> {
            Utils.showNoInternetDialog(getActivity());
        };
        if (request.equalsIgnoreCase(getString(R.string.request_products))) {
            Consumer<ModelData> onSuccess = response -> {
                List<ModelData.Item> items = response.getItems();
                vendingActivity.productsStorage = new ArrayList<>();
                vendingActivity.loadProductsToStorage(new Gson().toJson(items));
                Toast.makeText(getContext(), getString(R.string.maintenance_products_reset), Toast.LENGTH_LONG).show();
            };
            bag.add(new RetrofitVending().getInstance().create(RequestApi.class)
                    .getProducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess, onError));
        } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
            Consumer<ModelData> onSuccess = response -> {
                List<ModelData.Item> items = response.getItems();
                vendingActivity.coinsStorage = new ArrayList<>();
                vendingActivity.loadCoinsToStorage(new Gson().toJson(items));
                Toast.makeText(getContext(), getString(R.string.maintenance_coins_reset), Toast.LENGTH_LONG).show();
            };
            bag.add(new RetrofitVending().getInstance().create(RequestApi.class)
                    .getCoins()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess, onError));
        }
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showQuitAlert();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void showQuitAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_quit_maintenance_title);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
            NavHostFragment.findNavController(MaintenanceFragment.this)
                    .navigate(R.id.action_MaintenanceFragment_to_ProductsFragment);
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_quit_maintenance_cancel, (dialog, which) -> Utils.playSound(getContext(), R.raw.click_default));
        alertDialogBuilder.setCancelable(false).show();
    }
}
