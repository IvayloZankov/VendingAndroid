package com.example.vending.screen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vending.R;
import com.example.vending.machine.ItemData;
import com.example.vending.machine.Storage;
import com.example.vending.machine.VM;

import java.util.Locale;

public class ProductsFragment extends Fragment {

    ViewGroup productsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    private long mLastClickTime = 0;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsList = (ViewGroup) view.findViewById(R.id.products_list);
        VM vm = new VM();

        if (!vm.canReturnChange()) {
            showOutOfOrderAlert();
        }

        Storage<ItemData> products = vm.getProducts();

        for (int i = 0; i < products.getSize(); i++) {
            createItemButton(view, products.getItem(i));
        }
//        view.findViewById(R.id.button_reset_products).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                vm.loadProductsToStorage();
//            }
//        });
    }

    private void createItemButton(View view, ItemData product) {
        Button button = new Button(view.getContext());
        setButtonsStyle(button);
        this.productsList.addView(button);
        if (product.getQuantity() > 0) {
            String price = String.format(Locale.CANADA, "%.2f", (product.getPrice()));
            button.setText(getResources().getString(R.string.product_button_format, product.getName(), price));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new VM().setSelectedProduct(product);
                    NavHostFragment.findNavController(ProductsFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
            });
        } else {
            button.setText(getResources().getString(R.string.product_no_quantity, product.getName()));
        }
    }

    private void setButtonsStyle(Button button) {
        button.setBackgroundResource(R.drawable.product_button_round_background);
        button.setTextColor(getResources().getColor(R.color.design_default_color_secondary_variant));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
    }

    private void showOutOfOrderAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.alert_title_out_of_order);
        alertDialogBuilder.setMessage(R.string.alert_text_enter_maintenance);
        alertDialogBuilder.setPositiveButton(R.string.alert_out_of_order_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new VM().loadCoinsToStorage();
            }
                })
        .setCancelable(false).show();
    }
}