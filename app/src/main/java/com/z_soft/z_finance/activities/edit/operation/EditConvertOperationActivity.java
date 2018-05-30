package com.z_soft.z_finance.activities.edit.operation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
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
import com.z_soft.z_finance.core.impls.operations.ConvertOperation;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.ColorUtils;
import com.z_soft.z_finance.utils.IconUtils;
import com.z_soft.z_finance.utils.OperationTypeUtils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

// отвечает за добавление и редактирование операции Обмена
public class EditConvertOperationActivity extends BaseEditOperationActivity<ConvertOperation> {


    // специфичные поля для типа операции

    protected TextView tvOperationStorageFrom;
    protected ViewGroup layoutOperationStorageFrom;
    protected ImageView icOperationStorageFrom;


    protected TextView tvOperationStorageTo;
    protected ViewGroup layoutOperationStorageTo;
    protected ImageView icOperationStorageTo;


    protected EditText etOperationAmountFrom;
    protected Spinner spnCurrencyFrom;

    protected EditText etOperationAmountTo;
    protected Spinner spnCurrencyTo;


    public EditConvertOperationActivity() {
        super(R.layout.activity_edit_convert_operation);// какой макет будет использоваться
    }


    // список возможных валют для операции
    private List<Currency> currencyListFrom = new ArrayList<>();
    private ArrayAdapter<Currency> currencyAdapterFrom;

    private List<Currency> currencyListTo = new ArrayList<>();
    private ArrayAdapter<Currency> currencyAdapterTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();

        // заполнить тип операции
        tvOperationType.setText(OperationTypeUtils.convertType.toString());
        tvOperationType.setBackgroundColor(ContextCompat.getColor(EditConvertOperationActivity.this, ColorUtils.convertColor));

        if (actionType == AppContext.OPERATION_EDIT) {
            initValues();
        }


        // выбор справочного значения storage для операции
        layoutOperationStorageFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorageFrom;

                Intent intent = new Intent(EditConvertOperationActivity.this, StorageListActivity.class);


                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);
                ActivityCompat.startActivityForResult(EditConvertOperationActivity.this, intent, REQUEST_SELECT_STORAGE_FROM, ActivityOptionsCompat.makeSceneTransitionAnimation(EditConvertOperationActivity.this).toBundle());


            }


        });


        // выбор справочного значения source для операции
        layoutOperationStorageTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorageTo;

                Intent intent = new Intent(EditConvertOperationActivity.this, StorageListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);

                ActivityCompat.startActivityForResult(EditConvertOperationActivity.this, intent, REQUEST_SELECT_STORAGE_TO, ActivityOptionsCompat.makeSceneTransitionAnimation(EditConvertOperationActivity.this).toBundle());


            }


        });


        // при сохранении
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkValues()) {// сначала проверяем все ли заполнено
                    return;
                }


                operation.setFromAmount(convertString(etOperationAmountFrom.getText().toString()));
                operation.setToAmount(convertString(etOperationAmountTo.getText().toString()));


                operation.setDescription(etOperationDesc.getText().toString());
                operation.setFromCurrency((Currency) spnCurrencyFrom.getSelectedItem());
                operation.setToCurrency((Currency) spnCurrencyTo.getSelectedItem());
                operation.setDateTime(calendar);


                Intent intent = new Intent();
                intent.putExtra(AppContext.NODE_OBJECT, operation);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                setResult(RESULT_OK, intent);

                ActivityCompat.finishAfterTransition(EditConvertOperationActivity.this);//закрыть активити

            }


        });


    }

    private void initValues() {

        initCurrencySpinner();

        tvOperationStorageFrom.setText(operation.getFromStorage().getName().toUpperCase());
        icOperationStorageFrom.setImageDrawable(IconUtils.getIcon(operation.getFromStorage().getIconName()));

        etOperationAmountFrom.setText(operation.getFromAmount().toString());


        etOperationAmountTo.setText(operation.getToAmount().toString());
        icOperationStorageTo.setImageDrawable(IconUtils.getIcon(operation.getToStorage().getIconName()));
        tvOperationStorageTo.setText(operation.getToStorage().getName().toUpperCase());
    }

    private void initComponents() {

        layoutOperationStorageFrom = findViewById(R.id.layout_operation_from_storage);
        icOperationStorageFrom = findViewById(R.id.ic_operation_from_storage_selected);
        tvOperationStorageFrom = findViewById(R.id.tv_operation_from_storage_selected);
        etOperationAmountFrom = findViewById(R.id.et_operation_from_amount_selected);
        layoutOperationStorageTo = findViewById(R.id.layout_operation_to_storage);
        etOperationAmountTo = findViewById(R.id.et_operation_to_amount_selected);
        icOperationStorageTo = findViewById(R.id.ic_operation_to_storage_selected);
        tvOperationStorageTo = findViewById(R.id.tv_operation_to_storage_selected);

        spnCurrencyFrom = findViewById(R.id.spn_from_currency);
        spnCurrencyTo = findViewById(R.id.spn_to_currency);

        currencyAdapterFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyListFrom);
        spnCurrencyFrom.setAdapter(currencyAdapterFrom);

        currencyAdapterTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyListTo);
        spnCurrencyTo.setAdapter(currencyAdapterTo);

    }


    // проверяет, заполнены ли обязательные значения
    private boolean checkValues() {

        if (etOperationAmountFrom.getText().length() == 0) {
            Toast.makeText(EditConvertOperationActivity.this, R.string.enter_amount_from, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etOperationAmountTo.getText().length() == 0) {
            Toast.makeText(EditConvertOperationActivity.this, R.string.enter_amount_to, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (operation.getFromStorage() == null) {
            Toast.makeText(EditConvertOperationActivity.this, R.string.select_storage_from, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (operation.getToStorage() == null) {
            Toast.makeText(EditConvertOperationActivity.this, R.string.select_storage_to, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;// если все проверки прошли успешно - возвращается true

    }


    private void initCurrencySpinner() {

        currencyAdapterFrom.clear();
        currencyAdapterFrom.addAll(operation.getFromStorage().getAvailableCurrencies());

        currencyAdapterTo.clear();
        currencyAdapterTo.addAll(operation.getToStorage().getAvailableCurrencies());

        currencyAdapterTo.notifyDataSetChanged();
        currencyAdapterFrom.notifyDataSetChanged();


        spnCurrencyFrom.setSelection(currencyListFrom.indexOf(operation.getFromCurrency()));
        spnCurrencyTo.setSelection(currencyListTo.indexOf(operation.getToCurrency()));
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

                    // при изменении storage - обновляем список дотупных валют
                    updateCurrencyList(storage, currencyListFrom, currencyAdapterFrom, spnCurrencyFrom);


                    break;


                case REQUEST_SELECT_STORAGE_TO:
                    operation.setToStorage(storage);
                    icOperationStorageTo.setImageDrawable(IconUtils.getIcon(storage.getIconName()));
                    currentNodeSelect.setText(storage.getName().toUpperCase());

                    // при изменении storage - обновляем список дотупных валют
                    updateCurrencyList(storage, currencyListTo, currencyAdapterTo, spnCurrencyTo);

                    break;


            }
        }


    }


    @Override
    protected void onResume() {
        super.onResume();


        if (operation.getToStorage() != null) {
            updateCurrencyList(operation.getToStorage(), currencyListTo, currencyAdapterTo, spnCurrencyTo);
        }


        if (operation.getFromStorage() != null) {
            updateCurrencyList(operation.getFromStorage(), currencyListFrom, currencyAdapterFrom, spnCurrencyFrom);
        }


    }
}
