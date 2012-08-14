package view.resultspanel;

import domainmodel.TranscriptionFactor;
import view.IRegulonResourceBundle;
import view.resultspanel.motifview.detailpanel.TGPanel;
import view.resultspanel.motifview.tablemodels.FilteredMotifModel;
import view.resultspanel.motifview.tablemodels.FilteredPatternDocumentListener;
import view.resultspanel.motifview.tablemodels.GlobalMotifTableModel;
import view.resultspanel.motifview.tablemodels.MotifTableModel;
import view.resultspanel.motifview.tablemodels.ToolTipHeader;
import view.resultspanel.actions.*;
import view.resultspanel.renderers.BooleanRenderer;
import view.resultspanel.renderers.ColorRenderer;
import view.resultspanel.renderers.ColumnWidthRenderer;
import view.resultspanel.renderers.CombinedRenderer;
import view.resultspanel.renderers.DefaultRenderer;
import view.resultspanel.renderers.FloatRenderer;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

import view.resultspanel.actions.CreateNewNetworkAction;
import view.resultspanel.actions.DrawNodesAndEdgesAction;

import java.util.ArrayList;
import java.util.List;

import view.resultspanel.transcriptionfactorview.EnrichedTranscriptionFactorsView;

import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.Motif;
import domainmodel.Results;


public class ResultsView extends IRegulonResourceBundle {
    private final String runName;
    private final Results results;

    private boolean isSaved;
	private List<Motif> enrichedMotifs;
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

        this.enrichedMotifs = new ArrayList<Motif>(results.getMotifs());
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

    public List<Motif> getEnrichedMotifs() {
        return enrichedMotifs;
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

        // 2. Create master and detail panel ...
		final JScrollPane masterPanel = this.createMasterPanel(this.transcriptionFactorCB, this.filterAttributeTF, this.filterValueTF);
		final TGPanel detailPanel = new TGPanel(this.transcriptionFactorCB, this.results.getInput());
		this.selectedMotif.registerListener(detailPanel);


		//Create a split pane with the two scroll panes in it.
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		//Provide minimum sizes for the two components in the split pane
		final Dimension minimumSize = new Dimension(100, 50);
		masterPanel.setMinimumSize(minimumSize);
		detailPanel.setMinimumSize(minimumSize);
			
        final JTabbedPane tabbedPane = new JTabbedPane();
        EnrichedTranscriptionFactorsView tfOutput = new EnrichedTranscriptionFactorsView(this.results);
		tabbedPane.addTab("Transcription Factors", null, tfOutput.createPanel(), "Transcription factor oriented view.");
		tabbedPane.addTab("Motifs", null, splitPane, "Motif oriented view.");

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

	protected JScrollPane createMasterPanel(final JComboBox transcriptionFactorComboBox,
                                       final JComboBox filterAttributeCB, final JTextField filterValueTF) {
		//add a table model
		final MotifTableModel tableModel = new MotifTableModel(this.enrichedMotifs);
		//filtering table model
		final FilteredMotifModel filteredModel = new FilteredMotifModel(tableModel, FilteringOn.MOTIF, "");
		final JTable table = new JTable(filteredModel);
		
		//set the tooltips on the columns
		ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
		GlobalMotifTableModel modelTable = (GlobalMotifTableModel) table.getModel();
	    header.setToolTipStrings(modelTable.getTooltips());
	    header.setToolTipText("");
	    table.setTableHeader(header);

			
		//let the filtering model listen to the combobox that dessides the filtering (motif or TF)
		filterAttributeCB.addActionListener(new FilteringOnComboBoxAction(filteredModel));
		filterValueTF.getDocument().addDocumentListener(new FilteredPatternDocumentListener(filteredModel));
			
		//tableModel.initColumnSizes(table);
		//add mouse and selection listeners
		//MotifPopUpMenu interaction = new MotifPopUpMenu(table, selectedMotif, tc);
		table.addMouseListener(new MotifPopUpMenu(table, selectedMotif,
				(JTextComponent) transcriptionFactorComboBox.getEditor().getEditorComponent(), this.results.isRegionBased()));
		ListSelectionModel listSelectionModel = table.getSelectionModel();
		TableSelectionListener tableSelectListener = new TableSelectionListener(table, selectedMotif);
		listSelectionModel.addListSelectionListener(tableSelectListener);
	   	
			
		//colors of the table
		ColorRenderer cr=new ColorRenderer("ClusterCode");
			
		//setting the table renderer
		for (int i=0; i < table.getModel().getColumnCount(); i++){
			CombinedRenderer renderer = new CombinedRenderer();
			// the float renderer
			if (table.getModel().getColumnClass(i).equals(Float.class)){
				renderer.addRenderer(new FloatRenderer("0.000"));
			}else{
				if (table.getModel().getColumnClass(i).equals(Boolean.class)){
					renderer.addRenderer(new BooleanRenderer());
				}else{
					renderer.addRenderer(new DefaultRenderer());
				}
			}
			//the column renderer
			renderer.addRenderer(cr);
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setCellRenderer(renderer);
		}
		
		ColumnWidthRenderer columnWidth = new ColumnWidthRenderer(table);
		columnWidth.widthSetter();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		return new JScrollPane(table);
	}
}
