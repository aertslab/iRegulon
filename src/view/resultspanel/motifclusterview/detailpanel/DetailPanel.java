package view.resultspanel.motifclusterview.detailpanel;

import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.MotifCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.motifclusterview.tablemodels.ExtendedCandidateTargetGeneTableModel;
import view.resultspanel.motifclusterview.tablemodels.ExtendedTranscriptionFactorTableModel;
import view.resultspanel.LogoThumbnail;
import view.resultspanel.motifview.tablemodels.BaseMotifTableModel;
import view.resultspanel.renderers.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;


public class DetailPanel extends JPanel implements DetailPanelIF {
    private MotifCluster currentCluster;

    private NetworkMembershipSupport support;

    private JTable motifsTable;
    private JTable transcriptionFactorsTable;
    private JTable targetGeneTable;

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
        cc.weighty = 1.0; cc.gridwidth = 1;

        final BaseMotifTableModel motifsModel = new BaseMotifTableModel();
        motifsTable = new JTable(motifsModel);
        motifsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installRenderersOnMotifsTable();
        motifsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final ExtendedTranscriptionFactorTableModel model = (ExtendedTranscriptionFactorTableModel) transcriptionFactorsTable.getModel();
                model.setSelectedMotif(getSelectedMotif());
                model.fireTableDataChanged();
            }
        });
		motifsTable.setAutoCreateRowSorter(true);
        final ToolTipHeader header = new ToolTipHeader(motifsTable.getColumnModel());
		header.setToolTipStrings(motifsModel.getTooltips().toArray(new String[motifsModel.getTooltips().size()]));
	    header.setToolTipText("");
	    motifsTable.setTableHeader(header);

        cc.weightx = 2.0/5.0; cc.gridx = 0; cc.gridy = 0; cc.gridheight = 1;
        add(new JScrollPane(motifsTable), cc);

        cc.weightx = 2.0/5.0; cc.gridx = 0; cc.gridy = 1; cc.gridheight = 1;
        final LogoThumbnail thumbnail = new LogoThumbnail();
        add(thumbnail, cc);

        motifsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int rowIdx = motifsTable.getSelectedRow();
                if (rowIdx < 0) {
                    thumbnail.setMotif(null);
                } else {
                    final MotifTableModel model = (MotifTableModel) motifsTable.getModel();
                    thumbnail.setMotif((Motif) model.getMotifAtRow(motifsTable.convertRowIndexToModel(rowIdx)));
                }
            }
        });

        final TranscriptionFactorTableModelIF tfModel = new ExtendedTranscriptionFactorTableModel();
        transcriptionFactorsTable = new JTable(tfModel);
        transcriptionFactorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transcriptionFactorHighlighter = new NetworkMembershipHighlightRenderer("Transcription Factor Name");
        transcriptionFactorsTable.addMouseMotionListener(new TranscriptionFactorTooltip(transcriptionFactorsTable));
        installRenderersOnTranscriptionFactorsTable();
        transcriptionFactorsTable.setAutoCreateRowSorter(true);
        final ToolTipHeader tfHeader = new ToolTipHeader(transcriptionFactorsTable.getColumnModel());
        tfHeader.setToolTipStrings(tfModel.getTooltips());
        tfHeader.setToolTipText("");
        transcriptionFactorsTable.setTableHeader(tfHeader);


        cc.weightx = 2.0/5.0; cc.gridx = 1; cc.gridy = 0; cc.gridheight = 2;
        add(new JScrollPane(transcriptionFactorsTable), cc);

        final CandidateTargetGeneTableModelIF tgModel = new ExtendedCandidateTargetGeneTableModel();
        targetGeneTable = new JTable(tgModel);
        targetGeneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetGeneHighlighter = new NetworkMembershipHighlightRenderer("Target Name");
        installRenderersOnTargetGeneTable();
        targetGeneTable.setAutoCreateRowSorter(true);
        final ToolTipHeader tgHeader = new ToolTipHeader(targetGeneTable.getColumnModel());
        tgHeader.setToolTipStrings(tgModel.getTooltips());
        tgHeader.setToolTipText("");
        targetGeneTable.setTableHeader(tgHeader);

        cc.weightx = 1.0/5.0; cc.gridx = 2; cc.gridy = 0; cc.gridheight = 2;
        add(new JScrollPane(targetGeneTable), cc);
    }

    private void installRenderersOnTargetGeneTable() {
        for (int i=0; i < this.targetGeneTable.getModel().getColumnCount(); i++){
			this.targetGeneTable.getColumn(this.targetGeneTable.getColumnName(i)).setCellRenderer(targetGeneHighlighter);
		}

        new ColumnWidthSetter(targetGeneTable).setWidth();
    }

    private void installRenderersOnTranscriptionFactorsTable() {
        for (int i=0; i < transcriptionFactorsTable.getModel().getColumnCount(); i++){
			final CombinedRenderer renderer = new CombinedRenderer();
			switch(i){
            case 0 : renderer.addRenderer(new BooleanRenderer()); //float renderer
                    break;
			case 3 : renderer.addRenderer(new FloatRenderer("0.###E0", "N/A")); //float renderer
					break;
			case 4 : renderer.addRenderer(new FloatRenderer("0.###E0", "Direct")); //float renderer
					break;
			default : renderer.addRenderer(new DefaultRenderer());
			}
			renderer.addRenderer(transcriptionFactorHighlighter);
			final TableColumn column = transcriptionFactorsTable.getColumnModel().getColumn(i);
			column.setCellRenderer(renderer);
		}

        new ColumnWidthSetter(transcriptionFactorsTable).setWidth();
    }

    private void installRenderersOnMotifsTable() {
        for (int i = 0; i < motifsTable.getModel().getColumnCount(); i++) {
            final TableColumn column = motifsTable.getColumnModel().getColumn(i);
            if (motifsTable.getModel().getColumnClass(i).equals(Float.class)) {
                column.setCellRenderer(new FloatRenderer("0.000"));
            } else {
                column.setCellRenderer(new DefaultRenderer());
            }
        }

        new ColumnWidthSetter(motifsTable).setWidth();
    }

    public boolean hasCurrentCluster() {
        return currentCluster != null;
    }

    public MotifCluster getCurrentCluster() {
        return currentCluster;
    }

    public void setCurrentCluster(final MotifCluster newCluster) {
        final MotifCluster oldCluster = this.currentCluster;
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

        motifsTable.setModel(new BaseMotifTableModel(hasCurrentCluster() ? getCurrentCluster().getMotifs() : null));
        installRenderersOnMotifsTable();
        transcriptionFactorsTable.setModel(new ExtendedTranscriptionFactorTableModel(getCurrentCluster()));
        installRenderersOnTranscriptionFactorsTable();
        targetGeneTable.setModel(new ExtendedCandidateTargetGeneTableModel(getCurrentCluster()));
        installRenderersOnTargetGeneTable();
    }

    @Override
    public AbstractMotif getSelectedMotif() {
        if (!hasCurrentCluster()) {
            return null;
        }
        final int rowIdx = motifsTable.getSelectedRow();
        if (rowIdx < 0) {
            return getCurrentCluster();
        } else {
            final MotifTableModel model = (MotifTableModel) motifsTable.getModel();
            return model.getMotifAtRow(motifsTable.convertRowIndexToModel(rowIdx));
        }
    }

    public void setSelectedMotif(final AbstractMotif motif) {
        final int rowIdx = findModelIndexForMotif(motif);
        if (rowIdx < 0) {
            motifsTable.getSelectionModel().clearSelection();
        } else {
            final int viewIdx = motifsTable.convertRowIndexToModel(rowIdx);
            motifsTable.getSelectionModel().setSelectionInterval(viewIdx, viewIdx);
        }
    }

    private int findModelIndexForMotif(final AbstractMotif motif) {
        if (motif == null) return -1;
        final MotifTableModel model = (MotifTableModel) motifsTable.getModel();
        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
            if (model.getMotifAtRow(rowIndex).getDatabaseID() == motif.getDatabaseID()) {
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
    public void registerSelectionComponents(TFComboBox tfcombobox) {
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
    public void newMotifSelected(AbstractMotif currentSelection) {
        setCurrentCluster((MotifCluster) currentSelection);
    }

    private class TranscriptionFactorSelectionListener implements ListSelectionListener {
        private final TFComboBox comboBox;

        private TranscriptionFactorSelectionListener(TFComboBox comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            this.comboBox.setSelectedItem(getSelectedTranscriptionFactor());
        }
    }
}
