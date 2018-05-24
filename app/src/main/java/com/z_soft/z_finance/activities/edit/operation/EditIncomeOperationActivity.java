package com.z_soft.z_finance.activities.edit.operation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseEditOperationActivity;
import com.z_soft.z_finance.activities.list.SourceListActivity;
import com.z_soft.z_finance.activities.list.StorageListActivity;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.impls.operations.IncomeOperation;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.ColorUtils;
import com.z_soft.z_finance.utils.IconUtils;
import com.z_soft.z_finance.utils.OperationTypeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

// отвечает за добавление и редактирование категории
public class EditIncomeOperationActivity extends BaseEditOperationActivity<IncomeOperation> {


    protected TextView tvOperationSource;
    protected ViewGroup layoutOperationSource;
    protected ImageView icOperationSource;


    protected TextView tvOperationStorage;
    protected ImageView icOperationStorage;
    protected ViewGroup layoutOperationStorage;

    protected EditText etOperationAmount;
    protected Spinner spnCurrency;


    public EditIncomeOperationActivity() {
        super(R.layout.activity_edit_income_operation);// какой макет будет использоваться
    }


    // список возможных валют для операции
    private List<Currency> currencyList = new ArrayList<>();
    private ArrayAdapter<Currency> currencyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();

        tvOperationType.setText(OperationTypeUtils.incomeType.toString());
        tvOperationType.setBackgroundColor(ContextCompat.getColor(EditIncomeOperationActivity.this, ColorUtils.incomeColor));

        if (actionType == AppContext.OPERATION_EDIT) {
            setValues();
        }


        // для выбора справочного значения storage
        layoutOperationStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorage;

                Intent intent = new Intent(EditIncomeOperationActivity.this, StorageListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);
                ActivityCompat.startActivityForResult(EditIncomeOperationActivity.this,intent, REQUEST_SELECT_STORAGE_TO, ActivityOptionsCompat.makeSceneTransitionAnimation(EditIncomeOperationActivity.this).toBundle());


            }


        });


        // для выбора справочного значения source
        layoutOperationSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationSource;

                Intent intent = new Intent(EditIncomeOperationActivity.this, SourceListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);

                // параметр для фильтрации списка по типу (чтобы не все source показывал, а только по этому типу)
                intent.putExtra(AppContext.LIST_TYPE, OperationType.INCOME.getId());// передаем параметр, который позволит выбирать значение и возвращать его

                ActivityCompat.startActivityForResult(EditIncomeOperationActivity.this,intent, REQUEST_SELECT_SOURCE_FROM, ActivityOptionsCompat.makeSceneTransitionAnimation(EditIncomeOperationActivity.this).toBundle());


            }


        });


        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkValues()) {
                    return;
                }

                operation.setFromAmount(convertString(etOperationAmount.getText().toString()));

                operation.setDescription(etOperationDesc.getText().toString());
                operation.setDateTime(calendar);

                operation.setFromCurrency((Currency) spnCurrency.getSelectedItem());


                Intent intent = new Intent();
                intent.putExtra(AppContext.NODE_OBJECT, operation);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                setResult(RESULT_OK, intent);

                ActivityCompat.finishAfterTransition(EditIncomeOperationActivity.this);//закрыть активити
            }


        });


    }

    private void setValues() {

        initCurrencySpinner();

        tvOperationSource.setText(operation.getFromSource().getName().toUpperCase());
        tvOperationStorage.setText(operation.getToStorage().getName().toUpperCase());
        etOperationAmount.setText(operation.getFromAmount().toString());

        icOperationSource.setImageDrawable(IconUtils.getIcon(operation.getFromSource().getIconName()));
        icOperationStorage.setImageDrawable(IconUtils.getIcon(operation.getToStorage().getIconName()));
    }

    private void initComponents() {
        icOperationSource = findViewById(R.id.ic_operation_source_selected);
        icOperationStorage = findViewById(R.id.ic_operation_storage_selected);

        tvOperationSource = findViewById(R.id.tv_operation_source_selected);
        tvOperationStorage = findViewById(R.id.tv_operation_storage_selected);
        etOperationAmount = findViewById(R.id.et_operation_amount_selected);

        layoutOperationSource = findViewById(R.id.layout_operation_source);
        layoutOperationStorage = findViewById(R.id.layout_operation_storage);

        spnCurrency = findViewById(R.id.spn_currency);
        currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyList);
        spnCurrency.setAdapter(currencyAdapter);

    }


    // проверяет, заполнены ли обязательные значения
    private boolean checkValues() {

        if (etOperationAmount.getText().length() == 0) {
            Toast.makeText(EditIncomeOperationActivity.this, R.string.enter_amount, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (operation.getFromSource() == null) {
            Toast.makeText(EditIncomeOperationActivity.this, R.string.select_source_from, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (operation.getToStorage() == null) {
            Toast.makeText(EditIncomeOperationActivity.this, R.string.select_storage_to, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;// если все проверки прошли успешно - возвращается true

    }


    // заполнить выпадающий список значениями валют
    private void initCurrencySpinner() {

        currencyList.clear();
        currencyList.addAll(operation.getToStorage().getAvailableCurrencies());

        currencyAdapter.clear();
        currencyAdapter.addAll(operation.getToStorage().getAvailableCurrencies());

        currencyAdapter.notifyDataSetChanged();
        spnCurrency.setSelection(currencyList.indexOf(operation.getFromCurrency()));
    }


    // сюда попадаем, когда возвращается результат выбора какого либо справочного значения
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_SELECT_STORAGE_TO: // если выбирали storage
                    Storage storage = (Storage) data.getSerializableExtra(AppContext.NODE_OBJECT);
                    operation.setToStorage(storage);
                    icOperationStorage.setImageDrawable(IconUtils.getIcon(storage.getIconName()));
                    currentNodeSelect.setText(storage.getName().toUpperCase());


                    updateCurrencyList(storage, currencyList, currencyAdapter, spnCurrency);


                    break;


                case REQUEST_SELECT_SOURCE_FROM: // если выбирали source
                    Source source = (Source) data.getSerializableExtra(AppContext.NODE_OBJECT);
                    operation.setFromSource(source);
                    icOperationSource.setImageDrawable(IconUtils.getIcon(source.getIconName()));
                    currentNodeSelect.setText(source.getName().toUpperCase());
                    break;


            }


        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (operation.getToStorage()!=null){
            updateCurrencyList(operation.getToStorage(), currencyList, currencyAdapter, spnCurrency);
        }


    }

}

