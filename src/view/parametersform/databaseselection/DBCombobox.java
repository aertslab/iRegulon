package view.parametersform.databaseselection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import domainmodel.RankingsDatabase;

public class DBCombobox extends JComboBox{

	private List<RankingsDatabase> database;
	
	public DBCombobox(){
		super();
		this.database = new ArrayList<RankingsDatabase>();
	}
	
	public void updateDatabases(List<RankingsDatabase> newDatabase){
		this.database = newDatabase;
		this.removeAllItems();
		for (RankingsDatabase db : newDatabase){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(RankingsDatabase aDatabase){
		return this.database.contains(aDatabase);
	}
	
	
}
