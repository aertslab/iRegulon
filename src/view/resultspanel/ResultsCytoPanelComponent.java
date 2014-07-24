package view.resultspanel;

import domainmodel.*;
import infrastructure.CytoscapeEnvironment;
import infrastructure.IRegulonResourceBundle;
import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import view.Refreshable;
import view.actions.*;
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
import java.util.Properties;


public final class ResultsCytoPanelComponent extends JPanel implements CytoPanelComponent, Refreshable {
    private final String runName;
    private final Results results;

    private boolean isSaved;

    private SelectedMotifOrTrack selectedMotifOrTrack;
    private JComboBox filterAttributeCB;
    private JTextField filterValueTF;
    private TranscriptionFactorComboBox transcriptionFactorCB;

    private JButton closeButton;
    private JTabbedPane tabbedPane;
    private MotifAndTrackView currentTabView = null;
    private MotifAndTrackView previousTabView = null;

    private final DefaultMetaTargetomeParameters parameters;

    private final NetworkViewAddedListener addedRefreshListener;
    private final NetworkViewDestroyedListener destroyedRefreshListener;
    private final SetCurrentNetworkViewListener changedRefreshListener;

    public ResultsCytoPanelComponent(final String runName, final Results results) {
        this.runName = runName;
        this.results = results;
        this.isSaved = false;

        this.parameters = new DefaultMetaTargetomeParameters(QueryMetatargetomeAction.DEFAULT_PARAMETERS);
        this.selectedMotifOrTrack = new SelectedMotifOrTrack(results.getParameters().getAttributeName());

        initPanel();

        addedRefreshListener = new NetworkViewAddedListener() {
            @Override
            public void handleEvent(NetworkViewAddedEvent networkViewAddedEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        };
        destroyedRefreshListener = new NetworkViewDestroyedListener() {
            @Override
            public void handleEvent(NetworkViewDestroyedEvent networkViewDestroyedEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        };
        changedRefreshListener = new SetCurrentNetworkViewListener() {
            @Override
            public void handleEvent(SetCurrentNetworkViewEvent networkViewChangedEvent) {
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


    public String getRunName() {
        return this.runName;
    }

    public Results getResults() {
        return results;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.EAST;
    }

    @Override
    public String getTitle() {
        return "<html>" + IRegulonResourceBundle.PLUGIN_VISUAL_NAME + " " + getRunName() + "</html>";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    private void registerRefreshListeners() {
        final CyServiceRegistrar serviceRegistrar = CytoscapeEnvironment.getInstance().getServiceRegistrar();
        serviceRegistrar.registerService(addedRefreshListener, NetworkViewAddedListener.class, new Properties());
        serviceRegistrar.registerService(destroyedRefreshListener, NetworkViewDestroyedListener.class, new Properties());
        serviceRegistrar.registerService(changedRefreshListener, SetCurrentNetworkViewListener.class, new Properties());
    }

    public void unregisterRefreshListeners() {
        final CyServiceRegistrar serviceRegistrar = CytoscapeEnvironment.getInstance().getServiceRegistrar();
        serviceRegistrar.unregisterService(addedRefreshListener, NetworkViewAddedListener.class);
        serviceRegistrar.unregisterService(destroyedRefreshListener, NetworkViewDestroyedListener.class);
        serviceRegistrar.unregisterService(changedRefreshListener, SetCurrentNetworkViewListener.class);
    }

    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        return selectedMotifOrTrack.getMotifOrTrack();
    }

    public GeneIdentifier getTranscriptionFactor() {
        final TranscriptionFactor factor = (TranscriptionFactor) transcriptionFactorCB.getSelectedItem();
        return factor == null ? null : factor.getGeneID();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved() {
        isSaved = true;
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        final MotifAndTrackView view = (MotifAndTrackView) tabbedPane.getSelectedComponent();
        if (view != null) view.refresh();
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
        closeButton.setAction(new CloseResultsViewAction(this));
        closeButton.setText("");
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
        final Action addRegulatoryNetworkAction = AddRegulatoryNetworkAction.createAddRegulatoryNetworkAction(
                results.getParameters().getAttributeName(),
                selectedMotifOrTrack, transcriptionFactorComboBox, this);
        final JButton buttonDrawEdges = new JButton(addRegulatoryNetworkAction);
        buttonDrawEdges.setText("");
        toolBar.add(buttonDrawEdges, c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        final Action createNewRegulatoryNetworkAction = AddRegulatoryNetworkAction.createCreateNewRegulatoryNetworkAction(
                results.getParameters().getAttributeName(),
                selectedMotifOrTrack, transcriptionFactorComboBox, this);
        JButton buttonDrawNetwork = new JButton(createNewRegulatoryNetworkAction);
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
        //final Action createNewCombinedRegulatoryNetworkAction = new CreateNewCombinedRegulatoryNetworkAction(results.getParameters().getAttributeName(), this);
        //JButton drawMergedButton = new JButton(createNewCombinedRegulatoryNetworkAction);
        // Hide merge button until we have implemented the merging of networks.
        JButton drawMergedButton = new JButton();
        drawMergedButton.setText("");
        drawMergedButton.setVisible(false);
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
        final String resourceName = IRegulonResourceBundle.getBundle().getString(keyName);
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
