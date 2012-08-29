package view.parametersform;


import domainmodel.GeneIdentifier;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;

final class GeneIdentifierComboBoxModel implements ComboBoxModel {
    private Object selection;
    private final List<GeneIdentifier> identifiers;

    GeneIdentifierComboBoxModel(List<GeneIdentifier> identifiers) {
        this.identifiers = identifiers;
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
