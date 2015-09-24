package view.resultspanel.motifandtrackclusterview.detailpanel;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.MotifAndTrackCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.LogoThumbnail;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackclusterview.tablemodels.ExtendedCandidateTargetGeneTableModel;
import view.resultspanel.motifandtrackclusterview.tablemodels.ExtendedTranscriptionFactorTableModel;
import view.resultspanel.motifandtrackview.tablemodels.BaseMotifAndTrackTableModel;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;


public class DetailPanel extends JPanel implements DetailPanelIF {
    private MotifAndTrackCluster currentCluster;

    private NetworkMembershipSupport support;

    private JTable motifAndTrackTable;
    private JTable transcriptionFactorsTable;
    private JTable targetGeneTable;
    private ToolTipHeader motifAndTrackTableHeader;
    private ToolTipHeader transcriptionFactorsTableHeader;
    private ToolTipHeader targetGeneTableHeader;

    private NetworkMembershipHighlightRenderer transcriptionFactorHighlighter;
    private NetworkMembershipHighlightRenderer targetGeneHighlighter;

    private ListSelectionListener transcriptionFactorSelectionListener;


    public DetailPanel() {
        super();
        this.support = new NetworkMembershipSupport();
        initPanel();
    }

    private void initPanel() {
        setLayout(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();
        cc.fill = GridBagConstraints.BOTH;
        cc.gridwidth = 1;

        final BaseMotifAndTrackTableModel motifsAndTrackModel = new BaseMotifAndTrackTableModel();
        motifAndTrackTable = new JTable(motifsAndTrackModel);
        motifAndTrackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installRenderersOnMotifsAndTrackTable();
        motifAndTrackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final ExtendedTranscriptionFactorTableModel model = (ExtendedTranscriptionFactorTableModel) transcriptionFactorsTable.getModel();
                model.setSelectedMotifAndTrack(getSelectedMotifOrTrack());
                model.fireTableDataChanged();
            }
        });
        motifAndTrackTable.setAutoCreateRowSorter(true);

        motifAndTrackTableHeader = new ToolTipHeader(motifAndTrackTable.getColumnModel());
        motifAndTrackTableHeader.setToolTipStrings(motifsAndTrackModel.getTooltips().toArray(new String[motifsAndTrackModel.getTooltips().size()]));
        motifAndTrackTableHeader.setToolTipText("");
        motifAndTrackTable.setTableHeader(motifAndTrackTableHeader);

        cc.weightx = 2.0 / 5.0;
        cc.weighty = 1.0;
        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridheight = 1;
        add(new JScrollPane(motifAndTrackTable), cc);

        cc.weightx = 2.0 / 5.0;
        cc.weighty = 0.0;
        cc.gridx = 0;
        cc.gridy = 1;
        cc.gridheight = 1;
        final LogoThumbnail thumbnail = new LogoThumbnail();
        motifAndTrackTable.getSelectionModel().addListSelectionListener(new UpdateLogoListener(motifAndTrackTable, thumbnail));
        add(thumbnail, cc);

        final TranscriptionFactorTableModelIF tfModel = new ExtendedTranscriptionFactorTableModel();
        transcriptionFactorsTable = new JTable(tfModel);
        transcriptionFactorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transcriptionFactorHighlighter = new NetworkMembershipHighlightRenderer("Transcription Factor Name");
        transcriptionFactorsTable.addMouseMotionListener(new TranscriptionFactorTooltip(transcriptionFactorsTable));
        installRenderersOnTranscriptionFactorsTable();
        transcriptionFactorsTable.setAutoCreateRowSorter(true);

        transcriptionFactorsTableHeader = new ToolTipHeader(transcriptionFactorsTable.getColumnModel());
        transcriptionFactorsTableHeader.setToolTipStrings(tfModel.getTooltips());
        transcriptionFactorsTableHeader.setToolTipText("");
        transcriptionFactorsTable.setTableHeader(transcriptionFactorsTableHeader);


        cc.weightx = 2.0 / 5.0;
        cc.weighty = 1.0;
        cc.gridx = 1;
        cc.gridy = 0;
        cc.gridheight = 2;
        add(new JScrollPane(transcriptionFactorsTable), cc);

        final CandidateTargetGeneTableModelIF tgModel = new ExtendedCandidateTargetGeneTableModel();
        targetGeneTable = new JTable(tgModel);
        targetGeneTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        targetGeneHighlighter = new NetworkMembershipHighlightRenderer("Target Name");
        installRenderersOnTargetGeneTable();
        targetGeneTable.setAutoCreateRowSorter(true);

        targetGeneTableHeader = new ToolTipHeader(targetGeneTable.getColumnModel());
        targetGeneTableHeader.setToolTipStrings(tgModel.getTooltips());
        targetGeneTableHeader.setToolTipText("");
        targetGeneTable.setTableHeader(targetGeneTableHeader);

        cc.weightx = 1.0 / 5.0;
        cc.weighty = 1.0;
        cc.gridx = 2;
        cc.gridy = 0;
        cc.gridheight = 2;
        add(new JScrollPane(targetGeneTable), cc);
    }

    private void installRenderersOnTargetGeneTable() {
        for (int i = 0; i < this.targetGeneTable.getModel().getColumnCount(); i++) {
            this.targetGeneTable.getColumn(this.targetGeneTable.getColumnName(i)).setCellRenderer(targetGeneHighlighter);
        }

        new ColumnWidthSetter(targetGeneTable).setWidth();
    }

    private void installRenderersOnTranscriptionFactorsTable() {
        for (int i = 0; i < transcriptionFactorsTable.getModel().getColumnCount(); i++) {
            final CombinedRenderer renderer = new CombinedRenderer();
            switch (i) {
                case 0:
                    renderer.addRenderer(new BooleanRenderer());
                    break;
                case 3:
                    renderer.addRenderer(new PercentageRenderer("N/A"));
                    break;
                case 4:
                    renderer.addRenderer(new FloatRenderer("0.###E0", "Direct"));
                    break;
                default:
                    renderer.addRenderer(new DefaultRenderer());
            }
            renderer.addRenderer(transcriptionFactorHighlighter);
            final TableColumn column = transcriptionFactorsTable.getColumnModel().getColumn(i);
            column.setCellRenderer(renderer);
        }

        new ColumnWidthSetter(transcriptionFactorsTable).setWidth();
    }

    private void installRenderersOnMotifsAndTrackTable() {
        for (int i = 0; i < motifAndTrackTable.getModel().getColumnCount(); i++) {
            final TableColumn column = motifAndTrackTable.getColumnModel().getColumn(i);
            if (motifAndTrackTable.getModel().getColumnClass(i).equals(Float.class)) {
                column.setCellRenderer(new FloatRenderer("0.000"));
            } else {
                column.setCellRenderer(new DefaultRenderer());
            }
        }

        new ColumnWidthSetter(motifAndTrackTable).setWidth();
    }

    public boolean hasCurrentCluster() {
        return currentCluster != null;
    }

    public MotifAndTrackCluster getCurrentCluster() {
        return currentCluster;
    }

    public void setCurrentCluster(final MotifAndTrackCluster newCluster) {
        final MotifAndTrackCluster oldCluster = this.currentCluster;
        this.currentCluster = newCluster;
        assert oldCluster != null;
        if ((oldCluster != null && newCluster == null)
                || (oldCluster == null && newCluster != null)
                || (oldCluster != null && !oldCluster.equals(newCluster))) {
            refresh();
        }
    }

    public void refresh() {
        transcriptionFactorHighlighter.setIDsToBeHighlighted(support.getCurrentIDs());
        targetGeneHighlighter.setIDsToBeHighlighted(support.getCurrentIDs());

        final BaseMotifAndTrackTableModel motifsAndTrackModel = new BaseMotifAndTrackTableModel(hasCurrentCluster() ? getCurrentCluster().getMotifsAndTracks() : null, hasCurrentCluster() ? getCurrentCluster().getTrackType() : AbstractMotifAndTrack.TrackType.MOTIF_CLUSTER);
        motifAndTrackTable.setModel(motifsAndTrackModel);

        motifAndTrackTableHeader.setToolTipStrings(motifsAndTrackModel.getTooltips().toArray(new String[motifsAndTrackModel.getTooltips().size()]));
        motifAndTrackTable.setTableHeader(motifAndTrackTableHeader);

        installRenderersOnMotifsAndTrackTable();

        final TranscriptionFactorTableModelIF tfModel = new ExtendedTranscriptionFactorTableModel(hasCurrentCluster() ? getCurrentCluster() : null);
        transcriptionFactorsTable.setModel(tfModel);
        transcriptionFactorsTableHeader.setToolTipStrings(tfModel.getTooltips());
        transcriptionFactorsTable.setTableHeader(transcriptionFactorsTableHeader);

        installRenderersOnTranscriptionFactorsTable();

        final ExtendedCandidateTargetGeneTableModel tgModel = new ExtendedCandidateTargetGeneTableModel(hasCurrentCluster() ? getCurrentCluster() : null);
        targetGeneTable.setModel(tgModel);
        targetGeneTableHeader.setToolTipStrings(tgModel.getTooltips());
        targetGeneTable.setTableHeader(targetGeneTableHeader);
        installRenderersOnTargetGeneTable();
        setSelectedMotifOrTrack(getCurrentCluster());
    }

    @Override
    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        if (!hasCurrentCluster()) {
            return null;
        }
        final int rowIdx = motifAndTrackTable.getSelectedRow();
        if (rowIdx < 0) {
            return getCurrentCluster();
        } else {
            final MotifAndTrackTableModel model = (MotifAndTrackTableModel) motifAndTrackTable.getModel();
            return model.getMotifOrTrackAtRow(motifAndTrackTable.convertRowIndexToModel(rowIdx));
        }
    }

    public void setSelectedMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        final int rowIdx = findModelIndexForMotifOrTrack(motifOrTrack);
        if (rowIdx < 0) {
            motifAndTrackTable.getSelectionModel().clearSelection();
        } else {
            final int viewIdx = motifAndTrackTable.convertRowIndexToModel(rowIdx);
            motifAndTrackTable.getSelectionModel().setSelectionInterval(viewIdx, viewIdx);
        }
    }

    private int findModelIndexForMotifOrTrack(final AbstractMotifAndTrack motifOrTrack) {
        if (motifOrTrack == null) return -1;
        final MotifAndTrackTableModel model = (MotifAndTrackTableModel) motifAndTrackTable.getModel();
        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
            if (model.getMotifOrTrackAtRow(rowIndex).getDatabaseID() == motifOrTrack.getDatabaseID()) {
                return rowIndex;
            }
        }
        return -1;
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        if (!hasCurrentCluster()) {
            return null;
        }
        final int rowIdx = transcriptionFactorsTable.getSelectedRow();
        if (rowIdx < 0) {
            return getCurrentCluster().getBestTranscriptionFactor();
        } else {
            final TranscriptionFactorTableModelIF model = (TranscriptionFactorTableModelIF) transcriptionFactorsTable.getModel();
            return model.getTranscriptionFactorAtRow(transcriptionFactorsTable.convertRowIndexToModel(rowIdx));
        }
    }

    @Override
    public void registerSelectionComponents(TranscriptionFactorComboBox tfcombobox) {
        if (transcriptionFactorSelectionListener == null) {
            transcriptionFactorSelectionListener = new TranscriptionFactorSelectionListener(tfcombobox);
            transcriptionFactorsTable.getSelectionModel().addListSelectionListener(transcriptionFactorSelectionListener);
        }
    }

    @Override
    public void unregisterSelectionComponents() {
        if (transcriptionFactorSelectionListener != null) {
            transcriptionFactorsTable.getSelectionModel().removeListSelectionListener(transcriptionFactorSelectionListener);
            transcriptionFactorSelectionListener = null;
        }
    }

    @Override
    public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
        setCurrentCluster((MotifAndTrackCluster) currentSelection);
    }

    private class TranscriptionFactorSelectionListener implements ListSelectionListener {
        private final TranscriptionFactorComboBox comboBox;

        private TranscriptionFactorSelectionListener(TranscriptionFactorComboBox comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            this.comboBox.setSelectedItem(getSelectedTranscriptionFactor());
        }
    }
}
