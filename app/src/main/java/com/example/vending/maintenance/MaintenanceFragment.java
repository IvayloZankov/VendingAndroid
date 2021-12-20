package com.example.vending.maintenance;

import static com.example.vending.utils.Constant.IS_QUIT;
import static com.example.vending.utils.Constant.IS_RESET_COINS;
import static com.example.vending.utils.Constant.IS_RESET_PRODUCTS;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.utils.Utils;
import com.example.vending.VendingViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaintenanceFragment extends Fragment implements OptionsRecyclerViewAdapter.OptionListener {

    public static final String TAG = MaintenanceFragment.class.getSimpleName();

    private VendingViewModel mViewModel;
    private RecyclerView optionsList;
    private final List<MaintenanceOption> mOptions = new ArrayList<>(Arrays.asList(MaintenanceOption.values()));
    private long mLastClickTime = 0;
    private AlertDialog alertNoConn;
    private AlertDialog alertAreYouSure;
    private boolean isResetProductsShown, isResetCoinsShown, isQuitShown;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isResetProductsShown = savedInstanceState.getBoolean(IS_RESET_PRODUCTS);
            isResetCoinsShown = savedInstanceState.getBoolean(IS_RESET_COINS);
            isQuitShown = savedInstanceState.getBoolean(IS_QUIT);
        }
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleBackButton();
        iniViewModel();
        initRecyclerView(view);
        initExitButton(view);
        checkForAlert();
    }

    private void checkForAlert() {
        if (isResetProductsShown) initResetProductsAlert();
        if (isResetCoinsShown) initResetCoinsAlert();
        if (isQuitShown) showQuitAlert();
    }

    private void iniViewModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(VendingViewModel.class);
    }

    private void initExitButton(@NonNull View view) {
        view.findViewById(R.id.button_exit).setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Utils.playSound(getContext(), R.raw.click_default);
            Utils.animateClick(v);
            showQuitAlert();
        });
    }

    private void initRecyclerView(@NonNull View view) {
        optionsList = view.findViewById(R.id.options_list);
        OptionsRecyclerViewAdapter mAdapter = new OptionsRecyclerViewAdapter(mOptions, this);
        optionsList.setAdapter(mAdapter);
    }

    @Override
    public void onOptionClicked(int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Utils.playSound(getContext(), R.raw.click_default);
        Utils.animateClick(optionsList.getChildAt(position));
        if (mOptions.get(position) == MaintenanceOption.RESET_PRODUCTS) {
            initResetProductsAlert();
        } else if (mOptions.get(position) == MaintenanceOption.RESET_COINS) {
            initResetCoinsAlert();
        }
    }

    private void initResetCoinsAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_coins);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
            mViewModel.makeCoinsRequest().observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel.getError() == null) {
                    mViewModel.updateLiveDataCoins(responseModel.getItems());
                    Toast.makeText(getContext(), getString(R.string.maintenance_coins_reset), Toast.LENGTH_LONG).show();
                } else {
                    responseModel.getError().printStackTrace();
                    showServerError();
                }
            });
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_cancel,
                (dialog, which) -> Utils.playSound(getContext(), R.raw.click_default));
        alertDialogBuilder.setCancelable(false);
        alertAreYouSure = alertDialogBuilder.create();
        alertAreYouSure.show();
        isQuitShown = false;
        isResetProductsShown = false;
        isResetCoinsShown = true;
    }

    private void initResetProductsAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_products);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
            mViewModel.makeProductsRequest().observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel.getError() == null) {
                    mViewModel.updateLiveDataProducts(responseModel.getItems());
                    Toast.makeText(getContext(), getString(R.string.maintenance_products_reset), Toast.LENGTH_LONG).show();
                } else {
                    responseModel.getError().printStackTrace();
                    showServerError();
                }
            });
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_cancel,
                (dialog, which) -> Utils.playSound(getContext(), R.raw.click_default));
        alertDialogBuilder.setCancelable(false);
        alertAreYouSure = alertDialogBuilder.create();
        alertAreYouSure.show();
        isQuitShown = false;
        isResetProductsShown = true;
        isResetCoinsShown = false;
    }

    private void showQuitAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_quit_maintenance_title);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, (dialog, which) -> {
            Utils.playSound(getContext(), R.raw.click_default);
            NavHostFragment.findNavController(MaintenanceFragment.this)
                    .navigate(R.id.action_products);
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_cancel, (dialog, which) -> Utils.playSound(getContext(), R.raw.click_default));
        alertDialogBuilder.setCancelable(false);
        alertAreYouSure = alertDialogBuilder.create();
        alertAreYouSure.show();
        isQuitShown = true;
        isResetProductsShown = false;
        isResetCoinsShown = false;
    }

    private void showServerError() {
        if (alertNoConn == null || !alertNoConn.isShowing()) {
            alertNoConn = Utils.buildNoInternetDialog(getContext());
            alertNoConn.show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (alertNoConn != null) alertNoConn.dismiss();
        if (alertAreYouSure != null) alertAreYouSure.dismiss();
        outState.putBoolean(IS_RESET_PRODUCTS, isResetProductsShown);
        outState.putBoolean(IS_RESET_COINS, isResetCoinsShown);
        outState.putBoolean(IS_QUIT, isQuitShown);
        super.onSaveInstanceState(outState);
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
}
