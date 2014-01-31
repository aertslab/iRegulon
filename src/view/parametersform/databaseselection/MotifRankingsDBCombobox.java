package view.parametersform.databaseselection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import domainmodel.MotifRankingsDatabase;

public class MotifRankingsDBCombobox extends JComboBox{

	private List<MotifRankingsDatabase> motifRankingsDatabase;
	
	public MotifRankingsDBCombobox(){
		super();
		this.motifRankingsDatabase = new ArrayList<MotifRankingsDatabase>();
	}
	
	public void updateDatabases(List<MotifRankingsDatabase> newMotifRankingsDatabase){
		this.motifRankingsDatabase = newMotifRankingsDatabase;
		this.removeAllItems();
		for (MotifRankingsDatabase db : newMotifRankingsDatabase){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(MotifRankingsDatabase aMotifRankingsDatabase){
		return this.motifRankingsDatabase.contains(aMotifRankingsDatabase);
	}
	
	
}
