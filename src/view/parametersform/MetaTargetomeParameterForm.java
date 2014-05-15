package view.parametersform;


import domainmodel.GeneIdentifier;
import domainmodel.MetaTargetomeParameters;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import infrastructure.CytoscapeNetworkUtilities;
import view.Refreshable;
import view.actions.QueryMetatargetomeAction;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;


public final class MetaTargetomeParameterForm extends JPanel implements MetaTargetomeParameters, Refreshable {
    private static final int MARGIN_IN_PIXELS = 5;

    private final Map<SpeciesNomenclature,Set<GeneIdentifier>> nomenclature2factors;

    private JComboBox transcriptionFactorCB;
    private JComboBox speciesNomenclatureCB;
    private JList targetomeDatabaseList;
    private JComboBox attributeNameCB;
    private JTextField occurrenceCountLimitTF;
    private JTextField maxNodeCountTF;
    private JCheckBox createNewNetworkCB;

    private final List<ParameterChangeListener> listeners = new ArrayList<ParameterChangeListener>();

    private final ActionListener actionListener;
    private final ItemListener refreshListener;
    private final ItemListener itemListener;
    private final ListSelectionListener selectionListener;
    private final DocumentListener documentListener;


    public MetaTargetomeParameterForm(final MetaTargetomeParameters parameters,
                                      final Map<SpeciesNomenclature, Set<GeneIdentifier>> nomenclature2factors) {
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
        documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireParametersChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireParametersChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireParametersChanged();
            }
        };
        registerListeners();

        if (parameters != null) initParameters(parameters);

        refresh();
    }

    private void initParameters(final MetaTargetomeParameters parameters) {
        setMaxNumberOfNodes(parameters.getMaxNumberOfNodes());
        setOccurrenceCountThreshold(parameters.getOccurrenceCountThreshold());
        final GeneIdentifier factor = parameters.getTranscriptionFactor();
        setSpeciesNomenclature(factor == null ? null : parameters.getTranscriptionFactor().getSpeciesNomenclature());
        setTranscriptionFactor(factor);
        setTargetomeDatabases(parameters.getTargetomeDatabases());
        setAttributeName(parameters.getAttributeName());
        setCreateNewNetwork(parameters.createNewNetwork());
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
        unregisterListeners();
        speciesNomenclatureCB.setSelectedItem(speciesNomenclature == null ? SpeciesNomenclature.HOMO_SAPIENS_HGNC : speciesNomenclature);
        refresh();
        registerListeners();
        fireParametersChanged();
    }

    public void setTargetomeDatabases(List<TargetomeDatabase> targetomeDatabases) {
        targetomeDatabaseList.clearSelection();
        for (TargetomeDatabase targetomeDatabase: targetomeDatabases) {
            final int idx = findTargetomeDatabase(targetomeDatabase);
            if (idx >= 0) {
                targetomeDatabaseList.getSelectionModel().addSelectionInterval(idx, idx);
            }
        }
    }

    private int findTargetomeDatabase(TargetomeDatabase targetomeDatabase) {
        for (int idx = 0; idx < targetomeDatabaseList.getModel().getSize(); idx++) {
             if (targetomeDatabaseList.getModel().getElementAt(idx).equals(targetomeDatabase)) {
                 return idx;
             }
        }
        return -1;
    }

    @Override
    public List<TargetomeDatabase> getTargetomeDatabases() {
        final List<TargetomeDatabase> result = new ArrayList<TargetomeDatabase>();
        for (Object targetomeDatabase : targetomeDatabaseList.getSelectedValues()) {
            result.add((TargetomeDatabase) targetomeDatabase);
        }
        return result;
    }

    @Override
    public String getAttributeName() {
        final String attributeName = (String) attributeNameCB.getSelectedItem();
        return attributeName == null ? CytoscapeNetworkUtilities.ID_ATTRIBUTE_NAME : attributeName;
    }

    public void setAttributeName(final String attributeName) {
        attributeNameCB.setSelectedItem(attributeName);
    }

    public void setOccurrenceCountThreshold(final int limit) {
        occurrenceCountLimitTF.setText(Integer.toString(limit));
    }

    @Override
    public int getOccurrenceCountThreshold() {
        try {
            return Integer.parseInt(occurrenceCountLimitTF.getText());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int getMaxNumberOfNodes() {
        try {
            return Integer.parseInt(maxNodeCountTF.getText());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxNumberOfNodes(int nodes) {
        maxNodeCountTF.setText(Integer.toString(nodes));
    }

    @Override
    public boolean createNewNetwork() {
        return createNewNetworkCB.isSelected();
    }

    public void setCreateNewNetwork(boolean isSelected) {
        createNewNetworkCB.setSelected(isSelected);
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

        final JLabel speciesNomenclatureLB = new JLabel("Species and Gene nomenclature:");
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

        cc.gridx = 0; cc.gridy = 2;
        cc.gridwidth = 2;
        cc.fill=GridBagConstraints.HORIZONTAL;
        final GridBagConstraints DBcc = new GridBagConstraints();
        final JPanel DBpanel = new JPanel(new GridBagLayout());
        final TitledBorder DBborder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Database");
        DBborder.setTitleJustification(TitledBorder.LEFT);
        DBborder.setTitlePosition(TitledBorder.CENTER);
        DBpanel.setBorder(DBborder);

        final JLabel targetomeDatabasesLB = new JLabel("Databases:");
        targetomeDatabaseList = new JList(new TargetomeDatabaseListModel(TargetomeDatabase.getAllTargetomeDatabases()));
        targetomeDatabasesLB.setLabelFor(targetomeDatabaseList);
        targetomeDatabaseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        DBcc.gridx = 0; DBcc.gridy = 0;
        DBcc.gridwidth = 1; DBcc.gridheight = 1;
        DBcc.weightx = 0.0; DBcc.weighty = 0.0;
        DBcc.fill = GridBagConstraints.NONE;
        DBcc.anchor = GridBagConstraints.LINE_START;
        DBcc.insets = new Insets(0, MARGIN_IN_PIXELS, 0, 0);
        DBpanel.add(targetomeDatabasesLB, DBcc);

        DBcc.gridx++; DBcc.gridy = 0;
        DBcc.gridwidth = 1; DBcc.gridheight = 1;
        DBcc.weightx = 1.0; DBcc.weighty = 1.0;
        DBcc.fill = GridBagConstraints.BOTH;
        DBcc.insets = new Insets(0, 0, 0, MARGIN_IN_PIXELS);
        JScrollPane DBscroll = new JScrollPane(targetomeDatabaseList);
        DBscroll.setPreferredSize(new Dimension(DBscroll.getWidth() , 70));
        DBpanel.add(DBscroll, DBcc);

        final JLabel occurrenceCountLimitLB = new JLabel("Occurrence count threshold:");
        occurrenceCountLimitTF = new JTextField(Integer.toString(QueryMetatargetomeAction.DEFAULT_THRESHOLD));

        DBcc.gridx = 0; DBcc.gridy = 1;
        DBcc.gridwidth = 1; DBcc.gridheight = 1;
        DBcc.weightx = 0.0; DBcc.weighty = 0.0;
        DBcc.fill = GridBagConstraints.NONE;
        DBcc.anchor = GridBagConstraints.LINE_START;
        DBcc.insets = new Insets(0, MARGIN_IN_PIXELS, 0, 0);
        DBpanel.add(occurrenceCountLimitLB, DBcc);

        DBcc.gridx++; DBcc.gridy = 1;
        DBcc.gridwidth = 1; DBcc.gridheight = 1;
        DBcc.weightx = 1.0; DBcc.weighty = 0.0;
        DBcc.fill = GridBagConstraints.HORIZONTAL;
        DBcc.insets = new Insets(0, 0, 0, MARGIN_IN_PIXELS);
        DBpanel.add(occurrenceCountLimitTF, DBcc);

        add(DBpanel, cc);

        cc.gridx = 0; cc.gridy = 3;
        cc.gridwidth = 2;
        cc.fill=GridBagConstraints.HORIZONTAL;
        final GridBagConstraints NWcc = new GridBagConstraints();
        final JPanel NWpanel = new JPanel(new GridBagLayout());
        final TitledBorder NWborder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Network");
        NWborder.setTitleJustification(TitledBorder.LEFT);
        NWborder.setTitlePosition(TitledBorder.CENTER);
        NWpanel.setBorder(NWborder);

        final JLabel maxNodeCountLB = new JLabel("Number nodes (approx.):");
        maxNodeCountTF = new JTextField(Integer.toString(QueryMetatargetomeAction.DEFAULT_MAX_NODE_COUNT));

        NWcc.gridx = 0; NWcc.gridy = 0;
        NWcc.gridwidth = 1; NWcc.gridheight = 1;
        NWcc.weightx = 0.0; NWcc.weighty = 0.0;
        NWcc.fill = GridBagConstraints.NONE;
        NWcc.anchor = GridBagConstraints.LINE_START;
        NWcc.insets = new Insets(0, MARGIN_IN_PIXELS, 0, 0);
        NWpanel.add(maxNodeCountLB, NWcc);

        NWcc.gridx++; NWcc.gridy = 0;
        NWcc.gridwidth = 1; NWcc.gridheight = 1;
        NWcc.weightx = 1.0; NWcc.weighty = 0.0;
        NWcc.fill = GridBagConstraints.HORIZONTAL;
        NWcc.insets = new Insets(0, 0, 0, MARGIN_IN_PIXELS);
        NWpanel.add(maxNodeCountTF, NWcc);

        createNewNetworkCB = new JCheckBox("Create new network", true);

        NWcc.gridx = 0; NWcc.gridy = 1;
        NWcc.gridwidth = 2; NWcc.gridheight = 1;
        NWcc.weightx = 0.0; NWcc.weighty = 0.0;
        NWcc.fill = GridBagConstraints.NONE;
        NWcc.anchor = GridBagConstraints.LINE_START;
        NWcc.insets = new Insets(0, MARGIN_IN_PIXELS, 0, MARGIN_IN_PIXELS);
        NWpanel.add(createNewNetworkCB, NWcc);

        final JLabel attributeNameLB = new JLabel("Attribute name:");
        attributeNameCB = new JComboBox();

        NWcc.gridx = 0; NWcc.gridy = 2;
        NWcc.gridwidth = 1; NWcc.gridheight = 1;
        NWcc.weightx = 0.0; NWcc.weighty = 0.0;
        NWcc.fill = GridBagConstraints.NONE;
        NWcc.anchor = GridBagConstraints.LINE_START;
        NWcc.insets = new Insets(0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS, 0);
        NWpanel.add(attributeNameLB, NWcc);

        NWcc.gridx++; NWcc.gridy = 2;
        NWcc.gridwidth = 1; NWcc.gridheight = 1;
        NWcc.weightx = 1.0; NWcc.weighty = 0.0;
        NWcc.fill = GridBagConstraints.HORIZONTAL;
        NWcc.insets = new Insets(0, 0, MARGIN_IN_PIXELS, MARGIN_IN_PIXELS);
        NWpanel.add(attributeNameCB, NWcc);

        add(NWpanel, cc);
    }

    private void registerListeners() {
        speciesNomenclatureCB.addActionListener(actionListener);
        attributeNameCB.addActionListener(actionListener);
        targetomeDatabaseList.getSelectionModel().addListSelectionListener(selectionListener);
        transcriptionFactorCB.addActionListener(actionListener);
        transcriptionFactorCB.addItemListener(itemListener);
        occurrenceCountLimitTF.getDocument().addDocumentListener(documentListener);
        maxNodeCountTF.getDocument().addDocumentListener(documentListener);
        speciesNomenclatureCB.addItemListener(refreshListener);
    }

    private void unregisterListeners() {
        speciesNomenclatureCB.removeActionListener(actionListener);
        attributeNameCB.removeActionListener(actionListener);
        targetomeDatabaseList.getSelectionModel().removeListSelectionListener(selectionListener);
        transcriptionFactorCB.removeActionListener(actionListener);
        transcriptionFactorCB.removeItemListener(itemListener);
        occurrenceCountLimitTF.getDocument().removeDocumentListener(documentListener);
        maxNodeCountTF.getDocument().removeDocumentListener(documentListener);
        speciesNomenclatureCB.removeItemListener(refreshListener);
    }

    @Override
    public void refresh() {
        unregisterListeners();

        final GeneIdentifier curID = getTranscriptionFactor();

        final Set<GeneIdentifier> IDs = nomenclature2factors.containsKey(getSpeciesNomenclature())
                ? nomenclature2factors.get(getSpeciesNomenclature())
                : Collections.<GeneIdentifier>emptySet();
        transcriptionFactorCB.setModel(new GeneIdentifierComboBoxModel(IDs));
        transcriptionFactorCB.setEnabled(transcriptionFactorCB.getModel().getSize() != 0);

        if (IDs.contains(curID)) setTranscriptionFactor(curID);
        else if (!IDs.isEmpty()) setTranscriptionFactor(IDs.iterator().next());

        attributeNameCB.setModel(new DefaultComboBoxModel(CytoscapeNetworkUtilities.getPossibleGeneIDAttributesWithDefault().toArray()));

        registerListeners();
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
