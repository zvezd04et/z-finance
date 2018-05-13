package com.z_soft.z_finance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.fragments.IconListFragment;
import com.z_soft.z_finance.utils.IconUtils;

import java.util.List;

public class SelectIconAdapter extends RecyclerView.Adapter<SelectIconAdapter.ViewHolder> {

    private List<String> iconNames;
    private Context context;
    private IconListFragment.SelectIconListener selectIconListener;

    public SelectIconAdapter(Context context, List<String> iconNames, IconListFragment.SelectIconListener selectIconListener) {
        this.iconNames = iconNames;
        this.context = context;
        this.selectIconListener = selectIconListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String iconName = iconNames.get(position);

        holder.iconCard.setImageDrawable(IconUtils.iconsMap.get(iconName));


        holder.iconCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIconListener.onIconSelected(iconName);
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.iconNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView iconCard;

        public ViewHolder(View view) {
            super(view);
            iconCard = (ImageView) view.findViewById(R.id.icon_card);

        }

    }
}
