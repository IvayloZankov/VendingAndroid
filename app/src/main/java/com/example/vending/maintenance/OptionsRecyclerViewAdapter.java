package com.example.vending.maintenance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;

import java.util.List;

public class OptionsRecyclerViewAdapter extends RecyclerView.Adapter<OptionsRecyclerViewAdapter.ViewHolder> {

    private final List<MaintenanceOption> mOptions;
    private final OptionListener mOptionListener;

    public OptionsRecyclerViewAdapter(List<MaintenanceOption> options, OptionListener optionListener) {
        mOptions = options;
        this.mOptionListener = optionListener;
    }

    public interface OptionListener {
        void onOptionClicked(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance, parent, false);
        return new ViewHolder(view, mOptionListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mName.setText(mOptions.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;

        public final OptionListener optionListener;

        public ViewHolder(View view, OptionListener optionListener) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.maintenance_option);
            this.optionListener = optionListener;

            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            optionListener.onOptionClicked(getAbsoluteAdapterPosition());
        }
    }
}