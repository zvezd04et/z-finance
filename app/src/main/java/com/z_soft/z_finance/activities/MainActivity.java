package com.z_soft.z_finance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.z_soft.z_finance.adapters.SourceNodeAdapter;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.impls.DefaultSource;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.SprListFragment;
import com.z_soft.z_finance.listeners.TreeNodeActionListener;

import java.util.List;

import static com.z_soft.z_finance.activities.EditSourceActivity.NODE_OBJECT;
import static com.z_soft.z_finance.activities.EditSourceActivity.REQUEST_NODE_ADD;
import static com.z_soft.z_finance.activities.EditSourceActivity.REQUEST_NODE_EDIT;
import static com.z_soft.z_finance.activities.EditSourceActivity.REQUEST_NODE_ADD_CHILD;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TreeNodeActionListener<Source> {

    private ImageView iconBack;
    private ImageView iconAdd;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private Source selectedParentNode;
    private OperationType defaultType;// для автоматического проставления типа при создании нового элемента
    private SourceNodeAdapter sourceNodeAdapter;

    private TabLayout tabLayout;
    private List<Source> currentList;// хранит корневые элементы списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_source_list);
        initToolbar();

        //initFloatingButton();

        initNavigationDrawer(toolbar);

        RecyclerView rv = findViewById(R.id.spr_list_fragment);
        sourceNodeAdapter = (SourceNodeAdapter)rv.getAdapter();

        initTabs();

    }

    private void initTabs() {

        currentList = Initializer.getSourceManager().getAll();
        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                toolbarTitle.setText(R.string.sources);
                iconBack.setVisibility(View.INVISIBLE);

                switch (tab.getPosition()){
                    case 0:// все
                        currentList = Initializer.getSourceManager().getAll();
                        defaultType = null;
                        break;
                    case 1:// доход
                        currentList = Initializer.getSourceManager().getList(OperationType.INCOME);
                        defaultType = OperationType.INCOME;
                        break;
                    case 2: // расход
                        currentList = Initializer.getSourceManager().getList(OperationType.OUTCOME);
                        defaultType = OperationType.OUTCOME;
                        break;
                }

                sourceNodeAdapter.refreshList(currentList, sourceNodeAdapter.animatorParents);

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

        toolbar = findViewById(R.id.tlb_tree_list_actions);
        setSupportActionBar(toolbar);

        toolbarTitle = findViewById(R.id.toolbar_title);
        iconBack = findViewById(R.id.ic_back_node);
        iconAdd = findViewById(R.id.ic_add_node);

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedParentNode.getParent()==null){// показать корневые элементы


                    sourceNodeAdapter.refreshList(currentList, sourceNodeAdapter.animatorParents);
                    toolbarTitle.setText(R.string.sources);
                    iconBack.setVisibility(View.INVISIBLE);
                    selectedParentNode = null;

                }else{// показать родительские элементы
                    sourceNodeAdapter.refreshList(selectedParentNode.getParent().getChilds(), sourceNodeAdapter.animatorParents);//
                    selectedParentNode = (Source) selectedParentNode.getParent();
                    iconBack.setVisibility(View.VISIBLE);
                    toolbarTitle.setText(selectedParentNode.getName());
                }

            }
        });

        // при нажатии на кнопку добавления элемента
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Source source = new DefaultSource();

                // если пользователь выбрал таб и создает новый элемент - сразу прописываем тип
                if (defaultType!=null){
                    source.setOperationType(defaultType);
                }


                Intent intent = new Intent(MainActivity.this, EditSourceActivity.class); // какой акивити хоти вызвать
                intent.putExtra(NODE_OBJECT, source); // помещаем выбранный объект node для передачи в активити
                ActivityCompat.startActivityForResult(MainActivity.this,intent, REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

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

        if (selectedParentNode == null) {

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

            return;
        }

        if (selectedParentNode.getParent() == null) {// показать корневые элементы

            sourceNodeAdapter.refreshList(currentList, sourceNodeAdapter.animatorParents);
            toolbarTitle.setText(R.string.sources);
            iconBack.setVisibility(View.INVISIBLE);
            selectedParentNode = null;

        } else {// показать родительские элементы
            sourceNodeAdapter.refreshList(selectedParentNode.getParent().getChilds(), sourceNodeAdapter.animatorParents);//
            selectedParentNode = (Source) selectedParentNode.getParent();
            toolbarTitle.setText(selectedParentNode.getName());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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
    public void onSelectNode(Source selectedParentNode) {// при каждом нажатии на элемент списка - срабатывает этот слушатель событий - записывает выбранный node

        if (selectedParentNode.hasChilds()) {
            this.selectedParentNode = selectedParentNode;// в selectedParentNode хранится ссылка на выбранную родительскую категорию
            toolbarTitle.setText(selectedParentNode.getName());// показывает выбранную категорию
            iconBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPopup(Source selectedParentNode) {
        this.selectedParentNode = selectedParentNode;

    }

    @Override
    public void onClick(Source node) {



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Source node = null;

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_NODE_EDIT:
                    sourceNodeAdapter.updateNode((Source) data.getSerializableExtra(NODE_OBJECT));// отправляем на обновление измененный объект
                    break;

                case REQUEST_NODE_ADD:
                    node = (Source) data.getSerializableExtra(NODE_OBJECT);

                    if (selectedParentNode != null) {// если создаем дочерний элемент, а не корневой
                        node.setParent(selectedParentNode);// setParent нужно выполнять, когда объект уже вернулся из активити
                    }

                    sourceNodeAdapter.addNode(node);// отправляем на добавление новый объект
                    break;

                case REQUEST_NODE_ADD_CHILD:

                    node = (Source) data.getSerializableExtra(NODE_OBJECT);
                    node.setParent(selectedParentNode);

                    sourceNodeAdapter.insertChildNode(node);// отправляем на добавление новый объект
                    break;


            }

        }

    }
}
