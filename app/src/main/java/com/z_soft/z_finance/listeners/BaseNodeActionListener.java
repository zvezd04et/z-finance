package com.z_soft.z_finance.listeners;

// интерфейс для слушателя события при нажатии на элемент списка
public interface BaseNodeActionListener<T> {
    void onClick(T node);
    //void onSwipe(T node);
}
