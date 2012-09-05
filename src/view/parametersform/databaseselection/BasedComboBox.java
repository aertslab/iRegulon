package view.parametersform.databaseselection;

import java.util.ArrayList;

import javax.swing.JComboBox;

public class BasedComboBox extends JComboBox {

	private final String[] based = {"Gene Based", "Region Based"};
	private ArrayList<String> newBased;
	
	public BasedComboBox(){
		super();
		this.newBased = new ArrayList<String>();
		for (String base : this.based){
			this.addItem(base);
			this.newBased.add(base);
		}
		this.setSelectedIndex(0);
		
	}
	
	public boolean isRegionBased(){
		String selected = (String) this.getSelectedItem();
		if (selected.equals(this.based[1])){
			return true;
		}
		return false;
	}
	
	public boolean isGeneBased(){
		String selected = (String) this.getSelectedItem();
		if (selected.equals(this.based[0])){
			return true;
		}
		return false;
	}
	
	public void setOnlyGene(){
		this.newBased = new ArrayList<String>();
		this.newBased.add(based[0]);
		this.update();
	}
	
	public void setOnlyRegion(){
		this.newBased = new ArrayList<String>();
		this.newBased.add(based[1]);
		this.update();
	}
	
	public void setGeneAndRegion(){
		this.newBased = new ArrayList<String>();
		this.newBased.add(based[0]);
		this.newBased.add(based[1]);
		this.update();
	}
	
	private void update(){
		this.removeAllItems();
		for (String item : this.newBased){
			this.addItem(item);
		}
	}
	
	public boolean canBeSelected(String aString){
		return this.newBased.contains(aString);
	}
	
	
}
