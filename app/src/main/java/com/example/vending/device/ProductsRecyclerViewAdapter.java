package com.example.vending.device;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vending.R;
import com.example.vending.backend.ItemData;
import com.example.vending.backend.VM;

import java.util.List;
import java.util.Locale;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private List<ItemData> mValues;

    private long mLastClickTime = 0;

    public ProductsRecyclerViewAdapter(List<ItemData> products) {
        mValues = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).getName());
        if (holder.mItem.getQuantity() > 0) {
            holder.mPrice.setText(String.format(Locale.CANADA, "%.2f", (mValues.get(position).getPrice())));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new VM().setSelectedProduct(holder.mItem);
                    NavHostFragment.findNavController(FragmentManager.findFragment(view))
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
            });
        } else {
            holder.mPrice.setText(R.string.product_no_quantity);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mPrice;
        public ItemData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.product_name_new);
            mPrice = (TextView) view.findViewById(R.id.product_price_new);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPrice.getText() + "'";
        }
    }

    public void refreshScreen(List<ItemData> items) {
        this.mValues = items;
        notifyDataSetChanged();
    }
}