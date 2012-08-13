package iRegulonOutput.DetailPanel;


import iRegulonOutput.MotifListener;
import iRegulonOutput.TFComboBox;
import iRegulonOutput.MotifTableModels.ToolTipHeader;
import iRegulonOutput.renderers.CombinedRenderer;
import iRegulonOutput.renderers.DefaultRenderer;
import iRegulonOutput.renderers.FloatRenderer;
import iRegulonOutput.renderers.HighLightColorRenderer;
import iRegulonOutput.renderers.UpDateHighLightRenderer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;


import domainmodel.Input;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class TGPanel extends JPanel implements MotifListener{

	
	private JTable targetGeneTable;
	private JLabel jlbMotif;
	private JLabel jlbDescription;
	//private JTextField jtfNEScore;
	//private JTextField jtfOrthologousIdentifier;
	//private JTextField jtfMotifSimilarityFDR;
	private ImageJLabel jlbLogo;
	private JTable transcriptionFactorTable;
	private HighLightColorRenderer hlcrtf;
	private HighLightColorRenderer hlcrtg;
	private UpDateHighLightRenderer updateHLCR;
	private TFandMotifSelected tfMotif;
	private static Logos logos = new Logos();
	
	private int ipadx = 150;
	private int ipady = 50;
	private double imageHeight = 50.0;
	private double imagewidth = 100.0;
	
	public TGPanel(TFComboBox tfcombobox, Input input){
		super();
		this.tfMotif = new TFandMotifSelected(input);
		this.updateHLCR = new UpDateHighLightRenderer();
		/*this.SelectedRegulatoryTree = selectedRegulatoryTree;
		String motif;
		final JLabel label = new JLabel("Enriched Motif: ");
		jtfMotif = new JTextField();
		jtfMotif.setEnabled(false);
		if (this.SelectedRegulatoryTree.getRegulatoryTree().isEmpty()){
			jtfMotif.setText("");
		}else{
			GeneIdentifier child = this.SelectedRegulatoryTree.getRegulatoryTree().get(0).getChildIDs().iterator().next();
			jtfMotif.setText(this.SelectedRegulatoryTree.getRegulatoryTree().get(0).getEnrichedMotifID());
		}
		this.add(new JPanel() {
			{
				setLayout(new FlowLayout());
				add(label);
				add(jtfMotif);
			}
		}, BorderLayout.NORTH);
		
		Collection<RegulatoryTree> treeCollection = this.SelectedRegulatoryTree.getRegulatoryTree();
		RegulatoryTree tree;
		if (treeCollection.isEmpty()){
			tree = null;
		}else{
			tree = treeCollection.iterator().next();
		}
		table = new JTable(new TargetGeneTableModel(null));
		
		//this.add(label, BorderLayout.PAGE_START);
		
		this.add(new JScrollPane(table), BorderLayout.CENTER);*/
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		/*
		JLabel label = new JLabel("Enriched Motif: ");
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0;
		this.add(label, c);
		*/
		
		this.jlbMotif = new JLabel();
		this.jlbMotif.setEnabled(true);
		this.jlbMotif.setText("");
		Dimension maximumSize = new Dimension((int) this.imagewidth, 1);
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
		
		/*
		label = new JLabel("NEScore: ");
		c.gridx = 0;
		c.gridy = 5;
		c.weightx=0;
		this.add(label, c);
		
		this.jtfNEScore = new JTextField();
		this.jtfNEScore.setText("");
		this.jtfNEScore.setEditable(false);
		c.gridx = 1;
		c.gridy = 5;
		c.weightx=0.25;
		c.ipadx = ipadx;
		this.add(this.jtfNEScore, c);
		*/

		/*
		label = new JLabel("Ortholous Identifier: ");
		c.gridx = 0;
		c.gridy = 6;
		this.add(label, c);
		
		this.jtfOrthologousIdentifier = new JTextField();
		this.jtfOrthologousIdentifier.setText("");
		c.gridx = 1;
		c.gridy = 6;
		c.ipadx = 50;
		this.add(this.jtfOrthologousIdentifier, c);
		
		label = new JLabel("Motif Similarity FDR: ");
		c.gridx = 0;
		c.gridy = 7;
		this.add(label, c);
		
		this.jtfMotifSimilarityFDR = new JTextField();
		this.jtfMotifSimilarityFDR.setText("");
		c.gridx = 1;
		c.gridy = 7;
		c.ipadx = 50;
		this.add(this.jtfMotifSimilarityFDR, c);
		*/
		
		this.targetGeneTable = new JTable(new TGTableModel(null));
		this.hlcrtg=new HighLightColorRenderer("Target Name");
		this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getIDs());
		
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
		this.transcriptionFactorTable.getSelectionModel().addListSelectionListener(new TFTableSelectionListen(this.transcriptionFactorTable, tfcombobox));
		this.transcriptionFactorTable.getSelectionModel().addListSelectionListener(new TFSelectionListener(this.transcriptionFactorTable, this.tfMotif));
		this.transcriptionFactorTable.addMouseListener(new TFMouseListener(this.transcriptionFactorTable, this.tfMotif));
		this.hlcrtf=new HighLightColorRenderer("Transcription Factor Name");
		this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getIDs());
		c.gridx = 1;
		c.gridy = 0;
		c.weightx=0.3;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.ipadx = ipadx;
		c.ipady = ipady;
		
		this.add(new JScrollPane(transcriptionFactorTable), c);
		
		
		this.jlbLogo = new ImageJLabel(null);
		maximumSize = new Dimension((int) this.imagewidth, (int) this.imageHeight);
		this.jlbLogo.setMaximumSize(maximumSize);
		this.jlbLogo.setMinimumSize(maximumSize);
		
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
	    this.transcriptionFactorTable.setAutoCreateRowSorter(true);
	    this.targetGeneTable.setAutoCreateRowSorter(true);
		
	}


	@Override
	public void newRegTree(Motif currentSelection) {
		this.refresh(currentSelection);
	}
	
	
	public void refresh(Motif motif){
		
		//System.out.println("Redrawn");
		this.targetGeneTable.setModel(new TGTableModel(motif));
		this.transcriptionFactorTable.setModel(new TFTableModel(motif));
		this.tfMotif.setMotif(motif);
		
		if (motif == null){
			motif = null;
			this.jlbMotif.setText("");
			this.jlbMotif.setToolTipText("");
			this.jlbDescription.setText("");
			this.jlbDescription.setToolTipText("");
			//this.jtfNEScore.setText("");
			//this.jtfOrthologousIdentifier.setText("");
			//this.jtfMotifSimilarityFDR.setText("");
			this.jlbLogo.setIcon(null);
		}else{
			this.jlbMotif.setText(motif.getEnrichedMotifID());
			this.jlbMotif.setToolTipText("Motif name: " + motif.getEnrichedMotifID());
			//float score = motif.getNeScore();
			this.jlbDescription.setText(motif.getDescription());
			this.jlbDescription.setToolTipText("Description: " + motif.getDescription());
			//this.jtfNEScore.setText("" + score);
			//float orthologousIdentifier = tree.getOrthologousIdentity();
			//this.jtfOrthologousIdentifier.setText("" + orthologousIdentifier);
			//float motifSimilarityFDR = tree.getMotifSimilarityFDR();
			//this.jtfMotifSimilarityFDR.setText("" + motifSimilarityFDR);
			
			
			
			this.jlbLogo.setIcon(this.logos.createSizedImageIcon(motif.getEnrichedMotifID()));
			this.jlbLogo.setFullIcon(this.logos.createImageIcon(motif.getEnrichedMotifID()));
			//String path = "/logos/" + motif.getEnrichedMotifID() + ".png";
			//String tooltiptext = "<html>" + "<img src=" + path + "/>" + "</html>";
			//this.jlbLogo.setToolTipText(tooltiptext);
			
			//colors of the table
			if (this.updateHLCR.mustUpdate()){
				this.hlcrtg.setIDsToBeHighlighted(this.updateHLCR.getIDs());
				this.hlcrtf.setIDsToBeHighlighted(this.updateHLCR.getIDs());
			}
			
		}
		
		//setting the table renderer
		for (int i=0; i < this.transcriptionFactorTable.getModel().getColumnCount(); i++){
			CombinedRenderer renderer = new CombinedRenderer();
			// the float renderer
			switch(i){
			case 1 : renderer.addRenderer(new FloatRenderer("#.##")); //float renderer
					break;
			case 2 : renderer.addRenderer(new FloatRenderer("0.###E0")); //float renderer
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
