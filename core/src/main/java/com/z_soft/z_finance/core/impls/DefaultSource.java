package com.z_soft.z_finance.core.impls;

import com.z_soft.z_finance.core.abstracts.AbstractTreeNode;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.TreeNode;
import com.z_soft.z_finance.core.objects.OperationType;

import java.util.List;


public class DefaultSource extends AbstractTreeNode implements Source{

    private OperationType operationType;

    public DefaultSource() {
    }

    public DefaultSource(String name) {
        super(name);
    }

    public DefaultSource(List<TreeNode> childs) {
        super(childs);
    }

    public DefaultSource(String name, long id) {
        super(name, id);
    }

    public DefaultSource(long id, List<TreeNode> childs, TreeNode parent, String name) {
        super(id, childs, parent, name);
    }

    public DefaultSource(String name, long id, OperationType operationType) {
        super(name, id);
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public void add(TreeNode child) {

        //TODO поискать паттерны
        //для дочернего элемента устанавливаем тип операции родительского элемента
        if (child instanceof DefaultSource) {
            ((DefaultSource)child).setOperationType(operationType);
        }

        super.add(child);
    }
}
