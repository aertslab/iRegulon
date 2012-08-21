package view.resultspanel;


import domainmodel.AbstractMotif;
import javax.swing.*;


public class MotifViewSupport {
    private final MotifView view;

    public MotifViewSupport(MotifView view) {
        this.view = view;
    }

    public MotifView getView() {
        return view;
    }

    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        final AbstractFilterMotifTableModel filteredModel = getView().getModel();

        filteredModel.setFilterAttribute((FilterAttribute) filterAttributeCB.getSelectedItem());
        filteredModel.setPattern(filterValueTF.getText());

        getView().setFilterAttributeListener(new FilterAttributeActionListener(filteredModel));
        filterAttributeCB.addActionListener(getView().getFilterAttributeListener());

        getView().setFilterPatternListener(new FilterPatternDocumentListener(filteredModel));
        filterValueTF.getDocument().addDocumentListener(getView().getFilterPatternListener());

        filteredModel.fireTableDataChanged();
    }

    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        if (getView().getFilterAttributeListener() != null) {
            filterAttributeCB.removeActionListener(getView().getFilterAttributeListener());
            getView().setFilterAttributeListener(null);
        }
        if (getView().getFilterPatternListener() != null) {
            filterValueTF.getDocument().removeDocumentListener(getView().getFilterPatternListener());
            getView().setFilterPatternListener(null);
        }
    }
}
