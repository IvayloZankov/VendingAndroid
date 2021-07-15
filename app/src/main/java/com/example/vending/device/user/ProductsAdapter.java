package com.example.vending.device.user;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vending.R;
import com.example.vending.backend.ItemData;

import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> {

    private List<ItemData> mProducts;
    private ProductListener pListener;

    public ProductsAdapter(List<ItemData> products, ProductListener pListener) {
        this.mProducts = products;
        this.pListener = pListener;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view, pListener);
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

    public interface ProductListener {
        void onProductClick(int position);
    }

    public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public final TextView mPrice;
        public ItemData mItem;

        ProductListener pListener;

        public ProductHolder(View view, ProductListener pListener) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.product_name_new);
            mPrice = (TextView) view.findViewById(R.id.product_price_new);
            this.pListener = pListener;
            mView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPrice.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            pListener.onProductClick(getAbsoluteAdapterPosition());
        }
    }

    public void refreshScreen(List<ItemData> items) {
        this.mProducts = items;
        notifyDataSetChanged();
    }
}