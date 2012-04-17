package cisTargetX.databaseSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;

import cisTargetX.CisTargetResourceBundle;

import domainModel.SpeciesNomenclature;

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
