package view.parametersform.databaseselection;

import domainmodel.MotifRankingsDatabase;

import java.util.List;

import javax.swing.JComboBox;

public class SearchSpaceTypeComboBox extends JComboBox {
	public SearchSpaceTypeComboBox(final MotifRankingsDatabase.Type[] types) {
		super();
        for (MotifRankingsDatabase.Type type: types) {
            this.addItem(type);
        }
		this.setSelectedIndex(0);
	}

    public SearchSpaceTypeComboBox() {
        this(MotifRankingsDatabase.Type.values());
    }

    public boolean isRegionBased() {
        return MotifRankingsDatabase.Type.REGION.equals(getSelectedItem());
	}
	
	public boolean isGeneBased(){
		return MotifRankingsDatabase.Type.GENE.equals(getSelectedItem());
	}

    public void setTypes(final List<MotifRankingsDatabase.Type> types) {
        final MotifRankingsDatabase.Type curSelection = (MotifRankingsDatabase.Type) getSelectedItem();
        removeAllItems();
        for (MotifRankingsDatabase.Type type: types) {
            addItem(type);
        }
        if (types.size() == 1) setSelectedItem(types.get(0));
        else if (types.contains(curSelection)) setSelectedItem(curSelection);
        else setSelectedItem(types.get(0));
    }
}
