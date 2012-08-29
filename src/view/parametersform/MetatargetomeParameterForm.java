package view.parametersform;


import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


final class MetatargetomeParameterForm extends JPanel implements MetatargetomeParameters {
    private static final int MARGIN_IN_PIXELS = 5;

    private final List<GeneIdentifier> factors;

    private JComboBox transcriptionFactorCB;
    private JList databaseList;

    public MetatargetomeParameterForm(List<GeneIdentifier> factors) {
        super();
        this.factors = factors;
        initPanel();
    }

    @Override
    public GeneIdentifier getTranscriptionFactor() {
        return (GeneIdentifier) transcriptionFactorCB.getSelectedItem();
    }

    public void setTranscriptionFactor(final GeneIdentifier geneID) {
        transcriptionFactorCB.setSelectedItem(geneID);
    }

    public void setDatabases(List<TargetomeDatabase> databases) {
        databaseList.clearSelection();
        for (TargetomeDatabase database: databases) {
            final int idx = findDatabase(database);
            if (idx >= 0) {
                databaseList.getSelectionModel().addSelectionInterval(idx, idx);
            }
        }
    }

    private int findDatabase(TargetomeDatabase database) {
        for (int idx = 0; idx < databaseList.getModel().getSize(); idx++) {
             if (databaseList.getModel().getElementAt(idx).equals(database)) {
                 return idx;
             }
        }
        return -1;
    }

    @Override
    public List<TargetomeDatabase> getDatabases() {
        final List<TargetomeDatabase> result = new ArrayList<TargetomeDatabase>();
        for (Object database : databaseList.getSelectedValues()) {
            result.add((TargetomeDatabase) database);
        }
        return result;
    }

    private void initPanel() {
        setLayout(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();

        final JLabel transcriptionFactorLB = new JLabel("Transcription Factor:");
        transcriptionFactorCB = new JComboBox(new GeneIdentifierComboBoxModel(factors));
        transcriptionFactorLB.setLabelFor(transcriptionFactorCB);

        cc.gridx = 0; cc.gridy = 0;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 0.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.insets = new Insets(MARGIN_IN_PIXELS, MARGIN_IN_PIXELS, 0, 0);
        add(transcriptionFactorLB, cc);

        cc.gridx++; cc.gridy = 0;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 1.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.insets = new Insets(MARGIN_IN_PIXELS, 0, 0, MARGIN_IN_PIXELS);
        add(transcriptionFactorCB, cc);

        final JLabel speciesNomenclatureLB = new JLabel("Species and nomemclature:");
        final JComboBox speciesNomenclatureCB = new JComboBox(new SpeciesNomenclatureComboBoxModel(SpeciesNomenclature.getAllNomenclatures()));
        speciesNomenclatureCB.setSelectedItem(SpeciesNomenclature.HOMO_SAPIENS_HGNC);
        speciesNomenclatureLB.setLabelFor(speciesNomenclatureCB);
        speciesNomenclatureCB.setEnabled(false);

        cc.gridx = 0; cc.gridy = 1;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 0.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.insets = new Insets(0, MARGIN_IN_PIXELS, 0, 0);
        add(speciesNomenclatureLB, cc);

        cc.gridx++; cc.gridy = 1;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 1.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.insets = new Insets(0, 0, 0, MARGIN_IN_PIXELS);
        add(speciesNomenclatureCB, cc);

        final JLabel databasesLB = new JLabel("Databases:");
        databaseList = new JList(new TargetomeDatabaseListModel(TargetomeDatabase.getAllDatabases()));
        databasesLB.setLabelFor(databaseList);
        databaseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        cc.gridx = 0; cc.gridy = 2;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 0.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.insets = new Insets(0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS, 0);
        add(databasesLB, cc);

        cc.gridx++; cc.gridy = 2;
        cc.gridwidth = 1; cc.gridheight = 2;
        cc.weightx = 1.0; cc.weighty = 1.0;
        cc.fill = GridBagConstraints.BOTH;
        cc.insets = new Insets(0, 0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS);
        add(new JScrollPane(databaseList), cc);
    }
}
