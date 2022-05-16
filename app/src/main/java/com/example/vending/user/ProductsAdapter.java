package com.example.vending.user;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vending.R;
import com.example.vending.server.response.ResponseModel;
import com.example.vending.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> {

    private List<ResponseModel.Item> mProducts = new ArrayList<>();
    private final ProductClickListener mClickListener;

    public ProductsAdapter(ProductClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, int position) {
        holder.mItem = mProducts.get(position);
        holder.mName.setText(mProducts.get(position).getName());
        if (holder.mItem.getQuantity() > 0) {
            holder.mPrice.setText(String.format(Locale.CANADA, "%.2f", (mProducts.get(position).getPrice())));
        } else {
            holder.mPrice.setText(R.string.product_no_quantity);
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public void setProducts(List<ResponseModel.Item> products) {
        mProducts = products;
        notifyDataSetChanged();
    }

    public interface ProductClickListener {
        void onProductClick(int position, View v);
    }

    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public final TextView mPrice;
        public ResponseModel.Item mItem;

        public final ProductClickListener pListener;

        public ProductHolder(View view, ProductClickListener pListener) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.product_name_new);
            mPrice = view.findViewById(R.id.product_price_new);
            this.pListener = pListener;
            mView.setOnClickListener(this);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mPrice.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            SoundManager.getInstance().playClick();
//            Utils.animateClick(view);
            pListener.onProductClick(getAbsoluteAdapterPosition(), view);
        }
    }
}