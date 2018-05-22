package com.z_soft.z_finance.activities.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.SelectIconActivityBase;
import com.z_soft.z_finance.core.interfaces.IconNode;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.IconUtils;

import java.math.BigDecimal;

// общие поля и реалищация для активити редактирования справочных значений
public abstract class BaseEditNodeActivity<T extends IconNode> extends AppCompatActivity {

    protected Toolbar toolbar;
    protected EditText etName;
    protected TextView tvNodeName;
    protected ImageView imgSave;
    protected ImageView imgClose;
    protected ImageView imgNodeIcon;

    protected T node;

    private int layoutId;


    public BaseEditNodeActivity(int layoutId) {
        this.layoutId = layoutId;
    }

    protected String newIconName;


    // метод работает с общими компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        toolbar = findViewById(R.id.tlb_edit_actions);
        setSupportActionBar(toolbar);
        etName = findViewById(R.id.et_node_name);
        tvNodeName =  findViewById(R.id.tv_node_name);
        imgSave =  findViewById(R.id.img_node_save);
        imgClose =  findViewById(R.id.img_node_close);
        imgNodeIcon = findViewById(R.id.img_node_icon);


        node = (T)getIntent().getSerializableExtra(AppContext.NODE_OBJECT); // получаем переданный объект для редактирования

        changeTitle();

        changeIcon();




        createListeners();// должен вызываться после того, как все компоненты определены выше (через findViewById)

    }


    private void createListeners() {


        // открытие окна выбора иконки
        imgNodeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseEditNodeActivity.this, SelectIconActivityBase.class); // какой акивити хотим вызвать
                ActivityCompat.startActivityForResult(BaseEditNodeActivity.this, intent, SelectIconActivityBase.REQUEST_SELECT_ICON,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(BaseEditNodeActivity.this).toBundle()); // устанавливаем анимацию перехода

            }
        });


        // слушатель события при отмене
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(BaseEditNodeActivity.this);
            }
        });


    }


    // показать иконку элемента или заглушку, если иконка не указана
    private void changeIcon() {
        if (node.getIconName() == null || IconUtils.getIcon(node.getIconName()) == null) {
            imgNodeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_empty, null));
        } else {
            imgNodeIcon.setImageDrawable(IconUtils.getIcon(node.getIconName()));
        }
    }

    // в зависимости от типа действия (создание или редактирование) - меняем заголовок
    private void changeTitle(){
        if (node.getName() != null) {
            tvNodeName.setText(R.string.editing);
            etName.setText(node.getName());
        } else {
            tvNodeName.setText(R.string.adding);
            etName.setText("");
        }
    }



    // сюда попадаем, когда возвращается результат выбора иконки
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            // обработка результата активити для выбора новой иконки
            if (requestCode== SelectIconActivityBase.REQUEST_SELECT_ICON){
                newIconName = data.getStringExtra(SelectIconActivityBase.ICON_NAME); // получаем переданное имя выбранной иконки

                if (!newIconName.equals(node.getIconName())) { // если иконка изменилась - обновляем отображение
                    imgNodeIcon.setImageDrawable(IconUtils.getIcon(newIconName));// получаем новую иконку из карты иконок
                }
            }
        }

    }


    // для проверки, изменились ли данные элемента
    protected boolean edited(T node, String newName, String newIconName){
        // описываем условия, при которых будет считаться, что произошло редактирование - чтобы сохранять только при изменении данных и не делать лишних запросов в БД
        // если хотя бы одно из условий равно true - значит запись была отредактирована
        return (node.getName() == null && newName != null) || // если имя объекта было пустым и пользователь ввел новое имя
                (node.getIconName() == null && newIconName != null) || // если иконка объекта не была указана и пользователь выбрал иконку из списка
                (node.getName() != null && newName != null && !node.getName().equals(newName)) || // если название было заполнено и пользователь ввел новое имя, отличное от старого
                (node.getIconName() != null && newIconName != null && !node.getIconName().equals(newIconName)); // если пользователь выбрал новую иконку

    }

    // конвертация из текста, введенного пользователем - в BigDecimal (для корректного сохранения)
    protected BigDecimal convertString(String value){

        if (value.trim().length()!=0){
            return new BigDecimal(value);
        }

        return BigDecimal.ZERO;

    }


}