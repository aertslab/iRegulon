package view.resultspanel.trackview.detailpanel;


import domainmodel.*;
import view.resultspanel.*;
import view.resultspanel.guiwidgets.LinkLabel;
import view.resultspanel.guiwidgets.LogoThumbnail;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.motifandtrackview.tablemodels.CandidateTargetGeneTableModel;
import view.resultspanel.motifandtrackview.tablemodels.TranscriptionFactorTableModel;
import view.resultspanel.renderers.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

public class DetailPanel extends JPanel implements DetailPanelIF {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("iRegulon");
    private static final String TRANSFAC_PREFIX = "transfac_pro-";
    private static final String TRANSFAC_URL = BUNDLE.getString("transfac_pro_url");

	private JTable targetGeneTable;
	private LinkLabel jlbTrack;
	private JLabel jlbDescription;
	private LogoThumbnail jlbLogo;
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

	public DetailPanel(InputParameters input){
		super();
		this.tfTrack = new TFandTrackSelected(input);
		this.updateHLCR = new NetworkMembershipSupport();

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		this.jlbTrack = new LinkLabel();
		this.jlbTrack.setEnabled(true);
		this.jlbTrack.setText("");
		Dimension maximumSize = new Dimension(LogoThumbnail.THUMBNAIL_WIDTH, 1);
		this.jlbTrack.setHorizontalAlignment(JLabel.CENTER);
		this.jlbTrack.setMaximumSize(maximumSize);
		this.jlbTrack.setMinimumSize(maximumSize);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0.3;
		c.weighty=0.1;
		c.ipadx = 1;
		c.ipady = 1;
		this.add(this.jlbTrack, c);
		
		
		this.jlbDescription = new JLabel();
		this.jlbDescription.setEnabled(true);
		this.jlbDescription.setText("");
		this.jlbDescription.setHorizontalAlignment(JLabel.CENTER);
		this.jlbDescription.setMaximumSize(maximumSize);
		this.jlbDescription.setMinimumSize(maximumSize);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx=0.3;
		c.weighty=0.1;
		c.ipadx = 1;
		c.ipady = 1;
		this.add(this.jlbDescription, c);

		this.targetGeneTable = new JTable(new CandidateTargetGeneTableModel(null));
        this.targetGeneTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.hlcrtg=new NetworkMembershipHighlightRenderer("Target Name");
		this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
		
		c.gridx = 2;
		c.gridy = 0;
		c.weightx=0.3;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.ipadx = ipadx;
		c.ipady = ipady;
		
		this.add(new JScrollPane(targetGeneTable), c);
		
		
		this.transcriptionFactorTable = new JTable(new TranscriptionFactorTableModel(null, AbstractMotifAndTrack.TrackType.TRACK));
		this.transcriptionFactorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.hlcrtf=new NetworkMembershipHighlightRenderer("Transcription Factor Name");
		this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
		c.gridx = 1;
		c.gridy = 0;
		c.weightx=0.3;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.ipadx = ipadx;
		c.ipady = ipady;
		
		this.add(new JScrollPane(transcriptionFactorTable), c);
		
		
		this.jlbLogo = new LogoThumbnail();
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx=0.3;
		c.weighty=0.8;
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
		this.transcriptionFactorTable.setModel(new TranscriptionFactorTableModel(motifOrTrack, AbstractMotifAndTrack.TrackType.TRACK));
		this.tfTrack.setTrack((Track) motifOrTrack);
		
		if (motifOrTrack == null) {
			motifOrTrack = null;
			this.jlbTrack.disableLink("");
			this.jlbDescription.setText("");
			this.jlbDescription.setToolTipText("");
			this.jlbLogo.setMotif(null);
		} else {
            if (motifOrTrack.getName().startsWith(TRANSFAC_PREFIX)) {
			    this.jlbTrack.enableLink(motifOrTrack.getName(), composeURI(motifOrTrack));
            } else {
                this.jlbTrack.disableLink(motifOrTrack.getName());
            }
			this.jlbDescription.setText(motifOrTrack.getDescription());
			this.jlbDescription.setToolTipText("Description: " + motifOrTrack.getDescription());

			//this.jlbLogo.set((Track) motifOrTrack);

			//colors of the table
            refreshHighlighting();
		}
		
		//setting the table renderer
		for (int i=0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++){
			CombinedRenderer renderer = new CombinedRenderer();
			// the float renderer
			switch(i){
			case 1 : renderer.addRenderer(new FloatRenderer("0.###E0", "N/A")); //float renderer
					break;
			case 2 : renderer.addRenderer(new FloatRenderer("0.###E0", "Direct")); //float renderer
					break;
			default : renderer.addRenderer(new DefaultRenderer());
			}
			//the column renderer
			renderer.addRenderer(this.hlcrtf);
			TableColumn col = this.transcriptionFactorTable.getColumnModel().getColumn(i);
			col.setCellRenderer(renderer);
		}
		for (int i=0; i < this.targetGeneTable.getModel().getColumnCount(); i++){
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
