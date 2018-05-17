package com.z_soft.z_finance.activities.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.list.SourceListActivity;

// базовый класс для любого активити, который должен иметь drawer
// нужно унаследоваться от этого класса и вызвать метод  createDrawer после создания toolbar
public abstract class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Intent sourceListIntent;



    // для выполнения анимации при переходе между активити
    private Bundle bundle;

    //protected TransitionSlide transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sourceListIntent = new Intent(this, SourceListActivity.class);

        bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(BaseDrawerActivity.this).toBundle();

    }



    // для инициализации drawer - ему нужно передать toolbar
    protected void createDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    // обработка нажатия пунктов drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation itemView item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_operations) {

        } else if (id == R.id.nav_sources) {
            startActivity(sourceListIntent, bundle);
        } else if (id == R.id.nav_storages) {

        } else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // хранит значение, как отработал метод onBackPressed, чтобы понимать что должны делать кнопка back - либо закрывать drawer, либо другое действие, указанное в переопределенном дочернем методе
    protected boolean drawerClosed;


    // закрывает drawer кнопкой back, если он активен
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            drawerClosed = true;

        } else{
            drawerClosed = false;
        }

    }
}