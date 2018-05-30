package com.z_soft.z_finance.activities.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Button;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseListActivity;
import com.z_soft.z_finance.activities.edit.operation.EditConvertOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditIncomeOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditOutcomeOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditTransferOperationActivity;
import com.z_soft.z_finance.adapters.OperationListAdapter;
import com.z_soft.z_finance.core.impls.operations.ConvertOperation;
import com.z_soft.z_finance.core.impls.operations.IncomeOperation;
import com.z_soft.z_finance.core.impls.operations.OutcomeOperation;
import com.z_soft.z_finance.core.impls.operations.TransferOperation;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.fragments.BaseNodeListFragment;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

public class OperationListActivity extends BaseListActivity<Operation, BaseNodeListFragment> {

    private Button btnAddIncome;
    private Button btnAddOutcome;
    private Button btnAddTransfer;
    private Button btnAddConvert;

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

        btnAddIncome = findViewById(R.id.btn_add_income);
        btnAddOutcome = findViewById(R.id.btn_add_outcome);
        btnAddTransfer = findViewById(R.id.btn_add_transfer);
        btnAddConvert = findViewById(R.id.btn_add_convert);


        // каждая кнопка создает свой операцию нужного типа
        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAddOperationActivity(EditIncomeOperationActivity.class, new IncomeOperation());
            }
        });

        btnAddOutcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAddOperationActivity(EditOutcomeOperationActivity.class, new OutcomeOperation());
            }
        });

        btnAddTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAddOperationActivity(EditTransferOperationActivity.class, new TransferOperation());
            }
        });

        btnAddConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAddOperationActivity(EditConvertOperationActivity.class, new ConvertOperation());
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

