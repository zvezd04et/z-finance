package com.z_soft.z_finance.listeners;

// интерфейс для слушателя события при появлении popup меню
// нужен для древовидных списков, где доступны кнопки вызова popup меню для каждого элемента списка
public interface TreeNodeActionListener<T> extends BaseNodeActionListener<T> {
    void onPopup(T selectedNode);
    void onSelectNode(T selectedNode);// вызывается, когда справочник находится в режиме выбора (при редактировании операции), чтобы вернуть выбранное значение
}
