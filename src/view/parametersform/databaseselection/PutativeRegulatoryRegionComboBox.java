package view.parametersform.databaseselection;

import domainmodel.GenePutativeRegulatoryRegion;

import javax.swing.*;
import java.util.List;

public class PutativeRegulatoryRegionComboBox extends JComboBox {
    public PutativeRegulatoryRegionComboBox() {
        super();
    }

    public void setRegions(final List<GenePutativeRegulatoryRegion> regions) {
        final GenePutativeRegulatoryRegion curSelection = (GenePutativeRegulatoryRegion) getSelectedItem();
        removeAllItems();
        for (GenePutativeRegulatoryRegion region : regions) {
            addItem(region);
        }

        if (regions.size() == 1) {
            setSelectedItem(regions.get(0));
        } else if (regions.contains(curSelection)) {
            setSelectedItem(curSelection);
        } else {
            setSelectedItem(regions.get(0));
        }

        setEnabled(regions.size() != 1
                   || !(GenePutativeRegulatoryRegion.UNKNOWN.equals(regions.get(0))
                        || GenePutativeRegulatoryRegion.NONE.equals(regions.get(0))));
    }
}
