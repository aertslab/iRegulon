package view.resultspanel;


import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;

import javax.swing.*;


public class MotifViewSupport {
    private final MotifView view;

    public MotifViewSupport(MotifView view) {
        this.view = view;
    }

    public MotifView getView() {
        return view;
    }

    public AbstractMotif getSelectedMotif() {
        final int selectedRowIndex = getView().getMasterTable().getSelectedRow();
		if (selectedRowIndex < 0) {
			return null;
		} else {
            final MotifTableModel model = getView().getModel();
			final int modelRowIdx = getView().getMasterTable().convertRowIndexToModel(selectedRowIndex);
			return model.getMotifAtRow(modelRowIdx);
		}
    }

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        final AbstractMotif motif = getSelectedMotif();
        if (motif == null) return null;

        final TranscriptionFactor transcriptionFactor = getView().getDetailPanel().getSelectedTranscriptionFactor();
        if (transcriptionFactor != null) return transcriptionFactor;
        else return getSelectedMotif().getBestTranscriptionFactor();
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
