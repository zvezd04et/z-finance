package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.SelectIconAdapter;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.IconListFragment;

public class SelectIconActivity<T extends TreeNode> extends AbstractAnimationActivity implements IconListFragment.SelectIconListener {// AbstractAnimationActivity устанавливает анимацию при открытии или закрытии {

    public final static int REQUEST_SELECT_ICON = 201;
    public static final String ICON_NAME = "SelectIconActivity.IconName";

    private ImageView imgClose;

    private IconListFragment iconListFragment;
    private SelectIconAdapter iconAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon);

        initToolbar();

//        RecyclerView rv = findViewById(R.id.icon_list_fragment);
//        iconAdapter = (SelectIconAdapter)rv.getAdapter();
        //initFragment();

    }

    private void initFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        iconListFragment = new IconListFragment();
        fragmentTransaction.replace(R.id.icon_list_fragment, iconListFragment);
        fragmentTransaction.commit();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tlb_tree_list_actions);
        setSupportActionBar(toolbar);

        imgClose = findViewById(R.id.img_close_select_icon);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithTransition();
            }
        });
    }


    @Override
    public void onIconSelected(String name) {

        Intent intent = new Intent();
        intent.putExtra(ICON_NAME, name);// обратно передаем имя выбранной иконки
        setResult(RESULT_OK, intent);

        finishWithTransition();

    }
}