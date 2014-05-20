package view.resultspanel.motifview.detailpanel;

import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.LinkLabel;
import view.resultspanel.guiwidgets.LogoThumbnail;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackview.tablemodels.CandidateTargetGeneTableModel;
import view.resultspanel.motifandtrackview.tablemodels.TranscriptionFactorTableModel;
import view.resultspanel.renderers.CombinedRenderer;
import view.resultspanel.renderers.DefaultRenderer;
import view.resultspanel.renderers.FloatRenderer;
import view.resultspanel.renderers.NetworkMembershipHighlightRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;


public class DetailPanel extends JPanel implements DetailPanelIF {
    private static final String TRANSFAC_PREFIX = "transfac_";
    private static final String TRANSFAC_URL = IRegulonResourceBundle.getBundle().getString("transfac_url");

    private JTable targetGeneTable;
    private LinkLabel jlbMotif;
    private JLabel jlbDescription;
    private LogoThumbnail jlbLogo;
    private JTable transcriptionFactorTable;
    private NetworkMembershipHighlightRenderer hlcrtf;
    private NetworkMembershipHighlightRenderer hlcrtg;
    private NetworkMembershipSupport updateHLCR;

    private TFandMotifSelected tfMotif;

    private int ipadx = 150;
    private int ipady = 50;

    private ListSelectionListener selectedTranscriptionFactorListener;
    private ListSelectionListener selectedMotifListener;
    private MouseListener popupMouseListener;

    public DetailPanel(PredictRegulatorsParameters predictRegulatorsParameters) {
        super();
        this.tfMotif = new TFandMotifSelected(predictRegulatorsParameters);
        this.updateHLCR = new NetworkMembershipSupport();

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        this.jlbMotif = new LinkLabel();
        this.jlbMotif.setEnabled(true);
        this.jlbMotif.setText("");
        Dimension maximumSize = new Dimension(LogoThumbnail.THUMBNAIL_WIDTH, 1);
        this.jlbMotif.setHorizontalAlignment(JLabel.CENTER);
        this.jlbMotif.setMaximumSize(maximumSize);
        this.jlbMotif.setMinimumSize(maximumSize);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        c.weighty = 0.1;
        c.ipadx = 1;
        c.ipady = 1;
        this.add(this.jlbMotif, c);


        this.jlbDescription = new JLabel();
        this.jlbDescription.setEnabled(true);
        this.jlbDescription.setText("");
        this.jlbDescription.setHorizontalAlignment(JLabel.CENTER);
        this.jlbDescription.setMaximumSize(maximumSize);
        this.jlbDescription.setMinimumSize(maximumSize);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.3;
        c.weighty = 0.1;
        c.ipadx = 1;
        c.ipady = 1;
        this.add(this.jlbDescription, c);

        this.targetGeneTable = new JTable(new CandidateTargetGeneTableModel(null));
        this.targetGeneTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.hlcrtg = new NetworkMembershipHighlightRenderer("Target Name");
        this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridwidth = 1;
        c.gridheight = 3;
        c.ipadx = ipadx;
        c.ipady = ipady;

        this.add(new JScrollPane(targetGeneTable), c);


        this.transcriptionFactorTable = new JTable(new TranscriptionFactorTableModel(null, AbstractMotifAndTrack.TrackType.MOTIF));
        this.transcriptionFactorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.hlcrtf = new NetworkMembershipHighlightRenderer("Transcription Factor Name");
        this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridwidth = 1;
        c.gridheight = 3;
        c.ipadx = ipadx;
        c.ipady = ipady;

        this.add(new JScrollPane(transcriptionFactorTable), c);


        this.jlbLogo = new LogoThumbnail();

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.3;
        c.weighty = 0.8;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;

        this.add(jlbLogo, c);


        //tooltips in the headers of the tables

        ToolTipHeader header = new ToolTipHeader(this.transcriptionFactorTable.getColumnModel());
        TranscriptionFactorTableModelIF modelTable = (TranscriptionFactorTableModelIF) this.transcriptionFactorTable.getModel();
        header.setToolTipStrings(modelTable.getTooltips());
        header.setToolTipText("");
        this.transcriptionFactorTable.setTableHeader(header);

        header = new ToolTipHeader(this.targetGeneTable.getColumnModel());
        CandidateTargetGeneTableModelIF TGmodelTable = (CandidateTargetGeneTableModelIF) this.targetGeneTable.getModel();
        header.setToolTipStrings(TGmodelTable.getTooltips());
        header.setToolTipText("");
        this.targetGeneTable.setTableHeader(header);

        //Sorting on the table
        //this.transcriptionFactorTable.setAutoCreateRowSorter(true);
        this.targetGeneTable.setAutoCreateRowSorter(true);

    }

    @Override
    public AbstractMotif getSelectedMotifOrTrack() {
        return this.tfMotif.getMotif();
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        if (transcriptionFactorTable.getSelectedRowCount() == 0) return null;
        else {
            final int[] indices = transcriptionFactorTable.getSelectedRows();
            final TranscriptionFactorTableModelIF model = (TranscriptionFactorTableModelIF) transcriptionFactorTable.getModel();
            return model.getTranscriptionFactorAtRow(transcriptionFactorTable.convertRowIndexToModel(indices[0]));
        }
    }

    @Override
    public void registerSelectionComponents(final TranscriptionFactorComboBox tfcombobox) {
        if (selectedTranscriptionFactorListener == null) {
            selectedTranscriptionFactorListener = new SelectedTranscriptionFactorListener(this.transcriptionFactorTable, tfcombobox);
            this.transcriptionFactorTable.getSelectionModel().addListSelectionListener(selectedTranscriptionFactorListener);
        }

        if (selectedMotifListener == null) {
            selectedMotifListener = new SelectedMotifListener(this.transcriptionFactorTable, this.tfMotif);
            this.transcriptionFactorTable.getSelectionModel().addListSelectionListener(selectedMotifListener);
        }

        if (popupMouseListener == null) {
            popupMouseListener = new PopupMouseListener(this.transcriptionFactorTable, this.tfMotif);
            this.transcriptionFactorTable.addMouseListener(popupMouseListener);
        }
    }

    @Override
    public void unregisterSelectionComponents() {
        if (selectedTranscriptionFactorListener != null) {
            this.transcriptionFactorTable.getSelectionModel().removeListSelectionListener(selectedTranscriptionFactorListener);
            selectedTranscriptionFactorListener = null;
        }

        if (selectedMotifListener != null) {
            this.transcriptionFactorTable.getSelectionModel().removeListSelectionListener(selectedMotifListener);
            selectedMotifListener = null;
        }

        if (popupMouseListener != null) {
            this.transcriptionFactorTable.removeMouseListener(popupMouseListener);
            popupMouseListener = null;
        }
    }

    @Override
    public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
        this.refresh(currentSelection);
    }

    public void refresh() {
        refresh(getSelectedMotifOrTrack());
    }

    private URI composeURI(final AbstractMotifAndTrack motifOrTrack) {
        try {
            final String ID = motifOrTrack.getName().split("-")[1];
            return new URI(TRANSFAC_URL + ID);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private void refresh(AbstractMotifAndTrack motifOrTrack) {
        this.targetGeneTable.setModel(new CandidateTargetGeneTableModel(motifOrTrack));
        this.transcriptionFactorTable.setModel(new TranscriptionFactorTableModel(motifOrTrack, AbstractMotifAndTrack.TrackType.MOTIF));
        this.tfMotif.setMotif((Motif) motifOrTrack);

        if (motifOrTrack == null) {
            motifOrTrack = null;
            this.jlbMotif.disableLink("");
            this.jlbDescription.setText("");
            this.jlbDescription.setToolTipText("");
            this.jlbLogo.setMotif(null);
        } else {
            if (motifOrTrack.getName().startsWith(TRANSFAC_PREFIX)) {
                this.jlbMotif.enableLink(motifOrTrack.getName(), composeURI(motifOrTrack));
            } else {
                this.jlbMotif.disableLink(motifOrTrack.getName());
            }
            this.jlbDescription.setText(motifOrTrack.getDescription());
            this.jlbDescription.setToolTipText("Description: " + motifOrTrack.getDescription());

            this.jlbLogo.setMotif((Motif) motifOrTrack);

            //colors of the table
            refreshHighlighting();
        }

        //setting the table renderer
        for (int i = 0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++) {
            CombinedRenderer renderer = new CombinedRenderer();
            // the float renderer
            switch (i) {
                case 1:
                    renderer.addRenderer(new FloatRenderer("0.###E0", "N/A")); //float renderer
                    break;
                case 2:
                    renderer.addRenderer(new FloatRenderer("0.###E0", "Direct")); //float renderer
                    break;
                default:
                    renderer.addRenderer(new DefaultRenderer());
            }
            //the column renderer
            renderer.addRenderer(this.hlcrtf);
            TableColumn col = this.transcriptionFactorTable.getColumnModel().getColumn(i);
            col.setCellRenderer(renderer);
        }
        for (int i = 0; i < this.targetGeneTable.getModel().getColumnCount(); i++) {
            this.targetGeneTable.getColumn(this.targetGeneTable.getColumnName(i)).setCellRenderer(this.hlcrtg);
        }

        this.transcriptionFactorTable.addMouseMotionListener(new TranscriptionFactorTooltip(this.transcriptionFactorTable));

        this.invalidate();
    }

    private void refreshHighlighting() {
        this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
        this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
    }
}
