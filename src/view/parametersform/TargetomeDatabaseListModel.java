package view.parametersform;

import domainmodel.TargetomeDatabase;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;


final class TargetomeDatabaseListModel implements ListModel {
    private final List<TargetomeDatabase> databases;

    public TargetomeDatabaseListModel(final List<TargetomeDatabase> databases) {
        this.databases = databases;
    }

    @Override
    public int getSize() {
        return this.databases.size();
    }

    @Override
    public Object getElementAt(int index) {
        return this.databases.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}
