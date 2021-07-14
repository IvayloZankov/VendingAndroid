package com.example.vending.device.maintenance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vending.R;
import com.example.vending.backend.MaintenanceOption;

import java.util.List;

public class OptionsRecyclerViewAdapter extends RecyclerView.Adapter<OptionsRecyclerViewAdapter.ViewHolder> {

    private List<MaintenanceOption> mOptions;
    private OptionListener mOptionListener;

    private long mLastClickTime = 0;

    public OptionsRecyclerViewAdapter(List<MaintenanceOption> options, OptionListener optionListener) {
        mOptions = options;
        this.mOptionListener = optionListener;
    }

    public interface OptionListener {
        void onOptionClicked(int position);
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public String mItem;

        OptionListener optionListener;

        public ViewHolder(View view, OptionListener optionListener) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.maintenance_option);
            this.optionListener = optionListener;

            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            optionListener.onOptionClicked(getAdapterPosition());
        }
    }
}