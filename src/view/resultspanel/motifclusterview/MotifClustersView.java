package view.resultspanel.motifclusterview;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import domainmodel.*;
import view.resultspanel.*;
import view.resultspanel.motifclusterview.detailpanel.DetailPanel;
import view.resultspanel.renderers.*;
import view.resultspanel.motifclusterview.tablemodels.BaseMotifClusterTableModel;
import view.resultspanel.motifclusterview.tablemodels.FilterMotifClusterTableModel;


public class MotifClustersView extends JPanel implements MotifView {
    private final MotifViewSupport viewSupport;
    private final NetworkMembershipSupport networkSupport;

    private final Results results;
    private List<MotifCluster> clusters;

    private JTable table;
    private DetailPanelIF detailPanel;

    private ListSelectionListener selectionListener;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;
    private MouseListener popupListener;

    public MotifClustersView(final Results results) {
        this.viewSupport = new MotifViewSupport(this);
        this.results = results;

        this.networkSupport = new NetworkMembershipSupport();
        this.clusters = Collections.emptyList();

        setLayout(new BorderLayout());
        initPanel();
        refreshImp();
	}

    public Results getResults() {
        return results;
    }

    public List<MotifCluster> getMotifClusters() {
        return clusters;
    }

    @Override
    public JTable getMasterTable() {
        return table;
    }

    @Override
    public DetailPanelIF getDetailPanel() {
        return detailPanel;
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

    @Override
    public AbstractMotif getSelectedMotif() {
        return viewSupport.getSelectedMotif();
    }

    @Override
    public void setSelectedMotif(final AbstractMotif motif) {
        viewSupport.setSelectedMotif(motif);
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        return viewSupport.getSelectedTranscriptionFactor();
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
        installRenderers();

        add(new JScrollPane(table), BorderLayout.CENTER);

        detailPanel = new DetailPanel();
        add((JPanel) detailPanel, BorderLayout.SOUTH);
	}

    private void installRenderers() {
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
    }

    @Override
    public void refresh() {
        if (networkSupport.isRefreshNecessary()) {
           refreshImp();
        }
    }

    private void refreshImp() {
        final AbstractMotif currentSelection = getSelectedMotif();
        this.clusters = results.getMotifClusters(networkSupport.getCurrentIDs());
        final FilterMotifClusterTableModel model = new FilterMotifClusterTableModel(
                new BaseMotifClusterTableModel(this.clusters),
                FilterAttribute.TRANSCRIPTION_FACTOR, "");
        table.setModel(model);
        installRenderers();
        setSelectedMotif(currentSelection);
        detailPanel.refresh();
    }

    public void registerSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        if (selectionListener == null) {
            selectionListener = TableMotifClusterSelectionConnector.connect(table, selectedMotif);
            selectedMotif.setMotif(getSelectedMotif());
            transcriptionFactorCB.setSelectedItem(getSelectedTranscriptionFactor());
        }
        if (popupListener == null) {
            popupListener = new MotifPopUpMenu(selectedMotif, transcriptionFactorCB, getResults().isRegionBased(), this);
            table.addMouseListener(popupListener);
        }
        detailPanel.registerSelectionComponents(transcriptionFactorCB);
        selectedMotif.registerListener(detailPanel);
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
        detailPanel.unregisterSelectionComponents();
        selectedMotif.unregisterListener(detailPanel);
    }

    @Override
    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.registerFilterComponents(filterAttributeCB, filterValueTF);
    }

    @Override
    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.unregisterFilterComponents(filterAttributeCB, filterValueTF);
    }
}
