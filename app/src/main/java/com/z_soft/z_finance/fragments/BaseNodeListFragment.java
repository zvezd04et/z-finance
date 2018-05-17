package com.z_soft.z_finance.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.abstracts.BaseNodeListAdapter;
import com.z_soft.z_finance.core.interfaces.IconNode;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

import java.util.List;

public class BaseNodeListFragment<T extends IconNode, A extends BaseNodeListAdapter, L extends BaseNodeActionListener> extends Fragment {

    private L clickListener;

    protected A adapter; // адаптер для базовых действий

    protected RecyclerView recyclerView;

    protected View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // обязательно сначала нужно определить view
        // каждая реализация подставляет свой макет для отображения
        view = inflater.inflate(R.layout.node_list, container, false);


        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));// выбираем стандартный тип показа - как список

            recyclerView.setAdapter(adapter);

        }
        return view;
    }




    // эти методы выполняются для обновления элементов любого списка
    public void refreshList(List<T> list) {
        adapter.refreshList(list, BaseNodeListAdapter.animatorParents);
    }

    public void updateNode(T node) {
        adapter.updateNode(node);
    }

    public void addNode(T node) {
        adapter.addNode(node);
    }

    public List<T> getList(){
        return adapter.getAdapterList();
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        // если активити, где используется фрагмент, не реализовывает интерфейс - уведомляем исключением
        // таким образом - разработчик принуждается к тому, чтобы активити, где размещен фрагмент, реализовывал этот интерфейс
        if (context instanceof BaseNodeActionListener) {

            clickListener = (L) context;

            adapter.setListener(clickListener);
            adapter.setContext((Activity) getContext());
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NodeActionListener");
        }

    }



    public void setAdapter(A adapter) {
        this.adapter = adapter;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}