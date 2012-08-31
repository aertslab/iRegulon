package view.parametersform;


import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import view.resultspanel.Refreshable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


final class MetatargetomeParameterForm extends JPanel implements MetatargetomeParameters, Refreshable {
    private static final int MARGIN_IN_PIXELS = 5;

    private final Map<SpeciesNomenclature, List<GeneIdentifier>> nomenclature2factors;

    private JComboBox transcriptionFactorCB;
    private JComboBox speciesNomenclatureCB;
    private JList databaseList;
    private JComboBox attributeNameCB;

    private final List<ParameterChangeListener> listeners = new ArrayList<ParameterChangeListener>();

    private final ActionListener actionListener;
    private final ItemListener refreshListener;
    private final ItemListener itemListener;
    private final ListSelectionListener selectionListener;


    public MetatargetomeParameterForm(Map<SpeciesNomenclature, List<GeneIdentifier>> nomenclature2factors) {
        super();
        this.nomenclature2factors = nomenclature2factors;
        initPanel();

        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireParametersChanged();
            }
        };
        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fireParametersChanged();
            }
        };
        selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fireParametersChanged();
            }
        };
        refreshListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        };
        registerListeners();

        refresh();
    }

    @Override
    public GeneIdentifier getTranscriptionFactor() {
        return (GeneIdentifier) transcriptionFactorCB.getSelectedItem();
    }

    public void setTranscriptionFactor(final GeneIdentifier geneID) {
        transcriptionFactorCB.setSelectedItem(geneID);
    }

    public SpeciesNomenclature getSpeciesNomenclature() {
        return (SpeciesNomenclature) speciesNomenclatureCB.getSelectedItem();
    }

    public void setSpeciesNomenclature(final SpeciesNomenclature speciesNomenclature) {
        speciesNomenclatureCB.setSelectedItem(speciesNomenclature == null ? SpeciesNomenclature.HOMO_SAPIENS_HGNC : speciesNomenclature);
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

    @Override
    public String getAttributeName() {
        final String attributeName = (String) attributeNameCB.getSelectedItem();
        return attributeName == null ? AttributeComboBox.ID_ATTRIBUTE_NAME : attributeName;
    }

    public void setAttributeName(final String attributeName) {
        attributeNameCB.setSelectedItem(attributeName);
    }

    private void initPanel() {
        setLayout(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();

        final JLabel transcriptionFactorLB = new JLabel("Transcription Factor:");
        transcriptionFactorCB = new JComboBox();
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
        speciesNomenclatureCB = new JComboBox(new SpeciesNomenclatureComboBoxModel(nomenclature2factors.keySet()));
        speciesNomenclatureLB.setLabelFor(speciesNomenclatureCB);

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

        final JLabel attributeNameLB = new JLabel("Attribute name:");
        attributeNameCB = new JComboBox(AttributeComboBox.getPossibleGeneIDAttributesWithDefault().toArray());

        cc.gridx = 0; cc.gridy = 4;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 0.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.insets = new Insets(0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS, 0);
        add(attributeNameLB, cc);

        cc.gridx++; cc.gridy = 4;
        cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 1.0; cc.weighty = 0.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.insets = new Insets(0, 0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS);
        add(attributeNameCB, cc);
    }

    private void registerListeners() {
        speciesNomenclatureCB.addActionListener(actionListener);
        attributeNameCB.addActionListener(actionListener);
        databaseList.getSelectionModel().addListSelectionListener(selectionListener);
        transcriptionFactorCB.addActionListener(actionListener);
        transcriptionFactorCB.addItemListener(itemListener);
        speciesNomenclatureCB.addItemListener(refreshListener);
    }

    private void unregisterListeners() {
        speciesNomenclatureCB.removeActionListener(actionListener);
        attributeNameCB.removeActionListener(actionListener);
        databaseList.getSelectionModel().removeListSelectionListener(selectionListener);
        transcriptionFactorCB.removeActionListener(actionListener);
        transcriptionFactorCB.removeItemListener(itemListener);
        speciesNomenclatureCB.removeItemListener(refreshListener);
    }

    @Override
    public void refresh() {
        unregisterListeners();

        final GeneIdentifier curID = getTranscriptionFactor();

        final List<GeneIdentifier> IDs = nomenclature2factors.containsKey(getSpeciesNomenclature())
                ? nomenclature2factors.get(getSpeciesNomenclature())
                : Collections.<GeneIdentifier>emptyList();
        transcriptionFactorCB.setModel(new GeneIdentifierComboBoxModel(IDs));
        transcriptionFactorCB.setEnabled(transcriptionFactorCB.getModel().getSize() != 0);

        if (IDs.contains(curID)) setTranscriptionFactor(curID);
        else if (!IDs.isEmpty()) setTranscriptionFactor(IDs.get(0));

        registerListeners();
        fireParametersChanged();
    }

    public void addParameterChangeListener(final ParameterChangeListener listener) {
        listeners.add(listener);
    }

    private void fireParametersChanged() {
        for (ParameterChangeListener listener: new ArrayList<ParameterChangeListener>(listeners)) {
            listener.parametersChanged();
        }
    }
}
