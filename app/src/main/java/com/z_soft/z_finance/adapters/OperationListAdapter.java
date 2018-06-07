package com.z_soft.z_finance.adapters;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.edit.operation.EditConvertOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditIncomeOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditOutcomeOperationActivity;
import com.z_soft.z_finance.activities.edit.operation.EditTransferOperationActivity;
import com.z_soft.z_finance.adapters.abstracts.BaseNodeListAdapter;
import com.z_soft.z_finance.adapters.holders.OperationViewHolder;
import com.z_soft.z_finance.comparators.OperationDateComparator;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.impls.operations.ConvertOperation;
import com.z_soft.z_finance.core.impls.operations.IncomeOperation;
import com.z_soft.z_finance.core.impls.operations.OutcomeOperation;
import com.z_soft.z_finance.core.impls.operations.TransferOperation;
import com.z_soft.z_finance.core.interfaces.Operation;
import com.z_soft.z_finance.listeners.BaseNodeActionListener;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.ColorUtils;
import com.z_soft.z_finance.utils.IconUtils;
import com.z_soft.z_finance.utils.OperationTypeUtils;

import java.util.Calendar;

public class OperationListAdapter extends BaseNodeListAdapter<Operation, OperationViewHolder, BaseNodeActionListener<Operation>> {

    private static final String TAG = OperationListAdapter.class.getName();


    public OperationListAdapter() {
        super(Initializer.getOperationManager(), R.layout.operation_item);
        comparator = OperationDateComparator.getInstance();
    }


    @Override
    protected void openActivityOnClick(Operation node, int requestCode) {

        Operation operation = null;
        Class activityClass = null;

        switch (requestCode){

            case AppContext.REQUEST_NODE_ADD: // для редактирования создаем новый пустой объект
                break;

            case AppContext.REQUEST_NODE_EDIT:

                switch (node.getOperationType()){
                    case INCOME:
                        activityClass = EditIncomeOperationActivity.class;
                        break;
                    case OUTCOME:
                        activityClass = EditOutcomeOperationActivity.class;
                        break;
                    case TRANSFER:
                        activityClass = EditTransferOperationActivity.class;
                        break;
                    case CONVERT:
                        activityClass = EditConvertOperationActivity.class;
                        break;
                }

                operation = node;
                break;
        }


        if (activityClass == null) {
            return;
        }

        Intent intent = new Intent(activityContext, activityClass); // какой акивити хотим вызвать
        intent.putExtra(AppContext.NODE_OBJECT, operation); // помещаем выбранный объект operation для передачи в активити
        intent.putExtra(AppContext.OPERATION_ACTION, AppContext.OPERATION_EDIT); // режим редактирования
        (activityContext).startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(activityContext).toBundle()); // устанавливаем анимацию перехода

    }

    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);// обязательно нужно вызывать
        return new OperationViewHolder(itemView);
    }




    // этот метод устанавливает только специфичные данные для элемента списка
    @Override
    public void onBindViewHolder(final OperationViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);// не забывать вызывать, чтобы заполнить общие компоненты

        final Operation node = adapterList.get(position);// определяем выбранный пункт
        holder.layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu dropDownMenu = new PopupMenu(activityContext, holder.tvNodeName);
                dropDownMenu.getMenuInflater().inflate(R.menu.oper_popup_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        // считываем, какой пункт нажали по его id
                         if (id == R.id.item_edit) {
                             runEditActivity(position, node);
                        } else if (id == R.id.item_delete) {// если нажали пункт удаления
                             deleteWithSnackbar(node, position);
                        }

                        return true;
                    }


                });

                dropDownMenu.show();

                return true;
            }
        });


        final Operation operation = adapterList.get(position);// определяем выбранный пункт

        String subTitle;

        // если год операции совпадает с текущим - показывать просто дату (без года)
        if (operation.getDateTime().get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)){
            subTitle= DateUtils.formatDateTime(activityContext, operation.getDateTime().getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
        }else{
            subTitle= DateUtils.formatDateTime(activityContext, operation.getDateTime().getTimeInMillis(), DateUtils.FORMAT_ABBREV_ALL);
        }


        int operationColor = 0;

        String amountTitle = null;
        // поля для каждого типа операции отличаются
        switch (operation.getOperationType()){
            case INCOME:
                operationColor = ContextCompat.getColor(activityContext, ColorUtils.incomeColor);
                IncomeOperation incomeOperation = (IncomeOperation) operation;
                amountTitle = "+" +  incomeOperation.getFromAmount().toString()+" "+incomeOperation.getFromCurrency().getSymbol().substring(0, 1);

                holder.tvNodeName.setText(incomeOperation.getFromSource().getName() + " -> " + incomeOperation.getToStorage().getName());
                holder.tvOperationTypeTag.setText(OperationTypeUtils.incomeType.toString());
                holder.imgNodeIcon.setImageDrawable(IconUtils.getIcon(incomeOperation.getFromSource().getIconName())); // иконка для операции - из категории, откуда пришли деньги

                break;

            case OUTCOME:
                operationColor = ContextCompat.getColor(activityContext, ColorUtils.outcomeColor);
                OutcomeOperation outcomeOperation = (OutcomeOperation) operation;
                amountTitle = "-" + outcomeOperation.getFromAmount().toString()+" "+outcomeOperation.getFromCurrency().getSymbol().substring(0, 1);
                holder.tvNodeName.setText(outcomeOperation.getFromStorage().getName() + " -> " + outcomeOperation.getToSource().getName());
                holder.tvOperationTypeTag.setText(OperationTypeUtils.outcomeType.toString());
                holder.imgNodeIcon.setImageDrawable(IconUtils.getIcon(outcomeOperation.getToSource().getIconName()));// иконка для операции - из категории, куда потратили деньги

                break;

            case TRANSFER:
                operationColor = ContextCompat.getColor(activityContext, ColorUtils.transferColor);
                TransferOperation transferOperation = (TransferOperation) operation;
                holder.tvNodeName.setText(transferOperation.getFromStorage().getName() + " -> " + transferOperation.getToStorage().getName());
                holder.tvOperationTypeTag.setText(OperationTypeUtils.transferType.toString());
                holder.imgNodeIcon.setImageDrawable(IconUtils.getIcon(transferOperation.getToStorage().getIconName()));// иконка для операции - из счета, куда перевели деньги

                break;

            case CONVERT:
                operationColor = ContextCompat.getColor(activityContext, ColorUtils.convertColor);
                ConvertOperation convertOperation = (ConvertOperation) operation;
                amountTitle = convertOperation.getToAmount().toString()+" "+convertOperation.getFromCurrency().getSymbol().substring(0, 1);
                holder.tvNodeName.setText(convertOperation.getFromStorage().getName() + " -> " + convertOperation.getToStorage().getName());
                holder.tvOperationTypeTag.setText(OperationTypeUtils.convertType.toString());
                holder.imgNodeIcon.setImageDrawable(IconUtils.getIcon(convertOperation.getToStorage().getIconName()));// иконка для операции - из счета, куда перевели деньги

                break;
        }

        holder.tvOperationTypeTag.setBackgroundColor(operationColor);
        holder.tvOperationAmount.setTextColor(operationColor);


        holder.tvOperationAmount.setText(amountTitle);
        holder.tvOperationSubtitle.setText(subTitle);

    }

}

