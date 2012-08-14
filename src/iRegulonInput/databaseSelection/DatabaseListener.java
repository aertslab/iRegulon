package iRegulonInput.databaseselection;

import iRegulonInput.IRegulonResourceBundle;
import iRegulonInput.NodesActions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cytoscape.Cytoscape;


import domainmodel.Database;
import domainmodel.Delineation;
import domainmodel.SpeciesNomenclature;

public class DatabaseListener extends IRegulonResourceBundle implements ActionListener, DocumentListener{

	private final JTextField txtName;
	private final JTextField txtEscore;
	private final JTextField txtAUCvalue;
	private final JTextField txtVisualisation;
	private final JTextField txtMinOrthologous;
	private final JTextField txtMaxSimilarity;
	
	private final JComboBox jcbSpecies;
	private final BasedComboBox jcbBased;
	private final DBCombobox jcbDatabase;
	private final JLabel labelOverlap;
	private final JTextField txtOverlap;
	private final JRadioButton rbtnDelineation;
	private final JComboBox jcbDelineation;
	private final JRadioButton rbtnConversion;
	private final JLabel labelUp;
	private final JTextField txtUp;
	private final JLabel labelDown;
	private final JTextField txtDown;
	private final JComboBox jcbGeneNameAttr;
	private final JTextField txtAmountNodes;
	
	private final JButton btnSubmit;
	
	private boolean canSubmit;
	private boolean isBussy;
	private boolean changedDatabase;
	
	public DatabaseListener(JTextField txtName, 
								JTextField txtEscore, 
								JTextField txtAUCvalue, 
								JTextField txtVisualisation, 
								JTextField txtMinOrthologous, 
								JTextField txtMaxSimilarity, 
								JComboBox jcbSpecies, 
								BasedComboBox jcbBased, 
								DBCombobox jcbDatabase,
								JLabel labelOverlap,
								JTextField txtOverlap, 
								JRadioButton rbtnDelineation,
								JComboBox jcbDelineation, 
								JRadioButton rbtnConversion, 
								JTextField txtUp, 
								JLabel labelUp,
								JTextField txtDown, 
								JLabel labelDown,
								JComboBox jcbGeneNameAttr, 
								JTextField txtAmountNodes, 
								JButton btnSubmit){
		this.txtName = txtName;
		this.txtEscore = txtEscore;
		this.txtAUCvalue = txtAUCvalue;
		this.txtVisualisation = txtVisualisation;
		this.txtMinOrthologous = txtMinOrthologous;
		this.txtMaxSimilarity = txtMaxSimilarity;
		this.jcbSpecies = jcbSpecies;
		this.jcbBased = jcbBased;
		this.jcbDatabase = jcbDatabase;
		this.labelOverlap = labelOverlap;
		this.txtOverlap = txtOverlap;
		this.rbtnDelineation = rbtnDelineation;
		this.jcbDelineation = jcbDelineation;
		this.rbtnConversion = rbtnConversion;
		this.txtUp = txtUp;
		this.labelUp = labelUp;
		this.txtDown = txtDown;
		this.labelDown = labelDown;
		this.jcbGeneNameAttr = jcbGeneNameAttr;
		this.txtAmountNodes = txtAmountNodes;
		this.btnSubmit = btnSubmit;
		this.canSubmit = true;
		this.isBussy = false;
		this.changedDatabase = false;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.refresh();
	}

	public void refresh(){
		if (! this.isBussy){
			this.isBussy = true;
			this.canSubmit = true;
			this.changedDatabase = false;
			this.refreshName();
			this.refreshBased();
			this.refreshDatabase();
			this.refreshOverlap();
			this.refreshRbtns();
			this.refreshDelineation();
			this.refreshUp();
			this.refreshDown();
			this.refreshEscore();
			this.refreshAUC();
			this.refreshVisualisation();
			this.refreshMinOrthologous();
			this.refreshMaxSimilarity();
			this.refreshGeneName();
			this.refreshAmountOfNodes();
			//Must always be last: refresh the button for the submit
			this.refreshSubmit();
			this.isBussy = false;
		}
	}
	
	private void refreshName(){
		this.txtName.setBackground(Color.WHITE);
		if (this.txtName.getText().isEmpty() || 
				this.txtName.getText().toLowerCase().equalsIgnoreCase(this.getBundle().getString("plugin_name") + " name")){
			this.canSubmit = false;
			this.txtName.setBackground(Color.RED);
		}
		if (this.txtName.getText().toLowerCase().equalsIgnoreCase(this.getBundle().getString("plugin_name") + " name")){
			if (!(Cytoscape.getCurrentNetwork().getTitle() == null || Cytoscape.getCurrentNetwork().getTitle().equals("0"))){
				this.txtName.setText(Cytoscape.getCurrentNetwork().getTitle());
				this.txtName.setBackground(Color.WHITE);
				this.canSubmit = true;
			}
		}
	}
	
	private void refreshEscore(){
		this.txtEscore.setBackground(Color.WHITE);
		try{
			if (this.txtEscore.getText().isEmpty() || 1.5 > Float.parseFloat(this.txtEscore.getText())){
				this.canSubmit = false;
				this.txtEscore.setBackground(Color.RED);	
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtEscore.setBackground(Color.RED);
		}
	}
	
	private void refreshAUC(){
		this.txtAUCvalue.setBackground(Color.WHITE);
		if (this.changedDatabase){
			Database dat = (Database) this.jcbDatabase.getSelectedItem();
			this.txtAUCvalue.setText("" + dat.getAUCvalue());
		}
		try{
			if (this.txtAUCvalue.getText().isEmpty() || 0 > Float.parseFloat(this.txtAUCvalue.getText())
					|| Float.parseFloat(this.txtAUCvalue.getText()) > 1){
				this.canSubmit = false;
				this.txtAUCvalue.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtAUCvalue.setBackground(Color.RED);
		}
	}
	
	private void refreshVisualisation(){
		this.txtVisualisation.setBackground(Color.WHITE);
		if (this.changedDatabase){
			Database dat = (Database) this.jcbDatabase.getSelectedItem();
			this.txtVisualisation.setText("" + dat.getVisualisationValue());
		}
		
		try{
			if (this.txtVisualisation.getText().isEmpty() || 1 > Integer.parseInt(this.txtVisualisation.getText())){
				this.canSubmit = false;
				this.txtVisualisation.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtVisualisation.setBackground(Color.RED);
		}
	}
	
	private void refreshMinOrthologous(){
		this.txtMinOrthologous.setBackground(Color.WHITE);
		try{
			if (this.txtMinOrthologous.getText().isEmpty() || 0 > Float.parseFloat(this.txtMinOrthologous.getText()) 
					|| Float.parseFloat(this.txtMinOrthologous.getText()) > 1){
				this.canSubmit = false;
				this.txtMinOrthologous.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtMinOrthologous.setBackground(Color.RED);
		}
	}
	
	private void refreshMaxSimilarity(){
		this.txtMaxSimilarity.setBackground(Color.WHITE);
		try{
			if (this.txtMaxSimilarity.getText().isEmpty() || 0 > Float.parseFloat(this.txtMaxSimilarity.getText())
					|| Float.parseFloat(this.txtMaxSimilarity.getText()) > 1){
				this.canSubmit = false;
				this.txtMaxSimilarity.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtMaxSimilarity.setBackground(Color.RED);
		}
	}
	
	private void refreshBased(){
		String based = (String) this.jcbBased.getSelectedItem();
		SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
		if (! species.getGeneDatabases().isEmpty() && ! species.getRegionDatabases().isEmpty()){
			this.jcbBased.setGeneAndRegion();
		}else{
			if (! species.getGeneDatabases().isEmpty()){
				this.jcbBased.setOnlyGene();
			}
			if (! species.getRegionDatabases().isEmpty()){
				this.jcbBased.setOnlyRegion();
			}
		}
		if (this.jcbBased.canBeSelected(based)){
			this.jcbBased.setSelectedItem(based);
		}	
	}
	
	private void refreshDatabase(){
		Database database = (Database) this.jcbDatabase.getSelectedItem();
		SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
		if (this.jcbBased.isGeneBased()){
			this.jcbDatabase.updateDatabases(species.getGeneDatabases());
		}else{
			if(this.jcbBased.isRegionBased()){
				this.jcbDatabase.updateDatabases(species.getRegionDatabases());
			}
		}
		if (this.jcbDatabase.canBeSelected(database)){
			this.jcbDatabase.setSelectedItem(database);
		}else{
			this.changedDatabase = true;
		}
		if (this.jcbDatabase.canBeSelected(database)){
			this.jcbDatabase.setSelectedItem(database);
		}	
	}
	
	private void refreshOverlap(){
		this.txtOverlap.setBackground(Color.WHITE);
		if (this.jcbBased.isGeneBased()){
			this.labelOverlap.setEnabled(false);
			this.txtOverlap.setEnabled(false);
		}
		if (this.jcbBased.isRegionBased()){
			this.labelOverlap.setEnabled(true);
			this.txtOverlap.setEnabled(true);
			try{
				if (this.txtOverlap.getText().isEmpty() || 0 > Float.parseFloat(this.txtOverlap.getText()) 
						|| Float.parseFloat(this.txtOverlap.getText()) > 1){
					this.canSubmit = false;
					this.txtOverlap.setBackground(Color.RED);
				}
			}catch(NumberFormatException e){
				this.canSubmit = false;
				this.txtOverlap.setBackground(Color.RED);
			}
		}
	}
	
	private void refreshRbtns(){
		if (this.jcbBased.isRegionBased()){
			boolean conversion = this.rbtnConversion.isSelected();
			boolean delineation = this.rbtnDelineation.isSelected();
			this.rbtnConversion.setEnabled(true);
			this.rbtnDelineation.setEnabled(true);
			SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
			if (species.getRegionDelineations().isEmpty()){
				this.rbtnDelineation.setEnabled(false);
				this.rbtnConversion.setSelected(true);
				delineation = false;
				conversion = true;
			}
			if (delineation){
				this.jcbDelineation.setEnabled(true);
				
				this.txtDown.setEnabled(false);
				this.txtUp.setEnabled(false);
				this.labelDown.setEnabled(false);
				this.labelUp.setEnabled(false);
			}
			if (conversion){
				this.txtDown.setEnabled(true);
				this.txtUp.setEnabled(true);
				this.labelDown.setEnabled(true);
				this.labelUp.setEnabled(true);
				
				this.jcbDelineation.setEnabled(false);
			}
		}else{
			this.rbtnConversion.setEnabled(false);
			this.rbtnDelineation.setEnabled(false);
			this.jcbDelineation.setEnabled(false);
			this.txtDown.setEnabled(false);
			this.txtUp.setEnabled(false);
			this.labelDown.setEnabled(false);
			this.labelUp.setEnabled(false);
		}
	}
	
	private void refreshDelineation(){
		Delineation delineation = (Delineation) this.jcbDelineation.getSelectedItem();
		SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
		this.jcbDelineation.removeAllItems();
		for(Delineation key : species.getRegionDelineations()){
			this.jcbDelineation.addItem(key);
		}
		if (species.getRegionDelineations().contains(delineation)){
			this.jcbDelineation.setSelectedItem(delineation);
		}
		
	}
	
	private void refreshUp(){
		this.txtUp.setBackground(Color.WHITE);
		try{
			if (this.txtUp.getText().isEmpty() || 0 > Float.parseFloat(this.txtUp.getText())){
				this.canSubmit = false;
				this.txtUp.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtUp.setBackground(Color.RED);
		}
	}
	
	private void refreshDown(){
		this.txtDown.setBackground(Color.WHITE);
		try{
			if (this.txtDown.getText().isEmpty() || 0 > Float.parseFloat(this.txtDown.getText())){
				this.canSubmit = false;
				this.txtDown.setBackground(Color.RED);
			}
		}catch(NumberFormatException e){
			this.canSubmit = false;
			this.txtDown.setBackground(Color.RED);
		}
	}
	
	private void refreshGeneName(){
		if (this.jcbGeneNameAttr.getSelectedItem() == null){
			this.canSubmit = false;
		}
	}
	
	private void refreshAmountOfNodes(){
		this.txtAmountNodes.setBackground(Color.WHITE);
		if (this.jcbGeneNameAttr.getSelectedItem() != null){
			int amountNodes = NodesActions.getGenes((String) this.jcbGeneNameAttr.getSelectedItem(), 
					(SpeciesNomenclature) this.jcbSpecies.getSelectedItem()).size();
			if (amountNodes == 0){
				this.txtAmountNodes.setBackground(Color.RED);
				this.canSubmit = false;
			}
			this.txtAmountNodes.setText("" + amountNodes);
		}else{
			this.txtAmountNodes.setText("0");
			this.txtAmountNodes.setBackground(Color.RED);
			this.canSubmit = false;
		}
	}
	
	private void refreshSubmit(){
		if (this.canSubmit){
			this.btnSubmit.setEnabled(true);
		}else{
			this.btnSubmit.setEnabled(false);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.refresh();
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.refresh();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.refresh();
	}
	
}
