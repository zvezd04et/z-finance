package com.z_soft.z_finance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.z_soft.z_finance.adapters.abstracts.TreeNodeListAdapter;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.listeners.TreeNodeActionListener;

public class TreeNodeListFragment<T extends TreeNode> extends BaseNodeListFragment<T, TreeNodeListAdapter, TreeNodeActionListener> {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); // потом уже вызывать родительский метод
        return view;
    }

    public void insertChildNode(TreeNode node) {
        adapter.insertChildNode(node);
    }


}
