package com.z_soft.z_finance.activities.list;

import android.os.Bundle;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseListActivity;
import com.z_soft.z_finance.adapters.OperationListAdapter;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.fragments.BaseNodeListFragment;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;

public class OperationListActivity extends BaseListActivity<Operation, BaseNodeListFragment> {


    public OperationListActivity() {

        BaseNodeListFragment<Operation, OperationListAdapter, BaseNodeActionListener> fragment = new BaseNodeListFragment<>();
        fragment.setAdapter(new OperationListAdapter());

        // обязательно надо проинициализировать
        init(fragment, R.layout.activity_operation_list, R.id.tlb_operation_list_actions);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarTitle(getResources().getString(R.string.operations));

        createDrawer(toolbar);

    }


    @Override
    protected void initListeners() {
        super.initListeners();

        // при нажатии на кнопку добавления элемента
//        iconAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//                Source source = new DefaultSource();
//
//                // если пользователь выбрал таб и создает новый элемент - сразу прописываем тип
//                if (defaultSourceType != null) {
//                    source.setOperationType(defaultSourceType);
//                }
//
//
//                Intent intent = new Intent(SourceListActivity.this, EditSourceActivity.class); // какой акивити хоти вызвать
//                intent.putExtra(AppContext.NODE_OBJECT, source); // помещаем выбранный объект node для передачи в активити
//                startActivityForResult(intent, AppContext.REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(SourceListActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

//            }
//        });


    }

    @Override
    public void onClick(Operation node) {

    }

}

