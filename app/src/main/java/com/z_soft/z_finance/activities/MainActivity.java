package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.TreeNodeAdapter;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.SprListFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SprListFragment.OnListFragmentInteractionListener  {

    private ImageView backIcon;
    private  Toolbar toolbar;
    private TextView toolbarTitle;

    private TreeNode selectedNode;
    private TreeNodeAdapter treeNodeAdapter;
    //private SprListFragment sprListFragment;

    private TabLayout tabLayout;
    private List<? extends TreeNode> list;// хранит корневые элементы списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        //initFloatingButton();

        initNavigationDrawer(toolbar);

        RecyclerView rv = findViewById(R.id.spr_list_fragment);
        treeNodeAdapter = (TreeNodeAdapter)rv.getAdapter();

        initTabs();

    }

    private void initTabs() {

        list = Initializer.getSourceManager().getAll();
        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                toolbarTitle.setText(R.string.sources);
                backIcon.setVisibility(View.INVISIBLE);

                switch (tab.getPosition()){
                    case 0:// все
                        list = Initializer.getSourceManager().getAll();
                        break;
                    case 1:// доход
                        list = Initializer.getSourceManager().getList(OperationType.INCOME);
                        break;
                    case 2: // расход
                        list = Initializer.getSourceManager().getList(OperationType.OUTCOME);
                        break;
                }

                treeNodeAdapter.updateData(list);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = findViewById(R.id.toolbar_title);
        backIcon = findViewById(R.id.back_icon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedNode.getParent()==null){// показать корневые элементы


                    treeNodeAdapter.updateData(list);
                    toolbarTitle.setText(R.string.sources);
                    backIcon.setVisibility(View.INVISIBLE);
                    selectedNode = null;

                }else{// показать родительские элементы
                    treeNodeAdapter.updateData(selectedNode.getParent().getChilds());//
                    selectedNode = selectedNode.getParent();
                    toolbarTitle.setText(selectedNode.getName());
                }

            }
        });

    }

    private void initFloatingButton() {
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (selectedNode == null) {

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

            return;
        }

        if (selectedNode.getParent() == null) {// показать корневые элементы

            treeNodeAdapter.updateData(list);
            toolbarTitle.setText(R.string.sources);
            backIcon.setVisibility(View.INVISIBLE);
            selectedNode = null;

        } else {// показать родительские элементы
            treeNodeAdapter.updateData(selectedNode.getParent().getChilds());//
            selectedNode = selectedNode.getParent();
            toolbarTitle.setText(selectedNode.getName());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_operations) {
            // Handle the camera action
        } else if (id == R.id.nav_sources) {

        } else if (id == R.id.nav_storages) {

        } else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(TreeNode selectedNode) {

        this.selectedNode = selectedNode;
        if (selectedNode.hasChilds()) {
            toolbarTitle.setText(selectedNode.getName());// показывает выбранную категорию
            backIcon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EditSourceActivity.REQUEST_NODE_EDIT) {// кто был инициатором вызова
            if (resultCode == RESULT_OK){ // какой результат вернулся
                treeNodeAdapter.updateNode((TreeNode)data.getSerializableExtra(EditSourceActivity.NODE_OBJECT));// отправляем на обновление измененный объект
            }
        }
    }
}
