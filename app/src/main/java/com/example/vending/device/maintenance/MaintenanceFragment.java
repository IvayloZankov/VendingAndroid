package com.example.vending.device.maintenance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.example.vending.backend.MaintenanceOption;
import com.example.vending.backend.VM;
import com.example.vending.device.NetworkHandler;
import com.example.vending.server.JsonUtil;
import com.example.vending.server.RequestMethod;
import com.example.vending.server.RequestUrl;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MaintenanceFragment extends Fragment implements OptionsRecyclerViewAdapter.OptionListener {

    RecyclerView optionsList;
    OptionsRecyclerViewAdapter mAdapter;
    List<MaintenanceOption> mOptions;

    VM vm;
    NetworkHandler network;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }


    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new VM();
        network = new NetworkHandler(getContext());
        mOptions = vm.getMaintenanceOptions();

        handleBackButton();

        optionsList = view.findViewById(R.id.options_list);
        mAdapter = new OptionsRecyclerViewAdapter(mOptions, this);
        optionsList.setAdapter(mAdapter);

        view.findViewById(R.id.button_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showQuitAlert();
            }
        });
    }

    @Override
    public void onOptionClicked(int position) {
        if (mOptions.get(position) == MaintenanceOption.RESET_PRODUCTS) {
            if (network.isNetworkAvailable()) {
                resetProducts();
            } else {
                network.showNoInternetDialog(getContext());
            }
        } else if (mOptions.get(position) == MaintenanceOption.RESET_COINS) {
            resetCoins();
        }
    }

    private void resetProducts() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_products);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();
                        String jsonString = bundle.getString("products");

                        JSONObject json = null;
                        try {
                            json = new JSONObject(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        vm.initProductsStorage();
                        JSONArray jsonArray = JsonUtil.convertToArray(json, "data");
                        if (jsonArray != null) {
                            vm.loadProductsToStorage(jsonArray);
                            Toast.makeText(getContext(), "Products reset", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                Runnable runnable = new Runnable() {
                    public void run() {
                        JSONObject json = new ServerRequest().getResponse(RequestMethod.GET, RequestUrl.GET_PRODUCTS.toString());
                        String jsonString = json.toString();
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("products", jsonString);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                };
                Thread myThread = new Thread(runnable);
                myThread.start();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_quit_maintenance_cancel, null);
        alertDialogBuilder.setCancelable(false).show();

    }

    private void resetCoins() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_coins);
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vm.loadCoinsToStorage();
                Toast.makeText(getContext(), "Coins reset", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_quit_maintenance_cancel, null);
        alertDialogBuilder.setCancelable(false).show();

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
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NavHostFragment.findNavController(MaintenanceFragment.this)
                        .navigate(R.id.action_MaintenanceFragment_to_ProductsFragment);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_quit_maintenance_cancel, null);
        alertDialogBuilder.setCancelable(false).show();
    }
}
