package com.example.vending;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vending.server.ServerRequestRx;
import com.example.vending.server.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {

    private NetworkHandler network;
    private ConstraintLayout noConnectionLayout;
    private Button retryButton;
    private long mLastClickTime = 0;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noConnectionLayout = view.findViewById(R.id.layoutNoConnection);
        retryButton = view.findViewById(R.id.button_retry);
        Context context = getContext();
        network = new NetworkHandler(context);
        if (network.isNetworkAvailable()) {
            serverRequest(getString(R.string.request_products));
        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            v.animate().scaleX(1).scaleY(1).setDuration(100);
                        }
                    });
                    if (network.isNetworkAvailable()) {
                        serverRequest(getString(R.string.request_products));
                        retryButton.setOnClickListener(null);
                    }
                }
            });
        }
    }

    private void serverRequest(String request) {
        ServerRequestRx.RxRequest instance = new ServerRequestRx().getInstance();

        Flowable<ResponseBody> response = null;
        if (request.equalsIgnoreCase(getString(R.string.request_products))) {
            response = instance.get(request);
        } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
            response = instance.get(request);
        }
        if (response != null)
            response
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {
//                        Log.e("onSubscribe: ", "in");
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                JSONObject json = new JSONObject(responseBody.string());
                                MainActivity mainActivity = (MainActivity) getActivity();
                                if (request.equalsIgnoreCase(getString(R.string.request_products))) {
                                    JSONArray jsonArray = Utils.extractJsonArray(json, getString(R.string.request_data));
                                    if (jsonArray != null) {
                                        mainActivity.loadProductsToStorage(jsonArray);
                                        serverRequest(getString(R.string.request_coins));
                                    }
                                } else if (request.equalsIgnoreCase(getString(R.string.request_coins))) {
                                    JSONArray jsonArray = Utils.extractJsonArray(json, getString(R.string.request_data));
                                    //TODO null
                                    if (jsonArray != null) {
                                        mainActivity.loadCoinsToStorage(jsonArray);
                                    }
                                    NavHostFragment.findNavController(SplashFragment.this)
                                            .navigate(R.id.action_SplashFragment_to_ProductsFragment);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
    }
}