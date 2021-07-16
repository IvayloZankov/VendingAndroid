package com.example.vending.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.ItemData;

import java.util.List;
import java.util.Locale;

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinsHolder> {

    private CoinListener cListener;
    private List<ItemData> mCoins;

    public CoinsAdapter(List<ItemData> mCoins, CoinListener cListener) {
        this.mCoins = mCoins;
        this.cListener = cListener;
    }

    @NonNull
    @Override
    public CoinsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin, parent, false);
        return new CoinsAdapter.CoinsHolder(view, cListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinsHolder holder, int position) {
        holder.mItem = mCoins.get(position);
        holder.mNominal.setText(String.format(Locale.CANADA, "%.2f", (mCoins.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {
        return mCoins.size();
    }

    public interface CoinListener {
        void onCoinClick(int position);
    }

    public class CoinsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View mView;
        public TextView mNominal;
        public ItemData mItem;

        CoinListener cListener;

        public CoinsHolder(@NonNull View itemView, CoinListener cListener) {
            super(itemView);
            mView = itemView;
            mNominal = itemView.findViewById(R.id.coin_nominal_text);
            this.cListener = cListener;
            mView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            cListener.onCoinClick(getAbsoluteAdapterPosition());
        }
    }
}
