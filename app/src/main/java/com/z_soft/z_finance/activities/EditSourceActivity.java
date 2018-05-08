package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.TreeNode;

public class EditSourceActivity<T extends TreeNode> extends AppCompatActivity {


    public static String NODE_OBJECT = "com.z_soft.z_finance.activities.EditSourceActivity.NodeObject";
    public static int REQUEST_NODE_EDIT = 101;

    private Toolbar toolbar;
    private TextView etName;
    private ImageView imgSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_source);

        toolbar = findViewById(R.id.toolbar_edit_source);
        etName = findViewById(R.id.et_source_name);
        imgSave = findViewById(R.id.img_node_save);

        setSupportActionBar(toolbar);

        final T node = (T) getIntent().getSerializableExtra(NODE_OBJECT); // получаем переданный объект для редактирования

        etName.setText(node.getName());

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etName.getText().toString();


                // чтобы лишний раз не сохранять - проверяем, были ли изменены данные, если нет, то при сохранении просто закрываем активити
                if (!newName.equals(node.getName())
                    //TODO также проверять иконку
                        ) {
                    node.setName(newName);

                    Intent intent = new Intent();
                    intent.putExtra(NODE_OBJECT, node);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                    setResult(RESULT_OK, intent);

                }

                finish();// закрыть активити

            }
        });

    }

}
