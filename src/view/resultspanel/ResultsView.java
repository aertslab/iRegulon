package view.resultspanel;

import domainmodel.TranscriptionFactor;
import view.IRegulonResourceBundle;
import view.resultspanel.motifview.EnrichedMotifsView;
import view.resultspanel.actions.*;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import view.resultspanel.actions.CreateNewNetworkAction;
import view.resultspanel.actions.DrawNodesAndEdgesAction;

import view.resultspanel.transcriptionfactorview.EnrichedTranscriptionFactorsView;

import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.Motif;
import domainmodel.Results;


public class ResultsView extends IRegulonResourceBundle {
    private final String runName;
    private final Results results;

    private boolean isSaved;

    private SelectedMotif selectedMotif;
    private JComboBox filterAttributeTF;
    private JTextField filterValueTF;
    private TFComboBox transcriptionFactorCB;

    private JButton closeButton;
	private JPanel mainPanel = null;
	
	public ResultsView(final String runName, final Results results) {
		this.runName = runName;
        this.results = results;
		this.isSaved = false;

        this.selectedMotif = new SelectedMotif(results.getInput().getAttributeName());
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

    public Motif getSelectedMotif() {
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
        getCloseButton().addActionListener(new CloseResultsViewAction(panel, this));
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
        this.filterAttributeTF = new JComboBox(FilteringOn.values());
        this.filterAttributeTF.setSelectedItem(FilteringOn.MOTIF);
        this.filterValueTF = new JTextField();
        this.closeButton = new JButton(new CloseResultsViewAction(null, this));
        final JPanel toolBar = createToolBar(this.selectedMotif, this.transcriptionFactorCB, this.closeButton, this.filterAttributeTF, this.filterValueTF);

        // 2. Create enriched motifs view ...
        final EnrichedMotifsView motifsView = new EnrichedMotifsView(this.results);
        // 3. Create enriched TF view ...
        final EnrichedTranscriptionFactorsView tfsView = new EnrichedTranscriptionFactorsView(this.results);

        // 4. Create tabbed pane with these views ...
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Transcription Factors", null,
                tfsView.createPanel(selectedMotif, transcriptionFactorCB, filterAttributeTF, filterValueTF),
                "Transcription factor oriented view.");
		tabbedPane.addTab("Motifs", null,
                motifsView.createPanel(selectedMotif, transcriptionFactorCB, filterAttributeTF, filterValueTF),
                "Motif oriented view.");
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                final JTabbedPane pane = (JTabbedPane) changeEvent.getSource();
                // 1. Refresh motif selection ...
                final MotifView view = (MotifView) pane.getSelectedComponent();
                selectedMotif.setMotif((view != null) ? view.getSelectedMotif() : null);
                // 2. Refresh table row filter ...
                //TODO: also refresh of filter => Possibly also necessary to rewire filter according to view ...
            }
        });

		final JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
        result.add(toolBar, BorderLayout.NORTH);
        result.add(tabbedPane, BorderLayout.CENTER);
        return result;
	}

    private JPanel createToolBar(final SelectedMotif selectedMotif, final JComboBox transcriptionFactorComboBox,
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
        final TranscriptionFactorDependentAction drawNodesAndEdgesAction = new DrawNodesAndEdgesAction(selectedMotif);
        final JButton buttonDrawEdges = new JButton(drawNodesAndEdgesAction);
        buttonDrawEdges.setText("+");
        toolBar.add(buttonDrawEdges, c);

        c.gridx = 1; c.gridy = 1;
		c.weightx = 0.1; c.weighty = 0.0;
        final TranscriptionFactorDependentAction drawNetworkAction = new CreateNewNetworkAction(selectedMotif);
        JButton buttonDrawNetwork = new JButton(drawNetworkAction);
        buttonDrawNetwork.setText("N");

		toolBar.add(buttonDrawNetwork, c);

        c.gridx = 2; c.gridy = 1;
        c.weightx = 0.1; c.weighty = 0.0;
		final JLabel labelTF = new JLabel("Transcription Factor");
		toolBar.add(labelTF, c);

        c.gridx = 3; c.gridy = 1;
		c.weightx = 0.5; c.weighty = 0.0;
		transcriptionFactorComboBox.setEditable(true);
		final JTextComponent tc = (JTextComponent) transcriptionFactorComboBox.getEditor().getEditorComponent();
		tc.getDocument().addDocumentListener(drawNodesAndEdgesAction);
		tc.getDocument().addDocumentListener(drawNetworkAction);
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
        closeButton.setText("");
		toolBar.add(closeButton, c);

		c.gridx = 0; c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0.1; c.weighty = 0.0;
		toolBar.add(new JLabel("Filtering on: "), c);

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
