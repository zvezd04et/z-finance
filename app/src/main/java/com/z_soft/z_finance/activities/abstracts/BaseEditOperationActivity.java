package com.z_soft.z_finance.activities.abstracts;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.fragments.datetime.DatePickerFragment;
import com.z_soft.z_finance.fragments.datetime.TimePickerFragment;
import com.z_soft.z_finance.utils.AppContext;

import java.util.Calendar;

// общие поля и реалищация для активити редактирования справочных значений
public abstract class BaseEditOperationActivity<T extends Operation> extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    // фрагменты для выбора даты и времени
    private TimePickerFragment timeFragment = new TimePickerFragment();
    private DatePickerFragment dateFragment = new DatePickerFragment();

    // сюда будет записывать новые значения даты и времени
    protected Calendar newCalendarValue = Calendar.getInstance();

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
    protected static final int REQUEST_SELECT_SOURCE = 1; // для обработки вернувшегося объекта source
    protected static final int REQUEST_SELECT_STORAGE = 2; // для обработки вернувшегося объекта storage

    protected TextView currentNodeSelect; // хранит ссылку на компонент, для которого выбираем справочное значение (т.к. e операции может быть несколько полей, где нужно выбирать справочник)

    protected ImageView imgSave;
    protected ImageView imgClose;


    protected T operation;

    // какой макет использовать (т.к. для разных типов операций макеты будут разными) - у них будут совпадать только общие поля
    private int layoutId;

    public BaseEditOperationActivity(int layoutId) {
        this.layoutId = layoutId;
    }


    // метод работает с общими компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        toolbar = (Toolbar) findViewById(R.id.tlb_edit_actions);
        setSupportActionBar(toolbar);

        tvOperationType = (TextView) findViewById(R.id.tv_operation_type_selected);
        etOperationDesc = (EditText) findViewById(R.id.et_operation_desc);
        tvOperationDate = (TextView) findViewById(R.id.tv_operation_date);
        tvOperationTime = (TextView) findViewById(R.id.tv_operation_time);
        scrollView = (ScrollView) findViewById(R.id.scroll);


        // при открытии - переходим на самый верх
        scrollView.smoothScrollTo(0, scrollView.getTop());


        imgClose = (ImageView) findViewById(R.id.img_node_close);
        imgSave = (ImageView) findViewById(R.id.img_node_save);

        // получаем переданный объект для редактирования (или добавления)
        operation = (T) getIntent().getSerializableExtra(AppContext.NODE_OBJECT);

        // заполняем все поля значениями из переданной операции
        etOperationDesc.setText(operation.getDescription());
        tvOperationDate.setText(formatDate(operation.getDateTime()));
        tvOperationTime.setText(formatTime(operation.getDateTime()));

        tvTitle = (TextView) findViewById(R.id.tv_node_name);

        // задаем текущие значения календаря, чтобы устанавливать их в диалоговом окне при выборе даты или временив
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppContext.DATE_CALENDAR, operation.getDateTime());

        dateFragment.setArguments(bundle);
        timeFragment.setArguments(bundle);

        changeTitle();

        createListeners();// должен вызываться после того, как все компоненты определены выше (через findViewById)

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
        newCalendarValue.set(year, month, day);
        tvOperationDate.setText(formatDate(newCalendarValue));
    }

    // слушает событие выбора времени в диалоговом окне
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        newCalendarValue.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCalendarValue.set(Calendar.MINUTE, minute);
        tvOperationTime.setText(formatTime(newCalendarValue));
    }

    // форматврование при выводе даты, используя системный класс DateUtils
    private String formatDate(Calendar calendar){
        return DateUtils.formatDateTime(getBaseContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
    }

    // форматврование при выводе времени, используя системный класс DateUtils
    private String formatTime(Calendar calendar){
        return DateUtils.formatDateTime(getBaseContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }

}
