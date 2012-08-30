package view.resultspanel.motifview;


import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.motifview.detailpanel.DetailPanel;
import view.resultspanel.motifview.tablemodels.*;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public final class EnrichedMotifsView extends JPanel implements MotifView {
    private JTable table;
    private DetailPanel detailPanel;
    private final MotifViewSupport viewSupport;

    private final Results results;
    private List<Motif> enrichedMotifs;

    private ListSelectionListener selectionListener;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;
    private MouseListener popupListener;

    public EnrichedMotifsView(final Results results) {
        this.viewSupport = new MotifViewSupport(this);
        this.results = results;
        this.enrichedMotifs = new ArrayList<Motif>(results.getMotifs());
        setLayout(new BorderLayout());
        initPanel();
	}

    public Results getResults() {
        return results;
    }

    public java.util.List<Motif> getEnrichedMotifs() {
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
		final BaseMotifTableModel tableModel = new BaseMotifTableModel(this.enrichedMotifs);
		final FilterMotifTableModel filteredModel = new FilterMotifTableModel(tableModel, FilterAttribute.MOTIF, "");
		table = new JTable(filteredModel);

		final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
		header.setToolTipStrings(filteredModel.getTooltips().toArray(new String[filteredModel.getTooltips().size()]));
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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);

		return new JScrollPane(table);
	}

    public void registerSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        if (selectionListener == null) {
            selectionListener = TableMotifSelectionConnector.connect(table, selectedMotif);
            selectedMotif.setMotif(getSelectedMotif());
            transcriptionFactorCB.setSelectedItem(getSelectedTranscriptionFactor());
            detailPanel.registerSelectionComponents(transcriptionFactorCB);
            selectedMotif.registerListener(detailPanel);
        }
        if (popupListener == null) {
            popupListener = new MotifPopUpMenu(selectedMotif, transcriptionFactorCB, this.results.isRegionBased(), this, results.getParameters().getAttributeName());
            table.addMouseListener(popupListener);
        }
    }

    public void unregisterSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
        if (selectionListener != null) {
            TableMotifSelectionConnector.unconnect(table, selectionListener);
            selectionListener = null;
            detailPanel.unregisterSelectionComponents();
            selectedMotif.unregisterListener(detailPanel);
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
        detailPanel.refresh();
    }
}
