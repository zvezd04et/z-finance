package com.z_soft.z_finance.activities.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.z_soft.z_finance.R;
import com.z_soft.z_finance.activities.abstracts.BaseEditNodeActivity;
import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.utils.AppContext;
import com.z_soft.z_finance.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

// отвеачает за добавление и редактирование счета
public class EditStorageActivity extends BaseEditNodeActivity<Storage> {

    protected static final String TAG = EditStorageActivity.class.getName();


    private TableLayout tableCurrencyAmount;
    private TableLayout tableCurrencyList;
    private TextView tvAmountTotal;

    // заполняется динамически в зависимости от действий пользователя (может включить или выключить нужную валюту)
    private List<EditText> listBalance = new ArrayList<>();// хранит ссылки на добавленные компоненты валют и их значений


    public EditStorageActivity() {
        super(R.layout.activity_edit_storage);// какой макет будет использоваться

    }


    // метод работает со специфичными компонентами
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tableCurrencyAmount = (TableLayout) findViewById(R.id.tbl_currency_amount);
        tableCurrencyList = (TableLayout) findViewById(R.id.tbl_currency_list);
        tvAmountTotal = (TextView) findViewById(R.id.tv_total_balance);

        if (!node.getAvailableCurrencies().contains(CurrencyUtils.defaultCurrency)) {
            addCurrencyRow(CurrencyUtils.defaultCurrency, ""); // автомтически добавлять валюту по-умолчанию, если ее еще нет в этом storage
        }

        // показать все валюты storage вместе с остатками для каждой валюты
        showBalance();

        // показать доступные валюты, которые можно включить в данный storage (если необходимо)
        showCurrencies();

        // слушатель события при сохранении
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etName.getText().toString();

                // не давать сохранять пустое значение
                if (newName.trim().length() == 0) {
                    Toast.makeText(EditStorageActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                node.setName(newName);

                // если имя иконки было изменено
                if (newIconName != null) {
                    node.setIconName(newIconName);
                }

                // обновляем список валют с остатками для каждой валюты
                node.deleteAllCurrencies();// сначала удаляем все

                // затем добавляем по-одной валюте
                for (EditText et : listBalance) {
                    Currency c = Currency.getInstance((String) et.getTag()); // в tag хранится код валюты

                    BigDecimal amount = convertString(et.getText().toString());

                    try {
                        node.addCurrency(c, amount); // добавляем сумму с валютой
                    } catch (CurrencyException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }


                Intent intent = new Intent();
                intent.putExtra(AppContext.NODE_OBJECT, node);// передаем отредактированный объект, который нужно сохранить в БД
                setResult(RESULT_OK, intent);

                ActivityCompat.finishAfterTransition(EditStorageActivity.this);//закрыть активити


            }
        });


    }

    // показать все валюты storage вместе с остатками для каждой валюты
    private void showBalance() {

        Map<Currency, BigDecimal> map = node.getCurrencyAmounts();


        for (Map.Entry<Currency, BigDecimal> entry : map.entrySet()) {

            Currency c = entry.getKey();
            BigDecimal amount = entry.getValue() == null ? BigDecimal.ZERO : entry.getValue();

            addCurrencyRow(c, String.valueOf(amount));

        }


        showApproxAmount();
    }

    // показать среднее значение в одной валюте
    private void showApproxAmount() {

        String prefix = "";

        if (node.getAvailableCurrencies().size() > 1) {
            prefix = "~";
        }

        try {
            BigDecimal totalAmount = node.getApproxAmount(CurrencyUtils.defaultCurrency);
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = totalAmount.setScale(0, BigDecimal.ROUND_UP);

                tvAmountTotal.setText(prefix + totalAmount.toString() + " " + CurrencyUtils.defaultCurrency.getSymbol());
            } else {
                tvAmountTotal.setText("0 " + CurrencyUtils.defaultCurrency.getSymbol());
            }


        } catch (CurrencyException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // показать доступные валюты, которые можно включить в данный storage (если необходимо)
    private void showCurrencies() {

        for (Currency c : CurrencyUtils.globalCurrencies) {

            TableRow row = new TableRow(this);

            TextView tvCurrency = new TextView(this);
            tvCurrency.setText(c.getDisplayName());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(0, 20, 0, 0);
            tvCurrency.setGravity(Gravity.LEFT);

            tvCurrency.setLayoutParams(lp);

            final Switch switchCurrency = new Switch(this);
            switchCurrency.setTag(c);

            if (c.getCurrencyCode().equals(CurrencyUtils.defaultCurrency.getCurrencyCode())) {// вылюту по-умолчанию не даем отключать
                switchCurrency.setChecked(true);
                switchCurrency.setEnabled(false);
            } else {

                switchCurrency.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {


                                                          final Currency c = (Currency) switchCurrency.getTag();

                                                          if (switchCurrency.isChecked()) {
                                                              addCurrencyRow(c, ""); // если пользователь добавил новую валюту в storage
                                                          } else {


                                                              new AlertDialog.Builder(EditStorageActivity.this)
                                                                      .setTitle(R.string.confirm)
                                                                      .setMessage(R.string.confirm_delete_amount)
                                                                      .setIcon(android.R.drawable.ic_dialog_alert)


                                                                      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                                          @Override
                                                                          public void onClick(DialogInterface dialog, int which) {
                                                                              deleteCurrencyRow(c);
                                                                          }
                                                                      }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      dialog.cancel();
                                                                      switchCurrency.setChecked(true);
                                                                  }
                                                              }).show();


                                                          }


                                                      }
                                                  }

                );
            }


            switchCurrency.setLayoutParams(lp);
            switchCurrency.setGravity(Gravity.RIGHT);


            // для валют, которые уже есть storage - автоматически включаем переключатель
            if (node.getAvailableCurrencies().contains(c)) {
                switchCurrency.setChecked(true);
            } else {
                switchCurrency.setChecked(false);
            }

            row.addView(tvCurrency);
            row.addView(switchCurrency);

            tableCurrencyList.addView(row);

        }


    }

    // добавляет новую строку, содержащую название валюты и поле EditText, чтобы можно было вручную изменить баланс
    private void addCurrencyRow(Currency c, String value) {
        TableRow row = new TableRow(EditStorageActivity.this);

        TextView tvCurrency = new TextView(EditStorageActivity.this);
        tvCurrency.setText(c.getDisplayName());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(0, 20, 0, 0);
        tvCurrency.setGravity(Gravity.LEFT);

        tvCurrency.setLayoutParams(lp);

        EditText etAmount = new EditText(EditStorageActivity.this);
        etAmount.setText(value);


        etAmount.setLayoutParams(lp);
        etAmount.setGravity(Gravity.RIGHT);
        etAmount.setTag(c.getCurrencyCode());// чтобы потом можно было получить валюту, над которой пользователь изменил значение


        row.addView(tvCurrency);
        row.addView(etAmount);

        listBalance.add(etAmount);

        tableCurrencyAmount.addView(row);
    }


    private void deleteCurrencyRow(Currency c) {

        for (int i = 0; i <= tableCurrencyAmount.getChildCount() - 1; i++) {
            View view = tableCurrencyAmount.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                EditText e = (EditText) (row.getChildAt(1));// 1 - индекс компонента EditText внутри TableRow
                String currencyCode = (String) e.getTag();
                if (currencyCode.equals(c.getCurrencyCode())) {
                    row.setVisibility(View.GONE);
                    listBalance.remove(e);
                }
            }

        }


    }
}