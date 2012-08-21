package view.resultspanel;


import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFilterMotifTableModel extends AbstractTableModel implements MotifTableModel {
    private static final String FILTER_COLUMN_NAME = "Filtered";
    private static final String FILTER_COLUMN_TOOLTIP = "Check if the filter expression is found in this motif (the motif, target or transcription factor), cross otherwise";
    private static final int FILTER_COLUMN_IMPORTANCE = 3;
    protected final MotifTableModel model;
    protected String pattern;
    protected FilterAttribute filterAttribute;

    public AbstractFilterMotifTableModel(FilterAttribute filterOn, MotifTableModel model, String pattern) {
        this.filterAttribute = filterOn;
        this.model = model;
        this.pattern = pattern;
    }

    public MotifTableModel getModel() {
        return model;
    }

    public FilterAttribute getFilterAttribute(){
        return this.filterAttribute;
    }

    public void setFilterAttribute(FilterAttribute filter){
        this.filterAttribute = filter;
        this.fireTableDataChanged();
    }

    public String getPattern(){
        return this.pattern;
    }

    public void setPattern(String pattern){
        this.pattern = (pattern == null) ? "": pattern;
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int col){
        if (col == 0) return FILTER_COLUMN_NAME;
        else return model.getColumnName(col-1);
    }

    @Override
    public int getRowCount() {
        return this.model.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return this.model.getColumnCount() + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
        case 0 : return this.hasPattern(rowIndex);
        default : return this.model.getValueAt(rowIndex, columnIndex-1);
        }
    }

    protected abstract boolean hasPattern(int rowIndex);

    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch(columnIndex){
        case 0: return Boolean.class;
        default: return this.model.getColumnClass(columnIndex - 1);
        }
    }

    public AbstractMotif getMotifAtRow(int row) {
        return this.model.getMotifAtRow(row);
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int row) {
        return this.model.getTranscriptionFactorAtRow(row);
    }

    public List<Integer> getColumnImportances() {
        final List<Integer> importances = new ArrayList<Integer>(model.getColumnImportances());
        importances.add(0, FILTER_COLUMN_IMPORTANCE);
        return importances;
    }

    public List<String> getTooltips() {
        final List<String> tooltips = new ArrayList<String>(model.getTooltips());
        tooltips.add(0, FILTER_COLUMN_TOOLTIP);
        return tooltips;
    }
}
