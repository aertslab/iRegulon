package view.resultspanel.motifandtrackclusterview;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.MotifAndTrackCluster;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackclusterview.detailpanel.DetailPanel;
import view.resultspanel.motifandtrackclusterview.tablemodels.BaseMotifAndTrackClusterTableModel;
import view.resultspanel.motifandtrackclusterview.tablemodels.FilterMotifAndTrackClusterTableModel;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Collections;
import java.util.List;


public class MotifAndTrackClustersView extends JPanel implements MotifAndTrackView {
    private final MotifAndTrackViewSupport viewSupport;
    private final NetworkMembershipSupport networkSupport;

    private final Results results;
    private List<MotifAndTrackCluster> clusters;

    private JTable table;
    private DetailPanel detailPanel;

    private ListSelectionListener selectionListener;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;
    private MotifAndTrackPopUpMenu popupListener;

    private JComboBox filterAttributeCB;
    private JTextField filterValueTF;

    public MotifAndTrackClustersView(final Results results) {
        this.viewSupport = new MotifAndTrackViewSupport(this);
        this.results = results;

        this.networkSupport = new NetworkMembershipSupport();
        this.clusters = Collections.emptyList();

        setLayout(new BorderLayout());
        initPanel();
        refresh();
    }

    public Results getResults() {
        return results;
    }

    public List<MotifAndTrackCluster> getMotifAndTrackClusters() {
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
    public AbstractFilterMotifAndTrackTableModel getModel() {
        return (AbstractFilterMotifAndTrackTableModel) table.getModel();
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
    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        return detailPanel.getSelectedMotifOrTrack();
    }

    @Override
    public void setSelectedMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        final int modelIdx = findModelIndexForMotifOrTrack(motifOrTrack);
        if (modelIdx < 0) table.getSelectionModel().clearSelection();
        else {
            final int viewIdx = table.convertRowIndexToView(modelIdx);
            table.getSelectionModel().setSelectionInterval(viewIdx, viewIdx);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    detailPanel.setSelectedMotifOrTrack(motifOrTrack);
                }
            });
        }
    }

    private int findModelIndexForMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        if (motifOrTrack == null) return -1;
        final MotifAndTrackTableModel model = (MotifAndTrackTableModel) table.getModel();
        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
            if (model.getMotifOrTrackAtRow(rowIndex) instanceof MotifAndTrackCluster) {
                MotifAndTrackCluster motifOrTrackCluster = (MotifAndTrackCluster) model.getMotifOrTrackAtRow(rowIndex);
                for (AbstractMotifAndTrack memberMotifOrTrack : motifOrTrackCluster.getMotifsAndTracks()) {
                    if (motifOrTrack.getDatabaseID() == memberMotifOrTrack.getDatabaseID()) {
                        return rowIndex;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        return viewSupport.getSelectedTranscriptionFactor();
    }

    private void initPanel() {
        final JComponent masterPanel = createMasterPanel();
        detailPanel = new DetailPanel();

        //Create a split pane with the two scroll panes in it.
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        //Provide minimum sizes for the two components in the split pane
        final Dimension minimumSize = new Dimension(100, 50);
        masterPanel.setMinimumSize(minimumSize);
        detailPanel.setMinimumSize(minimumSize);

        add(splitPane, BorderLayout.CENTER);
    }

    private JComponent createMasterPanel() {
        final FilterMotifAndTrackClusterTableModel model = new FilterMotifAndTrackClusterTableModel(
                new BaseMotifAndTrackClusterTableModel(this.clusters),
                FilterAttribute.TRANSCRIPTION_FACTOR, "");
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
        header.setToolTipStrings(model.getTooltips().toArray(new String[model.getTooltips().size()]));
        header.setToolTipText("");
        table.setTableHeader(header);
        installRenderers();

        return new JScrollPane(table);
    }

    private void installRenderers() {
        final ClusterColorRenderer clusterColorRenderer = new ClusterColorRenderer("ClusterNumber");
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
        final AbstractMotifAndTrack currentSelection = getSelectedMotifOrTrack();

        final JComboBox curFilterAttributeCB = filterAttributeCB;
        final JTextField curFilterValueTF = filterValueTF;
        if (curFilterAttributeCB != null) unregisterFilterComponents(filterAttributeCB, filterValueTF);

        final FilterMotifAndTrackClusterTableModel oldModel = (FilterMotifAndTrackClusterTableModel) table.getModel();
        final FilterAttribute curFilterAttribute = oldModel.getFilterAttribute();
        final String curFilterPattern = oldModel.getPattern();

        this.clusters = results.getMotifAndTrackClusters(networkSupport.getCurrentIDs());
        final FilterMotifAndTrackClusterTableModel newModel = new FilterMotifAndTrackClusterTableModel(
                new BaseMotifAndTrackClusterTableModel(this.clusters),
                curFilterAttribute, curFilterPattern);
        table.setModel(newModel);
        installRenderers();

        if (curFilterAttributeCB != null) registerFilterComponents(curFilterAttributeCB, curFilterValueTF);

        setSelectedMotifOrTrack(currentSelection);

        if (popupListener != null) popupListener.refresh();
        detailPanel.refresh();
    }

    public void registerSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox transcriptionFactorCB) {
        if (selectionListener == null) {
            selectionListener = TableMotifAndTrackClusterSelectionConnector.connect(table, selectedMotifOrTrack);
            selectedMotifOrTrack.setMotifOrTrack(getSelectedMotifOrTrack());
            transcriptionFactorCB.setSelectedItem(getSelectedTranscriptionFactor());
        }
        if (popupListener == null) {
            popupListener = new MotifAndTrackPopUpMenu(selectedMotifOrTrack, transcriptionFactorCB, false, this, results.getParameters().getAttributeName());
            table.addMouseListener(popupListener);
        }
        detailPanel.registerSelectionComponents(transcriptionFactorCB);
        selectedMotifOrTrack.registerListener(detailPanel);
    }

    public void unregisterSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox transcriptionFactorCB) {
        if (selectionListener != null) {
            TableMotifAndTrackClusterSelectionConnector.unconnect(table, selectionListener);
            selectionListener = null;
        }
        if (popupListener != null) {
            table.removeMouseListener(popupListener);
            popupListener = null;
        }
        detailPanel.unregisterSelectionComponents();
        selectedMotifOrTrack.unregisterListener(detailPanel);
    }

    @Override
    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.registerFilterComponents(filterAttributeCB, filterValueTF);
        this.filterAttributeCB = filterAttributeCB;
        this.filterValueTF = filterValueTF;
    }

    @Override
    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.unregisterFilterComponents(filterAttributeCB, filterValueTF);
        this.filterAttributeCB = null;
        this.filterValueTF = null;
    }
}
