package view.resultspanel.transcriptionfactorview;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableColumn;

import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.renderers.BooleanRenderer;
import view.resultspanel.renderers.ColumnWidthSetter;
import view.resultspanel.renderers.FloatRenderer;
import view.resultspanel.transcriptionfactorview.tablemodels.BaseEnrichedTranscriptionFactorTableModel;
import view.resultspanel.transcriptionfactorview.tablemodels.FilterEnrichedTranscriptionFactorTableModel;


public class EnrichedTranscriptionFactorsView extends JPanel implements MotifView {
    private JTable table;
    private final MotifViewSupport support;

    private final Results results;
    private final List<EnrichedTranscriptionFactor> transcriptionFactors;
    private FilterAttributeActionListener filterAttributeActionListener;
    private FilterPatternDocumentListener filterPatternDocumentListener;

    public EnrichedTranscriptionFactorsView(final Results results) {
        this.support = new MotifViewSupport(this);
        this.results = results;
        //TODO: CHange this towards MotifCluster objects and only display best TF and NEScore in the table.
		this.transcriptionFactors = transform(results);
        setLayout(new BorderLayout());
	}

    public Results getResults() {
        return results;
    }

    private List<EnrichedTranscriptionFactor> transform(final Results results) {
        final List<EnrichedTranscriptionFactor> transcriptionFactors = new ArrayList<EnrichedTranscriptionFactor>();
        for (Motif motif: results.getMotifs()) {
            for (TranscriptionFactor tf: motif.getTranscriptionFactors()){
                transcriptionFactors.add(new EnrichedTranscriptionFactor(tf, motif));
            }
        }
        Collections.sort(transcriptionFactors);
        return Collections.unmodifiableList(transcriptionFactors);
    }

    public List<EnrichedTranscriptionFactor> getTranscriptionFactors() {
        return transcriptionFactors;
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
        final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final MotifTableModel model = (MotifTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx);
		}
    }

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB) {
		final FilterEnrichedTranscriptionFactorTableModel model = new FilterEnrichedTranscriptionFactorTableModel(
                new BaseEnrichedTranscriptionFactorTableModel(this.transcriptionFactors),
                FilterAttribute.TRANSCRIPTION_FACTOR, "");
        table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        TableMotifSelectionConnector.connect(table, selectedMotif, transcriptionFactorCB);
        table.addMouseListener(new MotifPopUpMenu(selectedMotif, transcriptionFactorCB, getResults().isRegionBased()));

        final ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
        header.setToolTipStrings(model.getTooltips().toArray(new String[model.getTooltips().size()]));
        header.setToolTipText("");
        table.setTableHeader(header);

        table.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        for (Enumeration<TableColumn> e = table.getColumnModel().getColumns(); e.hasMoreElements();) {
            final TableColumn column = e.nextElement();
            switch (column.getModelIndex()) {
                case 4:
                    column.setCellRenderer(new FloatRenderer("0.##"));
                    break;
                case 5:
                    column.setCellRenderer(new FloatRenderer("0.###E0", "Not applicable"));
                    break;
                case 8:
                    column.setCellRenderer(new FloatRenderer("0.###E0", "Direct"));
                    break;
            }
        }

        final ColumnWidthSetter columnWidth = new ColumnWidthSetter(table);
		columnWidth.setWidth();

        add(new JScrollPane(table), BorderLayout.CENTER);

        //TODO: Add detail panel with other TFs, the enriched motifs in the clusters and the combined target genes ...
        return this;
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
