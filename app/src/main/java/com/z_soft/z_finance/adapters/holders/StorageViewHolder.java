package com.z_soft.z_finance.adapters.holders;

import android.view.View;
import android.widget.TextView;

import com.z_soft.z_finance.R;

// описывает компоненты, которые являются специфичными для списка счетов
public class StorageViewHolder extends TreeViewHolder {


    public final TextView tvAmount;


    public StorageViewHolder(View view) {
        super(view);
        tvAmount = view.findViewById(R.id.tv_node_amount);
        tvAmount.setVisibility(View.VISIBLE);

    }

}