package com.z_soft.z_finance.core.interfaces;

// для подсчета количества ссылок на этот node
// нужно для того, чтобы не давать удалять справочные записи, на которые есть ссылки внутри операций
public interface RefNode {

    int getRefCount();

}