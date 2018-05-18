package com.z_soft.z_finance.utils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class CurencyUtils {

    public static Currency defaultCurrency = Currency.getInstance("RUB");

    public static List<Currency> globalCurrencies = new ArrayList<>();

    static {
        globalCurrencies.add(Currency.getInstance("RUB"));
        globalCurrencies.add(Currency.getInstance("USD"));
    }

}
