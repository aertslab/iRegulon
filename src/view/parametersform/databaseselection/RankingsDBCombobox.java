package view.parametersform.databaseselection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import domainmodel.RankingsDatabase;

public class RankingsDBCombobox extends JComboBox{

	private List<RankingsDatabase> rankingsDatabase;
	
	public RankingsDBCombobox(){
		super();
		this.rankingsDatabase = new ArrayList<RankingsDatabase>();
	}
	
	public void updateDatabases(List<RankingsDatabase> newRankingsDatabase){
		this.rankingsDatabase = newRankingsDatabase;
		this.removeAllItems();
		for (RankingsDatabase db : newRankingsDatabase){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(RankingsDatabase aRankingsDatabase){
		return this.rankingsDatabase.contains(aRankingsDatabase);
	}
	
	
}
