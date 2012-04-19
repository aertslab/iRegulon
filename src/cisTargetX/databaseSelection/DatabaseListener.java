package cisTargetX.databaseSelection;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cisTargetX.CisTargetResourceBundle;
import cisTargetX.CisTargetXNodes;

import domainModel.SpeciesNomenclature;

public class DatabaseListener extends CisTargetResourceBundle implements ActionListener, DocumentListener{

	private final JTextField txtName;
	private final JTextField txtEscore;
	private final JTextField txtAUCvalue;
	private final JTextField txtVisualisation;
	private final JTextField txtMinOrthologous;
	private final JTextField txtMaxSimilarity;
	
	private final JComboBox jcbSpecies;
	private final BasedComboBox jcbBased;
	private final DBCombobox jcbDatabase;
	private final JTextField txtOverlap;
	private final JRadioButton rbtnDelineation;
	private final JComboBox jcbDelineation;
	private final JRadioButton rbtnConversion;
	private final JTextField txtUp;
	private final JTextField txtDown;
	private final JComboBox jcbGeneNameAttr;
	private final JTextField txtAmountNodes;
	
	private final JButton btnSubmit;
	
	private boolean canSubmit;
	private boolean isBussy;
	
	public DatabaseListener(JTextField txtName, JTextField txtEscore, 
			JTextField txtAUCvalue, JTextField txtVisualisation, 
			JTextField txtMinOrthologous, JTextField txtMaxSimilarity, 
			JComboBox jcbSpecies, BasedComboBox jcbBased, 
			DBCombobox jcbDatabase, JTextField txtOverlap, 
			JRadioButton rbtnDelineation,
			JComboBox jcbDelineation, JRadioButton rbtnConversion, JTextField txtUp, 
			JTextField txtDown, JComboBox jcbGeneNameAttr, 
			JTextField txtAmountNodes, JButton btnSubmit){
		this.txtName = txtName;
		this.txtEscore = txtEscore;
		this.txtAUCvalue = txtAUCvalue;
		this.txtVisualisation = txtVisualisation;
		this.txtMinOrthologous = txtMinOrthologous;
		this.txtMaxSimilarity = txtMaxSimilarity;
		this.jcbSpecies = jcbSpecies;
		this.jcbBased = jcbBased;
		this.jcbDatabase = jcbDatabase;
		this.txtOverlap = txtOverlap;
		this.rbtnDelineation = rbtnDelineation;
		this.jcbDelineation = jcbDelineation;
		this.rbtnConversion = rbtnConversion;
		this.txtUp = txtUp;
		this.txtDown = txtDown;
		this.jcbGeneNameAttr = jcbGeneNameAttr;
		this.txtAmountNodes = txtAmountNodes;
		this.btnSubmit = btnSubmit;
		this.canSubmit = true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.refresh();
	}

	public void refresh(){
		if (! this.isBussy){
			this.isBussy = true;
			this.canSubmit = true;
			this.refreshName();
			this.refreshEscore();
			this.refreshAUC();
			this.refreshVisualisation();
			this.refreshMinOrthologous();
			this.refreshMaxSimilarity();
			this.refreshBased();
			this.refreshDatabase();
			this.refreshOverlap();
			this.refreshRbtns();
			this.refreshDelineation();
			this.refreshUp();
			this.refreshDown();
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
		if (! species.getGeneDatabase().isEmpty() && ! species.getRegionsDatabase().isEmpty()){
			this.jcbBased.setGeneAndRegion();
		}else{
			if (! species.getGeneDatabase().isEmpty()){
				this.jcbBased.setOnlyGene();
			}
			if (! species.getRegionsDatabase().isEmpty()){
				this.jcbBased.setOnlyRegion();
			}
		}
		if (this.jcbBased.canBeSelected(based)){
			this.jcbBased.setSelectedItem(based);
		}	
	}
	
	private void refreshDatabase(){
		String database = (String) this.jcbDatabase.getSelectedItem();
		if (this.jcbBased.isGeneBased()){
			SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
			this.jcbDatabase.updateDatabases(species.getGeneDatabase().keySet());
		}else{
			if(this.jcbBased.isRegionBased()){
				SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
				this.jcbDatabase.updateDatabases(species.getRegionsDatabase().keySet());
			}
		}
		if (this.jcbDatabase.canBeSelected(database)){
			this.jcbDatabase.setSelectedItem(database);
		}
	}
	
	private void refreshOverlap(){
		this.txtOverlap.setBackground(Color.WHITE);
		if (this.jcbBased.isGeneBased()){
			this.txtOverlap.setEnabled(false);
		}
		if (this.jcbBased.isRegionBased()){
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
			if (species.getRegionsDelineation().isEmpty()){
				this.rbtnDelineation.setEnabled(false);
				this.rbtnConversion.setSelected(true);
				delineation = false;
				conversion = true;
			}
			if (delineation){
				this.jcbDelineation.setEnabled(true);
				
				this.txtDown.setEnabled(false);
				this.txtUp.setEnabled(false);
			}
			if (conversion){
				this.txtDown.setEnabled(true);
				this.txtUp.setEnabled(true);
				
				this.jcbDelineation.setEnabled(false);
			}
		}else{
			this.rbtnConversion.setEnabled(false);
			this.rbtnDelineation.setEnabled(false);
			this.jcbDelineation.setEnabled(false);
			this.txtDown.setEnabled(false);
			this.txtUp.setEnabled(false);
		}
	}
	
	private void refreshDelineation(){
		String delineation = (String) this.jcbDelineation.getSelectedItem();
		SpeciesNomenclature species = (SpeciesNomenclature) this.jcbSpecies.getSelectedItem();
		Set<String> keys = species.getRegionsDelineation().keySet();
		this.jcbDelineation.removeAllItems();
		for(String key : keys){
			this.jcbDelineation.addItem(key);
		}
		if (keys.contains(delineation)){
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
			int amountNodes = CisTargetXNodes.getGenes((String) this.jcbGeneNameAttr.getSelectedItem(), 
					(SpeciesNomenclature) this.jcbSpecies.getSelectedItem()).size();
			if (amountNodes == 0){
				this.txtAmountNodes.setBackground(Color.RED);
				this.canSubmit = false;
			}
			this.txtAmountNodes.setText("" + amountNodes);
		}else{
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
