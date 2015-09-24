package view.resultspanel.motifview.detailpanel;

import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.LinkTextField;
import view.resultspanel.guiwidgets.LogoThumbnail;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackview.tablemodels.CandidateTargetGeneTableModel;
import view.resultspanel.motifandtrackview.tablemodels.TranscriptionFactorTableModel;
import view.resultspanel.renderers.*;

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
    private LinkTextField jtfMotif;
    private JTextField jtfDescription;
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


        Dimension maximumSize = new Dimension(LogoThumbnail.THUMBNAIL_WIDTH, 15);

        /* Motif name with eventually link to a website. */
        this.jtfMotif = new LinkTextField();
        this.jtfMotif.setBorder(null);
        this.jtfMotif.setEditable(false);
        this.jtfMotif.setEnabled(true);
        this.jtfMotif.setOpaque(false);
        this.jtfMotif.setBackground(new Color(0, 0, 0, 0));
        this.jtfMotif.setForeground(Color.BLACK);
        this.jtfMotif.setHorizontalAlignment(JLabel.CENTER);
        this.jtfMotif.setMaximumSize(maximumSize);
        this.jtfMotif.setMinimumSize(maximumSize);
        this.jtfMotif.setText("");

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        c.weighty = 0.1;
        c.ipadx = 1;
        c.ipady = 1;
        this.add(this.jtfMotif, c);


        /* Motif description. */
        this.jtfDescription = new JTextField();
        this.jtfDescription.setBorder(null);
        this.jtfDescription.setEditable(false);
        this.jtfDescription.setEnabled(true);
        this.jtfDescription.setOpaque(false);
        this.jtfDescription.setBackground(new Color(0, 0, 0, 0));
        this.jtfDescription.setForeground(Color.BLACK);
        this.jtfDescription.setHorizontalAlignment(JLabel.CENTER);
        this.jtfDescription.setMaximumSize(maximumSize);
        this.jtfDescription.setMinimumSize(maximumSize);
        this.jtfDescription.setText("");

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.3;
        c.weighty = 0.1;
        c.ipadx = 1;
        c.ipady = 1;
        this.add(this.jtfDescription, c);


        /* Logo */
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


        /* List of transcription factors associated with the current selected motif. */
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

        /* Set tooltips in the header of the table with associated transcription factors. */
        ToolTipHeader header = new ToolTipHeader(this.transcriptionFactorTable.getColumnModel());
        TranscriptionFactorTableModelIF modelTable = (TranscriptionFactorTableModelIF) this.transcriptionFactorTable.getModel();
        header.setToolTipStrings(modelTable.getTooltips());
        header.setToolTipText("");
        this.transcriptionFactorTable.setTableHeader(header);


        /* Target gene list for the current selected motif. */
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

        /* Set tooltips in the header of the target gene list table. */
        header = new ToolTipHeader(this.targetGeneTable.getColumnModel());
        CandidateTargetGeneTableModelIF TGmodelTable = (CandidateTargetGeneTableModelIF) this.targetGeneTable.getModel();
        header.setToolTipStrings(TGmodelTable.getTooltips());
        header.setToolTipText("");
        this.targetGeneTable.setTableHeader(header);

        /* Sort the target genes table on rank. */
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
        final int motifOrTrackLength = motifOrTrack.getName().length();
        try {
            /* Extract the matrix ID from the TRANSFAC motif name and add it to the URL. */
            return new URI(TRANSFAC_URL + motifOrTrack.getName().substring(
                    motifOrTrackLength - 6, motifOrTrackLength));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private void refresh(AbstractMotifAndTrack motifOrTrack) {
        this.targetGeneTable.setModel(new CandidateTargetGeneTableModel(motifOrTrack));
        this.transcriptionFactorTable.setModel(new TranscriptionFactorTableModel(motifOrTrack, AbstractMotifAndTrack.TrackType.MOTIF));
        this.tfMotif.setMotif((Motif) motifOrTrack);

        if (motifOrTrack == null) {
            this.jtfMotif.disableLink("");
            this.jtfDescription.setText("");
            this.jtfDescription.setToolTipText("");
            this.jlbLogo.setMotif(null);
        } else {
            if (motifOrTrack.getName().startsWith(TRANSFAC_PREFIX)) {
                this.jtfMotif.enableLink(motifOrTrack.getName(), composeURI(motifOrTrack));
            } else {
                this.jtfMotif.disableLink(motifOrTrack.getName());
            }

            this.jtfDescription.setText(motifOrTrack.getDescription());
            this.jtfDescription.setToolTipText("Description: " + motifOrTrack.getDescription());

            this.jlbLogo.setMotif((Motif) motifOrTrack);

            /* Refresh highlighting. */
            refreshHighlighting();
        }

        /* Set table renderer. */
        for (int i = 0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++) {
            CombinedRenderer renderer = new CombinedRenderer();

            switch (i) {
                case 1:
                    renderer.addRenderer(new PercentageRenderer("N/A"));
                    break;
                case 2:
                    renderer.addRenderer(new FloatRenderer("0.###E0", "Direct"));
                    break;
                default:
                    renderer.addRenderer(new DefaultRenderer());
            }

            /* Column renderer. */
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
