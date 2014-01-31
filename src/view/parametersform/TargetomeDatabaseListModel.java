package view.parametersform;

import domainmodel.TargetomeDatabase;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;


final class TargetomeDatabaseListModel implements ListModel {
    private final List<TargetomeDatabase> targetomeDatabases;

    public TargetomeDatabaseListModel(final List<TargetomeDatabase> targetomeDatabases) {
        this.targetomeDatabases = targetomeDatabases;
    }

    @Override
    public int getSize() {
        return this.targetomeDatabases.size();
    }

    @Override
    public Object getElementAt(int index) {
        return this.targetomeDatabases.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}
