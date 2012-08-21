package view.resultspanel.motifview.detailpanel;


import domainmodel.AbstractMotif;
import view.resultspanel.MotifListener;
import view.resultspanel.TFComboBox;
import view.resultspanel.ToolTipHeader;
import view.resultspanel.NetworkMembershipSupport;
import view.resultspanel.renderers.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;


import domainmodel.InputParameters;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class DetailPanel extends JPanel implements MotifListener {
	private JTable targetGeneTable;
	private JLabel jlbMotif;
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

	public DetailPanel(InputParameters input){
		super();
		this.tfMotif = new TFandMotifSelected(input);
		this.updateHLCR = new NetworkMembershipSupport();

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		this.jlbMotif = new JLabel();
		this.jlbMotif.setEnabled(true);
		this.jlbMotif.setText("");
		Dimension maximumSize = new Dimension(LogoThumbnail.THUMBNAIL_WIDTH, 1);
		this.jlbMotif.setMaximumSize(maximumSize);
		this.jlbMotif.setMinimumSize(maximumSize);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0.3;
		c.weighty=0.1;
		c.ipadx = 1;
		c.ipady = 1;
		this.add(this.jlbMotif, c);
		
		
		this.jlbDescription = new JLabel();
		this.jlbDescription.setEnabled(true);
		this.jlbDescription.setText("");
		this.jlbDescription.setMaximumSize(maximumSize);
		this.jlbDescription.setMinimumSize(maximumSize);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx=0.3;
		c.weighty=0.1;
		c.ipadx = 1;
		c.ipady = 1;
		this.add(this.jlbDescription, c);

		this.targetGeneTable = new JTable(new TGTableModel(null));
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
		
		
		this.transcriptionFactorTable = new JTable(new TFTableModel(null));
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
		TFTableModel modelTable = (TFTableModel) this.transcriptionFactorTable.getModel();
	    header.setToolTipStrings(modelTable.getTooltips());
	    header.setToolTipText("");
	    this.transcriptionFactorTable.setTableHeader(header);
		
	    header = new ToolTipHeader(this.targetGeneTable.getColumnModel());
		TGTableModel TGmodelTable = (TGTableModel) this.targetGeneTable.getModel();
	    header.setToolTipStrings(TGmodelTable.getTooltips());
	    header.setToolTipText("");
	    this.targetGeneTable.setTableHeader(header);
	    
	    //Sorting on the table
	    //this.transcriptionFactorTable.setAutoCreateRowSorter(true);
	    this.targetGeneTable.setAutoCreateRowSorter(true);
		
	}

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        if (transcriptionFactorTable.getSelectedRowCount() == 0) return null;
        else {
            final int[] indices = transcriptionFactorTable.getSelectedRows();
			final TFTableModel model = (TFTableModel) transcriptionFactorTable.getModel();
            return model.getTranscriptionFactorAtRow(transcriptionFactorTable.convertRowIndexToModel(indices[0]));
        }
    }

    public void registerSelectionComponents(final TFComboBox tfcombobox) {
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
	public void newMotifSelected(AbstractMotif currentSelection) {
		this.refresh(currentSelection);
	}
	
	public void refresh(AbstractMotif motif) {
		this.targetGeneTable.setModel(new TGTableModel(motif));
		this.transcriptionFactorTable.setModel(new TFTableModel(motif));
		this.tfMotif.setMotif((Motif) motif);
		
		if (motif == null) {
			motif = null;
			this.jlbMotif.setText("");
			this.jlbMotif.setToolTipText("");
			this.jlbDescription.setText("");
			this.jlbDescription.setToolTipText("");
			this.jlbLogo.setMotif(null);
		} else {
			this.jlbMotif.setText(motif.getName());
			this.jlbMotif.setToolTipText("Motif name: " + motif.getName());
			this.jlbDescription.setText(motif.getDescription());
			this.jlbDescription.setToolTipText("Description: " + motif.getDescription());

			this.jlbLogo.setMotif((Motif) motif);

			//colors of the table
			if (this.updateHLCR.refresh()){
				this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
				this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getCurrentIDs());
			}
		}
		
		//setting the table renderer
		for (int i=0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++){
			CombinedRenderer renderer = new CombinedRenderer();
			// the float renderer
			switch(i){
			case 1 : renderer.addRenderer(new FloatRenderer("0.###E0", "Not applicable")); //float renderer
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
		
		this.transcriptionFactorTable.addMouseMotionListener(new MouseMotionAdapter(){
			   public void mouseMoved(MouseEvent e){
				   //get column and row index
			        Point p = e.getPoint(); 
			        int row = transcriptionFactorTable.rowAtPoint(p);
			        int column = transcriptionFactorTable.columnAtPoint(p);
			        TFTableModel tfModel = (TFTableModel) transcriptionFactorTable.getModel();
			        TranscriptionFactor tf = tfModel.getTranscriptionFactorAtRow(row);
			        if (column == 1){
			        	String orthology = "<html>";
			        	if (tf.getOrthologousGeneName() != null){
			        		orthology = orthology + "Orthologous Gene Name = " + tf.getOrthologousGeneName();
			        	}
			        	if (tf.getOrthologousSpecies() != null){
			        		orthology = orthology + "<br/> Orthologous Species = " + tf.getOrthologousSpecies();
			        	}
			        	orthology = orthology + "</html>";
			        	if (orthology.equalsIgnoreCase("<html></html>")){
			        		transcriptionFactorTable.setToolTipText(null);
			        	}else{
			        		transcriptionFactorTable.setToolTipText(orthology);
			        	}

			        }
			        if (column == 2){
			        	String motifSimilarity = "<html>";
			        	if (tf.getSimilarMotifName() != null){
			        		motifSimilarity = motifSimilarity + "Similar motif name = " + tf.getSimilarMotifName();
			        	}
			        	if (tf.getSimilarMotifDescription() != null){
			        		motifSimilarity = motifSimilarity + "<br/> Similar motif description = " + tf.getSimilarMotifDescription();
			        	}
			        	motifSimilarity = motifSimilarity + "</html>";
			        	if (motifSimilarity.equalsIgnoreCase("<html></html>")){
			        		transcriptionFactorTable.setToolTipText(null);
			        	}else{
			        		transcriptionFactorTable.setToolTipText(motifSimilarity);
			        	}

			        }
			   }//end MouseMoved
			});
		
		this.invalidate();
	}
}