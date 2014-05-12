package view.parametersform.databaseselection;

import domainmodel.RankingsDatabase;

import javax.swing.*;
import java.util.List;

public class SearchSpaceTypeComboBox extends JComboBox {
	public SearchSpaceTypeComboBox(final RankingsDatabase.Type[] types) {
		super();
        for (RankingsDatabase.Type type: types) {
            this.addItem(type);
        }
		this.setSelectedIndex(0);
	}

    public SearchSpaceTypeComboBox() {
        this(RankingsDatabase.Type.values());
    }

    public boolean isRegionBased() {
        return RankingsDatabase.Type.REGION.equals(getSelectedItem());
	}
	
	public boolean isGeneBased(){
		return RankingsDatabase.Type.GENE.equals(getSelectedItem());
	}

    public void setTypes(final List<RankingsDatabase.Type> types) {
        final RankingsDatabase.Type curSelection = (RankingsDatabase.Type) getSelectedItem();
        removeAllItems();
        for (RankingsDatabase.Type type: types) {
            addItem(type);
        }

        if (types.size() == 1) {
            setSelectedItem(types.get(0));
        } else if (types.contains(curSelection)) {
            setSelectedItem(curSelection);
        } else {
            setSelectedItem(types.get(0));
        }
    }
}
