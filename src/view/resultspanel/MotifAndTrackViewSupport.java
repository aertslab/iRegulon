package view.resultspanel;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;

import javax.swing.*;


public class MotifAndTrackViewSupport {
    private final MotifAndTrackView view;

    public MotifAndTrackViewSupport(MotifAndTrackView view) {
        this.view = view;
    }

    public MotifAndTrackView getView() {
        return view;
    }

    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        final int selectedRowIndex = getView().getMasterTable().getSelectedRow();
        if (selectedRowIndex < 0) {
            return null;
        } else {
            final MotifAndTrackTableModel model = getView().getModel();
            final int modelRowIdx = getView().getMasterTable().convertRowIndexToModel(selectedRowIndex);
            return model.getMotifOrTrackAtRow(modelRowIdx);
        }
    }

    public void setSelectedMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        final int modelIdx = findModelIndexForMotif(motifOrTrack);
        if (modelIdx < 0) getView().getMasterTable().getSelectionModel().clearSelection();
        else {
            final int viewIdx = getView().getMasterTable().convertRowIndexToView(modelIdx);
            getView().getMasterTable().getSelectionModel().setSelectionInterval(viewIdx, viewIdx);
        }
    }

    private int findModelIndexForMotif(final AbstractMotifAndTrack motifOrTrack) {
        if (motifOrTrack == null) return -1;
        final MotifAndTrackTableModel model = (MotifAndTrackTableModel) getView().getMasterTable().getModel();
        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
            if (model.getMotifOrTrackAtRow(rowIndex).getDatabaseID() == motifOrTrack.getDatabaseID()) {
                return rowIndex;
            }
        }
        return -1;
    }

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        final AbstractMotifAndTrack motifOrTrack = getSelectedMotifOrTrack();
        if (motifOrTrack == null) return null;

        final TranscriptionFactor transcriptionFactor = getView().getDetailPanel().getSelectedTranscriptionFactor();
        if (transcriptionFactor != null) return transcriptionFactor;
        else return getSelectedMotifOrTrack().getBestTranscriptionFactor();
    }

    public void registerFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF) {
        final AbstractFilterMotifAndTrackTableModel filteredModel = getView().getModel();

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
