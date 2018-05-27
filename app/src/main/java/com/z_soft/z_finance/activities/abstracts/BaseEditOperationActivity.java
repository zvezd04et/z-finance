package com.z_soft.z_finance.activities.abstracts;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.fragments.datetime.DatePickerFragment;
import com.z_soft.z_finance.fragments.datetime.TimePickerFragment;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

// общие поля и реалищация для активити редактирования справочных значений
public abstract class BaseEditOperationActivity<T extends Operation> extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    // фрагменты для выбора даты и времени
    private TimePickerFragment timeFragment = new TimePickerFragment();
    private DatePickerFragment dateFragment = new DatePickerFragment();

    // сюда будет записывать новые значения даты и времени
    protected Calendar calendar;

    // верхний тулбар при редактировании операции
    protected Toolbar toolbar;
    protected TextView tvTitle;

    // общие поля для всех операций
    protected TextView tvOperationType;
    protected EditText etOperationDesc;
    protected TextView tvOperationDate;
    protected TextView tvOperationTime;

    // ссылка на scrollview (используется для прокрутки в нужное место при открытии активити)
    protected ScrollView scrollView;

    // для обработки результата выбора справочного значения
    protected static final int REQUEST_SELECT_SOURCE_TO = 1;
    protected static final int REQUEST_SELECT_SOURCE_FROM = 2;
    protected static final int REQUEST_SELECT_STORAGE_TO = 3;
    protected static final int REQUEST_SELECT_STORAGE_FROM = 4;

    protected TextView currentNodeSelect; // хранит ссылку на компонент, для которого выбираем справочное значение (т.к. e операции может быть несколько полей, где нужно выбирать справочник)

    protected ImageView imgSave;
    protected ImageView imgClose;

    protected int actionType;

    protected T operation;

    // какой макет использовать (т.к. для разных типов операций макеты будут разными) - у них будут совпадать только общие поля
    private int layoutId;

    public BaseEditOperationActivity(int layoutId) {
        this.layoutId = layoutId;
    }


    // метод работает с общими компонентами
    // метод работает с общими компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        findComponents();

        // получаем тип действия (добавление, редактирование)
        actionType = getIntent().getIntExtra(AppContext.OPERATION_ACTION, -1);

        // получаем переданный объект
        operation = (T) getIntent().getSerializableExtra(AppContext.NODE_OBJECT);

        setValues(actionType);


        changeTitle();

        createListeners();// должен вызываться после того, как все компоненты определены выше (через findViewById)

        // при открытии - переходим на самый верх
        scrollView.smoothScrollTo(0, scrollView.getTop());


    }

    private void setValues(int action) {

        switch (action) {
            case AppContext.OPERATION_EDIT:
                calendar = operation.getDateTime();

                // заполняем все поля значениями из переданной операции
                etOperationDesc.setText(operation.getDescription());
                break;

            case AppContext.OPERATION_ADD:
                calendar = Calendar.getInstance(); // если новая операция - ставим текущие дату и время
                break;

        }

        tvOperationDate.setText(formatDate(calendar));
        tvOperationTime.setText(formatTime(calendar));


        // задаем текущие значения календаря, будут показываться при открытии диалогового окна выбора даты и времени
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppContext.DATE_CALENDAR, calendar);

        dateFragment.setArguments(bundle);
        timeFragment.setArguments(bundle);

    }

    private void findComponents() {
        toolbar = findViewById(R.id.tlb_edit_actions);
        setSupportActionBar(toolbar);

        tvOperationType = findViewById(R.id.tv_operation_type_selected);
        etOperationDesc = findViewById(R.id.et_operation_desc);
        tvOperationDate = findViewById(R.id.tv_operation_date);
        tvOperationTime = findViewById(R.id.tv_operation_time);
        scrollView = findViewById(R.id.scroll);

        imgClose = findViewById(R.id.img_node_close);
        imgSave = findViewById(R.id.img_node_save);

        tvTitle = findViewById(R.id.tv_node_name);

    }


    private void createListeners() {


        // слушатель события при отмене
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(BaseEditOperationActivity.this);
            }
        });

        // при нажатии на текстовый компонент - показываем диалоговое окно для выбора даты
        tvOperationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFragment.show(BaseEditOperationActivity.this.getFragmentManager(), AppContext.DATE_CALENDAR);
            }
        });


        // при нажатии на текстовый компонент - показываем диалоговое окно для выбора времени
        tvOperationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeFragment.show(BaseEditOperationActivity.this.getFragmentManager(), AppContext.DATE_CALENDAR);
            }
        });


    }


    // в зависимости от типа действия (создание или редактирование) - меняем заголовок
    private void changeTitle() {
        if (operation.getId() > 0) {
            tvTitle.setText(R.string.editing);
        } else {
            tvTitle.setText(R.string.adding);
        }
    }


    // слушает событие выбора даты в диалоговом окне
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day);
        tvOperationDate.setText(formatDate(calendar));
    }

    // слушает событие выбора времени в диалоговом окне
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        tvOperationTime.setText(formatTime(calendar));
    }

    // форматирование при выводе даты, используя системный класс DateUtils
    private String formatDate(Calendar calendar) {
        return DateUtils.formatDateTime(getBaseContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
    }

    // форматирование при выводе времени, используя системный класс DateUtils
    private String formatTime(Calendar calendar) {
        return DateUtils.formatDateTime(getBaseContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }


    // обновить список доступных валют (нужно при выборе счета, чтобы пользователь мог выбирать актуальные для данного счета валюты в выпадающем списке)
    protected void updateCurrencyList(Storage storage, List<Currency> adapterList, ArrayAdapter<Currency> adapter, Spinner spinner) {

        Currency selectedCurrency = (Currency) spinner.getSelectedItem();

        adapterList.clear();
        adapterList.addAll(storage.getAvailableCurrencies());

        adapter.clear();
        adapter.addAll(storage.getAvailableCurrencies());

        //adapter.notifyDataSetChanged();


        // если ранее (до выбора счета) уже выбрали в списке валюту
        if (selectedCurrency != null) {
            if (storage.getAvailableCurrencies().contains(selectedCurrency)) {
                spinner.setSelection(adapterList.indexOf(selectedCurrency), true); // выбрать ранее установленную валюту операции
                return;
            }
        }

        // выбираем валюту по-умолчанию в выпадаюшем списке
        if (selectedCurrency == null && storage.getAvailableCurrencies().contains(CurrencyUtils.defaultCurrency)) {
            spinner.setSelection(adapterList.indexOf(CurrencyUtils.defaultCurrency), true); // выбрать ранее установленную валюту операции
            return;
        }

        // если ничего не полчилось - просто выбираем первое значение
        spinner.setSelection(0);
    }

    // конвертация из текста, введенного пользователем - в BigDecimal (для корректного сохранения операции)
    protected BigDecimal convertString(String value){

        if (value.trim().length()!=0){
            return new BigDecimal(value);
        }

        return BigDecimal.ZERO;

    }

}
