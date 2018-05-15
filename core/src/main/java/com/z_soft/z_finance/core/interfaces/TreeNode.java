package com.z_soft.z_finance.core.interfaces;

import java.io.Serializable;
import java.util.List;

// позволяет создать древовидную структуру из любого набора объектов, которые реализуют интерфейс TreeNode
// паттерн "Компоновщик" - вольная реализация
public interface TreeNode<T extends TreeNode> extends IconNode, RefNode{

    long getParentId();

    void add(T child); // добавить один дочерний элемент

    void remove(T child); // удалить один дочерний элемент

    List<T> getChilds(); // дочерних элементов может быть любое количество

    T getChild(long id); // получение дочернего элемента по id

    T getParent(); // получение родительского элемента - пригодится в разных ситуациях, например для отчетности по всем узлам дерева

    void setParent(T parent);	// установка родительского элемента

    boolean hasChilds(); // проверяет, есть ли дочерние элементы

    boolean hasParent(); // проверяет, есть ли родитель

}

