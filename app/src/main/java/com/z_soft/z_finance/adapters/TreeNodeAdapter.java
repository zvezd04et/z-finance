package com.z_soft.z_finance.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.fragments.SprListFragment.OnListFragmentInteractionListener;

import java.sql.SQLException;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TreeNode} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TreeNodeAdapter<T extends TreeNode> extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {

    private static final String TAG = TreeNodeAdapter.class.getName();

    private List<T> nodeList;
    private final OnListFragmentInteractionListener tapListener;
    private Context context;

    private Snackbar snackbar; // для возможности отменить удаление

    public TreeNodeAdapter(List<T> items, OnListFragmentInteractionListener listener, Context context) {
        nodeList = items;
        tapListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_node, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TreeNodeAdapter.ViewHolder holder, final int position) {// вызывается для каждой записи списка

        T node = nodeList.get(position);// определяем выбранный пункт
        holder.tvSprName.setText(node.getName());

        // показать кол-во дочерних элементов, если они есть
        if (node.getChilds() != null && !node.getChilds().isEmpty()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
        } else {
            holder.tvChildCount.setText("");
        }

        // для каждого пункта формируем слушатель
        initPopup(holder.btnPopup, context, node, position);


        // обработка события при нажатии на любую запись списка
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T treeNode = nodeList.get(position);// определяем объект

                // если был присвоен слушатель
                if (tapListener != null) {
                    // уведомляем слушателя, что был нажат пункт из списка
                    tapListener.onListFragmentInteraction(treeNode);// передаем, какой именно объект был нажат
                }


                if (treeNode.hasChilds()) {// если есть дочерние значения
                    updateData((List<T>) treeNode.getChilds());// показать дочек
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return nodeList.size();
    }

    public void updateData(List<T> nodeList){
        this.nodeList = nodeList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvSprName;
        public final TextView tvChildCount;
        public final ViewGroup layoutMain;
        public final ImageView btnPopup;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvSprName = view.findViewById(R.id.spr_name);
            layoutMain = (LinearLayout) view.findViewById(R.id.spr_main_layout);
            tvChildCount = view.findViewById(R.id.spr_child_count);
            btnPopup = view.findViewById(R.id.spr_popup_button);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }

    private void initPopup(final ImageView btnPopup, final Context context, final T node, final int position) {


        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // при нажатии на каждый пункт - формируем отдельный объект контекстного меню
                PopupMenu dropDownMenu = new PopupMenu(context, btnPopup); // btnPopup - чтобы знал кто источник нажатия, для правильного расположения меню
                dropDownMenu.getMenuInflater().inflate(R.menu.spr_popup_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        // считываем, какой пункт нажали по его id
                        if (id == R.id.item_add) {

                        } else if (id == R.id.item_edit) {

                        } else if (id == R.id.item_delete) {// если нажали пункт удаления

                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.confirm)
                                    .setMessage(R.string.confirm_delete)
                                    .setIcon(android.R.drawable.ic_dialog_alert)

                                    // слушатель для кнопки ОК
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            // удаляем запись из коллекции (пока без удалении из базы)
                                            nodeList.remove(node);
                                            notifyItemRemoved(position);


                                            // при удалении - сначала даем пользователю отменить действие с помощью SnackBar
                                            snackbar = Snackbar.make(btnPopup, R.string.deleted, Snackbar.LENGTH_LONG);

                                            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {// если нажали на отмену удаления

                                                    if (node.hasParent()) { // если это был дочерний элемент
                                                        node.getParent().getChilds().add(position, node);// возвращаем обратно дочерний элемент
                                                    } else { // это был корневой элемент
                                                        nodeList.add(position, node);// возвращаем
                                                    }

                                                    notifyDataSetChanged();

                                                }
                                            }).addCallback(new Snackbar.Callback() {// для того, чтобы могли отловить момент, когда SnackBar исчезнет (т.е. ползователь не успел отменить удаление)

                                                @Override
                                                public void onDismissed(Snackbar snackbar, int event) {

                                                    if (event != DISMISS_EVENT_ACTION) {// если не была нажата ссылка отмены
                                                        deleteNode((Source) node, position, context); // удаляем из-базы и коллекции, обновляем список
                                                    }

                                                }
                                            }).show();


                                        }


                                    })

                                    // слушатель для кнопки отмены
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();// если нажали отмену - просто закрываем диалоговое окно
                                        }
                                    }).show();

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


//    // удаляет записи и обновляет список
    private void deleteNode(Source node, int position, Context context) {
        try {
            Initializer.getSourceManager().delete(node);
            notifyDataSetChanged();// обновляем список

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());

            // если вышла ошибка целостности данных
            if (e.getMessage().contains("SQLiteConstraintException")) {
                Toast.makeText(context, R.string.has_operations, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
