package view.parametersform;

import domainmodel.*;
import infrastructure.CytoscapeNetworkUtilities;
import view.IRegulonResourceBundle;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cytoscape.Cytoscape;


import view.parametersform.databaseselection.MotifCollectionComboBox;
import view.parametersform.databaseselection.PutativeRegulatoryRegionComboBox;
import view.parametersform.databaseselection.SearchSpaceTypeComboBox;
import view.parametersform.databaseselection.DBCombobox;

final class DatabaseListener extends IRegulonResourceBundle implements ActionListener, DocumentListener {

	private final JTextField txtName;
	private final JTextField txtEscore;
	private final JTextField txtAUCvalue;
	private final JTextField txtVisualisation;
	private final JTextField txtMinOrthologous;
	private final JTextField txtMaxSimilarity;
	
	private final JComboBox speciesNomenclatureCB;
    private final MotifCollectionComboBox motifCollectionCB;
    private final PutativeRegulatoryRegionComboBox genePutativeRegulatoryRegionCB;
	private final SearchSpaceTypeComboBox searchSpaceTypeCB;
	private final DBCombobox databaseCB;
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
								JComboBox speciesNomenclatureCB,
                                MotifCollectionComboBox motifCollectionCB,
                                PutativeRegulatoryRegionComboBox genePutativeRegulatoryRegionCB,
								SearchSpaceTypeComboBox jcbBased,
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
		this.speciesNomenclatureCB = speciesNomenclatureCB;
		this.searchSpaceTypeCB = jcbBased;
		this.databaseCB = jcbDatabase;
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
        this.motifCollectionCB = motifCollectionCB;
        this.genePutativeRegulatoryRegionCB = genePutativeRegulatoryRegionCB;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.refresh();
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

	public void refresh(){
		if (! this.isBussy){
			this.isBussy = true;
			this.canSubmit = true;
			this.changedDatabase = false;
			this.refreshName();
			this.refreshRankingDatabases();
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
				this.txtName.getText().toLowerCase().equalsIgnoreCase(PredictedRegulatorsForm.deriveDefaultJobName())) {
			this.canSubmit = false;
			this.txtName.setBackground(Color.RED);
		}
		if (this.txtName.getText().toLowerCase().equalsIgnoreCase(PredictedRegulatorsForm.deriveDefaultJobName())) {
			if (!(Cytoscape.getCurrentNetwork().getTitle() == null || Cytoscape.getCurrentNetwork().getTitle().equals("0"))){
				this.txtName.setBackground(Color.WHITE);
				this.canSubmit = true;
			}
		}
	}
	
	private void refreshEscore(){
		this.txtEscore.setBackground(Color.WHITE);
        if (this.changedDatabase){
            RankingsDatabase dat = (RankingsDatabase) this.databaseCB.getSelectedItem();
            this.txtEscore.setText("" + dat.getNESvalue());
        }
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
			RankingsDatabase dat = (RankingsDatabase) this.databaseCB.getSelectedItem();
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
			RankingsDatabase dat = (RankingsDatabase) this.databaseCB.getSelectedItem();
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

    private void refreshRankingDatabases() {
        final SpeciesNomenclature speciesNomenclature = (SpeciesNomenclature) this.speciesNomenclatureCB.getSelectedItem();

        MotifCollection curMotifCollection = (MotifCollection) this.motifCollectionCB.getSelectedItem();
        final List<MotifCollection> motifCollections = speciesNomenclature.getMotifCollections();
        this.motifCollectionCB.setMotifCollections(motifCollections);
        if (!motifCollections.contains(curMotifCollection)) {
            curMotifCollection = motifCollections.get(0);
        }
        this.motifCollectionCB.setSelectedItem(curMotifCollection);

        RankingsDatabase.Type curSearchSpaceType = (RankingsDatabase.Type) this.searchSpaceTypeCB.getSelectedItem();
        final List<RankingsDatabase.Type> searchSpaceTypes = speciesNomenclature.getSearchSpaceTypes();
        this.searchSpaceTypeCB.setTypes(searchSpaceTypes);
        if (!searchSpaceTypes.contains(curSearchSpaceType)) {
            curSearchSpaceType = searchSpaceTypes.get(0);
        }
        this.searchSpaceTypeCB.setSelectedItem(curSearchSpaceType);

        GenePutativeRegulatoryRegion curRegion = (GenePutativeRegulatoryRegion) this.genePutativeRegulatoryRegionCB.getSelectedItem();
        final List<GenePutativeRegulatoryRegion> regions = speciesNomenclature.getPutativeRegulatoryRegions(curMotifCollection, curSearchSpaceType);
        this.genePutativeRegulatoryRegionCB.setRegions(regions);
        if (!regions.contains(curRegion)) {
            curRegion = regions.get(0);
        }
        this.genePutativeRegulatoryRegionCB.setSelectedItem(curRegion);

        final RankingsDatabase curDatabase = (RankingsDatabase) databaseCB.getSelectedItem();
        final List<RankingsDatabase> databases = speciesNomenclature.getDatabases(curMotifCollection, curSearchSpaceType, curRegion);
        this.databaseCB.updateDatabases(databases);
        if (databases.contains(curDatabase)) {
            this.databaseCB.setSelectedItem(curDatabase);
            this.changedDatabase = false;
        } else {
            this.databaseCB.setSelectedItem(databases.get(0));
            this.changedDatabase = true;
        }
    }
	
	private void refreshOverlap(){
		this.txtOverlap.setBackground(Color.WHITE);
		if (this.searchSpaceTypeCB.isGeneBased()){
			this.labelOverlap.setEnabled(false);
			this.txtOverlap.setEnabled(false);
		}
		if (this.searchSpaceTypeCB.isRegionBased()){
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
		if (this.searchSpaceTypeCB.isRegionBased()){
			boolean conversion = this.rbtnConversion.isSelected();
			boolean delineation = this.rbtnDelineation.isSelected();
			this.rbtnConversion.setEnabled(true);
			this.rbtnDelineation.setEnabled(true);
			final RankingsDatabase database = (RankingsDatabase) this.databaseCB.getSelectedItem();
            if (database.getGene2regionDelineations().isEmpty()){
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
		final Delineation curDelineation = (Delineation) this.jcbDelineation.getSelectedItem();
		final RankingsDatabase database = (RankingsDatabase) this.databaseCB.getSelectedItem();
        this.jcbDelineation.removeAllItems();
		for (Delineation key : database.getGene2regionDelineations()){
			this.jcbDelineation.addItem(key);
		}
        if (this.changedDatabase){
            if (database.getGene2regionDelineations().contains(database.getDelineationDefault())){
                this.jcbDelineation.setSelectedItem(database.getDelineationDefault());
            }
        }else if (database.getGene2regionDelineations().contains(curDelineation)){
			this.jcbDelineation.setSelectedItem(curDelineation);
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
			int amountNodes = CytoscapeNetworkUtilities.getGenes((String) this.jcbGeneNameAttr.getSelectedItem(),
                    (SpeciesNomenclature) this.speciesNomenclatureCB.getSelectedItem()).size();
			if (amountNodes == 0){
				this.txtAmountNodes.setBackground(Color.RED);
				this.canSubmit = false;
			}
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
	
}
