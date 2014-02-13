package view.parametersform.databaseselection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import domainmodel.RankingsDatabase;

public class MotifRankingsDBCombobox extends JComboBox{

	private List<RankingsDatabase> motifRankingsDatabase;
	
	public MotifRankingsDBCombobox(){
		super();
		this.motifRankingsDatabase = new ArrayList<RankingsDatabase>();
	}
	
	public void updateDatabases(List<RankingsDatabase> newMotifRankingsDatabase){
		this.motifRankingsDatabase = newMotifRankingsDatabase;
		this.removeAllItems();
		for (RankingsDatabase db : newMotifRankingsDatabase){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(RankingsDatabase aMotifRankingsDatabase){
		return this.motifRankingsDatabase.contains(aMotifRankingsDatabase);
	}
	
	
}
