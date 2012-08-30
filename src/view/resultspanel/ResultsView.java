package view.resultspanel;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import domainmodel.*;
import view.IRegulonResourceBundle;
import view.actions.QueryMetatargetomeAction;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.guiwidgets.SummaryLabel;
import view.resultspanel.motifview.EnrichedMotifsView;
import view.resultspanel.actions.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import view.resultspanel.actions.CreateNewRegulatoryNetworkAction;
import view.resultspanel.actions.AddRegulatoryNetworkAction;

import view.resultspanel.motifclusterview.MotifClustersView;

import cytoscape.view.cytopanels.CytoPanel;


public class ResultsView extends IRegulonResourceBundle implements Refreshable {
    private final String runName;
    private final Results results;

    private boolean isSaved;

    private SelectedMotif selectedMotif;
    private JComboBox filterAttributeCB;
    private JTextField filterValueTF;
    private TranscriptionFactorComboBox transcriptionFactorCB;

    private JButton closeButton;
	private JPanel mainPanel = null;
    private JTabbedPane tabbedPane;

    private final PropertyChangeListener refreshListener;
    private CreateNewCombinedRegulatoryNetworkAction createNewCombinedRegulatoryNetworkAction;
    private AddRegulatoryNetworkAction drawNodesAndEdgesAction;

    public ResultsView(final String runName, final Results results) {
		this.runName = runName;
        this.results = results;
		this.isSaved = false;

        this.selectedMotif = new SelectedMotif(results.getParameters().getAttributeName());

        refreshListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        };
        registerRefreshListeners();
	}

    private void registerRefreshListeners() {
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS, refreshListener);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, refreshListener);
    }

    public void unregisterRefreshListeners() {
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS, refreshListener);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, refreshListener);
    }


    public String getRunName() {
        return this.runName;
    }

    public String getPanelName() {
        return getBundle().getString("plugin_visual_name") + " " + getRunName();
    }

    public Results getResults() {
        return results;
    }

    public AbstractMotif getSelectedMotif() {
        return selectedMotif.getMotif();
    }

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        return (TranscriptionFactor) transcriptionFactorCB.getSelectedItem();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved() {
        isSaved = true;
    }

    public void addToPanel(final CytoPanel panel) {
        if (this.mainPanel != null) {
            throw new IllegalStateException();
        }
		final JPanel resultsView = createMainPanel();
        this.mainPanel = resultsView;

        panel.add(getPanelName(), resultsView);

		// Make this new panel active ...
		final int index = panel.indexOfComponent(resultsView);
		panel.setSelectedIndex(index);

        // Add listener for closing this results view ...
        getCloseButton().setAction(new CloseResultsViewAction(panel, this));
        getCloseButton().setText("");
    }

    @Override
    public void refresh() {
        createNewCombinedRegulatoryNetworkAction.refresh();
        drawNodesAndEdgesAction.refresh();

        final MotifView view = (MotifView) tabbedPane.getSelectedComponent();
        if (view != null) view.refresh();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JButton getCloseButton() {
        return closeButton;
    }
	
	private JPanel createMainPanel() {
        // 1. Create toolbar ...
        this.transcriptionFactorCB = new TranscriptionFactorComboBox(this.selectedMotif, this.results.getSpeciesNomenclature());
        this.filterAttributeCB = new JComboBox(FilterAttribute.values());
        this.filterAttributeCB.setSelectedItem(FilterAttribute.MOTIF);
        this.filterValueTF = new JTextField();
        this.closeButton = new JButton();
        final JPanel toolBar = createToolBar(this.selectedMotif, this.transcriptionFactorCB, this.closeButton, this.filterAttributeCB, this.filterValueTF);

        // 2. Create enriched motifs view ...
        final EnrichedMotifsView motifsView = new EnrichedMotifsView(this.results);
        // 3. Create enriched TF view ...
        final MotifClustersView tfsView = new MotifClustersView(this.results);

        // 4. Create tabbed pane with these views ...
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Transcription Factors", null,
                tfsView,
                "Transcription factor oriented view.");
		tabbedPane.addTab("Motifs", null,
                motifsView,
                "Motif oriented view.");
        tabbedPane.addChangeListener(new ChangeListener() {
            private MotifView getOtherView(final MotifView view) {
                if (view == motifsView) return tfsView;
                if (view == tfsView) return motifsView;
                return null;
            }


            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                final JTabbedPane pane = (JTabbedPane) changeEvent.getSource();
                final MotifView curView = (MotifView) pane.getSelectedComponent();
                final MotifView prevView = getOtherView(curView);

                // 1. Refresh this view ...
                if (curView != null) curView.refresh();

                // 2. Refresh motif selection ...
                motifsView.unregisterSelectionComponents(selectedMotif, transcriptionFactorCB);
                tfsView.unregisterSelectionComponents(selectedMotif, transcriptionFactorCB);
                if (curView != null) curView.registerSelectionComponents(selectedMotif, transcriptionFactorCB);

                // 3. Refresh table row filter ...
                motifsView.unregisterFilterComponents(filterAttributeCB, filterValueTF);
                tfsView.unregisterFilterComponents(filterAttributeCB, filterValueTF);
                if (curView != null) curView.registerFilterComponents(filterAttributeCB, filterValueTF);

                // 4. Take over selection of previous view if possible ...
                if (curView != null) curView.setSelectedMotif(prevView != null ? prevView.getSelectedMotif() : null);
            }
        });
        final MotifView view = (MotifView) tabbedPane.getSelectedComponent();
        if (view != null) {
            view.registerSelectionComponents(selectedMotif, transcriptionFactorCB);
            view.registerFilterComponents(filterAttributeCB, filterValueTF);
        }

		final JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
        result.add(toolBar, BorderLayout.NORTH);
        result.add(tabbedPane, BorderLayout.CENTER);
        return result;
	}

    private JPanel createToolBar(final SelectedMotif selectedMotif, final TranscriptionFactorComboBox transcriptionFactorComboBox,
                                 final JButton closeButton, final JComboBox filterAttributeCB, final JTextField filterValueTF) {
        final JPanel toolBar = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();

        // First line ...
        c.gridx = 0; c.gridy = 0;
		c.weightx = 1.0; c.weighty = 0;
		c.gridwidth = 7; c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        toolBar.add(new SummaryLabel(getResults()), c);

        c.gridx = 7; c.gridy = 0;
        c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
		toolBar.add(closeButton, c);

        // Second line ...
        c.gridx = 0; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.gridwidth = 1; c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
		drawNodesAndEdgesAction = new AddRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, this, results.getParameters().getAttributeName());
        final JButton buttonDrawEdges = new JButton(drawNodesAndEdgesAction);
        buttonDrawEdges.setText("");
        toolBar.add(buttonDrawEdges, c);

        c.gridx = 1; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final TranscriptionFactorDependentAction drawNetworkAction = new CreateNewRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, this, results.getParameters().getAttributeName());
        JButton buttonDrawNetwork = new JButton(drawNetworkAction);
        buttonDrawNetwork.setText("");
        toolBar.add(buttonDrawNetwork, c);

        c.gridx = 2; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final QueryMetatargetomeAction queryMetatargetomeAction = new QueryMetatargetomeAction(this);
        transcriptionFactorComboBox.addActionListener(new QueryMetatargetomeActionListener(queryMetatargetomeAction, transcriptionFactorComboBox, results.getParameters().getAttributeName()));
        final JTextComponent textComponent = (JTextComponent) transcriptionFactorComboBox.getEditor().getEditorComponent();
        textComponent.getDocument().addDocumentListener(new QueryMetatargetomeDocumentListener(queryMetatargetomeAction, transcriptionFactorComboBox, results.getParameters().getAttributeName()));
        JButton buttonQueryMetatargetome = new JButton(queryMetatargetomeAction);
        buttonQueryMetatargetome.setText("");
        toolBar.add(buttonQueryMetatargetome, c);

        c.gridx = 3; c.gridy = 1;
        c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
		final JLabel labelTF = new JLabel("Transcription Factor");
		toolBar.add(labelTF, c);

        c.gridx = 4; c.gridy = 1;
		c.weightx = 1.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
		transcriptionFactorComboBox.setEditable(true);
        toolBar.add(transcriptionFactorComboBox, c);

        c.gridx = 5; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        createNewCombinedRegulatoryNetworkAction = new CreateNewCombinedRegulatoryNetworkAction(results.getParameters().getAttributeName(), this);
		JButton drawMergedButton = new JButton(createNewCombinedRegulatoryNetworkAction);
        drawMergedButton.setText("");
        toolBar.add(drawMergedButton, c);

        c.gridx = 6; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final JButton buttonSave = new JButton(new SaveResultsAction(this));
        buttonSave.setText("");
        toolBar.add(buttonSave, c);

        c.gridx = 7; c.gridy = 1;
		c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
		final JButton buttonTabDelimited = new JButton(new ExportResultsAction(this));
        buttonTabDelimited.setText("");
		toolBar.add(buttonTabDelimited, c);

        // Third line ...
		c.gridx = 0; c.gridy = 2;
        c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        toolBar.add(new JLabel(loadIcon("filter_icon")), c);

		c.gridx = 1; c.gridy = 2;
		c.gridwidth = 3;
		c.weightx = 1.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
		toolBar.add(filterAttributeCB, c);

		c.gridx = 4; c.gridy = 2;
		c.gridwidth = 4;
		c.weightx = 1.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
		toolBar.add(filterValueTF, c);

        return toolBar;
    }

    private ImageIcon loadIcon(final String keyName) {
        final String resourceName = getBundle().getString(keyName);
		return new ImageIcon(getClass().getResource(resourceName));
    }

    private static class QueryMetatargetomeActionListener implements ActionListener {
        private final QueryMetatargetomeAction queryMetatargetomeAction;
        private final TranscriptionFactorComboBox transcriptionFactorComboBox;
        private final String attributeName;

        public QueryMetatargetomeActionListener(QueryMetatargetomeAction queryMetatargetomeAction, TranscriptionFactorComboBox transcriptionFactorComboBox, final String attributeName) {
            this.queryMetatargetomeAction = queryMetatargetomeAction;
            this.transcriptionFactorComboBox = transcriptionFactorComboBox;
            this.attributeName = attributeName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            queryMetatargetomeAction.setParameters(new MetatargetomeParameters() {
                @Override
                public GeneIdentifier getTranscriptionFactor() {
                    final GeneIdentifier factor = transcriptionFactorComboBox.getTranscriptionFactor();
                    return (factor == null) ? null : factor;
                }

                @Override
                public List<TargetomeDatabase> getDatabases() {
                    return TargetomeDatabase.getAllDatabases();
                }

                @Override
                public String getAttributeName() {
                    return attributeName;
                }
            });
        }
    }

    private static class QueryMetatargetomeDocumentListener implements DocumentListener {
        private final QueryMetatargetomeAction queryMetatargetomeAction;
        private final TranscriptionFactorComboBox transcriptionFactorComboBox;
        private final String attributeName;

        public QueryMetatargetomeDocumentListener(QueryMetatargetomeAction queryMetatargetomeAction, TranscriptionFactorComboBox transcriptionFactorComboBox, final String attributeName) {
            this.queryMetatargetomeAction = queryMetatargetomeAction;
            this.transcriptionFactorComboBox = transcriptionFactorComboBox;
            this.attributeName = attributeName;
        }

        private void refresh() {
            queryMetatargetomeAction.setParameters(new MetatargetomeParameters() {
                @Override
                public GeneIdentifier getTranscriptionFactor() {
                    final GeneIdentifier factor = transcriptionFactorComboBox.getTranscriptionFactor();
                    return (factor == null) ? null : factor;
                }

                @Override
                public List<TargetomeDatabase> getDatabases() {
                    return TargetomeDatabase.getAllDatabases();
                }

                @Override
                public String getAttributeName() {
                    return attributeName;
                }
            });
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            refresh();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            refresh();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            refresh();
        }
    }
}
