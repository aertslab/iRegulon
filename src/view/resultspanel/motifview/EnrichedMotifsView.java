package view.resultspanel.motifview;


import domainmodel.*;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifview.detailpanel.DetailPanel;
import view.resultspanel.motifandtrackview.tablemodels.*;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class EnrichedMotifsView extends JPanel implements MotifAndTrackView {
    private JTable table;
    private DetailPanel detailPanel;
    private final MotifAndTrackViewSupport viewSupport;

    private final Results results;
    private List<AbstractMotifAndTrack> enrichedMotifs;

    private ListSelectionListener selectionListener;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;
    private MotifAndTrackPopUpMenu popupListener;

    public EnrichedMotifsView(final Results results) {
        this.viewSupport = new MotifAndTrackViewSupport(this);
        this.results = results;
        this.enrichedMotifs = new ArrayList<AbstractMotifAndTrack>(results.getMotifs());
        setLayout(new BorderLayout());
        initPanel();
	}

    public Results getResults() {
        return results;
    }

    public java.util.List<AbstractMotifAndTrack> getEnrichedMotifs() {
        return enrichedMotifs;
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
        return viewSupport.getSelectedMotifOrTrack();
    }

    @Override
    public void setSelectedMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        viewSupport.setSelectedMotifOrTrack(motifOrTrack);
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        return viewSupport.getSelectedTranscriptionFactor();
    }

    private void initPanel() {
        final JScrollPane masterPanel = createMasterPanel();
		detailPanel = new DetailPanel(results.getParameters());

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

	private JScrollPane createMasterPanel() {
		final BaseMotifAndTrackTableModel tableModel = new BaseMotifAndTrackTableModel(this.enrichedMotifs, AbstractMotifAndTrack.TrackType.MOTIF);
		final FilterMotifAndTrackTableModel filteredModel = new FilterMotifAndTrackTableModel(tableModel, FilterAttribute.MOTIF_OR_TRACK, "");
		table = new JTable(filteredModel);

		final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
		header.setToolTipStrings(filteredModel.getTooltips().toArray(new String[filteredModel.getTooltips().size()]));
	    header.setToolTipText("");
	    table.setTableHeader(header);

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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		return new JScrollPane(table);
	}

    public void registerSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox transcriptionFactorCB) {
        if (selectionListener == null) {
            selectionListener = TableMotifAndTrackSelectionConnector.connect(table, selectedMotifOrTrack);
            selectedMotifOrTrack.setMotifOrTrack(getSelectedMotifOrTrack());
            transcriptionFactorCB.setSelectedItem(getSelectedTranscriptionFactor());
            detailPanel.registerSelectionComponents(transcriptionFactorCB);
            selectedMotifOrTrack.registerListener(detailPanel);
        }
        if (popupListener == null) {
            popupListener = new MotifAndTrackPopUpMenu(selectedMotifOrTrack, transcriptionFactorCB, this.results.isRegionBased(), this, results.getParameters().getAttributeName());
            table.addMouseListener(popupListener);
        }
    }

    public void unregisterSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox transcriptionFactorCB) {
        if (selectionListener != null) {
            TableMotifAndTrackSelectionConnector.unconnect(table, selectionListener);
            selectionListener = null;
            detailPanel.unregisterSelectionComponents();
            selectedMotifOrTrack.unregisterListener(detailPanel);
        }
        if (popupListener != null) {
            table.removeMouseListener(popupListener);
            popupListener = null;
        }
    }

    @Override
    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.registerFilterComponents(filterAttributeCB, filterValueTF);
    }

    @Override
    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        viewSupport.unregisterFilterComponents(filterAttributeCB, filterValueTF);
    }

    @Override
    public void refresh() {
        if (popupListener != null) popupListener.refresh();
        detailPanel.refresh();
    }
}
