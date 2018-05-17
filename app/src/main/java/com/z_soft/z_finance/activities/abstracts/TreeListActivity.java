package com.z_soft.z_finance.activities.abstracts;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.TreeNodeListFragment;
import com.z_soft.z_finance.listeners.TreeNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

import static com.z_soft.z_finance.utils.AppContext.NODE_OBJECT;
import static com.z_soft.z_finance.utils.AppContext.REQUEST_NODE_ADD_CHILD;

// общие поля и реализация для активити со древовидным списком справочных значений
public abstract class TreeListActivity<T extends TreeNode> extends BaseListActivity<T, TreeNodeListFragment> implements TreeNodeActionListener<T> {

    protected int mode;// хранит выбранный режим работы со справочниками (SELECT_MODE или EDIT_MODE)

    // в зависимости от ситуации кнопка будет выполнять разные действия (переход к родительским элементам или выход из активити)
    protected ImageView icBack;

    protected T selectedNode;// запоминает элемент, над которым производим какие-либо действия (открываем дочерний список, добавляем дочерний элемент)
    protected T selectedParentNode;// запоминает родительский элемент (нужно при создании дочернего элемента, чтобы установить parent


    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// обязательно нужно вызывать


    }


    // все компоненты тулбара нужно инициализировать здесь
    @Override
    protected void initToolbar() {
        super.initToolbar();

        icBack = (ImageView) findViewById(R.id.ic_back_node);

        mode = getIntent().getIntExtra(AppContext.LIST_VIEW_MODE, 0); // режим работы со справочником



        if (mode == 0) {
            mode = AppContext.EDIT_MODE;// если не передали аргумент - ставим режим обычной работы со справочниками (без возврата в активити выбранного элемента)
        }


        if (mode == AppContext.EDIT_MODE) {
            super.createDrawer(toolbar); // нужно вызывать, чтобы создать drawer (обязательно нужно передавать не пустой toolbar)
            icBack.setVisibility(View.INVISIBLE);
        } else {
            icBack.setVisibility(View.VISIBLE);

        }


    }

    // сюда возврашается результат после редактирования или добавление элемента
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // если была нажата кнопка сохранения

            switch (requestCode) {
                // если был запрос на добавление дочернего элемента
                case REQUEST_NODE_ADD_CHILD:
                    addChild(data);
                    break;
            }

        }

    }

    private void addChild(Intent data) {
        T node = (T) data.getSerializableExtra(NODE_OBJECT);
        node.setParent(selectedNode);
        fragment.insertChildNode(node);// отправляем на добавление новый объект
    }


    @Override
    protected void initListeners() {
        super.initListeners();

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    protected void showParentNodes() {
        if (selectedParentNode.getParent() == null) {// показать корневые элементы
            showRootNodes();
            toolbarTitle.setText(defaultToolbarTitle);


            switch (mode) {
                case AppContext.EDIT_MODE: // для обычного режима
                    icBack.setVisibility(View.INVISIBLE);
                    break;
                case AppContext.SELECT_MODE: // для режима выбора элемента

                    // если больше нет родительских элементов - по нажатию на кнопку выходим из актитивити
                    icBack.setVisibility(View.VISIBLE);
                    icBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    break;
            }


        } else {// показать родительские элементы, если они существуют
            fragment.refreshList(selectedParentNode.getParent().getChilds());
            toolbarTitle.setText(selectedParentNode.getName());
            selectedParentNode = (T) selectedParentNode.getParent(); // в переменной selectedNode всегда должна быть родительская категория, в которой мы находимся в данный момент
            selectedNode = null;
        }
    }


    protected void showRootNodes() {
        selectedParentNode = null;
        selectedNode = null; // сбрасываем, в данный момент никакой родительский элемент не выбран
    }


    // елис нажали физическую кнопку назад на устройстве
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!drawerClosed) {// если был активен drawer - кнопка back его закроет, не нужно переходить по дереву
            if (icBack.isShown()) { // если кнопка видна - значит находимся в дочернем списке
                icBack.callOnClick(); // симулируем кнопку "назад" - переходим на уровень выше, к родительским элементам
            }else {
                //если кнопка не видна - закрываем приложение
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        }
    }


    @Override
    protected void addAction(T node) {

        if (selectedParentNode != null) {// если создаем дочерний элемент, а не корневой
            node.setParent(selectedParentNode);// setParent нужно выполнять, когда объект уже вернулся из активити
        }
        super.addAction(node);


    }

    @Override
    public void onPopup(T node) {
        selectedNode = node;// запоминаем, над каким элементом вызвали popup
    }

    @Override
    public void onClick(T node) {// при каждом нажатии на элемент списка - срабатывает этот слушатель событий - записывает выбранный node

        if (node.hasChilds()) {
            selectedParentNode = node;// в selectedParentNode хранится ссылка на выбранную родительскую категорию
            toolbarTitle.setText(node.getName());// показывает выбранную категорию
            icBack.setVisibility(View.VISIBLE);

            // при нажатии на кнопку перехода к родительским элементам справочника
            icBack.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              showParentNodes();
                                          }
                                      }

            );


        }
    }



    @Override
    public void onSelectNode(T selectedNode) {
        Intent intent = new Intent();
        intent.putExtra(NODE_OBJECT, selectedNode);// выбранная категория
        setResult(RESULT_OK, intent);
    }


}
