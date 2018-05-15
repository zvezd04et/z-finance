package com.z_soft.z_finance.adapters.abstracts;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.adapters.SourceNodeAdapter;
import com.z_soft.z_finance.adapters.holders.BaseViewHolder;
import com.z_soft.z_finance.core.decorator.CommonManager;
import com.z_soft.z_finance.core.interfaces.IconNode;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.IconUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

public abstract class BaseNodeListAdapter<T extends IconNode, VH extends BaseViewHolder, L extends BaseNodeActionListener<T>> extends RecyclerView.Adapter<VH> {

    protected static final String TAG = BaseNodeListAdapter.class.getName();

    // текущая обновляемая коллекция адаптера - заполняется только нужными объектами для показа в списке
    protected List<T> adapterList = new ArrayList<>();

    // ссылка на этот компонент нужна, чтобы установить анимацию переходов
    protected RecyclerView recyclerView;

    public static BaseItemAnimator animatorChilds; // анимация при открытии дочерних элементов (справа налево)
    public static BaseItemAnimator animatorParents; // анимация при открытии родительских элементов (слево направо)


    // синхронизатор для сохранения, удаления, изменения данных (работа с ядром)
    // связующее звено между ядром и Android UI
    protected CommonManager manager;

    // для возможности отмены удаления
    protected Snackbar snackbar;

    protected Activity activityContext;

    // текущая редактируемая позиция - это нужно для правильонго обновления элемента из списка
    protected int currentEditPosition;


    protected View itemView;

    // хранит слушателя события при нажатии на элемент списка
    protected L listener;

    // макет для элемента списка
    protected int itemLayout;

    public BaseNodeListAdapter(CommonManager commonManager, int itemLayout) {
        this.manager = commonManager;
        this.itemLayout = itemLayout;
        adapterList = commonManager.getAll();// по-умолчанию - показывать все записи, если никакой начальный список не передавали

    }

    public BaseNodeListAdapter(CommonManager commonManager, int itemLayout, List<T> initialList) {
        this.manager = commonManager;
        this.itemLayout = itemLayout;
        adapterList = initialList;
    }


    // какой активити открывать при клике - конкретный дочерний класс сам определяет
    protected abstract void openActivityOnClick(T node, int requestCode);




    // определяем макет, где будет находиться список значений
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);

        activityContext = (Activity) parent.getContext();

        recyclerView = (RecyclerView) parent;

        createAnimations();

        // этот метод не должен создавать ViewHolder, т.к. конкретные экземпляры холдеров создают дочерние классы
        return null;
    }


    // заполнение каждой позиции из списка
    @Override
    public void onBindViewHolder(VH holder, final int position) {
        //super.onBindViewHolder(holder, position);// обязательно нужно вызывать, чтобы проинициализировать все переменные в родительских классах

        final T node = adapterList.get(position);// определяем выбранный пункт

//        final SwipeLayout swipeLayout = holder.swipeLayout;
//
//        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);// для вызова меню - свайп справа налево
//        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);// эффект появления и скрытия

        //closeItem(position);

//        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                listener.onSwipe(node);
//            }
//        });


        initListeners(holder, position, node);

        holder.tvNodeName.setText(node.getName());

        // если пользователем не установлена иконка - показываем иконку по-умолчанию
        if (node.getIconName() == null || IconUtils.getIcon(node.getIconName()) == null) {
            holder.imgNodeIcon.setImageDrawable(ResourcesCompat.getDrawable(activityContext.getResources(), R.drawable.ic_empty, null));
        } else {
            holder.imgNodeIcon.setImageDrawable(IconUtils.getIcon(node.getIconName()));

        }

//        // вместо itemDecoration - используем itemView в виде разделителя (т.е. itemDecoration не всегда корректно работает при переходах)
//        if (position == adapterList.size() - 1) {
//            holder.lineSeparator.setVisibility(View.GONE);// для последней записи убираем разделитель снизу
//        } else {
//            holder.lineSeparator.setVisibility(View.VISIBLE);
//        }


    }

    protected void initListeners(VH holder, final int position, final T node) {

//        holder.imgSwipeDeleteNode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                closeItem(position);
//                closeSnackBar();
//                deleteWithSnackbar(node, position);
//            }
//        });

//        holder.imgSwipeEditNode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runEditActivity(position, node);
//            }
//        });


        // обработка события при нажатии на любую запись списка
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // при клике на любую область свайп меню, кроме иконок - просто закрыть его
                //closeItem(position);

                // если был присвоен слушатель
                if (listener != null) {
                    // уведомляем слушателя, что был нажат пункт из списка
                    listener.onClick(node);// передаем, какой именно объект был нажат
                }


                nodeClickAction(position, node);

            }
        });

    }

    // создание нужных объетов анимации, которые будут использоваться в зависимости от того, куда переходим (родительские или дочерние списки)
    private void createAnimations() {

        animatorParents = new BaseItemAnimator() {

            @Override
            protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(holder.itemView.getRootView().getWidth())
                        .setDuration(getRemoveDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultRemoveVpaListener(holder))
                        .setStartDelay(getRemoveDelay(holder))
                        .start();
            }

            @Override
            protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
            }

            @Override
            protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(0)
                        .setDuration(getAddDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultAddVpaListener(holder))
                        .setStartDelay(getAddDelay(holder))
                        .start();
            }
        };


        animatorChilds = new BaseItemAnimator() {


            @Override
            protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {

                ViewCompat.animate(holder.itemView)
                        .translationX(-holder.itemView.getRootView().getWidth())
                        .setDuration(getRemoveDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultRemoveVpaListener(holder))
                        .setStartDelay(getRemoveDelay(holder))
                        .start();

            }

            @Override
            protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
            }

            @Override
            protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(0)
                        .setDuration(getAddDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultAddVpaListener(holder))
                        .setStartDelay(getAddDelay(holder))
                        .start();
            }


        };


    }


    @Override
    public int getItemCount() {
        return adapterList.size();
    }


    // закрыть snackbar, если в данный момент открыт - при закрытии будет фактически удален объект (т.к. не отменили удаление)
    // чтобы не было ошибок с удалением объектов - snackbar нужно всегда закрывать, если во время его показа пользователь начинает выполнять другие действия
    protected void closeSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }


    }


    protected void nodeClickAction(int position, T node) {
        runEditActivity(position, node);// если нет дочерних - открываем пункт для редактирования
    }


    protected void runEditActivity(int position, T node) {

        closeSnackBar();
        currentEditPosition = position;

        // какой именно активити открывать - реализовывает дочерний адаптер, главное передать правильный requestCode
        openActivityOnClick(node, AppContext.REQUEST_NODE_EDIT);
    }


    protected void deleteWithSnackbar(final T node, final int position) {

        closeSnackBar();// елсли ранее был открыт snackbar для другого элемента - закрываем его, чтобы открыть новый, для текущего элемента

        // сначала удаляем запись просто из текушей коллекции адаптера (без удалении из базы) - даем сначала пользователю шанс отменить
        adapterList.remove(node);
//                        notifyItemRemoved(position);
        // TODO реализовать обновление position для всех элементов

        notifyDataSetChanged(); // чтобы правильно обновлял позиции элементов


        // показываем Snackbar, чтобы дать пользователю возможность отменить действие
        snackbar = Snackbar.make(recyclerView, R.string.deleted, Snackbar.LENGTH_LONG); // для возможности отменить удаление

        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {// если нажали на ссылку ОТМЕНИТЬ
//                closeSwipeLayouts();
                adapterList.add(position, node);// возвращаем обратно
//                                notifyItemInserted(position);

                notifyDataSetChanged();

            }
        }).setCallback(new Snackbar.Callback() {// для того, чтобы могли отловить момент, когда SnackBar исчезнет (т.е. ползователь не успел отменить удаление)

            @Override
            public void onDismissed(Snackbar snackbar, int event) {

                if (event != DISMISS_EVENT_ACTION) {// если не была нажата ссылка отмены
                    deleteNode(node, position); // удаляем из-базы и коллекции ядра, обновляем список
                }

            }
        }).show();


    }


    // метод обновления данных может вызываться из фрагмента, где используется данный адаптер
    public void refreshList(final List<T> list, RecyclerView.ItemAnimator animator) {


        if (snackbar != null && snackbar.isShown()) {
            closeSnackBar();
        }

        // ставит требуемую анимацию для перехода (в зависимости от того, какие элементы открываем, дочерние или родительские)
        recyclerView.setItemAnimator(animator);


        int range = adapterList.size();
        notifyItemRangeRemoved(0, range);


        adapterList = list;
        notifyItemRangeInserted(0, adapterList.size());

    }


    // вставка нового корневого элемента
    public void addNode(T node) {
        try {
            manager.add(node);// сначала вставляем в БД и общую коллекцию
            notifyItemInserted(adapterList.size() - 1);// вставка в последнюю позицию
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    // обновление значения
    public void updateNode(T node) {
        try {
            manager.update(node);
            notifyItemChanged(currentEditPosition);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

    }


    // удаляет записи и обновляет список
    public boolean deleteNode(T node, int position) {
        try {

            // ранее уже удалили запись из списка - поэтому не вызываем  notifyItemRemoved(position), а просто удаляем из БД
            manager.delete(node);
            notifyDataSetChanged(); // этот метод также нужен, если пользователь нажмет отмену удаления и успеет перейти в другой список
            return true;

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        return false;
    }


    public void setListener(L listener) {
        this.listener = listener;
    }

    public void setContext(Activity activityContext) {
        this.activityContext = activityContext;
    }

    public List<T> getAdapterList() {
        return adapterList;
    }
}
