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
import java.util.Currency;
import java.util.List;

// отвечает за добавление и редактирование категории
public class EditIncomeOperationActivity extends BaseEditOperationActivity<IncomeOperation> {


    // специфичные поля для типа операции Доход

    protected TextView tvOperationSource;
    protected ViewGroup layoutOperationSource;


    protected TextView tvOperationStorage;
    protected ViewGroup layoutOperationStorage;


    protected EditText etOperationAmount;
    protected Spinner spnCurrency;


    protected ImageView icOperationSource;
    protected ImageView icOperationStorage;


    public EditIncomeOperationActivity() {
        super(R.layout.activity_edit_income_operation);// какой макет будет использоваться
    }


    // список возможных валют для операции
    private List<Currency> currencyList;
    private ArrayAdapter<Currency> сurrencyAdapter;


    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // получаем список доступных валют из storage
        currencyList = operation.getToStorage().getAvailableCurrencies();

        fillCurrencySpinner();

        // установка всех значений

        tvOperationType.setText(OperationTypeUtils.incomeType.toString());
        tvOperationType.setBackgroundColor(ContextCompat.getColor(this, ColorUtils.incomeColor));

        icOperationSource = (ImageView) findViewById(R.id.ic_operation_source_selected);
        icOperationStorage = (ImageView) findViewById(R.id.ic_operation_storage_selected);

        tvOperationSource = (TextView) findViewById(R.id.tv_operation_source_selected);
        tvOperationStorage = (TextView) findViewById(R.id.tv_operation_storage_selected);
        etOperationAmount = (EditText) findViewById(R.id.et_operation_amount_selected);

        tvOperationSource.setText(operation.getFromSource().getName());//.toUpperCase()
        tvOperationStorage.setText(operation.getToStorage().getName());//.toUpperCase()
        etOperationAmount.setText(operation.getFromAmount().toString());

        icOperationSource.setImageDrawable(IconUtils.getIcon(operation.getFromSource().getIconName()));
        icOperationStorage.setImageDrawable(IconUtils.getIcon(operation.getToStorage().getIconName()));

        layoutOperationSource = (ViewGroup) findViewById(R.id.layout_operation_source);
        layoutOperationStorage = (ViewGroup) findViewById(R.id.layout_operation_storage);


        // для выбора нового справочного значения storage
        layoutOperationStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationStorage;

                Intent intent = new Intent(EditIncomeOperationActivity.this, StorageListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);
                ActivityCompat.startActivityForResult(EditIncomeOperationActivity.this, intent, REQUEST_SELECT_STORAGE, ActivityOptionsCompat.makeSceneTransitionAnimation(EditIncomeOperationActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

            }


        });



        // для выбора нового справочного значения source
        layoutOperationSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentNodeSelect = tvOperationSource;

                Intent intent = new Intent(EditIncomeOperationActivity.this, SourceListActivity.class);

                // передаем SELECT_MODE, чтобы выбранный объект возвращался обратно в этот активити
                intent.putExtra(AppContext.LIST_VIEW_MODE, AppContext.SELECT_MODE);

                // параметр для фильтрации списка по типу (чтобы не все source показывал, а только по этому типу)
                intent.putExtra(AppContext.LIST_TYPE, OperationType.INCOME.getId());// передаем параметр, который позволит выбирать значение и возвращать его
                ActivityCompat.startActivityForResult(EditIncomeOperationActivity.this,intent, REQUEST_SELECT_SOURCE, ActivityOptionsCompat.makeSceneTransitionAnimation(EditIncomeOperationActivity.this).toBundle());

            }


        });


        // слушатель события при нажатии на кнопку сохранения
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // не давать сохранять пустое значение
                if (etOperationAmount.getText().length() == 0) {
                    Toast.makeText(EditIncomeOperationActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                operation.setFromAmount(new BigDecimal(etOperationAmount.getText().toString()));
                operation.setDescription(etOperationDesc.getText().toString());
                operation.setDateTime(newCalendarValue);
                operation.setFromCurrency((Currency)spnCurrency.getSelectedItem());


                Intent intent = new Intent();
                intent.putExtra(AppContext.NODE_OBJECT, operation);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                setResult(RESULT_OK, intent);


                ActivityCompat.finishAfterTransition(EditIncomeOperationActivity.this);

            }


        });


    }


    // заполнить выпадающий список значениями валют
    private void fillCurrencySpinner() {
        сurrencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyList);
        spnCurrency = (Spinner) findViewById(R.id.spn_currency);
        spnCurrency.setAdapter(сurrencyAdapter);
        spnCurrency.setSelection(currencyList.indexOf(operation.getFromCurrency()));
    }


    // сюда попадаем, когда возвращается результат выбора какого либо справочного значения
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_SELECT_STORAGE: // если выбирали storage
                    Storage storage = (Storage) data.getSerializableExtra(AppContext.NODE_OBJECT); // получаем выбранную категорию
                    operation.setToStorage(storage);
                    icOperationStorage.setImageDrawable(IconUtils.getIcon(storage.getIconName()));
                    currentNodeSelect.setText(storage.getName()); //.toUpperCase()

                    сurrencyAdapter.clear();
                    сurrencyAdapter.addAll(storage.getAvailableCurrencies());


                    if (storage.getAvailableCurrencies().isEmpty()){
                        Toast.makeText(EditIncomeOperationActivity.this, R.string.no_available_currency, Toast.LENGTH_SHORT).show();
                    }else {
                        // если у нового выбранного счета нет той валюты, которая ранее была установлена у операции - уведомляем пользователя об этом
                        if (!storage.getAvailableCurrencies().contains(spnCurrency.getSelectedItem())) {
                            spnCurrency.setSelection(0);// выбрать первую валюту из списка
                            Toast.makeText(EditIncomeOperationActivity.this, R.string.currency+" "+operation.getFromCurrency().getDisplayName() + " "+R.string.currency_not_exist, Toast.LENGTH_SHORT).show();
                        } else {
                            spnCurrency.setSelection(currencyList.indexOf(operation.getFromCurrency())); // выбрать ранее установленную валюту операции
                        }
                    }

                    // обновляем список валют
                    currencyList.clear();
                    currencyList.addAll(storage.getAvailableCurrencies());


                    break;


                case REQUEST_SELECT_SOURCE: // если выбирали source
                    Source source = (Source) data.getSerializableExtra(AppContext.NODE_OBJECT); // получаем выбранную категорию
                    operation.setFromSource(source);
                    icOperationSource.setImageDrawable(IconUtils.getIcon(source.getIconName()));
                    currentNodeSelect.setText(source.getName()); //.toUpperCase(
                    break;

            }

        }

    }

}

