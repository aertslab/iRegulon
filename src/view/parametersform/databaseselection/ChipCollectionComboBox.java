package view.parametersform.databaseselection;


import domainmodel.ChipCollection;

import javax.swing.*;
import java.util.List;

public class ChipCollectionComboBox extends JComboBox {
    public ChipCollectionComboBox() {
        super();
    }

    public void setChipCollections(final List<ChipCollection> types) {
        final ChipCollection curSelection = (ChipCollection) getSelectedItem();
        removeAllItems();
        for (ChipCollection type: types) {
            addItem(type);
        }
        if (types.size() == 1) setSelectedItem(types.get(0));
        else if (types.contains(curSelection)) setSelectedItem(curSelection);
        else setSelectedItem(types.get(0));
    }
}
