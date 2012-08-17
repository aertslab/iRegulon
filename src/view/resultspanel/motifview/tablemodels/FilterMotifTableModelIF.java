package view.resultspanel.motifview.tablemodels;


import view.resultspanel.FilterAttribute;

public interface FilterMotifTableModelIF extends MotifTableModel {

    FilterAttribute getFilterAttribute();

    void setFilterAttribute(FilterAttribute filter);

    String getPattern();

    void setPattern(String pattern);
}
