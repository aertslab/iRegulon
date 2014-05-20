package view.resultspanel;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import view.Refreshable;
import view.actions.LoadResultsAction;
import view.actions.OpenQueryMetatargetomeFormAction;
import view.actions.QueryMetatargetomeAction;
import view.resultspanel.actions.*;
import view.resultspanel.guiwidgets.SummaryLabel;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackclusterview.MotifAndTrackClustersView;
import view.resultspanel.motifview.EnrichedMotifsView;
import view.resultspanel.trackview.EnrichedTracksView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class ResultsView extends IRegulonResourceBundle implements Refreshable {
    private final String runName;
    private final Results results;

    private boolean isSaved;

    private SelectedMotifOrTrack selectedMotifOrTrack;
    private JComboBox filterAttributeCB;
    private JTextField filterValueTF;
    private TranscriptionFactorComboBox transcriptionFactorCB;

    private JButton closeButton;
    private JPanel mainPanel = null;
    private JTabbedPane tabbedPane;
    private MotifAndTrackView currentTabView = null;
    private MotifAndTrackView previousTabView = null;

    private final DefaultMetaTargetomeParameters parameters;

    private final PropertyChangeListener refreshListener;
    private CreateNewCombinedRegulatoryNetworkAction createNewCombinedRegulatoryNetworkAction;
    private AddRegulatoryNetworkAction drawNodesAndEdgesAction;

    public ResultsView(final String runName, final Results results) {
        this.runName = runName;
        this.results = results;
        this.isSaved = false;

        this.parameters = new DefaultMetaTargetomeParameters(QueryMetatargetomeAction.DEFAULT_PARAMETERS);
        this.selectedMotifOrTrack = new SelectedMotifOrTrack(results.getParameters().getAttributeName());

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
        return "<html>" + PLUGIN_VISUAL_NAME + " " + getRunName() + "</html>";
    }

    public Results getResults() {
        return results;
    }

    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        return selectedMotifOrTrack.getMotifOrTrack();
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

        final MotifAndTrackView view = (MotifAndTrackView) tabbedPane.getSelectedComponent();
        if (view != null) view.refresh();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JButton getCloseButton() {
        return closeButton;
    }

    private JPanel createMainPanel() {

        // 1. Create toolbar.
        this.transcriptionFactorCB = new TranscriptionFactorComboBox(this.selectedMotifOrTrack, this.results.getSpeciesNomenclature());
        this.filterAttributeCB = new JComboBox(FilterAttribute.values());
        this.filterAttributeCB.setSelectedItem(FilterAttribute.MOTIF_OR_TRACK);
        this.filterValueTF = new JTextField();
        this.closeButton = new JButton();
        final JPanel toolBar = createToolBar(this.selectedMotifOrTrack, this.transcriptionFactorCB, this.closeButton, this.filterAttributeCB, this.filterValueTF);

        final boolean hasMotifCollection = results.hasMotifCollection();
        final boolean hasTrackCollection = results.hasTrackCollection();

        // 2. Create enriched motifs view if there was a motif collection specified.
        final EnrichedMotifsView motifsView = (hasMotifCollection) ? new EnrichedMotifsView(this.results) : null;

        // 3. Create enriched tracks view if there was a track collection specified.
        final EnrichedTracksView tracksView = (hasTrackCollection) ? new EnrichedTracksView(this.results) : null;

        // 4. Create enriched TF view.
        final MotifAndTrackClustersView tfsView = new MotifAndTrackClustersView(this.results);

        // 5. Create tabbed pane with these views.
        tabbedPane = new JTabbedPane();

        if (hasMotifCollection) {
            tabbedPane.addTab("Motifs", null, motifsView, "Motif oriented view.");
        }

        if (hasTrackCollection) {
            tabbedPane.addTab("Tracks", null, tracksView, "Track oriented view.");
        }

        tabbedPane.addTab("Transcription Factors", null, tfsView, "Transcription factor oriented view.");
        tabbedPane.addChangeListener(new ChangeListener() {
            private MotifAndTrackView getCompatibleTabViewForSelection(final MotifAndTrackView previousTabView, final MotifAndTrackView currentTabView) {
                if (currentTabView == tfsView) {
                    if (hasMotifCollection) if (previousTabView == motifsView) return motifsView;
                    if (hasTrackCollection) if (previousTabView == tracksView) return tracksView;
                } else if (previousTabView == tfsView) {
                    final AbstractMotifAndTrack selectedMotifOrTrack = tfsView.getSelectedMotifOrTrack();
                    if (selectedMotifOrTrack != null) {
                        if (selectedMotifOrTrack.isMotif() && currentTabView == motifsView) return tfsView;
                        if (selectedMotifOrTrack.isTrack() && currentTabView == tracksView) return tfsView;
                    }
                }
                return null;
            }

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                final JTabbedPane pane = (JTabbedPane) changeEvent.getSource();

                previousTabView = currentTabView;
                currentTabView = (MotifAndTrackView) pane.getSelectedComponent();

                final MotifAndTrackView compatibleTabViewForSelection = getCompatibleTabViewForSelection(previousTabView, currentTabView);

                // 1. Refresh this view.
                if (currentTabView != null) currentTabView.refresh();

                // 2. Refresh motif and/or track selection.
                if (hasMotifCollection)
                    motifsView.unregisterSelectionComponents(selectedMotifOrTrack, transcriptionFactorCB);
                if (hasTrackCollection)
                    tracksView.unregisterSelectionComponents(selectedMotifOrTrack, transcriptionFactorCB);
                tfsView.unregisterSelectionComponents(selectedMotifOrTrack, transcriptionFactorCB);

                if (currentTabView != null)
                    currentTabView.registerSelectionComponents(selectedMotifOrTrack, transcriptionFactorCB);

                // 3. Refresh table row filter.
                if (hasMotifCollection) motifsView.unregisterFilterComponents(filterAttributeCB, filterValueTF);
                if (hasTrackCollection) tracksView.unregisterFilterComponents(filterAttributeCB, filterValueTF);
                tfsView.unregisterFilterComponents(filterAttributeCB, filterValueTF);

                if (currentTabView != null) currentTabView.registerFilterComponents(filterAttributeCB, filterValueTF);

                // 4. Take over the selected motif of track of previous view if possible.
                if (currentTabView != null)
                    currentTabView.setSelectedMotifOrTrack(compatibleTabViewForSelection != null ? compatibleTabViewForSelection.getSelectedMotifOrTrack() : null);
            }
        });

        final MotifAndTrackView view = (MotifAndTrackView) tabbedPane.getSelectedComponent();
        if (view != null) {
            view.registerSelectionComponents(selectedMotifOrTrack, transcriptionFactorCB);
            view.registerFilterComponents(filterAttributeCB, filterValueTF);
        }

        final JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(toolBar, BorderLayout.NORTH);
        result.add(tabbedPane, BorderLayout.CENTER);
        return result;
    }

    private JPanel createToolBar(final SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox transcriptionFactorComboBox,
                                 final JButton closeButton, final JComboBox filterAttributeCB, final JTextField filterValueTF) {
        final JPanel toolBar = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();

        // First line ...
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 7;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        toolBar.add(new SummaryLabel(getResults()), c);

        c.gridx = 8;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        toolBar.add(closeButton, c);

        // Second line ...
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        drawNodesAndEdgesAction = new AddRegulatoryNetworkAction(selectedMotifOrTrack, transcriptionFactorComboBox, this, results.getParameters().getAttributeName());
        final JButton buttonDrawEdges = new JButton(drawNodesAndEdgesAction);
        buttonDrawEdges.setText("");
        toolBar.add(buttonDrawEdges, c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final TranscriptionFactorDependentAction drawNetworkAction = new CreateNewRegulatoryNetworkAction(selectedMotifOrTrack, transcriptionFactorComboBox, this, results.getParameters().getAttributeName());
        JButton buttonDrawNetwork = new JButton(drawNetworkAction);
        buttonDrawNetwork.setText("");
        toolBar.add(buttonDrawNetwork, c);

        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final OpenQueryMetatargetomeFormAction queryMetatargetomeAction = new OpenQueryMetatargetomeFormAction(parameters, this);
        parameters.setAttributeName(results.getParameters().getAttributeName());
        transcriptionFactorComboBox.addActionListener(new QueryMetatargetomeActionListener(queryMetatargetomeAction, parameters, transcriptionFactorComboBox));
        final JTextComponent textComponent = (JTextComponent) transcriptionFactorComboBox.getEditor().getEditorComponent();
        textComponent.getDocument().addDocumentListener(new QueryMetatargetomeDocumentListener(queryMetatargetomeAction, parameters, transcriptionFactorComboBox));
        JButton buttonQueryMetatargetome = new JButton(queryMetatargetomeAction);
        buttonQueryMetatargetome.setText("");
        toolBar.add(buttonQueryMetatargetome, c);

        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        createNewCombinedRegulatoryNetworkAction = new CreateNewCombinedRegulatoryNetworkAction(results.getParameters().getAttributeName(), this);
        JButton drawMergedButton = new JButton(createNewCombinedRegulatoryNetworkAction);
        drawMergedButton.setText("");
        toolBar.add(drawMergedButton, c);

        c.gridx = 4;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        final JLabel labelTF = new JLabel("TF");
        labelTF.setToolTipText("Transcription factor");
        toolBar.add(labelTF, c);

        c.gridx = 5;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        transcriptionFactorComboBox.setEditable(true);
        transcriptionFactorComboBox.setToolTipText("List of predicted transcription factors.");
        toolBar.add(transcriptionFactorComboBox, c);

        c.gridx = 6;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final JButton buttonLoad = new JButton(new LoadResultsAction());
        buttonLoad.setText("");
        toolBar.add(buttonLoad, c);

        c.gridx = 7;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final JButton buttonSave = new JButton(new SaveResultsAction(this));
        buttonSave.setText("");
        toolBar.add(buttonSave, c);

        c.gridx = 8;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final JButton buttonTabDelimited = new JButton(new ExportResultsAction(this));
        buttonTabDelimited.setText("");
        toolBar.add(buttonTabDelimited, c);

        // Third line ...
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        toolBar.add(new JLabel(loadIcon("filter_icon")), c);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        filterAttributeCB.setToolTipText("Select feature to filter on.");
        toolBar.add(filterAttributeCB, c);

        c.gridx = 4;
        c.gridy = 2;
        c.gridwidth = 5;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        filterValueTF.setToolTipText("Filter the main view table on motif, transcription factor or target gene.");
        toolBar.add(filterValueTF, c);

        return toolBar;
    }

    private ImageIcon loadIcon(final String keyName) {
        final String resourceName = RESOURCE_BUNDLE.getString(keyName);
        return new ImageIcon(getClass().getResource(resourceName));
    }

    private static class QueryMetatargetomeActionListener implements ActionListener {
        private final OpenQueryMetatargetomeFormAction queryMetatargetomeAction;
        private final DefaultMetaTargetomeParameters parameters;
        private final TranscriptionFactorComboBox transcriptionFactorComboBox;

        public QueryMetatargetomeActionListener(final OpenQueryMetatargetomeFormAction queryMetatargetomeAction,
                                                final DefaultMetaTargetomeParameters parameters, TranscriptionFactorComboBox transcriptionFactorComboBox) {
            this.queryMetatargetomeAction = queryMetatargetomeAction;
            this.parameters = parameters;
            this.transcriptionFactorComboBox = transcriptionFactorComboBox;
        }

        public GeneIdentifier getTranscriptionFactor() {
            final GeneIdentifier factor = transcriptionFactorComboBox.getTranscriptionFactor();
            return (factor == null) ? null : factor;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            parameters.setTranscriptionFactor(getTranscriptionFactor());
            queryMetatargetomeAction.setParameters(parameters);
        }
    }

    private static class QueryMetatargetomeDocumentListener implements DocumentListener {
        private final OpenQueryMetatargetomeFormAction queryMetatargetomeAction;
        private final DefaultMetaTargetomeParameters parameters;
        private final TranscriptionFactorComboBox transcriptionFactorComboBox;

        public QueryMetatargetomeDocumentListener(final OpenQueryMetatargetomeFormAction queryMetatargetomeAction,
                                                  final DefaultMetaTargetomeParameters parameters,
                                                  TranscriptionFactorComboBox transcriptionFactorComboBox) {
            this.queryMetatargetomeAction = queryMetatargetomeAction;
            this.parameters = parameters;
            this.transcriptionFactorComboBox = transcriptionFactorComboBox;
        }

        public GeneIdentifier getTranscriptionFactor() {
            final GeneIdentifier factor = transcriptionFactorComboBox.getTranscriptionFactor();
            return (factor == null) ? null : factor;
        }

        private void refresh() {
            parameters.setTranscriptionFactor(getTranscriptionFactor());
            queryMetatargetomeAction.setParameters(parameters);
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
