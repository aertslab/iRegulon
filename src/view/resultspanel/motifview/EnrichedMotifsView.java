package view.resultspanel.motifview;


import domainmodel.Motif;
import domainmodel.Results;
import view.resultspanel.*;
import view.resultspanel.motifview.detailpanel.TGPanel;
import view.resultspanel.motifview.tablemodels.*;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class EnrichedMotifsView {
    private final Results results;
	private List<Motif> enrichedMotifs;

    public EnrichedMotifsView(final Results results) {
        this.results = results;
        this.enrichedMotifs = new ArrayList<Motif>(results.getMotifs());
	}

    public Results getResults() {
        return results;
    }

    public java.util.List<Motif> getEnrichedMotifs() {
        return enrichedMotifs;
    }

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB,
                                  final JComboBox filterAttributeTF, final JTextField filterValueTF) {
        final JScrollPane masterPanel = this.createMasterPanel(selectedMotif, transcriptionFactorCB, filterAttributeTF, filterValueTF);
		final TGPanel detailPanel = new TGPanel(transcriptionFactorCB, results.getInput());
		selectedMotif.registerListener(detailPanel);

		//Create a split pane with the two scroll panes in it.
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		//Provide minimum sizes for the two components in the split pane
		final Dimension minimumSize = new Dimension(100, 50);
		masterPanel.setMinimumSize(minimumSize);
		detailPanel.setMinimumSize(minimumSize);

        return splitPane;
    }

	private JScrollPane createMasterPanel(final SelectedMotif selectedMotif, final JComboBox transcriptionFactorComboBox,
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

		table.addMouseListener(new MotifPopUpMenu(selectedMotif,
				(JTextComponent) transcriptionFactorComboBox.getEditor().getEditorComponent(), this.results.isRegionBased()));
		TableMotifSelectionConnector.connect(table, selectedMotif);

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
