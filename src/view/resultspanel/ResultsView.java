package view.resultspanel;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;
import view.IRegulonResourceBundle;
import view.resultspanel.guiwidgets.SummaryLabel;
import view.resultspanel.motifview.EnrichedMotifsView;
import view.resultspanel.actions.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.resultspanel.actions.CreateNewRegulatoryNetworkAction;
import view.resultspanel.actions.AddRegulatoryNetworkAction;

import view.resultspanel.motifclusterview.MotifClustersView;

import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.Results;


public class ResultsView extends IRegulonResourceBundle implements Refreshable {
    private final String runName;
    private final Results results;

    private boolean isSaved;

    private SelectedMotif selectedMotif;
    private JComboBox filterAttributeCB;
    private JTextField filterValueTF;
    private TFComboBox transcriptionFactorCB;

    private JButton closeButton;
	private JPanel mainPanel = null;
    private JTabbedPane tabbedPane;

    private final PropertyChangeListener refreshListener;


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
    }

    public void unregisterRefreshListeners() {
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS, refreshListener);
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
        this.transcriptionFactorCB = new TFComboBox(this.selectedMotif);
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

    private JPanel createToolBar(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorComboBox,
                                 final JButton closeButton, final JComboBox filterAttributeCB, final JTextField filterValueTF) {
        final JPanel toolBar = new JPanel();
		toolBar.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 0; c.ipady = 0;

		c.gridx = 0; c.gridy = 0;
		c.weightx = 0; c.weighty = 0;
		c.gridwidth = 4; c.gridheight = 1;
        toolBar.add(new SummaryLabel(getResults()), c);

		c.gridx = 0; c.gridy = 1;
		c.weightx=0.1; c.weighty = 0.0;
        c.gridwidth = 1; c.gridheight = 1;
        final TranscriptionFactorDependentAction drawNodesAndEdgesAction = new AddRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, this);
        final JButton buttonDrawEdges = new JButton(drawNodesAndEdgesAction);
        buttonDrawEdges.setText("+");
        toolBar.add(buttonDrawEdges, c);

        c.gridx = 1; c.gridy = 1;
		c.weightx = 0.1; c.weighty = 0.0;
        final TranscriptionFactorDependentAction drawNetworkAction = new CreateNewRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, this);
        JButton buttonDrawNetwork = new JButton(drawNetworkAction);
        buttonDrawNetwork.setText("N");

        //TODO: add combine action ...

        //TODO: add query metatargetome action ...

		toolBar.add(buttonDrawNetwork, c);

        c.gridx = 2; c.gridy = 1;
        c.weightx = 0.1; c.weighty = 0.0;
		final JLabel labelTF = new JLabel("Transcription Factor");
		toolBar.add(labelTF, c);

        c.gridx = 3; c.gridy = 1;
		c.weightx = 0.5; c.weighty = 0.0;
		transcriptionFactorComboBox.setEditable(true);
        toolBar.add(transcriptionFactorComboBox, c);

        c.gridx = 4; c.gridy = 1;
		c.weightx = 0.1; c.weighty = 0.0;
        final JButton buttonSave = new JButton(new SaveResultsAction(this));
        buttonSave.setText("");
        toolBar.add(buttonSave, c);

        c.gridx = 5; c.gridy = 1;
		c.weightx = 0.1; c.weighty = 0.0;
		final JButton buttonTabDelimited = new JButton(new ExportResultsAction(this));
        buttonTabDelimited.setText("");
		toolBar.add(buttonTabDelimited, c);

        c.gridx = 5; c.gridy = 0;
        c.weightx = 0.1; c.weighty = 0.0;
		toolBar.add(closeButton, c);

		c.gridx = 0; c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0.1; c.weighty = 0.0;
		toolBar.add(new JLabel("Filter on "), c);

		c.gridx = 2; c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0.1; c.weighty = 0.0;
		toolBar.add(filterAttributeCB, c);

		c.gridx = 3; c.gridy = 2;
		c.gridwidth = 3;
		c.weightx = 0.1; c.weighty = 0.0;
		toolBar.add(filterValueTF, c);

        return toolBar;
    }
}
