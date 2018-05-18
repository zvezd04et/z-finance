package com.z_soft.z_finance.activities.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseEditNodeActivity;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.utils.AppContext;

// отвеачает за добавление и редактирование счета
public class EditStorageActivity extends BaseEditNodeActivity<Storage> {


    public EditStorageActivity() {
        super(R.layout.activity_edit_storage);// какой макет будет использоваться

    }


    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // слушатель события при сохранении
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etName.getText().toString();

                // не давать сохранять пустое значение
                if (newName.trim().length() == 0) {
                    Toast.makeText(EditStorageActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                // чтобы лишний раз не сохранять - проверяем, были ли изменены данные, если нет, то при сохранении просто закрываем активити
                if (edited(node, newName, newIconName)) {

                    node.setName(newName);

                    if (newIconName != null) {
                        node.setIconName(newIconName);
                    }

                    Intent intent = new Intent();
                    intent.putExtra(AppContext.NODE_OBJECT, node);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                    setResult(RESULT_OK, intent);

                }


                ActivityCompat.finishAfterTransition(EditStorageActivity.this);//закрыть активити


            }
        });


    }
}