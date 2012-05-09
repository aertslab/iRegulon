package iRegulonInput.databaseSelection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;

import domainModel.Database;

public class DBCombobox extends JComboBox{

	private ArrayList<String> database;
	
	public DBCombobox(){
		super();
		this.database = new ArrayList<String>();
	}
	
	public void updateDatabases(List<Database> newDatabase){
		this.removeAllItems();
		for (Database db : newDatabase){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(Database aDatabase){
		return this.database.contains(aDatabase);
	}
	
	
}
