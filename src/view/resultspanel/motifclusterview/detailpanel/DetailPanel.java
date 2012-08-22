package view.resultspanel.motifclusterview.detailpanel;

import domainmodel.AbstractMotif;
import domainmodel.MotifCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.*;
import view.resultspanel.TranscriptionFactorTableModel;
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
        cc.weighty = 1.0;

        final BaseMotifTableModel motifsModel = new BaseMotifTableModel();
        motifsTable = new JTable(motifsModel);
        motifsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installRenderersOnMotifsTable();
		motifsTable.setAutoCreateRowSorter(true);
        final ToolTipHeader header = new ToolTipHeader(motifsTable.getColumnModel());
		header.setToolTipStrings(motifsModel.getTooltips().toArray(new String[motifsModel.getTooltips().size()]));
	    header.setToolTipText("");
	    motifsTable.setTableHeader(header);

        cc.weightx = 2.0/5.0; cc.gridx = 0; cc.gridy = 0;
        add(new JScrollPane(motifsTable), cc);

        final TranscriptionFactorTableModel tfModel = new TranscriptionFactorTableModel();
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


        cc.weightx = 2.0/5.0; cc.gridx = 1; cc.gridy = 0;
        add(new JScrollPane(transcriptionFactorsTable), cc);

        final CandidateTargetGeneTableModel tgModel = new CandidateTargetGeneTableModel();
        targetGeneTable = new JTable(tgModel);
        targetGeneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetGeneHighlighter = new NetworkMembershipHighlightRenderer("Target Name");
        installRenderersOnTargetGeneTable();
        targetGeneTable.setAutoCreateRowSorter(true);
        final ToolTipHeader tgHeader = new ToolTipHeader(targetGeneTable.getColumnModel());
        tgHeader.setToolTipStrings(tgModel.getTooltips());
        tgHeader.setToolTipText("");
        targetGeneTable.setTableHeader(tgHeader);

        cc.weightx = 1.0/5.0; cc.gridx = 2; cc.gridy = 0;
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
			case 1 : renderer.addRenderer(new FloatRenderer("0.###E0", "N/A")); //float renderer
					break;
			case 2 : renderer.addRenderer(new FloatRenderer("0.###E0", "Direct")); //float renderer
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

    private void refresh() {
        transcriptionFactorHighlighter.setIDsToBeHighlighted(support.getCurrentIDs());
        targetGeneHighlighter.setIDsToBeHighlighted(support.getCurrentIDs());

        motifsTable.setModel(new BaseMotifTableModel(hasCurrentCluster() ? getCurrentCluster().getMotifs() : null));
        installRenderersOnMotifsTable();
        transcriptionFactorsTable.setModel(new TranscriptionFactorTableModel(getCurrentCluster()));
        installRenderersOnTranscriptionFactorsTable();
        targetGeneTable.setModel(new CandidateTargetGeneTableModel(getCurrentCluster()));
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

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        if (!hasCurrentCluster()) {
            return null;
        }
        final int rowIdx = transcriptionFactorsTable.getSelectedRow();
        if (rowIdx < 0) {
            return getCurrentCluster().getBestTranscriptionFactor();
        } else {
            final TranscriptionFactorTableModel model = (TranscriptionFactorTableModel) transcriptionFactorsTable.getModel();
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
