package com.z_soft.z_finance.activities.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.edit.EditSourceActivity;
import com.z_soft.z_finance.activities.abstracts.TreeListActivity;
import com.z_soft.z_finance.adapters.SourceNodeAdapter;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.impls.DefaultSource;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.fragments.TreeNodeListFragment;
import com.z_soft.z_finance.utils.AppContext;

public class SourceListActivity extends TreeListActivity<Source> {


    private TabLayout tabLayout;

    private OperationType defaultSourceType;// для автоматического проставления типа (доход, расход) при создании нового элемента

    private OperationType filterType;



    public SourceListActivity() {

        TreeNodeListFragment<Source> fragment = new TreeNodeListFragment<>();

        // обязательно надо проинициализировать
        init(fragment, R.layout.activity_source_list, R.id.tlb_tree_list_actions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filterType = OperationType.getType(getIntent().getIntExtra(AppContext.LIST_TYPE, -1));

        if (filterType==null) {
            fragment.setAdapter(new SourceNodeAdapter(mode));
        }else{
            fragment.setAdapter(new SourceNodeAdapter(mode, Initializer.getSourceManager().getList(filterType)));
        }

        setToolbarTitle(getResources().getString(R.string.sources));

    }

    @Override
    protected void initToolbar() {
        super.initToolbar();

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (mode == AppContext.EDIT_MODE) {
            tabLayout.setVisibility(View.VISIBLE);
            initTabs();
        }else{
            tabLayout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initListeners() {
        super.initListeners();

        // при нажатии на кнопку добавления элемента
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Source source = new DefaultSource();

                // если пользователь выбрал таб и создает новый элемент - сразу прописываем тип
                if (defaultSourceType != null) {
                    source.setOperationType(defaultSourceType);
                }

                Intent intent = new Intent(SourceListActivity.this, EditSourceActivity.class); // какой акивити хоти вызвать
                intent.putExtra(AppContext.NODE_OBJECT, source); // помещаем выбранный объект node для передачи в активити
                ActivityCompat.startActivityForResult(SourceListActivity.this,intent, AppContext.REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(SourceListActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

            }
        });


    }




    private void initTabs() {

        tabLayout.getTabAt(0).setText(R.string.tab_all);
        tabLayout.getTabAt(1).setText(R.string.tab_income);
        tabLayout.getTabAt(2).setText(R.string.tab_outcome);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // при переключении табов - сбросить название и убрать стрелку возврата к родительским элементам
                icBack.setVisibility(View.INVISIBLE);
                toolbarTitle.setText(R.string.sources);

                showRootNodes();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });



    }

    @Override
    protected void showRootNodes() {
        super.showRootNodes();

        if (mode == AppContext.EDIT_MODE) {

            switch (tabLayout.getSelectedTabPosition()) {
                case 0:// все
                    fragment.refreshList(Initializer.getSourceManager().getAll());
                    defaultSourceType = null;
                    break;
                case 1:// доход
                    fragment.refreshList(Initializer.getSourceManager().getList(OperationType.INCOME));
                    defaultSourceType = OperationType.INCOME;
                    break;
                case 2: // расход
                    fragment.refreshList(Initializer.getSourceManager().getList(OperationType.OUTCOME));
                    defaultSourceType = OperationType.OUTCOME;
                    break;
            }
        }else{
            fragment.refreshList(Initializer.getSourceManager().getList(filterType));
        }


    }

    @Override
    protected void showParentNodes() {

        if (selectedNode==null || selectedNode.getParent() == null) {
            defaultSourceType = null;
        }

        super.showParentNodes();

    }

    @Override
    public void onPopup(Source node) {
        super.onPopup(node);
        defaultSourceType =node.getOperationType();// сохраняем тип, чтобы при создании нового элемента - автоматически его прописывать
    }

    @Override
    public void onClick(Source node) {
        super.onClick(node);
        defaultSourceType =node.getOperationType();// сохраняем тип, чтобы при создании нового элемента - автоматически его прописывать
    }



}
