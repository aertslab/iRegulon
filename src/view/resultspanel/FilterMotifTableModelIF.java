package view.resultspanel;


public interface FilterMotifTableModelIF extends MotifTableModel {

    FilterAttribute getFilterAttribute();

    void setFilterAttribute(FilterAttribute filter);

    String getPattern();

    void setPattern(String pattern);
}
