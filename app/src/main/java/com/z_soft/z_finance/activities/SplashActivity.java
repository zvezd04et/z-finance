package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.list.OperationListActivity;
import com.z_soft.z_finance.activities.list.SourceListActivity;
import com.z_soft.z_finance.utils.DbConnection;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        new Thread() {
            public void run() {
                // загрузка начальных данных (операции, справочники)
                DbConnection.initConnection(getApplicationContext());

                // имитация загрузки
                imitateLoading();

                // после загрузки переходим на главное окно
                Intent intent = new Intent(SplashActivity.this, OperationListActivity.class);
                startActivity(intent);
            }
        }.start();


    }

    private void imitateLoading() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
