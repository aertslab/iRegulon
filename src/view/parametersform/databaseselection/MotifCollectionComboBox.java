package view.parametersform.databaseselection;


import domainmodel.MotifCollection;

import javax.swing.*;
import java.util.List;

public class MotifCollectionComboBox extends JComboBox {
    public MotifCollectionComboBox() {
        super();
    }

    public void setMotifCollections(final List<MotifCollection> types) {
        final MotifCollection curSelection = (MotifCollection) getSelectedItem();
        removeAllItems();
        for (MotifCollection type: types) {
            addItem(type);
        }
        if (types.size() == 1) setSelectedItem(types.get(0));
        else if (types.contains(curSelection)) setSelectedItem(curSelection);
        else setSelectedItem(types.get(0));
    }
}
