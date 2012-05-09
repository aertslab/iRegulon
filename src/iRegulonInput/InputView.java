package iRegulonInput;

import iRegulonAnalysis.Input;
import iRegulonAnalysis.SubmitAction;
import iRegulonInput.databaseSelection.BasedComboBox;
import iRegulonInput.databaseSelection.DBCombobox;
import iRegulonInput.databaseSelection.DatabaseListener;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import cytoscape.Cytoscape;

import domainModel.Database;
import domainModel.Delineation;
import domainModel.SpeciesNomenclature;

public class InputView extends IRegulonResourceBundle implements Parameters{

	private JTextField jtfEscore;
	private JComboBox jcbSpecieAndNomenclature;
	private JTextField jtfROC;
	private JTextField jtfVisualisation;
	private Input input = null;
	private int fontPoints = 12;
	private IRegulonType iRegulonType = IRegulonType.PREDICTED_REGULATORS;
	private JTextField jtfName;
	private JTextField jtfMinOrthologous;
	private JTextField jtfMaxMotifSimilarityFDR;
	private JComboBox jcbGeneName;
	//needed for control panel
	private JPanel detailpanel;
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
	
	public InputView(){
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
	
	public InputView(JFrame frame){
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
	
	
	
	/**
	 * @post the jframe will be disposed if pressed on submit
	 * @param jframe
	 * @return the input panel 
	 * 
	 */
	public JPanel CreateClassicalInputView(){
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(layout);
		int yPos = 0;
		JLabel jtl;
		Font f = new Font("Serif", 0, 50);
		//not in a frame= in control pane: name is already drawn
		if (this.frame != null){
			jtl = new JLabel(getBundle().getString("plugin_visual_name"));
			jtl.setFont(f);
        
			c.gridx = 0;
			c.gridy = yPos;
			c.gridwidth = 3;
			c.fill=GridBagConstraints.HORIZONTAL;

			jtl.setVisible(true);
			panel.add(jtl, c);
			yPos += 2;
		}
        
        jtl = new JLabel("Classical ");
        f = new Font("Serif", 0, 30); 
        jtl.setFont(f);
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
		c.fill=GridBagConstraints.HORIZONTAL;

        jtl.setVisible(true);
        panel.add(jtl, c);
        yPos += 1;
        
        f = new Font("Serif", 0, fontPoints);
        
        //Name of the analysis
        /*
         * nnn    nn				eeee
         * nn n   nn    a			e
         * nn  n  nn   a a   mmmmm 	eeee
         * nn   n nn  aaaaa  m m m 	e
         * nn    nnn a     a m m m 	eeee
         */
        jtl = new JLabel("Choose a name:");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose a name for your analysis. </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfName = new JTextField(this.standardJobName);
        this.jtfName.setFont(f);
        this.jtfName.setVisible(true);
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
        jtl = new JLabel("Choose the species and the nomenclature");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the species and the nomenclature of the genes </html>");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jcbSpecieAndNomenclature = new JComboBox();
        this.jcbSpecieAndNomenclature.setModel(new javax.swing.DefaultComboBoxModel(SpeciesNomenclature.getSelectableNomenclatures().toArray()));
        this.jcbSpecieAndNomenclature.setFont(f);
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
		jtl = new JLabel("Choose the database system");
        jtl.setToolTipText("<html> Choose the base of the database. Region or Gene Based </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(jtl, c);
		
		this.jcbBased = new BasedComboBox();
		c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jcbBased, c);
		yPos += 1;
		
		jtl = new JLabel("Choose the database");
        jtl.setToolTipText("<html> Choose the database. </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(jtl, c);

		
		this.jcbdatabase = new DBCombobox();
		c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jcbdatabase, c);
		yPos += 1;
		
		jtl = new JLabel("Choose the Overlap");
        jtl.setToolTipText("<html> Percentage of the gene that must fall into the intreval to motif/gene. Must between 0 and 1. </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(jtl, c);

		this.txtOverlap = new JTextField();
		this.txtOverlap.setText(this.getBundle().getString("standard_overlap"));
		c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.txtOverlap, c);
		yPos += 1;
		
		this.rbtnDelineation = new JRadioButton();
		this.rbtnDelineation.setEnabled(true);
		this.rbtnDelineation.setSelected(true);
		
		this.rbtnConversion = new JRadioButton();
		this.rbtnConversion.setEnabled(true);
		
        ButtonGroup group = new ButtonGroup();
        group.add(this.rbtnDelineation);
        group.add(this.rbtnConversion);
        
        this.jcbDelation = new JComboBox();
		
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 1;
        panel.add(this.rbtnDelineation, c);
        
        c.gridx = 1;
		c.gridy = yPos;
		c.gridwidth = 4;
		panel.add(this.jcbDelation, c);
        yPos+=1;
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 1;
        panel.add(this.rbtnConversion, c);
        
        JLabel labelUp = new JLabel("Upstream");
        labelUp.setToolTipText("<html> Choose the amount of bp upstream of the conversion. </html>");
        c.gridx = 1;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(labelUp, c);
        
        this.txtUpStream = new JTextField();
        this.txtUpStream.setText(this.getBundle().getString("standard_upstream"));
        c.gridx = 3;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(this.txtUpStream, c);
		yPos+=1;
        
        JLabel labelDown = new JLabel("Downstream");
        labelDown.setToolTipText("<html> Choose the amount of bp downstream of the conversion. </html>");
        c.gridx = 1;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(labelDown, c);
        
        this.txtDownStream = new JTextField();
        this.txtDownStream.setText(this.getBundle().getString("standard_downstream"));
        c.gridx = 3;
		c.gridy = yPos;
		c.gridwidth = 2;
		panel.add(this.txtDownStream, c);
        yPos+=1;
        
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
        jtl = new JLabel("Choose an Enrichment score threshold");
        jtl.setFont(f);
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        jtl.setToolTipText("<html> Choose the minimal score threshold to consider " +
        		"a motif as being relevant. </html>");
        panel.add(jtl, c);
        
        this.jtfEscore = new JTextField("" + this.standardEscore);
        this.jtfEscore.setFont(f);
        this.jtfEscore.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		this.jtfEscore.setVisible(true);
        panel.add(this.jtfEscore, c);
        yPos += 1;
        
        // ROCthresholdAUC
        /* RRRRRRR
         * RR	RR
         * RRRRRRR	OOOOOO	  ccc
         * RRRR		OO	OO	 cc
         * RR RR	OO	OO	cc
         * RR  RR	OO	OO	 cc
         * RR   RR	OOOOOO	  ccc
         */
        jtl = new JLabel("Choose the ROC threshold for AUC.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
        jtl.setToolTipText("<html> The x-axis (region rank) cut-off at which <P> " +
				"to calculate the Area Under the Curve. This <P>" +
				"measure is used to compare and rank all motifs. </html>");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfROC = new JTextField("" + this.standardROC);
        this.jtfROC.setFont(f);
        this.jtfROC.setEditable(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		this.jtfROC.setVisible(true);
        panel.add(this.jtfROC, c);
        yPos += 1;

        // threshold for visualisation
        /*
         * v       v
         *  v     v
         *   v   v
         *    v v
         *     v
         * 
         */
        jtl = new JLabel("Choose the Threshold for visualisation.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>The x-axis cut-off for visualisation. " + "<P>" +
        		"When set to 5000, the recovery curve on the top 5000 " + "<P>" +
        		"regions is shown. </html>");
        
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfVisualisation = new JTextField("" + this.standardVisualisation);
        this.jtfVisualisation.setFont(f);
        this.jtfVisualisation.setEditable(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		this.jtfVisualisation.setVisible(true);
        panel.add(this.jtfVisualisation, c);
        yPos += 1;
        
		//Minimal Orthologous id
		/* OOOOOOO
		 * OO	OO
		 * OO	OO
		 * OO	OO
		 * OOOOOOO
		 */
		jtl = new JLabel("Choose the minimal orthologous.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>Choose the minimal orthologous. " + "<P>" +
        		"How closer to 0, how more orthologous will be shown." + "<P>" +
        		"Maximum 1.</html>");
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfMinOrthologous = new JTextField("" + this.standardminOrthologous);
        this.jtfMinOrthologous.setFont(f);
        this.jtfMinOrthologous.setEditable(true);
        this.jtfMinOrthologous.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jtfMinOrthologous, c);
		yPos += 1;
		
		//max motif similarity FDR
		/* SSSSSSS
		 * SS
		 * SSSSSSS
		 * 		SS
		 * SSSSSSS
		 */
		jtl = new JLabel("Choose the maximal motif similarity FDR.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>Choose the maximal motif similarity FDR." + "<P>" +
        		"How closer to 0, the motif will be more similar. " + "<P>" +
        		"Maximum 1. </html>");
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jtfMaxMotifSimilarityFDR = new JTextField("" + this.standardMaxMotifSimilarityFDR);
        this.jtfMaxMotifSimilarityFDR.setFont(f);
        this.jtfMaxMotifSimilarityFDR.setEditable(true);
        this.jtfMaxMotifSimilarityFDR.setVisible(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jtfMaxMotifSimilarityFDR, c);
		yPos += 1;
		
		// Choose attribute as name
		/* NNN   NN
		 * NN N  NN
		 * NN  N NN
		 * NN   NNN
		 * NN    NN
		 */
        jtl = new JLabel("Choose the attribute that is the gene name");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the attribute that represents the gene name. </html>");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        this.jcbGeneName = new AttributeComboBox();
        this.jcbGeneName.setFont(f);
        this.jcbGeneName.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(this.jcbGeneName, c);
		yPos += 1;
		
		 
        jtl = new JLabel("Amount of valid genes (nodes).");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>These is the amount of nodes that have a name that can be used for the analysis." + "<P>" +
        		"This doesn't mean that all nodes has valid names! </html>");
		c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        JTextField amountNodes = new JTextField("0");
        amountNodes.setFont(f);
        amountNodes.setEditable(false);
        amountNodes.setVisible(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(amountNodes, c);
		yPos += 1;
		
		
		
		//Button
		/* BBBBBB
		 * BB  BB
		 * BBBBBB
		 * BB  BB
		 * BBBBBB
		 */
        JButton jbtn = new JButton("Submit");
        jbtn.setFont(f);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 2;
		c.weightx = 0.5;
        jbtn.setVisible(true);
        
        //action of the button
        jbtn.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//frame.dispose();
				if (NodesActions.nodesSelected()){
					generateInput();
					Input input = getInput();
					if (input.parametersAreValid()){
						if (frame != null){
							frame.dispose();
						}
						super.actionPerformed(arg0);
					}else{
						JOptionPane.showMessageDialog(Cytoscape.getDesktop(), input.getErrorMessage());
						
					}
				}else{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No nodes are selected!");
				}
	
			}
		});
        panel.add(jbtn, c);
        

        this.dbListener = new DatabaseListener(this.jtfName, 
        		this.jtfEscore, this.jtfROC, this.jtfVisualisation, 
        		this.jtfMinOrthologous, this.jtfMaxMotifSimilarityFDR, 
        		this.jcbSpecieAndNomenclature, this.jcbBased, this.jcbdatabase, 
        		this.txtOverlap, rbtnDelineation, this.jcbDelation, rbtnConversion, 
        		this.txtUpStream, labelUp, this.txtDownStream, labelDown, this.jcbGeneName, amountNodes, 
        		jbtn);
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
        
        if (frame != null){
        	//Button Cancel
        	jbtn = new JButton("Cancel");
        	jbtn.setFont(f);
        
        	c.gridx = 4;
        	c.gridy = yPos;
        	c.gridwidth = 1;
        	jbtn.setVisible(true);
        	yPos += 1;
        	//action of the button
        	jbtn.addActionListener(new SubmitAction(this) {
			
        		@Override
        		public void actionPerformed(ActionEvent arg0) {
        			if (frame != null){
        				frame.dispose();
        			}
        		}
        	});
        	panel.add(jbtn, c);
        }
		return panel;
	}
	
	
	
	/**
	 * 
	 * @return the panel with all possible input parameters
	 */
	public JPanel CreateGeneralInputView(){
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(layout);
		int yPos = 0;
		
		JLabel jtl = new JLabel(getBundle().getString("plugin_visual_name"));
        Font f = new Font("Serif", 0, 45); 
        jtl.setFont(f);
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
		c.fill=GridBagConstraints.HORIZONTAL;

        jtl.setVisible(true);
        panel.add(jtl, c);
        yPos += 2;
        
        f = new Font("Serif", 0, fontPoints);
        
        //Change the kind of iRegulon action
        jtl = new JLabel("Choose the type of the " + this.getBundle().getString("plugin_name") + " action: ");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the type for your analysis. </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        
        jtl.setVisible(true);
        panel.add(jtl, c);
        yPos+=1;
        
        //Create the radio buttons.
        String predictedRegulatorsString = "Predict regulators";
        JRadioButton predictedRegulators = new JRadioButton(predictedRegulatorsString);
        predictedRegulators.setActionCommand(predictedRegulatorsString);
        predictedRegulators.setSelected(true);

        String databaseForRegulatorsString = "Database for regulators";
        JRadioButton databaseForRegulators = new JRadioButton(databaseForRegulatorsString);
        databaseForRegulators.setActionCommand(databaseForRegulatorsString);
        databaseForRegulators.setEnabled(false);

        String databaseForTargetomeString = "Database for Targetome";
        JRadioButton databaseForTargetome = new JRadioButton(databaseForTargetomeString);
        databaseForTargetome.setActionCommand(databaseForTargetomeString);
        databaseForTargetome.setEnabled(false);
        
        String databaseNetworkAnnotationsString = "Database for network annotations";
        JRadioButton databaseNetworkAnnotations = new JRadioButton(databaseNetworkAnnotationsString);
        databaseNetworkAnnotations.setActionCommand(databaseNetworkAnnotationsString);
        databaseNetworkAnnotations.setEnabled(false);
        
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(predictedRegulators);
        group.add(databaseForRegulators);
        group.add(databaseForTargetome);
        group.add(databaseNetworkAnnotations);

        this.detailpanel = CreateClassicalInputView();
        //Register a listener for the radio buttons.
        predictedRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				iRegulonType = IRegulonType.PREDICTED_REGULATORS;
				detailpanel = CreateClassicalInputView();
			}
		});
        databaseForRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				iRegulonType = IRegulonType.DATABASE_FOR_REGULATORS;
				detailpanel = new JPanel();
			}
		});
        databaseForTargetome.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				iRegulonType = IRegulonType.DATABASE_FOR_TARGETOME;
				detailpanel = new JPanel();
			}
		});
        databaseNetworkAnnotations.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				iRegulonType = IRegulonType.DATABASE_NETWORK_ANNOTATIONS;
				detailpanel = new JPanel();
			}
		});
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
        panel.add(predictedRegulators, c);
        yPos+=1;
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
        panel.add(databaseForRegulators, c);
        yPos+=1;
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
        panel.add(databaseForTargetome, c);
        yPos+=1;
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
        panel.add(databaseNetworkAnnotations, c);
        yPos+=1;
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 3;
        
        jtl.setVisible(true);
        panel.add(detailpanel, c);
        
        
		
		return panel;
	}
	
	
	/**
	 * 
	 * @return the total input in a input class
	 */
	public Input getInput(){
		return this.input;
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
	
	
	/**
	 * @post a new input class is created
	 * 			getInput() == new input()
	 * 			
	 */
	public void generateInput(){
		this.input = new Input(NodesActions.getGenes(this.getAttributeName(), 
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
				this.getDownStream());
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
	
	public Database getDatabase(){
		if (this.isRegionBased()){
			return (Database) this.jcbdatabase.getSelectedItem();
		}else{
			return (Database) this.jcbdatabase.getSelectedItem();
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
