package com.z_soft.z_finance.activities.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.IconNode;
import com.z_soft.z_finance.fragments.BaseNodeListFragment;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

import static com.z_soft.z_finance.utils.AppContext.NODE_OBJECT;
import static com.z_soft.z_finance.utils.AppContext.REQUEST_NODE_ADD;
import static com.z_soft.z_finance.utils.AppContext.REQUEST_NODE_EDIT;

// базовый класс для любого списка справочных значений
// содержит общие компоненты,
public abstract class BaseListActivity<T extends IconNode, F extends BaseNodeListFragment> extends BaseDrawerActivity implements BaseNodeActionListener<T> {


    protected ImageView iconAdd;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;

    protected String defaultToolbarTitle; // имя окна (для разных списков будет подставляться свое название)

    protected F fragment;


    private int listLayoutId; // основной контент списка
    private int toolbarLayoutId; // панель действий, который можно менять в зависимости от создаваемого списка



    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(listLayoutId);

        initFragment();

        initToolbar();

        initListeners();

    }

    private void initFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.node_list_fragment, fragment);
        fragmentTransaction.commit();
    }


    // здесь можно инициализировать слушатели
    protected void initListeners(){

    }


    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
        defaultToolbarTitle = title;
    }

    // показать фрагмент
    protected void init(F fragment, int listLayoutId, int toolbarLayoutId) {
        this.fragment = fragment;
        this.listLayoutId = listLayoutId;
        this.toolbarLayoutId = toolbarLayoutId;
    }


    // все компоненты тулбара нужно инициализировать здесь
    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(toolbarLayoutId);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        toolbarTitle.setText(defaultToolbarTitle);

        iconAdd = (ImageView) findViewById(R.id.ic_add_node);

    }


    // сюда возврашается результат после редактирования или добавление элемента
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        T node;

        if (resultCode == RESULT_OK) { // если была нажата кнопка сохранения

            switch (requestCode) {
                case REQUEST_NODE_EDIT:
                    fragment.updateNode((T) data.getSerializableExtra(NODE_OBJECT));// отправляем полученный объект на обновление в коллекции и БД
                    break;

                case REQUEST_NODE_ADD:
                    node = (T) data.getSerializableExtra(NODE_OBJECT);
                    addAction(node); // отправляем полученный объект на добавление в коллекции и БД
                    break;
            }

        }

    }


    protected void addAction(T node){
        fragment.addNode(node);// отправляем на добавление новый объект
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    // методы слушателя, можно оставить пустыми, чтобы в дочерних классах переопределять только необходимые методы
    @Override
    public void onAdd(T node) {}

    @Override
    public void onDelete(T node) {}

    @Override
    public void onUpdate(T node) {}
}