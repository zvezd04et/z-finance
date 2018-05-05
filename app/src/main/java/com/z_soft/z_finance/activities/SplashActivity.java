package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.app.DbConnection;

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
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }.start();


    }

    private void imitateLoading() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
