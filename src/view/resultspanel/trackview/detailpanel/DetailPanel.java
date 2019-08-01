package view.resultspanel.trackview.detailpanel;

import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.LinkTextField;
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
import java.util.ResourceBundle;


public class DetailPanel extends JPanel implements DetailPanelIF {
    private static final ResourceBundle RESOURCE_BUNDLE = IRegulonResourceBundle.getBundle();

    private static final String ENCODE_EXPERIMENT_PREFIX = "ENCFF";
    private static final String ENCODE_EXPERIMENTS_URL = RESOURCE_BUNDLE.getString("encode_experiments_url");

    private JTable targetGeneTable;
    private LinkTextField jtfTrack;
    private JTextField jtfDescription;
    private JTable transcriptionFactorTable;
    private NetworkMembershipHighlightRenderer hlcrtf;
    private NetworkMembershipHighlightRenderer hlcrtg;
    private NetworkMembershipSupport updateHLCR;

    private TFandTrackSelected tfTrack;

    private int ipadx = 150;
    private int ipady = 50;

    private ListSelectionListener selectedTranscriptionFactorListener;
    private ListSelectionListener selectedMotifListener;
    private MouseListener popupMouseListener;

    public DetailPanel(PredictRegulatorsParameters predictRegulatorsParameters) {
        super();
        this.tfTrack = new TFandTrackSelected(predictRegulatorsParameters);
        this.updateHLCR = new NetworkMembershipSupport();

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;


        Dimension maximumSize = new Dimension(LogoThumbnail.THUMBNAIL_WIDTH, 15);


        /* Track name with eventually link to a website. */
        this.jtfTrack = new LinkTextField();
        this.jtfTrack.setBorder(null);
        this.jtfTrack.setEditable(false);
        this.jtfTrack.setEnabled(true);
        this.jtfTrack.setOpaque(false);
        this.jtfTrack.setBackground(new Color(0, 0, 0, 0));
        this.jtfTrack.setForeground(Color.BLACK);
        this.jtfTrack.setHorizontalAlignment(JLabel.CENTER);
        this.jtfTrack.setMaximumSize(maximumSize);
        this.jtfTrack.setMinimumSize(maximumSize);
        this.jtfTrack.setText("");

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        c.weighty = 0.1;
        c.ipadx = 1;
        c.ipady = 1;
        this.add(this.jtfTrack, c);


        /* Track description. */
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


        /* Space filler so track name and description are shown close enough together. */
        JLabel jlbSpaceFiller = new JLabel();

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.3;
        c.weighty = 0.8;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;

        this.add(jlbSpaceFiller, c);


        /* List of transcription factors associated with the current selected track. */
        this.transcriptionFactorTable = new JTable(new TranscriptionFactorTableModel(null, AbstractMotifAndTrack.TrackType.TRACK));
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


        /* Set tooltips in the header of the target gene list table. */
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
    public AbstractTrack getSelectedMotifOrTrack() {
        return this.tfTrack.getTrack();
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
            selectedMotifListener = new SelectedTrackListener(this.transcriptionFactorTable, this.tfTrack);
            this.transcriptionFactorTable.getSelectionModel().addListSelectionListener(selectedMotifListener);
        }

        if (popupMouseListener == null) {
            popupMouseListener = new PopupMouseListener(this.transcriptionFactorTable, this.tfTrack);
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

    private URI createTrackURI(final Track track) {
        try {
            if (track.getName().startsWith(ENCODE_EXPERIMENT_PREFIX)) {
                return new URI(ENCODE_EXPERIMENTS_URL + track.getName() + "/");
            }
        } catch (URISyntaxException e) {
            return null;
        }

        return null;
    }

    private void refresh(AbstractMotifAndTrack motifOrTrack) {
        this.targetGeneTable.setModel(new CandidateTargetGeneTableModel(motifOrTrack));
        this.transcriptionFactorTable.setModel(new TranscriptionFactorTableModel(motifOrTrack, AbstractMotifAndTrack.TrackType.TRACK));
        final Track track = (Track) motifOrTrack;
        this.tfTrack.setTrack(track);

        if (motifOrTrack == null) {
            this.jtfTrack.disableLink("");
            this.jtfDescription.setText("");
            this.jtfDescription.setToolTipText("");
        } else {
            this.jtfTrack.disableLink(motifOrTrack.getName());
            if (motifOrTrack.getTrackType() == AbstractMotifAndTrack.TrackType.TRACK) {
                URI trackURI = createTrackURI(track);

                if (trackURI != null) {
                    this.jtfTrack.enableLink(track.getName(), trackURI);
                } else {
                    this.jtfTrack.disableLink(track.getName());
                }
            } else {
                this.jtfTrack.disableLink(motifOrTrack.getName());
            }

            this.jtfDescription.setText(motifOrTrack.getDescription());
            this.jtfDescription.setToolTipText("Description: " + motifOrTrack.getDescription());

            /* Refresh highlighting. */
            refreshHighlighting();
        }

        /* Set table renderer */
        for (int i = 0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++) {
            CombinedRenderer renderer = new CombinedRenderer();
            /* Float renderer. */
            switch (i) {
                case 1:
                    renderer.addRenderer(new FloatRenderer("0.###E0", "N/A"));
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
