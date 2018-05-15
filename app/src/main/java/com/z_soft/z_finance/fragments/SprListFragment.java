package com.z_soft.z_finance.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.SourceNodeAdapter;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.listeners.TreeNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SprListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener tapListener;
    private TreeNodeActionListener<Source> tapListener;
    private SourceNodeAdapter sourceNodeAdapter;
    private List<? extends TreeNode> listNodes;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SprListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SprListFragment newInstance(int columnCount) {
        SprListFragment fragment = new SprListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.node_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // добавить разделитель между элементами списка
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                    .marginResId(R.dimen.divider_left_margin, R.dimen.divider_right_margin)
                    .build());

            recyclerView.setAdapter(sourceNodeAdapter);
        }
        return view;
    }

//    public void updateData(List<? extends TreeNode> list){
//        sourceNodeAdapter.updateData(list);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TreeNodeActionListener) {
            tapListener = (TreeNodeActionListener<Source>) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        listNodes = Initializer.getSourceManager().getAll();
        sourceNodeAdapter = new SourceNodeAdapter(AppContext.SELECT_MODE);
        //sourceNodeAdapter = new SourceNodeAdapter(listNodes);
        sourceNodeAdapter.setListener(tapListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tapListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener { // интерфейс для слушателя события при нажатии записи списка
        void onClickNode(TreeNode selectedNode);
        void onPopupShow(TreeNode selectedNode);
    }
}
