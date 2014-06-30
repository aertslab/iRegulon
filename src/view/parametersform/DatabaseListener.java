package view.parametersform;

import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyNetwork;
import view.parametersform.databaseselection.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


final class DatabaseListener extends IRegulonResourceBundle implements ActionListener, DocumentListener {

    private final JTextField txtName;
    private final JTextField txtEscore;
    private final JTextField txtAUCvalue;
    private final JTextField txtVisualisation;
    private final JLabel labelMinOrthologous;
    private final JTextField txtMinOrthologous;
    private final JLabel labelMaxSimilarity;
    private final JTextField txtMaxSimilarity;

    private final JComboBox speciesNomenclatureCB;
    private final MotifCollectionComboBox motifCollectionCB;
    private final TrackCollectionComboBox trackCollectionCB;
    private final PutativeRegulatoryRegionComboBox genePutativeRegulatoryRegionCB;
    private final SearchSpaceTypeComboBox searchSpaceTypeCB;
    private final RankingsDBCombobox motifRankingsDatabaseCB;
    private final RankingsDBCombobox trackRankingsDatabaseCB;
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
    private boolean changedMotifRankingsDatabase;
    private boolean changedTrackRankingsDatabase;
    private boolean hasMotifCollection;
    private boolean hasTrackCollection;

    public DatabaseListener(JTextField txtName,
                            JTextField txtEscore,
                            JTextField txtAUCvalue,
                            JTextField txtVisualisation,
                            JLabel labelMinOrthologous,
                            JTextField txtMinOrthologous,
                            JLabel labelMaxSimilarity,
                            JTextField txtMaxSimilarity,
                            JComboBox speciesNomenclatureCB,
                            MotifCollectionComboBox motifCollectionCB,
                            TrackCollectionComboBox trackCollectionCB,
                            PutativeRegulatoryRegionComboBox genePutativeRegulatoryRegionCB,
                            SearchSpaceTypeComboBox jcbBased,
                            RankingsDBCombobox motifRankingsDatabaseCB,
                            RankingsDBCombobox trackRankingsDatabaseCB,
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
                            JButton btnSubmit) {
        this.txtName = txtName;
        this.txtEscore = txtEscore;
        this.txtAUCvalue = txtAUCvalue;
        this.txtVisualisation = txtVisualisation;
        this.labelMinOrthologous = labelMinOrthologous;
        this.txtMinOrthologous = txtMinOrthologous;
        this.labelMaxSimilarity = labelMaxSimilarity;
        this.txtMaxSimilarity = txtMaxSimilarity;
        this.speciesNomenclatureCB = speciesNomenclatureCB;
        this.searchSpaceTypeCB = jcbBased;
        this.motifRankingsDatabaseCB = motifRankingsDatabaseCB;
        this.trackRankingsDatabaseCB = trackRankingsDatabaseCB;
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
        this.hasMotifCollection = false;
        this.hasTrackCollection = false;
        this.changedMotifRankingsDatabase = false;
        this.changedTrackRankingsDatabase = false;
        this.motifCollectionCB = motifCollectionCB;
        this.trackCollectionCB = trackCollectionCB;
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

    public void refresh() {
        if (!this.isBussy) {
            this.hasMotifCollection = false;
            this.hasTrackCollection = false;
            this.isBussy = true;
            this.canSubmit = true;
            this.changedMotifRankingsDatabase = false;
            this.changedTrackRankingsDatabase = false;
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

    private void refreshName() {
        this.txtName.setBackground(Color.WHITE);
        if (this.txtName.getText().isEmpty() ||
                this.txtName.getText().toLowerCase().equalsIgnoreCase(PredictedRegulatorsForm.deriveDefaultJobName())) {
            this.canSubmit = false;
            this.txtName.setBackground(Color.RED);
        }
        if (this.txtName.getText().toLowerCase().equalsIgnoreCase(PredictedRegulatorsForm.deriveDefaultJobName())) {
            final String name = NetworkUtilities.getInstance().getCurrentNetworkName();
            if (!(name == null || "".equals(name.trim()) || name.equals("0"))) {
                this.txtName.setBackground(Color.WHITE);
                this.canSubmit = true;
            }
        }
    }

    private void refreshEscore() {
        this.txtEscore.setBackground(Color.WHITE);
        if (this.changedMotifRankingsDatabase) {
            RankingsDatabase motifRankingsDatabase = (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
            this.txtEscore.setText("" + motifRankingsDatabase.getNESvalue());
        }
        try {
            if (this.txtEscore.getText().isEmpty() || 1.5 > Float.parseFloat(this.txtEscore.getText())) {
                this.canSubmit = false;
                this.txtEscore.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtEscore.setBackground(Color.RED);
        }
    }

    private void refreshAUC() {
        this.txtAUCvalue.setBackground(Color.WHITE);
        if (this.changedMotifRankingsDatabase) {
            RankingsDatabase motifRankingsDatabase = (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
            this.txtAUCvalue.setText("" + motifRankingsDatabase.getAUCvalue());
        } else if (this.changedTrackRankingsDatabase) {
            RankingsDatabase trackRankingsDatabase = (RankingsDatabase) this.trackRankingsDatabaseCB.getSelectedItem();
            this.txtAUCvalue.setText("" + trackRankingsDatabase.getAUCvalue());
        }

        try {
            if (this.txtAUCvalue.getText().isEmpty() || 0 > Float.parseFloat(this.txtAUCvalue.getText())
                    || Float.parseFloat(this.txtAUCvalue.getText()) > 1) {
                this.canSubmit = false;
                this.txtAUCvalue.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtAUCvalue.setBackground(Color.RED);
        }
    }

    private void refreshVisualisation() {
        this.txtVisualisation.setBackground(Color.WHITE);
        if (this.changedMotifRankingsDatabase) {
            RankingsDatabase motifRankingsDatabase = (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
            this.txtVisualisation.setText("" + motifRankingsDatabase.getVisualisationValue());
        } else if (this.changedTrackRankingsDatabase) {
            RankingsDatabase trackRankingsDatabase = (RankingsDatabase) this.trackRankingsDatabaseCB.getSelectedItem();
            this.txtVisualisation.setText("" + trackRankingsDatabase.getVisualisationValue());
        }

        try {
            if (this.txtVisualisation.getText().isEmpty() || 1 > Integer.parseInt(this.txtVisualisation.getText())) {
                this.canSubmit = false;
                this.txtVisualisation.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtVisualisation.setBackground(Color.RED);
        }
    }

    private void refreshMinOrthologous() {
        this.txtMinOrthologous.setBackground(Color.WHITE);
        try {
            if (this.txtMinOrthologous.getText().isEmpty() || 0 > Float.parseFloat(this.txtMinOrthologous.getText())
                    || Float.parseFloat(this.txtMinOrthologous.getText()) > 1) {
                this.canSubmit = false;
                this.txtMinOrthologous.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtMinOrthologous.setBackground(Color.RED);
        }
    }

    private void refreshMaxSimilarity() {
        this.txtMaxSimilarity.setBackground(Color.WHITE);
        try {
            if (this.txtMaxSimilarity.getText().isEmpty() || 0 > Float.parseFloat(this.txtMaxSimilarity.getText())
                    || Float.parseFloat(this.txtMaxSimilarity.getText()) > 1) {
                this.canSubmit = false;
                this.txtMaxSimilarity.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtMaxSimilarity.setBackground(Color.RED);
        }
    }

    private void refreshRankingDatabases() {
        final SpeciesNomenclature speciesNomenclature = (SpeciesNomenclature) this.speciesNomenclatureCB.getSelectedItem();

        final List<RankingsDatabase.Type> searchSpaceTypes = speciesNomenclature.getSearchSpaceTypes();
        this.searchSpaceTypeCB.setTypes(searchSpaceTypes);
        RankingsDatabase.Type curSearchSpaceType = (RankingsDatabase.Type) this.searchSpaceTypeCB.getSelectedItem();

        final List<MotifCollection> motifCollections = speciesNomenclature.getMotifCollections(curSearchSpaceType);
        this.motifCollectionCB.setMotifCollections(motifCollections);
        MotifCollection curMotifCollection = (MotifCollection) this.motifCollectionCB.getSelectedItem();

        final List<TrackCollection> trackCollections = speciesNomenclature.getTrackCollections(curSearchSpaceType);
        this.trackCollectionCB.setTrackCollections(trackCollections);
        TrackCollection curTrackCollection = (TrackCollection) this.trackCollectionCB.getSelectedItem();

        if (curMotifCollection.getCode().equals(MotifCollection.NONE.getCode())) {
            this.hasMotifCollection = false;
        } else {
            this.hasMotifCollection = true;
        }

        if (curTrackCollection.getCode().equals(TrackCollection.NONE.getCode())) {
            this.hasTrackCollection = false;
        } else {
            this.hasTrackCollection = true;
        }

        GenePutativeRegulatoryRegion curRegion = GenePutativeRegulatoryRegion.NONE;

        if (!hasMotifCollection && !hasTrackCollection) {
            List<GenePutativeRegulatoryRegion> unknownGenePutativeRegulatoryRegion = new ArrayList<GenePutativeRegulatoryRegion>();
            unknownGenePutativeRegulatoryRegion.add(GenePutativeRegulatoryRegion.NONE);
            this.genePutativeRegulatoryRegionCB.setRegions(unknownGenePutativeRegulatoryRegion);

            this.canSubmit = false;
        } else {
            final List<GenePutativeRegulatoryRegion> regions = speciesNomenclature.getPutativeRegulatoryRegions(curMotifCollection, curTrackCollection, curSearchSpaceType);

            this.genePutativeRegulatoryRegionCB.setRegions(regions);
            curRegion = (GenePutativeRegulatoryRegion) this.genePutativeRegulatoryRegionCB.getSelectedItem();
        }

        if (this.hasMotifCollection) {
            final RankingsDatabase curMotifRankingsDatabase = (RankingsDatabase) motifRankingsDatabaseCB.getSelectedItem();
            final List<RankingsDatabase> motifRankingsDatabases = speciesNomenclature.getMotifDatabases(curMotifCollection, curSearchSpaceType, curRegion);
            this.motifRankingsDatabaseCB.updateDatabases(motifRankingsDatabases);
            /* Check if previous selected motif rankings database is in the list of motif rankings databases after updating it. */
            if (motifRankingsDatabases.contains(curMotifRankingsDatabase)) {
                this.changedMotifRankingsDatabase = false;
            } else {
                this.changedMotifRankingsDatabase = true;
            }
            this.motifRankingsDatabaseCB.setEnabled(true);

            /* Allow setting of TF prediction related settings. */
            this.labelMaxSimilarity.setEnabled(true);
            this.txtMaxSimilarity.setEnabled(true);
            this.labelMinOrthologous.setEnabled(true);
            this.txtMinOrthologous.setEnabled(true);
        } else {
            this.motifRankingsDatabaseCB.setEnabled(false);

            /* Disallow setting of TF prediction related settings. */
            this.labelMaxSimilarity.setEnabled(false);
            this.txtMaxSimilarity.setEnabled(false);
            this.labelMinOrthologous.setEnabled(false);
            this.txtMinOrthologous.setEnabled(false);
        }

        if (this.hasTrackCollection) {
            final RankingsDatabase curTrackRankingsDatabase = (RankingsDatabase) trackRankingsDatabaseCB.getSelectedItem();
            final List<RankingsDatabase> trackRankingsDatabases = speciesNomenclature.getTrackDatabases(curTrackCollection, curSearchSpaceType, curRegion);
            this.trackRankingsDatabaseCB.updateDatabases(trackRankingsDatabases);
            /* Check if previous selected track rankings database is in the list of track rankings databases after updating it. */
            if (trackRankingsDatabases.contains(curTrackRankingsDatabase)) {
                this.changedTrackRankingsDatabase = false;
            } else {
                this.changedTrackRankingsDatabase = true;
            }
            this.trackRankingsDatabaseCB.setEnabled(true);
        } else {
            this.trackRankingsDatabaseCB.setEnabled(false);
        }
    }

    private void refreshOverlap() {
        this.txtOverlap.setBackground(Color.WHITE);
        if (this.searchSpaceTypeCB.isGeneBased()) {
            this.labelOverlap.setEnabled(false);
            this.txtOverlap.setEnabled(false);
        }
        if (this.searchSpaceTypeCB.isRegionBased()) {
            this.labelOverlap.setEnabled(true);
            this.txtOverlap.setEnabled(true);
            try {
                if (this.txtOverlap.getText().isEmpty() || 0 > Float.parseFloat(this.txtOverlap.getText())
                        || Float.parseFloat(this.txtOverlap.getText()) > 1) {
                    this.canSubmit = false;
                    this.txtOverlap.setBackground(Color.RED);
                }
            } catch (NumberFormatException e) {
                this.canSubmit = false;
                this.txtOverlap.setBackground(Color.RED);
            }
        }
    }

    private void refreshRbtns() {
        if (this.searchSpaceTypeCB.isRegionBased()) {
            boolean conversion = this.rbtnConversion.isSelected();
            boolean delineation = this.rbtnDelineation.isSelected();
            this.rbtnConversion.setEnabled(true);
            this.rbtnDelineation.setEnabled(true);
            final RankingsDatabase motifRankingsDatabase = (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
            if (motifRankingsDatabase.getGene2regionDelineations().isEmpty()) {
                this.rbtnDelineation.setEnabled(false);
                this.rbtnConversion.setSelected(true);
                delineation = false;
                conversion = true;
            }
            if (delineation) {
                this.jcbDelineation.setEnabled(true);

                this.txtDown.setEnabled(false);
                this.txtUp.setEnabled(false);
                this.labelDown.setEnabled(false);
                this.labelUp.setEnabled(false);
            }
            if (conversion) {
                this.txtDown.setEnabled(true);
                this.txtUp.setEnabled(true);
                this.labelDown.setEnabled(true);
                this.labelUp.setEnabled(true);

                this.jcbDelineation.setEnabled(false);
            }
        } else {
            this.rbtnConversion.setEnabled(false);
            this.rbtnDelineation.setEnabled(false);
            this.jcbDelineation.setEnabled(false);
            this.txtDown.setEnabled(false);
            this.txtUp.setEnabled(false);
            this.labelDown.setEnabled(false);
            this.labelUp.setEnabled(false);
        }
    }

    private void refreshDelineation() {
        final Delineation curDelineation = (Delineation) this.jcbDelineation.getSelectedItem();
        final RankingsDatabase motifRankingsDatabase = (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
        final RankingsDatabase trackRankingsDatabase = (RankingsDatabase) this.trackRankingsDatabaseCB.getSelectedItem();
        if (motifRankingsDatabase.hasMotifCollection()) {
            this.jcbDelineation.removeAllItems();
            for (Delineation key : motifRankingsDatabase.getGene2regionDelineations()) {
                this.jcbDelineation.addItem(key);
            }
            if (this.changedMotifRankingsDatabase) {
                if (motifRankingsDatabase.getGene2regionDelineations().contains(motifRankingsDatabase.getDelineationDefault())) {
                    this.jcbDelineation.setSelectedItem(motifRankingsDatabase.getDelineationDefault());
                }
            } else if (motifRankingsDatabase.getGene2regionDelineations().contains(curDelineation)) {
                this.jcbDelineation.setSelectedItem(curDelineation);
            }
        } else if (trackRankingsDatabase.hasTrackCollection()) {
            this.jcbDelineation.removeAllItems();
            for (Delineation key : trackRankingsDatabase.getGene2regionDelineations()) {
                this.jcbDelineation.addItem(key);
            }
            if (this.changedTrackRankingsDatabase) {
                if (trackRankingsDatabase.getGene2regionDelineations().contains(trackRankingsDatabase.getDelineationDefault())) {
                    this.jcbDelineation.setSelectedItem(trackRankingsDatabase.getDelineationDefault());
                }
            } else if (trackRankingsDatabase.getGene2regionDelineations().contains(curDelineation)) {
                this.jcbDelineation.setSelectedItem(curDelineation);
            }
        }
    }

    private void refreshUp() {
        this.txtUp.setBackground(Color.WHITE);
        try {
            if (this.txtUp.getText().isEmpty() || 0 > Float.parseFloat(this.txtUp.getText())) {
                this.canSubmit = false;
                this.txtUp.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtUp.setBackground(Color.RED);
        }
    }

    private void refreshDown() {
        this.txtDown.setBackground(Color.WHITE);
        try {
            if (this.txtDown.getText().isEmpty() || 0 > Float.parseFloat(this.txtDown.getText())) {
                this.canSubmit = false;
                this.txtDown.setBackground(Color.RED);
            }
        } catch (NumberFormatException e) {
            this.canSubmit = false;
            this.txtDown.setBackground(Color.RED);
        }
    }

    private void refreshGeneName() {
        if (this.jcbGeneNameAttr.getSelectedItem() == null) {
            this.canSubmit = false;
        }
    }

    private void refreshAmountOfNodes() {
        this.txtAmountNodes.setBackground(Color.WHITE);
        if (this.jcbGeneNameAttr.getSelectedItem() != null) {
            final CyNetwork network = NetworkUtilities.getInstance().getCurrentNetwork();
            int amountNodes = NetworkUtilities.getInstance().getSelectedNodesAsGeneIDs(network, (String) this.jcbGeneNameAttr.getSelectedItem(),
                    (SpeciesNomenclature) this.speciesNomenclatureCB.getSelectedItem()).size();
            if (amountNodes == 0) {
                this.txtAmountNodes.setBackground(Color.RED);
                this.canSubmit = false;
            }
        } else {
            this.txtAmountNodes.setBackground(Color.RED);
            this.canSubmit = false;
        }
    }

    private void refreshSubmit() {
        if (this.canSubmit) {
            this.btnSubmit.setEnabled(true);
        } else {
            this.btnSubmit.setEnabled(false);
        }
    }

}
