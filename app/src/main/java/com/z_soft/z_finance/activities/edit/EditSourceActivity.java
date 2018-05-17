package com.z_soft.z_finance.activities.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.z_soft.z_finance.R;

import com.z_soft.z_finance.activities.abstracts.BaseEditNodeActivity;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.LocalizedOperationType;
import com.z_soft.z_finance.utils.OperationTypeUtils;

import java.util.ArrayList;
import java.util.List;

// отвечает за добавление и редактирование категории
public class EditSourceActivity extends BaseEditNodeActivity<Source> {


    private Spinner spSourceType;
    private ArrayAdapter<LocalizedOperationType> spAdapter;


    public EditSourceActivity() {
        super(R.layout.activity_edit_source);// какой макет будет использоваться
    }


    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // создает компонент выпадающего списка с типами
        createTypesSpinner();



        // слушатель события при сохранении
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etName.getText().toString();

                // не давать сохранять пустое значение
                if (newName.trim().length() == 0) {
                    Toast.makeText(EditSourceActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                // чтобы лишний раз не сохранять - проверяем, были ли изменены данные, если нет, то при сохранении просто закрываем активити
                if (edited(node, newName, newIconName)) {

                    node.setName(newName);
                    node.setOperationType(((LocalizedOperationType) spSourceType.getSelectedItem()).getOperationType());

                    if (newIconName != null) {
                        node.setIconName(newIconName);
                    }

                    Intent intent = new Intent();
                    intent.putExtra(AppContext.NODE_OBJECT, node);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                    setResult(RESULT_OK, intent);

                }

                ActivityCompat.finishAfterTransition(EditSourceActivity.this);//закрыть активити
            }
        });


    }


    // создает компонент выпадающего списка с типами
    private void createTypesSpinner() {
        spSourceType = findViewById(R.id.sp_source_type);


        List<LocalizedOperationType> listTypes = new ArrayList<>(2);
        listTypes.add(OperationTypeUtils.incomeType);
        listTypes.add(OperationTypeUtils.outcomeType);


        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTypes);// нам нужны только доход и расход для категорий
        spSourceType.setAdapter(spAdapter);

        if (node.getOperationType()!=null) {// при редактировании объекта - это поле будет заполнено и недоступно для изменения
            spSourceType.setEnabled(false);
            spSourceType.setSelection(OperationType.getList().indexOf(node.getOperationType()));
        }else{
            spSourceType.setEnabled(true);
        }
    }


}
