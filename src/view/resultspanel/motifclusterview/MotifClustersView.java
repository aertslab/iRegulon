package view.resultspanel.motifclusterview;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import domainmodel.*;
import view.resultspanel.*;
import view.resultspanel.renderers.*;
import view.resultspanel.motifclusterview.tablemodels.BaseMotifClusterTableModel;
import view.resultspanel.motifclusterview.tablemodels.FilterMotifClusterTableModel;


public class MotifClustersView extends JPanel implements MotifView {
    private JTable table;
    private final MotifViewSupport support;
    private final NetworkMembershipSupport networkSupport;

    private final Results results;
    private final List<MotifCluster> clusters;

    private ListSelectionListener selectionListener;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;
    private MouseListener popupListener;

    public MotifClustersView(final Results results) {
        this.support = new MotifViewSupport(this);
        this.results = results;

        this.networkSupport = new NetworkMembershipSupport();
        this.clusters = results.getMotifClusters(networkSupport.getCurrentIDs());

        setLayout(new BorderLayout());
        initPanel();
	}

    public Results getResults() {
        return results;
    }

    public List<MotifCluster> getMotifClusters() {
        return clusters;
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

    public AbstractMotif getSelectedMotif() {
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
        final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final MotifTableModel model = (MotifTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx);
		}
    }

    private void initPanel() {
		final FilterMotifClusterTableModel model = new FilterMotifClusterTableModel(
                new BaseMotifClusterTableModel(this.clusters),
                FilterAttribute.TRANSCRIPTION_FACTOR, "");
        table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
        header.setToolTipStrings(model.getTooltips().toArray(new String[model.getTooltips().size()]));
        header.setToolTipText("");
        table.setTableHeader(header);

        final ClusterColorRenderer clusterColorRenderer = new ClusterColorRenderer("ClusterCode");
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            final CombinedRenderer renderer = new CombinedRenderer();
            if (table.getModel().getColumnClass(i).equals(Float.class)) {
                renderer.addRenderer(new FloatRenderer("0.000"));
            } else if (table.getModel().getColumnClass(i).equals(Boolean.class)) {
                renderer.addRenderer(new BooleanRenderer());
            } else {
                renderer.addRenderer(new DefaultRenderer());
            }
            renderer.addRenderer(clusterColorRenderer);
            final TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(renderer);
        }

        final ColumnWidthSetter columnWidth = new ColumnWidthSetter(table);
		columnWidth.setWidth();

        add(new JScrollPane(table), BorderLayout.CENTER);
        //TODO: Add detail panel with other TFs, the enriched motifs in the clusters and the combined target genes ...
	}

    public void registerSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        if (selectionListener == null) {
            selectionListener = TableMotifClusterSelectionConnector.connect(table, selectedMotif);
            selectedMotif.setMotif(getSelectedMotif());
            transcriptionFactorCB.setSelectedItem(getSelectedTranscriptionFactor());
        }
        if (popupListener == null) {
            popupListener = new MotifPopUpMenu(selectedMotif, transcriptionFactorCB, getResults().isRegionBased());
            table.addMouseListener(popupListener);
        }
    }

    public void unregisterSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        if (selectionListener != null) {
            TableMotifClusterSelectionConnector.unconnect(table, selectionListener);
            selectionListener = null;
        }
        if (popupListener != null) {
            table.removeMouseListener(popupListener);
            popupListener = null;
        }
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
