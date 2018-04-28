package com.z_soft.z_finance.core.impls.operations;

import com.z_soft.z_finance.core.abstracts.AbstractOperation;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.Storage;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

//TODO для всех классов создать конструкторы без поля id, так как оно будет autoincrement
public class IncomeOperation extends AbstractOperation {

    private Source fromSource;
    private Storage toStorage;
    private BigDecimal amount;
    private Currency currency;

    public IncomeOperation(long id, Calendar dateTime, String addInfo, Source fromSource, Storage toStorage, BigDecimal amount, Currency currency) {
        super(id, dateTime, addInfo);
        this.fromSource = fromSource;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    public IncomeOperation(long id, Source fromSource, Storage toStorage, BigDecimal amount, Currency currency) {
        super(id);
        this.fromSource = fromSource;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    public IncomeOperation(Source fromSource, Storage toStorage, BigDecimal amount, Currency currency) {
        this.fromSource = fromSource;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    public Source getFromSource() {
        return fromSource;
    }

    public void setFromSource(Source fromSource) {
        this.fromSource = fromSource;
    }

    public Storage getToStorage() {
        return toStorage;
    }

    public void setToStorage(Storage toStorage) {
        this.toStorage = toStorage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
