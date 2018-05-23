package com.z_soft.z_finance.activities.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.TextView;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.TreeListActivity;
import com.z_soft.z_finance.activities.edit.EditStorageActivity;
import com.z_soft.z_finance.adapters.StorageNodeAdapter;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.impls.DefaultStorage;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.fragments.TreeNodeListFragment;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.CurencyUtils;
import com.z_soft.z_finance.utils.CurrencyUtils;

import java.math.BigDecimal;

public class StorageListActivity extends TreeListActivity<Storage> {

    protected static final String TAG = StorageListActivity.class.getName();

    private TextView tvTotalBalance;

    public StorageListActivity() {

        TreeNodeListFragment<Storage> fragment = new TreeNodeListFragment<>();


        // обязательно надо проинициализировать
        init(fragment, R.layout.activity_storage_list, R.id.tlb_tree_list_actions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // обязательно нужно вызывать

        tvTotalBalance = findViewById(R.id.tv_total_balance);

        refreshTotalBalance();

        fragment.setAdapter(new StorageNodeAdapter(mode));

        setToolbarTitle(getResources().getString(R.string.storages));

        initListeners();


    }

    @Override
    protected void showRootNodes() {
        super.showRootNodes();

        fragment.refreshList(Initializer.getStorageManager().getAll());
    }

    protected void initListeners() {
        super.initListeners();// обязательно нужно вызывать

        // при нажатии на кнопку добавления элемента
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Storage storage = new DefaultStorage();
                try {
                    storage.addCurrency(CurencyUtils.defaultCurrency, BigDecimal.ZERO);
                } catch (CurrencyException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(StorageListActivity.this, EditStorageActivity.class); // какой акивити хоти вызвать
                intent.putExtra(AppContext.NODE_OBJECT, storage); // помещаем выбранный объект node для передачи в активити
                ActivityCompat.startActivityForResult(StorageListActivity.this, intent, AppContext.REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(StorageListActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

            }
        });

    }

    // обновить общую сумму по всем счетам
    public void refreshTotalBalance() {
        if (mode == AppContext.EDIT_MODE) {
            if (!Initializer.getStorageManager().getAll().isEmpty()) {
                tvTotalBalance.setVisibility(View.VISIBLE); // показываем элемент только при обычном просмотре справочников (не из редактируемой операции)
                tvTotalBalance.setText(getResources().getString(R.string.total_balance) + " ~ " +  Initializer.getStorageManager().getTotalBalance(CurrencyUtils.defaultCurrency).setScale(0, BigDecimal.ROUND_UP).toString() + " " + CurrencyUtils.defaultCurrency.getSymbol(AppContext.defaultLocale));
            } else {
                tvTotalBalance.setVisibility(View.GONE); // если нет доступных счетов - не показываем баланс
            }
        }
    }

    // при добавлении, изменении удаления любого счета - сразу обновляем общий баланс по всем счетам
    @Override
    public void onAdd(Storage node) {
        super.onAdd(node);
        refreshTotalBalance();
    }

    @Override
    public void onDelete(Storage node) {
        super.onDelete(node);
        refreshTotalBalance();
    }

    @Override
    public void onUpdate(Storage node) {
        super.onUpdate(node);
        refreshTotalBalance();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            refreshTotalBalance();
        }
    }
}
