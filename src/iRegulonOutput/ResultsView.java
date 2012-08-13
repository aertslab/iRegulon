package iRegulonOutput;

import iRegulonInput.IRegulonResourceBundle;
import iRegulonOutput.DetailPanel.TGPanel;
import iRegulonOutput.MotifTableModels.FilteredMotifModel;
import iRegulonOutput.MotifTableModels.FilteredPatternDocumentListener;
import iRegulonOutput.MotifTableModels.GlobalMotifTableModel;
import iRegulonOutput.MotifTableModels.MotifTableModel;
import iRegulonOutput.MotifTableModels.ToolTipHeader;
import iRegulonOutput.renderers.BooleanRenderer;
import iRegulonOutput.renderers.ColorRenderer;
import iRegulonOutput.renderers.ColumnWidthRenderer;
import iRegulonOutput.renderers.CombinedRenderer;
import iRegulonOutput.renderers.DefaultRenderer;
import iRegulonOutput.renderers.FloatRenderer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

import networkDrawActions.DrawNetworkAction;
import networkDrawActions.DrawRegulonsAndEdgesAction;

import java.util.ArrayList;
import java.util.List;

import saveActions.SaveLoadDialogs;
import saveActions.SaveResults;
import transcriptionfactorview.EnrichedTranscriptionFactorsView;



import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainModel.Motif;
import domainModel.Results;

public class ResultsView extends IRegulonResourceBundle{
	private List<Motif> motifList;
	private String runName;
	private JButton buttonDrawEdges;
	private JButton buttonDrawNetwork;
	private TFComboBox tfcmbBox;
	private JButton buttonSave;
	private JButton buttonClose;
	private JButton buttonTabDelimited;
	private SelectedMotif selectedTFRegulons;
	private JSplitPane splitPane;
	private CytoPanel cytoPanel;

	private boolean isSaved;
	
	private Results result;
	
	private JComponent totalPanel;
	
	public ResultsView(String runName){
		this.runName = runName;
		this.isSaved = false;
	}
	
	
	public void drawPanel(Results results){
		this.result = results;
		this.motifList = new ArrayList<Motif>(result.getMotifs());
		//this.motifList = MotifList;
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		this.cytoPanel = desktop.getCytoPanel (SwingConstants.EAST);
		cytoPanel.setState(CytoPanelState.DOCK);
		//addCytoPanelListener(CytoPanelListener);
		
		
		this.selectedTFRegulons = new SelectedMotif(result.getInput().getAttributeName());
		
		//JPanel panel = new JPanel(layout);
		JPanel panel = this.createMasterPanel();
		TGPanel tgPanel = new TGPanel(this.tfcmbBox, this.result.getInput());
		this.selectedTFRegulons.registerListener(tgPanel);

			

		//Create a split pane with the two scroll panes in it.
		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			                           panel, tgPanel);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setDividerLocation(200);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		panel.setMinimumSize(minimumSize);
		tgPanel.setMinimumSize(minimumSize);
			
		//JScrollPane panelScrollPane = new JScrollPane(table);
		//JScrollPane tgPanelScrollPane = new JScrollPane(tgPanel);
			
		//Container contentPane = frame.getContentPane();
		String panelName = getBundle().getString("plugin_visual_name") + " " + runName;

        JTabbedPane tabbedPane = new JTabbedPane();
        EnrichedTranscriptionFactorsView tfOutput = new EnrichedTranscriptionFactorsView(this.runName, this.result);
		tabbedPane.addTab("Transcription Factors", null, tfOutput.createPanel(), "Transcription factor oriented view.");
		tabbedPane.addTab("Motifs", null, this.splitPane, "Motif oriented view.");

		
		this.totalPanel = new JPanel();
		this.totalPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		JLabel label = new JLabel(this.runName);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0.9;
		c.weighty=0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 0;
		c.ipady = 0;
		this.totalPanel.add(label, c);
		if (this.result.hasParameters()){
			label.setText(this.result.getName());
			String parameters = "<html>" 
						+ "Name:  " + this.result.getName() 
						+ "<br/>"
						+ "Species and nomenclature: " + this.result.getSpeciesNomenclature().toString()
						+ "<br/>"
						+ "Minimal NEscore: " + this.result.getEScore()
						+ "<br/>"
						+ "Threshold for visualisation: " + this.result.getThresholdForVisualisation()
						+ "<br/>"
						+ "ROC threshold AUC: " + this.result.getROCthresholdAUC()
						+ "<br/>"
						+ "minimal orthologous: " + this.result.getMinOrthologous()
						+ "<br/>"
						+ "maximal motif similarity: " + this.result.getMaxMotifSimilarityFDR()
						+ "<br/>"
						+ "<br/>"
						+ "database: " + this.result.getDatabaseName()
						+ "<br/>";
			if (this.result.isRegionBased()){
				parameters += "overlap: " + this.result.getOverlap()
						+ "<br/>";
				if (this.result.isDelineationBased()){
					parameters += "Delineation: " + this.result.getDelineationName();
				}else{
					parameters += "Upstream: " + this.result.getUpstream() + " kb"
							+ "<br/>"
							+ "Downstream: " + this.result.getDownstream() + " kb";
				}
			}
			parameters += "</html>";
			label.setToolTipText(parameters);
		}
		String pathClose = "/icons/close.png";
		java.net.URL imgURLClose = getClass().getResource(pathClose);
		ImageIcon iconClose = new ImageIcon(imgURLClose, "Save");
		this.buttonClose = new JButton(iconClose);
		this.buttonClose.setToolTipText("Close these results");
		this.buttonClose.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (! isSaved){
						JFrame frame = new JFrame();
						int n = JOptionPane.showConfirmDialog(frame,
						    	"Do you want to save this file?",
						    	"Save?",
						    	JOptionPane.YES_NO_OPTION);
						if (n == 0){
							SaveResults results = new SaveResults();
							String xml = results.saveResultsAsXML(result);
							String extention = ".irf";
							SaveLoadDialogs.saveDialogue(xml, runName, extention);
						}
					}
					cytoPanel.remove(totalPanel);
					if (cytoPanel.getCytoPanelComponentCount() == 0){
						cytoPanel.setState(CytoPanelState.HIDE);
					}
				}
			});
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		this.totalPanel.add(this.buttonClose, c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx=0;
		c.weighty=0.9;
		c.ipadx = 0;
		c.ipady = 0;
		this.totalPanel.add(tabbedPane, c);
		
		
		//add the panel to Cytoscape
		this.cytoPanel.add(panelName, this.totalPanel);
		//set this panel as active
		int index = cytoPanel.indexOfComponent(this.totalPanel);
		cytoPanel.setSelectedIndex(index);
		
		
		
	}

	/**
	 * 
	 * @return all the TFRegulons found
	 */
	protected List<Motif> getRegulatoryTreeList(){
		return this.motifList;
	}
	
	protected JPanel createMasterPanel(){
		
		//JPanel panel = new JPanel(layout);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		JLabel label = new JLabel(this.runName);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0;
		c.weighty=0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(label, c);
		if (this.result.hasParameters()){
			label.setText(this.result.getName());
			String parameters = "<html>" 
						+ "Name:  " + this.result.getName() 
						+ "<br/>"
						+ "Species and nomenclature: " + this.result.getSpeciesNomenclature().toString()
						+ "<br/>"
						+ "Minimal NEscore: " + this.result.getEScore()
						+ "<br/>"
						+ "Threshold for visualisation: " + this.result.getThresholdForVisualisation()
						+ "<br/>"
						+ "ROC threshold AUC: " + this.result.getROCthresholdAUC()
						+ "<br/>"
						+ "minimal orthologous: " + this.result.getMinOrthologous()
						+ "<br/>"
						+ "maximal motif similarity: " + this.result.getMaxMotifSimilarityFDR()
						+ "<br/>"
						+ "<br/>"
						+ "database: " + this.result.getDatabaseName()
						+ "<br/>";
			if (this.result.isRegionBased()){
				parameters += "overlap: " + this.result.getOverlap()
						+ "<br/>";
				if (this.result.isDelineationBased()){
					parameters += "Delineation: " + this.result.getDelineationName();
				}else{
					parameters += "Upstream: " + this.result.getUpstream() + " kb"
							+ "<br/>"
							+ "Downstream: " + this.result.getDownstream() + " kb";
				}
			}
			parameters += "</html>";
			label.setToolTipText(parameters);
		}
		
		//add buttons for drawing, and selecting transcription factor

		
		
		//Container for the selected regulons
		
		//add buttons for drawing, and selecting transcription factor
			
		DrawRegulonsAndEdgesAction drawNodesAndEdgesAction = new DrawRegulonsAndEdgesAction(selectedTFRegulons);
		this.buttonDrawEdges = new JButton(drawNodesAndEdgesAction);
		this.buttonDrawEdges.setText("+");
			
		DrawNetworkAction drawNetworkAction = new DrawNetworkAction(selectedTFRegulons);
		this.buttonDrawNetwork = new JButton(drawNetworkAction);
		this.buttonDrawNetwork.setText("N");
			
		JLabel labelTF = new JLabel("Transcription Factor");
		this.tfcmbBox = new TFComboBox(selectedTFRegulons);
		this.tfcmbBox.setEditable(true);
		
		final JTextComponent tc = (JTextComponent) this.tfcmbBox.getEditor().getEditorComponent();
		tc.getDocument().addDocumentListener(drawNodesAndEdgesAction);
		tc.getDocument().addDocumentListener(drawNetworkAction);
			
		String pathSave = "/icons/save.png";
		java.net.URL imgURLSave = getClass().getResource(pathSave);
		ImageIcon iconSave = new ImageIcon(imgURLSave, "Save");
		this.buttonSave = new JButton(iconSave);
		this.buttonSave.setToolTipText("Save these results as a ctf");
		this.buttonSave.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					SaveResults results = new SaveResults();
					String xml = results.saveResultsAsXML(result);
					String extention = ".irf";
					boolean saved = SaveLoadDialogs.saveDialogue(xml, runName, extention);
					if (! isSaved){
						isSaved = saved;
					}
				}
		});
		
		String pathTabDelimited = "/icons/save_tabDelimited.png";
		java.net.URL imgURLTabDelimited = getClass().getResource(pathTabDelimited);
		ImageIcon iconTabDelimited = new ImageIcon(imgURLTabDelimited, "Save");
		this.buttonTabDelimited = new JButton(iconTabDelimited);
		this.buttonTabDelimited.setToolTipText("Save these results as a tab delimited file");
		this.buttonTabDelimited.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					SaveResults results = new SaveResults();
					String tabfile = results.saveResultsAsTabDelimited(motifList);
					String extention = ".txt";
					SaveLoadDialogs.saveDialogue(tabfile, runName, extention);
				}
		});
			
		String pathClose = "/icons/close.png";
		java.net.URL imgURLClose = getClass().getResource(pathClose);
		ImageIcon iconClose = new ImageIcon(imgURLClose, "Save");
		this.buttonClose = new JButton(iconClose);
		this.buttonClose.setToolTipText("Close these results");
		this.buttonClose.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (! isSaved){
						JFrame frame = new JFrame();
						int n = JOptionPane.showConfirmDialog(frame,
						    	"Do you want to save this file?",
						    	"Save?",
						    	JOptionPane.YES_NO_OPTION);
						if (n == 0){
							SaveResults results = new SaveResults();
							String xml = results.saveResultsAsXML(result);
							String extention = ".irf";
							SaveLoadDialogs.saveDialogue(xml, runName, extention);
						}
					}
					cytoPanel.remove(totalPanel);
					if (cytoPanel.getCytoPanelComponentCount() == 0){
						cytoPanel.setState(CytoPanelState.HIDE);
					}
				}
			});
			
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.buttonDrawEdges, c);
			
			
		c.gridx = 1;
		c.gridy = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.buttonDrawNetwork, c);
			
			
		c.gridx = 2;
		c.gridy = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(labelTF, c);
			
		
		c.gridx = 3;
		c.gridy = 1;
		c.weightx=0.5;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.tfcmbBox, c);
			
		c.gridx = 4;
		c.gridy = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.buttonSave, c);
		
		c.gridx = 5;
		c.gridy = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.buttonTabDelimited, c);
			
		c.gridx = 5;
		c.gridy = 0;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(this.buttonClose, c);
			
		//the filtering options
		JLabel labelFilter = new JLabel("Filtering on: ");
		JComboBox filteringOn = new JComboBox(FilteringOn.values());
		JTextField textFilter = new JTextField();
			
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(labelFilter, c);
			
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(filteringOn, c);
		
		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 3;
		c.weightx=0.1;
		c.weighty=0;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(textFilter, c);
			
		//add a table model
		MotifTableModel tableModel = new MotifTableModel(this.motifList);
		//filtering table model
		FilteredMotifModel filteredModel = new FilteredMotifModel(tableModel, FilteringOn.MOTIF, "");
		JTable table = new JTable(filteredModel);
		
		//set the tooltips on the columns
		ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
		GlobalMotifTableModel modelTable = (GlobalMotifTableModel) table.getModel();
	    header.setToolTipStrings(modelTable.getTooltips());
	    header.setToolTipText("");
	    table.setTableHeader(header);

			
		//let the filtering model listen to the combobox that dessides the filtering (motif or TF)
		filteringOn.addActionListener(new FilteringOnComboBoxAction(filteredModel));
		filteringOn.setSelectedItem(FilteringOn.MOTIF);
		textFilter.getDocument().addDocumentListener(new FilteredPatternDocumentListener(filteredModel));
			
		//tableModel.initColumnSizes(table);
		//add mouse and selection listeners
		//MotifPopUpMenu interaction = new MotifPopUpMenu(table, selectedTFRegulons, tc);
		table.addMouseListener(new MotifPopUpMenu(table, selectedTFRegulons, 
				(JTextComponent) this.tfcmbBox.getEditor().getEditorComponent(), this.result.isRegionBased()));
		ListSelectionModel listSelectionModel = table.getSelectionModel();
		TableSelectionListener tableSelectListener = new TableSelectionListener(table, selectedTFRegulons);
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
			/*
			TableRowSorter trs = new TableRowSorter(tableModel);

	        class IntComparator implements Comparator {
	            public int compare(Object o1, Object o2) {
	                Integer int1 = (Integer)o1;
	                Integer int2 = (Integer)o2;
	                return int1.compareTo(int2);
	            }

	            public boolean equals(Object o2) {
	                return this.equals(o2);
	            }
	        }
	        trs.setComparator(0, new IntComparator());
	        trs.setComparator(4, new IntComparator());
	        trs.setComparator(5, new IntComparator());
	        trs.setComparator(6, new IntComparator());

	        table.setRowSorter(trs);
			*/
	        
			
			
			//panel.add(table, BorderLayout.CENTER);
			
		JScrollPane scrollPane = new JScrollPane(table);
	    panel.add(scrollPane);
	    c.gridx = 0;
		c.gridy = 3;
		c.weightx=1;
		c.weighty=0.8;
		c.gridwidth = 6;
		c.gridheight = 0;
		c.ipadx = 0;
		c.ipady = 100;
		panel.add(scrollPane, c);
		return panel;

	}
	
	
	

	
	
}
