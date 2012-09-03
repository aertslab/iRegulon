package view.parametersform;

import view.CytoscapeNetworkUtilities;
import view.parametersform.actions.PredictRegulatorsAction;
import view.IRegulonResourceBundle;
import view.parametersform.databaseselection.BasedComboBox;
import view.parametersform.databaseselection.DBCombobox;
import view.parametersform.databaseselection.DatabaseListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cytoscape.Cytoscape;

import domainmodel.RankingsDatabase;
import domainmodel.Delineation;
import domainmodel.InputParameters;
import domainmodel.SpeciesNomenclature;


public class PredictedRegulatorsForm extends IRegulonResourceBundle implements PredictedRegulatorsParameters {
	private JTextField jtfEscore;
	private JComboBox jcbSpecieAndNomenclature;
	private JTextField jtfROC;
	private JTextField jtfVisualisation;
	private IRegulonType iRegulonType = IRegulonType.PREDICTED_REGULATORS;
	private JTextField jtfName;
	private JTextField jtfMinOrthologous;
	private JTextField jtfMaxMotifSimilarityFDR;
	private JComboBox jcbGeneName;
    private BasedComboBox jcbBased;
	private DBCombobox jcbdatabase;
	private JTextField txtOverlap;
	private JComboBox jcbDelation;
	private JTextField txtUpStream;
	private JTextField txtDownStream;
	private JRadioButton rbtnDelineation;
	private JRadioButton rbtnConversion;
	private DatabaseListener dbListener;

	private String standardJobName;
	private float standardEscore;
	private float standardROC;
	private int standardVisualisation;
	private float standardminOrthologous;
	private float standardMaxMotifSimilarityFDR;
	private final JFrame frame;
	
	public PredictedRegulatorsForm(){
		this.standardJobName = Cytoscape.getCurrentNetwork().getTitle();
		if (this.standardJobName == null || this.standardJobName.equals("0")){
			this.standardJobName = this.getBundle().getString("plugin_name") + " name";
		}
		this.standardEscore = Float.parseFloat(this.getBundle().getString("standard_escore"));
		this.standardROC =Float.parseFloat(this.getBundle().getString("standard_ROC"));
		this.standardVisualisation = Integer.parseInt(this.getBundle().getString("standard_visualisation"));
		this.standardminOrthologous = Float.parseFloat(this.getBundle().getString("standard_minOrthologous"));
		this.standardMaxMotifSimilarityFDR = Float.parseFloat(this.getBundle().getString("standard_maxMotifSimilarityFDR"));
		this.frame = null;
		this.dbListener = null;
	}
	
	public PredictedRegulatorsForm(JFrame frame){
		this.standardJobName = Cytoscape.getCurrentNetwork().getTitle();
		if (this.standardJobName == null || this.standardJobName.equals("0")){
			this.standardJobName = this.getBundle().getString("plugin_name") + " name";
		}
		this.standardEscore = Float.parseFloat(this.getBundle().getString("standard_escore"));
		this.standardROC =Float.parseFloat(this.getBundle().getString("standard_ROC"));
		this.standardVisualisation = Integer.parseInt(this.getBundle().getString("standard_visualisation"));
		this.standardminOrthologous = Float.parseFloat(this.getBundle().getString("standard_minOrthologous"));
		this.standardMaxMotifSimilarityFDR = Float.parseFloat(this.getBundle().getString("standard_maxMotifSimilarityFDR"));
		this.frame = frame;
		this.dbListener = null;
	}

	
	public JPanel createClassicalInputView() {
		final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        int yPos = 0;

        JLabel jtl;

        //Name of the analysis
        /*
         * nnn    nn				eeee
         * nn n   nn    a			e
         * nn  n  nn   a a   mmmmm 	eeee
         * nn   n nn  aaaaa  m m m 	e
         * nn    nnn a     a m m m 	eeee
         */
        jtl = new JLabel("Name for analysis:");
        jtl.setToolTipText("Choose a name for your analysis.");
        c.gridx = 0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridy = yPos;
		c.gridwidth = 2;
        
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfName = new JTextField(this.standardJobName);
        this.jtfName.setVisible(true);
        this.jtfName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (Cytoscape.getCurrentNetwork() != null
                            && Cytoscape.getCurrentNetwork().getTitle() != null
                            && !Cytoscape.getCurrentNetwork().getTitle().equals("")
                            && !Cytoscape.getCurrentNetwork().getTitle().equals("0")) {
                        jtfName.setText(Cytoscape.getCurrentNetwork().getTitle());
                    }
                }
            }
        });
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
        this.jtfName.setVisible(true);
        panel.add(this.jtfName, c);
        yPos += 1;
		
        
		
        // Species and nomenclature
        /* SSSSSSS
         * SS
         * SSSSSSS
         * 		SS
         * SSSSSSS
         */
        jtl = new JLabel("Species and gene nomenclature:");
        jtl.setToolTipText("Choose the species and the nomenclature of the genes.");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jcbSpecieAndNomenclature = new JComboBox();
        this.jcbSpecieAndNomenclature.setModel(new javax.swing.DefaultComboBoxModel(SpeciesNomenclature.getSelectableNomenclatures().toArray()));
        this.jcbSpecieAndNomenclature.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jcbSpecieAndNomenclature, c);
		yPos += 1;
		
		//Choose the database system
		/* DDDDD
		 * DD	DD
		 * DD	 D
		 * DD	 D
		 * DD	DD
		 * DDDDD
		 */
		
		GridBagLayout layoutDatabase = new GridBagLayout();
		GridBagConstraints cDatabase = new GridBagConstraints();
		JPanel databasePanel = new JPanel(layoutDatabase);
		TitledBorder databaseBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Database");
		databaseBorder.setTitleJustification(TitledBorder.LEFT);
		databaseBorder.setTitlePosition(TitledBorder.CENTER);
		databasePanel.setBorder(databaseBorder);
		int lineY = 0;
		jtl = new JLabel("Region- or gene-based analysis?");
        jtl.setToolTipText("Choose the type of analysis to perform.");
        cDatabase.gridx = 0;
        cDatabase.gridy = lineY;
        cDatabase.gridwidth = 1;
        cDatabase.weightx=0;
        cDatabase.fill=GridBagConstraints.HORIZONTAL;
        databasePanel.add(jtl, cDatabase);
		
		this.jcbBased = new BasedComboBox();
		cDatabase.gridx = 1;
		cDatabase.gridy = lineY;
		cDatabase.gridwidth = 1;
		cDatabase.weightx=0.5;
		cDatabase.fill=GridBagConstraints.HORIZONTAL;
		databasePanel.add(this.jcbBased, cDatabase);
		lineY += 1;
		
		jtl = new JLabel("Database:");
        jtl.setToolTipText("Choose the database.");
        cDatabase.gridx = 0;
        cDatabase.gridy = lineY;
        cDatabase.gridwidth = 1;
        cDatabase.weightx=0;
        cDatabase.fill=GridBagConstraints.HORIZONTAL;
        databasePanel.add(jtl, cDatabase);

		
		this.jcbdatabase = new DBCombobox();
		cDatabase.gridx = 1;
		cDatabase.gridy = lineY;
		cDatabase.gridwidth = 1;
		cDatabase.weightx=0.5;
		cDatabase.fill=GridBagConstraints.HORIZONTAL;
		databasePanel.add(this.jcbdatabase, cDatabase);
		lineY += 1;
		
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 5;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(databasePanel, c);
		yPos+=1;
		
		GridBagLayout layoutRegion = new GridBagLayout();
		GridBagConstraints cRegion = new GridBagConstraints();
		JPanel panelRegion = new JPanel(layoutRegion);
		TitledBorder borderRegion = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Region-based specific parameters");
		borderRegion.setTitleJustification(TitledBorder.LEFT);
		borderRegion.setTitlePosition(TitledBorder.CENTER);
		panelRegion.setBorder(borderRegion);
		lineY = 0;
		
		JLabel overlapJtl = new JLabel("Overlap fraction:");
		overlapJtl.setToolTipText("<html>Percentage of the putative regulatory region associated with a gene<br/>that must overlap with the predefined regions. Must be between 0 and 1.</html>");
		cRegion.gridx = 0;
		cRegion.gridy = lineY;
		cRegion.gridwidth = 2;
		cRegion.weightx=0;
		cRegion.fill=GridBagConstraints.HORIZONTAL;
		panelRegion.add(overlapJtl, cRegion);

		this.txtOverlap = new JTextField();
		this.txtOverlap.setText(this.getBundle().getString("standard_overlap"));
		cRegion.gridx = 2;
		cRegion.gridy = lineY;
		cRegion.gridwidth = 3;
		cRegion.weightx=0.5;
		cRegion.fill=GridBagConstraints.HORIZONTAL;
		panelRegion.add(this.txtOverlap, cRegion);
		lineY += 1;
		
		this.rbtnDelineation = new JRadioButton();
		this.rbtnDelineation.setEnabled(true);
		this.rbtnDelineation.setSelected(true);
		
		this.rbtnConversion = new JRadioButton();
		this.rbtnConversion.setEnabled(true);
		
        ButtonGroup group = new ButtonGroup();
        group.add(this.rbtnDelineation);
        group.add(this.rbtnConversion);
        
        this.jcbDelation = new JComboBox();
		
        cRegion.gridx = 0;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 1;
        cRegion.weightx=0;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.rbtnDelineation, cRegion);
        
        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 4;
        cRegion.weightx=0;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.jcbDelation, cRegion);
		lineY+=1;
        
        cRegion.gridx = 0;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 1;
        cRegion.weightx=0;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.rbtnConversion, cRegion);
        
        JLabel labelUp = new JLabel("Upstream region:");
        labelUp.setToolTipText("Choose the amount of bp upstream of the TSS of a gene to use in the mapping to predefined regions.");
        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx=0;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(labelUp, cRegion);
        
        this.txtUpStream = new JTextField();
        this.txtUpStream.setText(this.getBundle().getString("standard_upstream"));
        cRegion.gridx = 3;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx=0.5;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.txtUpStream, cRegion);
		lineY+=1;
        
        JLabel labelDown = new JLabel("Downstream region:");
        labelDown.setToolTipText("Choose the amount of bp downstream of the TSS of a gene to use in the mapping to predefined regions.");
        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx=0;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(labelDown, cRegion);
        
        this.txtDownStream = new JTextField();
        this.txtDownStream.setText(this.getBundle().getString("standard_downstream"));
        cRegion.gridx = 3;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx=0.5;
        cRegion.fill=GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.txtDownStream, cRegion);
		lineY+=1;
		
		
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 5;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(panelRegion, c);
		yPos+=1;

		GridBagLayout layoutMotif = new GridBagLayout();
		GridBagConstraints cMotif = new GridBagConstraints();
		JPanel panelMotif = new JPanel(layoutMotif);
		TitledBorder borderMotif = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Motif prediction");
		borderMotif.setTitleJustification(TitledBorder.LEFT);
		borderMotif.setTitlePosition(TitledBorder.CENTER);
		panelMotif.setBorder(borderMotif);
		lineY = 0;
		// Escore
        /*
         * EEEEEEE
         * EE
         * EE
         * EEEEE
         * EE
         * EE
         * EEEEEEE
         */
        
        jtl = new JLabel("Enrichment score threshold:");
        cMotif.gridx = 0;
        cMotif.gridy = lineY;
        cMotif.gridwidth = 1;
        cMotif.weightx=0;
        cMotif.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        jtl.setToolTipText("<html>Choose the minimal score threshold to consider a motif as being relevant.</html>");
        panelMotif.add(jtl, cMotif);
        
        this.jtfEscore = new JTextField("" + this.standardEscore);
        this.jtfEscore.setVisible(true);
        cMotif.gridx = 1;
        cMotif.gridy = lineY;
        cMotif.gridwidth = 1;
        cMotif.weightx=0.5;
        cMotif.fill=GridBagConstraints.HORIZONTAL;
		this.jtfEscore.setVisible(true);
		panelMotif.add(this.jtfEscore, cMotif);
		lineY += 1;
        
        // ROCthresholdAUC
        /* RRRRRRR
         * RR	RR
         * RRRRRRR	OOOOOO	  ccc
         * RRRR		OO	OO	 cc
         * RR RR	OO	OO	cc
         * RR  RR	OO	OO	 cc
         * RR   RR	OOOOOO	  ccc
         */
        jtl = new JLabel("ROC threshold for AUC calculation:");
        jtl.setToolTipText("<html>The x-axis (region rank) cut-off at which to calculate the Area Under the Curve. This <br/>" +
				"measure is used to compare and rank all motifs. </html>");
        
        cMotif.gridx = 0;
        cMotif.gridy = lineY;
        cMotif.gridwidth = 1;
        cMotif.weightx=0;
        cMotif.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelMotif.add(jtl, cMotif);
        
        this.jtfROC = new JTextField("" + this.standardROC);
        this.jtfROC.setEditable(true);
        
        cMotif.gridx = 1;
        cMotif.gridy = lineY;
        cMotif.gridwidth = 1;
        cMotif.weightx=0.5;
        cMotif.fill=GridBagConstraints.HORIZONTAL;
		this.jtfROC.setVisible(true);
		panelMotif.add(this.jtfROC, cMotif);
		lineY += 1;

        // threshold for visualisation
        /*
         * v       v
         *  v     v
         *   v   v
         *    v v
         *     v
         * 
         */
        jtl = new JLabel("Rank threshold:");
		jtl.setToolTipText("<html>The x-axis cut-off for calculation of the ROC.</html>");
        
		cMotif.gridx = 0;
		cMotif.gridy = lineY;
		cMotif.gridwidth = 1;
		cMotif.weightx=0;
		cMotif.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelMotif.add(jtl, cMotif);
        
        this.jtfVisualisation = new JTextField("" + this.standardVisualisation);
        this.jtfVisualisation.setEditable(true);
        
        cMotif.gridx = 1;
        cMotif.gridy = lineY;
        cMotif.gridwidth = 1;
        cMotif.weightx=0.5;
        cMotif.fill=GridBagConstraints.HORIZONTAL;
		this.jtfVisualisation.setVisible(true);
		panelMotif.add(this.jtfVisualisation, cMotif);
		lineY += 1;
		
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 5;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(panelMotif, c);
		yPos+=1;

		//Minimal Orthologous id
		/* OOOOOOO
		 * OO	OO
		 * OO	OO
		 * OO	OO
		 * OOOOOOO
		 */
		GridBagLayout layoutMotif2TF = new GridBagLayout();
		GridBagConstraints cMotif2TF = new GridBagConstraints();
		JPanel panelMotif2TF = new JPanel(layoutMotif2TF);
		TitledBorder borderMotif2TF = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "TF prediction");
		borderMotif2TF.setTitleJustification(TitledBorder.LEFT);
		borderMotif2TF.setTitlePosition(TitledBorder.CENTER);
		panelMotif2TF.setBorder(borderMotif2TF);
		lineY = 0;
		
		jtl = new JLabel("Minimum orthologous identity:");
		jtl.setToolTipText("<html>Choose the minimal orthologous identity.<br/>" +
                "How closer to 0, how more orthologous the transcription factor will be to the gene for which an enriched motif was found.<br/>" +
                "(Value must be between 0 and 1).</html>");
		cMotif2TF.gridx = 0;
		cMotif2TF.gridy = lineY;
		cMotif2TF.gridwidth = 1;
		cMotif2TF.weightx=0;
		cMotif2TF.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelMotif2TF.add(jtl, cMotif2TF);
        
        this.jtfMinOrthologous = new JTextField("" + this.standardminOrthologous);
        this.jtfMinOrthologous.setEditable(true);
        this.jtfMinOrthologous.setVisible(true);
        cMotif2TF.gridx = 1;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx=0.5;
        cMotif2TF.fill=GridBagConstraints.HORIZONTAL;
        panelMotif2TF.add(this.jtfMinOrthologous, cMotif2TF);
        lineY += 1;
		
		//max motif similarity FDR
		/* SSSSSSS
		 * SS
		 * SSSSSSS
		 * 		SS
		 * SSSSSSS
		 */
		jtl = new JLabel("Maximum motif similarity FDR.");
		jtl.setToolTipText("<html>Choose the maximum motif similarity FDR. <br/>" +
                "How closer to 0, how similar the motif annotated for a displayed TF will be to the enriched motif.<br/>" +
                "(Value must be between 0 and 1).</html>");
		cMotif2TF.gridx = 0;
		cMotif2TF.gridy = lineY;
		cMotif2TF.gridwidth = 1;
		cMotif2TF.weightx=0;
		cMotif2TF.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelMotif2TF.add(jtl, cMotif2TF);
        
        this.jtfMaxMotifSimilarityFDR = new JTextField("" + this.standardMaxMotifSimilarityFDR);
        this.jtfMaxMotifSimilarityFDR.setEditable(true);
        this.jtfMaxMotifSimilarityFDR.setVisible(true);
        
        cMotif2TF.gridx = 1;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx=0.5;
        cMotif2TF.fill=GridBagConstraints.HORIZONTAL;
        panelMotif2TF.add(this.jtfMaxMotifSimilarityFDR, cMotif2TF);
        lineY += 1;
		
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 5;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(panelMotif2TF, c);
		yPos+=1;
        
		GridBagLayout layoutNode = new GridBagLayout();
		GridBagConstraints cNode = new GridBagConstraints();
		JPanel panelNode = new JPanel(layoutNode);
		TitledBorder borderNode = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Node information");
		borderNode.setTitleJustification(TitledBorder.LEFT);
		borderNode.setTitlePosition(TitledBorder.CENTER);
		panelNode.setBorder(borderNode);
		lineY = 0;
		
		jtl = new JLabel("Node attribute that corresponds to geneID:");
        jtl.setToolTipText("<html>Choose the node attribute that represents the gene name.</html>");
        
        cNode.gridx = 0;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx=0;
        cNode.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelNode.add(jtl, cNode);
        
        this.jcbGeneName = new AttributeComboBox();
        this.jcbGeneName.setVisible(true);
        cNode.gridx = 1;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx=0.5;
        cNode.fill=GridBagConstraints.HORIZONTAL;
        panelNode.add(this.jcbGeneName, cNode);
        lineY += 1;
		
		 
        jtl = new JLabel("Number of valid genes (nodes):");
		jtl.setToolTipText("<html>The number of nodes that have an ID usable for the analysis." + "<P>" +
        		"This doesn't mean that all nodes have valid names! </html>");
		cNode.gridx = 0;
		cNode.gridy = lineY;
		cNode.gridwidth = 1;
		cNode.weightx=0;
		cNode.fill=GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelNode.add(jtl, cNode);
        
        JTextField amountNodes = new JTextField("0");
        amountNodes.setEditable(false);
        amountNodes.setVisible(true);
        
        cNode.gridx = 1;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx=0.5;
        cNode.fill=GridBagConstraints.HORIZONTAL;
        panelNode.add(amountNodes, cNode);
        lineY += 1;
		
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 5;
		cNode.weightx=0.5;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(panelNode, c);
		yPos+=1;

		//Button
		/* BBBBBB
		 * BB  BB
		 * BBBBBB
		 * BB  BB
		 * BBBBBB
		 */
        final JPanel mainPanel = new JPanel(new BorderLayout());
        final JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        if (frame != null) frame.dispose();
                    }
                });
        final JButton submitButton = new JButton(new PredictRegulatorsAction(PredictedRegulatorsForm.this) {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        if (CytoscapeNetworkUtilities.hasSelectedNodes()) {
                            final InputParameters input = deriveParameters();
                            if (input.parametersAreValid()) {
                                if (frame != null) frame.dispose();
                                super.actionPerformed(arg0);
                            } else {
                                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), input.getErrorMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No nodes are selected!");
                        }
                    }
                });
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(new JPanel(new FlowLayout()) {
            {
                if (frame != null) add(cancelButton);
                add(submitButton);
            }
        }, BorderLayout.SOUTH);


        this.dbListener = new DatabaseListener(this.jtfName, 
        		this.jtfEscore, this.jtfROC, this.jtfVisualisation, 
        		this.jtfMinOrthologous, this.jtfMaxMotifSimilarityFDR, 
        		this.jcbSpecieAndNomenclature, this.jcbBased, this.jcbdatabase, 
        		overlapJtl, this.txtOverlap, rbtnDelineation, this.jcbDelation, rbtnConversion, 
        		this.txtUpStream, labelUp, this.txtDownStream, labelDown, this.jcbGeneName, amountNodes, 
        		submitButton);
        this.jtfName.addActionListener(this.dbListener);
        this.jtfName.getDocument().addDocumentListener(this.dbListener);
        this.jtfEscore.addActionListener(this.dbListener);
        this.jtfEscore.getDocument().addDocumentListener(this.dbListener);
        this.jtfROC.addActionListener(this.dbListener);
        this.jtfROC.getDocument().addDocumentListener(this.dbListener);
        this.jtfVisualisation.addActionListener(this.dbListener);
        this.jtfVisualisation.getDocument().addDocumentListener(this.dbListener);
        this.jtfMinOrthologous.addActionListener(this.dbListener);
        this.jtfMinOrthologous.getDocument().addDocumentListener(this.dbListener);
        this.jtfMaxMotifSimilarityFDR.addActionListener(this.dbListener);
        this.jtfMaxMotifSimilarityFDR.getDocument().addDocumentListener(this.dbListener);
		this.jcbSpecieAndNomenclature.addActionListener(this.dbListener);
		this.jcbBased.addActionListener(this.dbListener);
		this.jcbdatabase.addActionListener(this.dbListener);
		this.txtOverlap.addActionListener(this.dbListener);
		this.txtOverlap.getDocument().addDocumentListener(this.dbListener);
		rbtnDelineation.addActionListener(this.dbListener);
		this.jcbDelation.addActionListener(this.dbListener);
		rbtnConversion.addActionListener(this.dbListener);
		this.txtUpStream.addActionListener(this.dbListener);
		this.txtUpStream.getDocument().addDocumentListener(this.dbListener);
		this.txtDownStream.addActionListener(this.dbListener);
		this.txtDownStream.getDocument().addDocumentListener(this.dbListener);
		this.jcbGeneName.addActionListener(this.dbListener);
		amountNodes.addActionListener(this.dbListener);
		dbListener.refresh();

		return mainPanel;
	}
	
	/*
	 * Generating all needed input variabels
	 */
	
	/**
	 * 
	 * @return the e score as a float
	 */
	public float getNESThreshold(){
		return Float.parseFloat(((String) this.jtfEscore.getText()));
	}
	
	/**
	 * 
	 * @return the ROC threshold for AUC
	 */
	public float getAUCThreshold(){
		return Float.parseFloat((String) this.jtfROC.getText());
	}
	
	/**
	 * 
	 * @return the Threshold for visualisation.
	 */
	public int getRankThreshold(){
		return Integer.parseInt((String) this.jtfVisualisation.getText());
	}
	
	/**
	 * 
	 * @return the species and the nomenclature of this network
	 */
	public SpeciesNomenclature getSpeciesNomenclature(){
		return (SpeciesNomenclature) this.jcbSpecieAndNomenclature.getSelectedItem();
	}
	
	/**
	 * @return the type of the iRegulon that must be executed
	 */
	public IRegulonType getiRegulonType(){
		return this.iRegulonType;
	}
	
	/**
	 * 
	 * @return the name of the run
	 */
	private String getName(){
		return (String) this.jtfName.getText();
	}
	
	public InputParameters deriveParameters() {
		return new InputParameters(CytoscapeNetworkUtilities.getGenes(this.getAttributeName(),
                this.getSpeciesNomenclature()),
				this.getNESThreshold(), 
				this.getAUCThreshold(), 
				this.getRankThreshold(), 
				this.getSpeciesNomenclature(), 
				this.iRegulonType, 
				this.getName(), 
				this.getMinOrthologous(), 
				this.getMaxMotifSimilarityFDR(), 
				this.isRegionBased(), 
				this.getDatabase(), 
				this.getOverlap(), 
				this.getDelineation(), 
				this.getUpStream(), 
				this.getDownStream(),
				this.getAttributeName());
	}

	@Override
	public float getMinOrthologous() {
		return Float.parseFloat((String) this.jtfMinOrthologous.getText());
	}

	@Override
	public float getMaxMotifSimilarityFDR() {
		return Float.parseFloat((String) this.jtfMaxMotifSimilarityFDR.getText());
	}

	@Override
	public String getAttributeName() {
		return (String) this.jcbGeneName.getSelectedItem();
	}
	
	public boolean isRegionBased(){
		return (boolean) this.jcbBased.isRegionBased();
	}
	
	public RankingsDatabase getDatabase(){
		if (this.isRegionBased()){
			return (RankingsDatabase) this.jcbdatabase.getSelectedItem();
		}else{
			return (RankingsDatabase) this.jcbdatabase.getSelectedItem();
		}
	}
	
	public Float getOverlap(){
		if (this.isRegionBased()){
			return Float.parseFloat((String) this.txtOverlap.getText());
		}else{
			return -1f;
		}
	}
	
	public Delineation getDelineation(){
		if (this.isRegionBased() && this.rbtnDelineation.isSelected()){
			return (Delineation) this.jcbDelation.getSelectedItem();
		}else{
			return new Delineation(null, null);
		}
	}
	
	public int getUpStream(){
		if (this.isRegionBased() && this.rbtnConversion.isSelected()){
			return Integer.parseInt((String) this.txtUpStream.getText());
		}else{
			return -1;
		}
	}
	
	public int getDownStream(){
		if (this.isRegionBased() && this.rbtnConversion.isSelected()){
			return Integer.parseInt((String) this.txtDownStream.getText());
		}else{
			return -1;
		}
	}
	
	public DatabaseListener getListenerForClassicInput(){
		return this.dbListener;
	}
	
	
}
