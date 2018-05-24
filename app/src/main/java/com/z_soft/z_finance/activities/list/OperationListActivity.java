package com.z_soft.z_finance.activities.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseListActivity;
import com.z_soft.z_finance.activities.edit.operation.EditIncomeOperationActivity;
import com.z_soft.z_finance.adapters.OperationListAdapter;
import com.z_soft.z_finance.core.impls.operations.IncomeOperation;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.fragments.BaseNodeListFragment;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

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
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runAddOperationActivity(EditIncomeOperationActivity.class, new IncomeOperation());
            }
        });


    }

    // какой активити хотим вызвать для добавления новой операции (в зависимости от типа операции)
    private void runAddOperationActivity(Class activityClass, Operation operation) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(AppContext.NODE_OBJECT, operation); // помещаем выбранный объект operation для передачи в активити
        intent.putExtra(AppContext.OPERATION_ACTION, AppContext.OPERATION_ADD);
        ActivityCompat.startActivityForResult(OperationListActivity.this,intent, AppContext.REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onClick(Operation node) {

    }

}

