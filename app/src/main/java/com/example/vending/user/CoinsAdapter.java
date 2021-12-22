package com.example.vending.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.server.ResponseModel;

import java.util.List;
import java.util.Locale;

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinsHolder> {

    private final CoinListener cListener;
    private final List<ResponseModel.Item> mCoins;

    public CoinsAdapter(List<ResponseModel.Item> mCoins, CoinListener cListener) {
        this.mCoins = mCoins;
        this.cListener = cListener;
    }

    @NonNull
    @Override
    public CoinsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin, parent, false);
        return new CoinsHolder(view, cListener);
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
        void onCoinClick(int position, View v);
    }

    public static class CoinsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View mView;
        public final TextView mNominal;
        public ResponseModel.Item mItem;

        public final CoinListener cListener;

        public CoinsHolder(@NonNull View itemView, CoinListener cListener) {
            super(itemView);
            mView = itemView;
            mNominal = itemView.findViewById(R.id.coin_nominal_text);
            this.cListener = cListener;
            mView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            cListener.onCoinClick(getAbsoluteAdapterPosition(), view);
        }
    }
}
