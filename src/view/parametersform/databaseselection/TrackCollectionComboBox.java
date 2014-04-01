package view.parametersform.databaseselection;


import domainmodel.TrackCollection;

import javax.swing.*;
import java.util.List;

public class TrackCollectionComboBox extends JComboBox {
    public TrackCollectionComboBox() {
        super();
    }

    public void setTrackCollections(final List<TrackCollection> types) {
        final TrackCollection curSelection = (TrackCollection) getSelectedItem();
        removeAllItems();
        for (TrackCollection type: types) {
            addItem(type);
        }
        if (types.size() == 1) setSelectedItem(types.get(0));
        else if (types.contains(curSelection)) setSelectedItem(curSelection);
        else setSelectedItem(types.get(0));
    }
}
