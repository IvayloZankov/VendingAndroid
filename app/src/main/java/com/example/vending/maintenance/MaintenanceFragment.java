package com.example.vending.maintenance;

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
import com.example.vending.MainActivity;
import com.example.vending.NetworkHandler;
import com.example.vending.server.Utils;
import com.example.vending.server.RequestMethod;
import com.example.vending.server.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MaintenanceFragment extends Fragment implements OptionsRecyclerViewAdapter.OptionListener {

    RecyclerView optionsList;
    OptionsRecyclerViewAdapter mAdapter;
    List<MaintenanceOption> mOptions;

    NetworkHandler network;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }


    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        network = new NetworkHandler(getContext());
        mOptions = new ArrayList<>(Arrays.asList(MaintenanceOption.values()));

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
        if (network.isNetworkAvailable()) {
            if (mOptions.get(position) == MaintenanceOption.RESET_PRODUCTS) {
                serverRequest(getString(R.string.request_products));
            } else if (mOptions.get(position) == MaintenanceOption.RESET_COINS) {
                serverRequest(getString(R.string.request_coins));
            }
        } else {
            network.showNoInternetDialog(getContext());
        }
    }

    private void serverRequest(String request) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        if (request.equalsIgnoreCase(getString(R.string.request_products))) {
            alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_products);
        } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
            alertDialogBuilder.setTitle(R.string.alert_maintenance_reset_coins);
        }
        alertDialogBuilder.setMessage(R.string.alert_quit_maintenance_text);
        alertDialogBuilder.setPositiveButton(R.string.alert_quit_maintenance_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();
                        String jsonString = bundle.getString(getString(R.string.request_items));

                        JSONObject json = null;
                        try {
                            json = new JSONObject(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray = Utils.extractJsonArray(json, getString(R.string.request_data));
                        if (jsonArray != null) {
                            if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                                ((MainActivity) getActivity()).loadProductsToStorage(jsonArray);
                                Toast.makeText(getContext(), "Products reset", Toast.LENGTH_SHORT).show();
                            } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                                ((MainActivity) getActivity()).loadCoinsToStorage(jsonArray);
                                Toast.makeText(getContext(), "Coins reset", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };

                Runnable runnable = new Runnable() {
                    public void run() {
                        JSONObject json = null;
                        if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                            json = new ServerRequest().getResponse(RequestMethod.GET, request);
                        } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                            json = new ServerRequest().getResponse(RequestMethod.GET, request);
                        }
                        if (json != null) {
                            String jsonString = json.toString();
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(getString(R.string.request_items), jsonString);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                };
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(runnable);
                executor.shutdown();
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
