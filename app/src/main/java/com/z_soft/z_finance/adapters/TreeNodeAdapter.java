package com.z_soft.z_finance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final List<? extends TreeNode> mValues;
    private final OnListFragmentInteractionListener mListener;

    public TreeNodeAdapter(List<? extends TreeNode> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.mItem = mValues.get(position);
        holder.tvSprName.setText(mValues.get(position).getName());
        //holder.mContentView.setText(mValues.get(position).content);

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public final View mView;
        public final TextView tvSprName;
//        public final TextView mContentView;
//        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvSprName = (TextView) view.findViewById(R.id.spr_name);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}