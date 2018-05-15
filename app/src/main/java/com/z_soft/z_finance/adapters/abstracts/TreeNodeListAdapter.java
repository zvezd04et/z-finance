package com.z_soft.z_finance.adapters.abstracts;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.holders.TreeViewHolder;
import com.z_soft.z_finance.core.decorator.CommonManager;
import com.z_soft.z_finance.core.interfaces.IconNode;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.listeners.TreeNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;

import java.sql.SQLException;
import java.util.List;

// адаптер, который добавляет функционал работы с деревом
public abstract class TreeNodeListAdapter<T extends TreeNode, VH extends TreeViewHolder> extends BaseNodeListAdapter<T, VH, TreeNodeActionListener<T>> {
    protected static final String TAG = TreeNodeListAdapter.class.getName();

    // макет по-умолчанию, который применяется для списков справочных значений
    private static final int layoutId = R.layout.node_item;

    // хранит режим работы со справочником
    private int mode;


    public TreeNodeListAdapter(int mode, CommonManager manager) {
        super(manager, layoutId);
        this.mode = mode;
    }

    public TreeNodeListAdapter(int mode, CommonManager manager, List<T> initialList) {
        super(manager, layoutId, initialList);
        this.mode = mode;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);
        return null;// этот метод не должен создавать ViewHolder, т.к. конкретные экземпляры холдеров создают дочерние классы
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        super.onBindViewHolder(holder, position);// обязательно нужно вызывать, чтобы проинициализировать все переменные в родительских классах

        final T node = adapterList.get(position);// определяем выбранный пункт

        // для каждого пункта формируем слушатель для popup меню
        initPopup(holder.btnPopup, activityContext, node, position);


        // показать кол-во дочерних элементов, если они есть
        if (node.hasChilds()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
            holder.tvChildCount.setBackgroundColor(ContextCompat.getColor(activityContext, R.color.colorGray));
        } else {
            holder.tvChildCount.setText("");
            holder.tvChildCount.setBackground(null);// чтобы не рисовал пустую закрашенную область
        }

    }



    @Override
    protected void deleteWithSnackbar(T node, int position) {
        if (node.getRefCount()>0){// не даем удалять запись, если на нее есть ссылки из операций
            closeSnackBar();
            Toast.makeText(activityContext, R.string.has_operations, Toast.LENGTH_SHORT).show();
        }else {
            super.deleteWithSnackbar(node, position); // стандартное удаление
        }
    }



    private void initPopup(final ImageView btnPopup, final Context context, final T node, final int position) {

        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                closeSnackBar();

                listener.onPopup(node);

                // при нажатии на каждый пункт - формируем отдельный объект контекстного меню
                PopupMenu dropDownMenu = new PopupMenu(context, btnPopup); // btnPopup - чтобы знал кто источник нажатия, для правильного расположения меню
                dropDownMenu.getMenuInflater().inflate(R.menu.spr_popup_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        // считываем, какой пункт нажали по его id
                        if (id == R.id.item_child_add) {
                            runAddChildActivity(node);
                        } else if (id == R.id.item_edit) {
                            runEditActivity(position, node);
                        } else if (id == R.id.item_delete) {// если нажали пункт удаления
                            deleteWithSnackbar(node, position);
                        }


                        return true;
                    }


                });

                // для записей, где есть дочерние элементы - делать пункт Удалить неактивным
                if (node.hasChilds()) {
                    // TODO исправить антипаттерн magic number
                    dropDownMenu.getMenu().getItem(2).setEnabled(false);
                }

                dropDownMenu.show();
            }
        });

    }



    protected void runAddChildActivity(T node) {
        closeSnackBar();

        // какой именно активити открывать - реализовывает дочерний адаптер, главное передать правильный requestCode
        openActivityOnClick(node, AppContext.REQUEST_NODE_ADD_CHILD);
    }


    // вставка дочернего элемента
    public void insertChildNode(T node) {
        try {
            manager.add(node);
            List<T> list =  (List<T>)node.getParent().getChilds(); // загрузить все дочерние элементы родителя, для которого добавили элемент
            refreshList(list, animatorChilds);  // показываем список дочерних элементов, среди которых и новый добавленный
            listener.onClick((T)node.getParent()); // уведомляем слушателя, что перешли в дочерний список

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }




    protected void initListeners(VH holder, final int position, final T node) {
        super.initListeners(holder, position, node);

    }

    @Override
    protected void nodeClickAction(int position, T node) {
        if (node.hasChilds()) {// если есть дочерние значения
            refreshList((List<T>) node.getChilds(), animatorChilds);// перейти в список дочерних элементов
        } else {
            if (mode == AppContext.EDIT_MODE) {
                super.nodeClickAction(position, node);// иначе выполняем стандартный функционал - открытие элемента на редактирование
            }else if (mode == AppContext.SELECT_MODE){
                listener.onSelectNode(node);
            }
        }
    }


}
