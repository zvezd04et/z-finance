package com.z_soft.z_finance.activities.edit.operation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.z_soft.z_finance.activities.list.StorageListActivity;
import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.impls.operations.TransferOperation;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.ColorUtils;
import com.z_soft.z_finance.utils.IconUtils;
import com.z_soft.z_finance.utils.OperationTypeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

// отвечает за добавление и редактирование операции Перевода
public class EditTransferOperationActivity extends BaseEditOperationActivity<TransferOperation> {

    private static final String TAG = EditTransferOperationActivity.class.getName();


    protected TextView tvOperationStorageFrom;
    protected ViewGroup layoutOperationStorageFrom;
    protected ImageView icOperationStorageFrom;


    protected TextView tvOperationStorageTo;
    protected ViewGroup layoutOperationStorageTo;
    protected ImageView icOperationStorageTo;


    protected EditText etOperationAmount;
    protected Spinner spnCurrency;


    public EditTransferOperationActivity() {
        super(R.layout.activity_edit_transfer_operation);// какой макет будет использоваться
    }


    // список возможных валют для операции
    private List<Currency> currencyList = new ArrayList<>();
    private ArrayAdapter<Currency> сurrencyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();

        tvOperationType.setText(OperationTypeUtils.transferType.toString());
        tvOperationType.setBackgroundColor(ContextCompat.getColor(EditTransferOperationActivity.this,ColorUtils.transferColor));

        if (actionType == AppContext.OPERATION_EDIT) {
            setValues();
        }


        // для выбора справочного значения storage
        layoutOperationStorageFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorageFrom;

                Intent intent = new Intent(EditTransferOperationActivity.this, StorageListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);
                ActivityCompat.startActivityForResult(EditTransferOperationActivity.this, intent, REQUEST_SELECT_STORAGE_FROM, ActivityOptionsCompat.makeSceneTransitionAnimation(EditTransferOperationActivity.this).toBundle());


            }


        });


        // для выбора справочного значения
        layoutOperationStorageTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorageTo;

                Intent intent = new Intent(EditTransferOperationActivity.this, StorageListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);

                ActivityCompat.startActivityForResult(EditTransferOperationActivity.this, intent, REQUEST_SELECT_STORAGE_TO, ActivityOptionsCompat.makeSceneTransitionAnimation(EditTransferOperationActivity.this).toBundle());


            }


        });


        // слушатель события при нажатии на кнопку сохранения
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkValues()) {
                    return;
                }

                Currency selectedCurrency = (Currency) spnCurrency.getSelectedItem();

                // если при переводе в другой счет - там нет нужной валюты - создать ее
                if (!operation.getToStorage().getAvailableCurrencies().contains(selectedCurrency)) {
                    try {
                        operation.getToStorage().addCurrency(selectedCurrency, BigDecimal.ZERO);
                    } catch (CurrencyException e) {
                        Log.e(TAG, e.getMessage());

                    }
                }


                operation.setFromAmount(convertString(etOperationAmount.getText().toString()));


                operation.setDescription(etOperationDesc.getText().toString());
                operation.setDateTime(calendar);
                operation.setFromCurrency(selectedCurrency);

                Intent intent = new Intent();
                intent.putExtra(AppContext.NODE_OBJECT, operation);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                setResult(RESULT_OK, intent);

                ActivityCompat.finishAfterTransition(EditTransferOperationActivity.this);//закрыть активити

            }


        });


    }

    private void setValues() {

        fillCurrencySpinner();

        etOperationAmount.setText(operation.getFromAmount().toString());

        icOperationStorageFrom.setImageDrawable(IconUtils.getIcon(operation.getFromStorage().getIconName()));
        icOperationStorageTo.setImageDrawable(IconUtils.getIcon(operation.getToStorage().getIconName()));

        tvOperationStorageTo.setText(operation.getToStorage().getName().toUpperCase());
        tvOperationStorageFrom.setText(operation.getFromStorage().getName().toUpperCase());

    }

    private void initComponents() {
        icOperationStorageFrom = findViewById(R.id.ic_operation_from_storage_selected);
        icOperationStorageTo = findViewById(R.id.ic_operation_to_storage_selected);

        tvOperationStorageFrom = findViewById(R.id.tv_operation_from_storage_selected);
        tvOperationStorageTo = findViewById(R.id.tv_operation_to_storage_selected);
        etOperationAmount = findViewById(R.id.et_operation_amount_selected);

        layoutOperationStorageFrom = findViewById(R.id.layout_operation_from_storage);
        layoutOperationStorageTo = findViewById(R.id.layout_operation_to_storage);

        spnCurrency = findViewById(R.id.spn_currency);
        сurrencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyList);
        spnCurrency.setAdapter(сurrencyAdapter);

    }


    // проверяет, заполнены ли обязательные значения
    private boolean checkValues() {

        if (etOperationAmount.getText().length() == 0) {
            Toast.makeText(EditTransferOperationActivity.this, R.string.enter_amount, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (operation.getFromStorage() == null) {
            Toast.makeText(EditTransferOperationActivity.this, R.string.select_storage_from, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (operation.getToStorage() == null) {
            Toast.makeText(EditTransferOperationActivity.this, R.string.select_storage_to, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;// если все проверки прошли успешно - возвращается true

    }


    // заполнить выпадающий список значениями валют
    private void fillCurrencySpinner() {

        currencyList.clear();
        currencyList.addAll(operation.getFromStorage().getAvailableCurrencies());

        сurrencyAdapter.clear();
        сurrencyAdapter.addAll(operation.getFromStorage().getAvailableCurrencies());


        сurrencyAdapter.notifyDataSetChanged();

        spnCurrency.setSelection(currencyList.indexOf(operation.getFromCurrency()));
    }


    // сюда попадаем, когда возвращается результат выбора какого либо справочного значения
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Storage storage = (Storage) data.getSerializableExtra(AppContext.NODE_OBJECT);


            switch (requestCode) {
                case REQUEST_SELECT_STORAGE_FROM:

                    operation.setFromStorage(storage);
                    icOperationStorageFrom.setImageDrawable(IconUtils.getIcon(storage.getIconName()));
                    currentNodeSelect.setText(storage.getName().toUpperCase());

                    updateCurrencyList(storage, currencyList, сurrencyAdapter, spnCurrency);


                    break;

                case REQUEST_SELECT_STORAGE_TO:
                    operation.setToStorage(storage);
                    icOperationStorageTo.setImageDrawable(IconUtils.getIcon(storage.getIconName()));
                    currentNodeSelect.setText(storage.getName().toUpperCase());


                    break;

            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (operation.getFromStorage() != null) {
            updateCurrencyList(operation.getFromStorage(), currencyList, сurrencyAdapter, spnCurrency);
        }

    }


}