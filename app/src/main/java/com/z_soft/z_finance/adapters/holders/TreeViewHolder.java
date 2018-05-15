package com.z_soft.z_finance.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_soft.z_finance.R;


public class TreeViewHolder extends BaseViewHolder {

    public final ImageView btnPopup;
    public final TextView tvChildCount;


    public TreeViewHolder(View view) {
        super(view);

        // чтобы для каждого компонента не выполнять findViewById - сохраняем ссылки в константы
        btnPopup = (ImageView) view.findViewById(R.id.img_node_popup);
        tvChildCount = (TextView) view.findViewById(R.id.tv_node_child_count);

    }

}
