package view.parametersform;


import domainmodel.GeneIdentifier;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

final class GeneIdentifierComboBoxModel implements ComboBoxModel {
    private Object selection;
    private final List<GeneIdentifier> identifiers;

    GeneIdentifierComboBoxModel(Set<GeneIdentifier> identifiers) {
        this.identifiers = new ArrayList<GeneIdentifier>(identifiers);
        Collections.sort(this.identifiers);
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
        return identifiers.size();
    }

    @Override
    public Object getElementAt(int index) {
        return identifiers.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}
