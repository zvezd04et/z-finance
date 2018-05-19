package com.z_soft.z_finance.adapters.holders;

import android.view.View;
import android.widget.TextView;

import com.z_soft.z_finance.R;

// описывает компоненты, которые являются специфичными для списка операций
public class OperationViewHolder extends BaseViewHolder {

    public final TextView tvOperationSubtitle;
    public final TextView tvOperationAmount;
    public final TextView tvOperationTypeTag;


    public OperationViewHolder(View view) {
        super(view);

        tvOperationSubtitle = view.findViewById(R.id.tv_operation_subtitle);
        tvOperationAmount = view.findViewById(R.id.tv_operation_amount);
        tvOperationTypeTag = view.findViewById(R.id.tv_operation_type_tag);

    }
}
