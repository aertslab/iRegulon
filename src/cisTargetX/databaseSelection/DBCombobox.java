package cisTargetX.databaseSelection;

import java.util.ArrayList;
import java.util.Set;

import javax.swing.JComboBox;

public class DBCombobox extends JComboBox{

	private ArrayList<String> database;
	
	public DBCombobox(){
		super();
		this.database = new ArrayList<String>();
	}
	
	public void updateDatabases(Set<String> newDatabase){
		this.database = new ArrayList<String>(newDatabase);
		this.removeAllItems();
		for (String db : this.database){
			this.addItem(db);
		}
	}
	
	public boolean canBeSelected(String aString){
		return this.database.contains(aString);
	}
	
	
}
