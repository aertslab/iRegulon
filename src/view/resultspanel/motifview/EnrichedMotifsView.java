package view.resultspanel.motifview;


import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.motifview.detailpanel.TGPanel;
import view.resultspanel.motifview.tablemodels.*;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class EnrichedMotifsView extends JPanel implements MotifView {
    private JTable table;
    private TGPanel detailPanel;
    private final MotifViewSupport support;

    private final Results results;
    private List<Motif> enrichedMotifs;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;

    public EnrichedMotifsView(final Results results) {
        this.support = new MotifViewSupport(this);
        this.results = results;
        this.enrichedMotifs = new ArrayList<Motif>(results.getMotifs());
        setLayout(new BorderLayout());
	}

    public Results getResults() {
        return results;
    }

    public java.util.List<Motif> getEnrichedMotifs() {
        return enrichedMotifs;
    }

    @Override
    public AbstractFilterMotifTableModel getModel() {
        return (AbstractFilterMotifTableModel) table.getModel();
    }

    @Override
    public FilterAttributeActionListener getFilterAttributeListener() {
        return filterAttributeActionListener;
    }

    @Override
    public void setFilterAttributeListener(FilterAttributeActionListener listener) {
        filterAttributeActionListener = listener;
    }

    @Override
    public FilterPatternDocumentListener getFilterPatternListener() {
        return filterPatternDocumentListener;
    }

    @Override
    public void setFilterPatternListener(FilterPatternDocumentListener listener) {
       filterPatternDocumentListener = listener;
    }

    public Motif getSelectedMotif() {
        final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final MotifTableModel model = (MotifTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getMotifAtRow(modelRowIdx);
		}
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        final Motif motif = getSelectedMotif();
        if (motif == null) return null;
        final TranscriptionFactor transcriptionFactor = detailPanel.getSelectedTranscriptionFactor();
        if (transcriptionFactor != null) return transcriptionFactor;
        else return getSelectedMotif().getBestTranscriptionFactor();
    }

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        final JScrollPane masterPanel = this.createMasterPanel(selectedMotif, transcriptionFactorCB);
		detailPanel = new TGPanel(transcriptionFactorCB, results.getParameters());
		selectedMotif.registerListener(detailPanel);

		//Create a split pane with the two scroll panes in it.
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		//Provide minimum sizes for the two components in the split pane
		final Dimension minimumSize = new Dimension(100, 50);
		masterPanel.setMinimumSize(minimumSize);
		detailPanel.setMinimumSize(minimumSize);

        add(splitPane, BorderLayout.CENTER);
        return this;
    }

	private JScrollPane createMasterPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorComboBox) {
		final BaseMotifTableModel tableModel = new BaseMotifTableModel(this.enrichedMotifs);
		final FilterMotifTableModel filteredModel = new FilterMotifTableModel(tableModel, FilterAttribute.MOTIF, "");
		table = new JTable(filteredModel);

		final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
		header.setToolTipStrings(filteredModel.getTooltips().toArray(new String[filteredModel.getTooltips().size()]));
	    header.setToolTipText("");
	    table.setTableHeader(header);

		//let the filtering model listen to the combobox that dessides the filtering (motif or TF)

		table.addMouseListener(new MotifPopUpMenu(selectedMotif, transcriptionFactorComboBox, this.results.isRegionBased()));
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

		final ColumnWidthSetter columnWidth = new ColumnWidthSetter(table);
		columnWidth.setWidth();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		return new JScrollPane(table);
	}

    @Override
    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        support.registerFilterComponents(filterAttributeCB, filterValueTF);
    }

    @Override
    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        support.unregisterFilterComponents(filterAttributeCB, filterValueTF);
    }
}
