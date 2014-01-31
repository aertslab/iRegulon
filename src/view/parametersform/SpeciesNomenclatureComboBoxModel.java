package view.parametersform;

import domainmodel.SpeciesNomenclature;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


final class SpeciesNomenclatureComboBoxModel implements ComboBoxModel {
    private Object selection;
    private final List<SpeciesNomenclature> speciesNomenclatureDatabases;

    public SpeciesNomenclatureComboBoxModel(final Collection<SpeciesNomenclature> speciesNomenclatureDatabases) {
        this.speciesNomenclatureDatabases = new ArrayList<SpeciesNomenclature>(speciesNomenclatureDatabases);
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
        return this.speciesNomenclatureDatabases.size();
    }

    @Override
    public Object getElementAt(int index) {
        return this.speciesNomenclatureDatabases.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}
