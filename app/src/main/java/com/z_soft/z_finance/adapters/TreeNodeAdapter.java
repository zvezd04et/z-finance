package com.z_soft.z_finance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.SprListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TreeNode} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {

    private List<? extends TreeNode> nodeList;
    private final OnListFragmentInteractionListener tapListener;

    public TreeNodeAdapter(List<? extends TreeNode> items, OnListFragmentInteractionListener listener) {
        nodeList = items;
        tapListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_node, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final TreeNode node = nodeList.get(position);

        holder.tvSprName.setText(nodeList.get(position).getName());

        if (node.getChilds() != null && !node.getChilds().isEmpty()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
        }else {
            holder.tvChildCount.setText("");
        }

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != tapListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    tapListener.onListFragmentInteraction(node);
                }

                if (node.hasChilds()) {// если есть дочерние значения
                    updateData(node.getChilds());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return nodeList.size();
    }

    public void updateData(List<? extends TreeNode> nodeList){
        this.nodeList = nodeList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvSprName;
        public final TextView tvChildCount;
        public final ViewGroup layoutMain;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvSprName = view.findViewById(R.id.spr_name);
            layoutMain = (LinearLayout) view.findViewById(R.id.spr_main_layout);
            tvChildCount = view.findViewById(R.id.spr_child_count);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
