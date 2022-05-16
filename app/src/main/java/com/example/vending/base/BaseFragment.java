package com.example.vending.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.example.vending.R;
import com.example.vending.server.ResponseEvent;
import com.example.vending.server.response.ResponseModel;
import com.example.vending.utils.Utils;

import java.net.ConnectException;

public class BaseFragment<T extends BaseViewModel> extends Fragment {

    protected T mViewModel;
    private AlertDialog alertNoConn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getServerErrorLiveData().observe(getViewLifecycleOwner(), responseEvent ->
                handleResponseEvent(responseEvent, response ->
                        showErrorAlert(getString(R.string.error), response.message))
        );
        mViewModel.getOnErrorLiveData().observe(getViewLifecycleOwner(), throwableEvent -> {
            Throwable throwable = throwableEvent.getResponseIfNotHandled();
            if (throwable != null) {
                if (throwable instanceof ConnectException) {
                    mViewModel.setNoConnection(true);
                    showErrorAlert(getString(R.string.no_internet), getString(R.string.check_connection));
                } else {
                    showErrorAlert(getString(R.string.error), throwable.getMessage());
                }
            }
        });
    }

    protected void showErrorAlert(String title, String message) {
        if (alertNoConn == null || !alertNoConn.isShowing()) {
            alertNoConn = Utils.buildNoInternetDialog(getContext(), title, message);
            alertNoConn.show();
//            layoutNoConnection.setVisibility(View.VISIBLE);
        }
    }

    protected <RM extends ResponseModel> void handleResponseEvent(
            @Nullable ResponseEvent<RM> responseEvent,
            @Nullable Consumer<RM> func) {
        if (responseEvent != null) {
            RM response = responseEvent.getResponseIfNotHandled();
            handleResponse(response, func);
        }
    }

    protected <RM extends ResponseModel> void handleResponse(
            @Nullable RM response,
            @Nullable Consumer<RM> func) {
        if (response != null && func != null)
            func.accept(response);
    }
}
