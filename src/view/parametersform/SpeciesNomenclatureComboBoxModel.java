package view.parametersform;

import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


final class SpeciesNomenclatureComboBoxModel implements ComboBoxModel {
    private Object selection;
    private final List<SpeciesNomenclature> databases;

    public SpeciesNomenclatureComboBoxModel(final Collection<SpeciesNomenclature> databases) {
        this.databases = new ArrayList<SpeciesNomenclature>(databases);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
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
