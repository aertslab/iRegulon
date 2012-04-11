package cisTargetX;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import cisTargetAnalysis.CisTargetXInput;
import cisTargetAnalysis.SubmitAction;
import domainModel.SpeciesNomenclature;

public class InputView extends CisTargetResourceBundle implements Parameters{

	private JTextField jtfEscore;
	private JComboBox jcbSpecieAndNomenclature;
	private JTextField jtfROC;
	private JTextField jtfVisualisation;
	private CisTargetXInput m_input = null;
	private int fontPoints = 12;
	private CisTargetType cisTargetType = CisTargetType.PREDICTED_REGULATORS;
	private JTextField jtfName;
	private JTextField jtfMinOrthologous;
	private JTextField jtfMaxMotifSimilarityFDR;
	private JComboBox jcbGeneName;
	//needed for control panel
	private JPanel detailpanel;
	
	
	private String standardJobName;
	private float standardEscore;
	private float standardROC;
	private int standardVisualistion;
	private float standardminOrthologous;
	private float standardMaxMotifSimilarityFDR;
	private final JFrame frame;
	
	public InputView(){
		this.standardJobName = Cytoscape.getCurrentNetwork().getTitle();
		if (this.standardJobName == null || this.standardJobName.equals("0")){
			this.standardJobName = "iRegulon name";
		}
		this.standardEscore = 2.5f;
		this.standardROC = 0.01f;
		this.standardVisualistion = 5000;
		this.standardminOrthologous = 0f;
		this.standardMaxMotifSimilarityFDR = 0.05f;
		this.frame = null;
	}
	
	public InputView(JFrame frame){
		this.standardJobName = Cytoscape.getCurrentNetwork().getTitle();
		if (this.standardJobName == null || this.standardJobName.equals("0")){
			this.standardJobName = "CisTarget name";
		}
		this.standardEscore = 2.5f;
		this.standardROC = 0.01f;
		this.standardVisualistion = 5000;
		this.standardminOrthologous = 0f;
		this.standardMaxMotifSimilarityFDR = 0.05f;
		this.frame = frame;
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
        
        //Name of the 
        jtl = new JLabel("Choose a name:");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose a name for your analysis. </html>");
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        jtfName = new JTextField(this.standardJobName);
        jtfName.setFont(f);
        jtfName.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
        jtfName.setVisible(true);
        panel.add(jtfName, c);
        yPos += 1;
		
        // Escore
        jtl = new JLabel("Choose an Enrichment score threshold");
        jtl.setFont(f);
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        jtl.setToolTipText("<html> Choose the minimal score threshold to consider " +
        		"a motif as being relevant. </html>");
        panel.add(jtl, c);
        
        String[] values = {"2.5", "2.0"};
        jtfEscore = new JTextField("" + this.standardEscore);
        jtfEscore.setFont(f);
        jtfEscore.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
        jtfEscore.setVisible(true);
        panel.add(jtfEscore, c);
        yPos += 1;
        
        // ROCthresholdAUC
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
        
        jtfROC = new JTextField("" + this.standardROC);
        jtfROC.setFont(f);
        jtfROC.setEditable(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
        jtfROC.setVisible(true);
        panel.add(jtfROC, c);
        yPos += 1;

        // threshold for visualisation
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
        
        jtfVisualisation = new JTextField("" + this.standardVisualistion);
        jtfVisualisation.setFont(f);
        jtfVisualisation.setEditable(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
        jtfVisualisation.setVisible(true);
        panel.add(jtfVisualisation, c);
        yPos += 1;
		
        // Species and nomenclature
        jtl = new JLabel("Choose the species and the nomenclature");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the species and the nomenclature of the genes </html>");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        jcbSpecieAndNomenclature = new JComboBox(values);
        jcbSpecieAndNomenclature.setModel(new javax.swing.DefaultComboBoxModel(SpeciesNomenclature.getSelectableNomenclatures().toArray()));
        jcbSpecieAndNomenclature.setFont(f);
        jcbSpecieAndNomenclature.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(jcbSpecieAndNomenclature, c);
		yPos += 1;
		
		//Minimal Orthologou id
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
        
        jtfMinOrthologous = new JTextField("" + this.standardminOrthologous);
        jtfMinOrthologous.setFont(f);
        jtfMinOrthologous.setEditable(true);
        jtfMinOrthologous.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(jtfMinOrthologous, c);
		yPos += 1;
		
		//max motif similarity FDR
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
        
        jtfMaxMotifSimilarityFDR = new JTextField("" + this.standardMaxMotifSimilarityFDR);
        jtfMaxMotifSimilarityFDR.setFont(f);
        jtfMaxMotifSimilarityFDR.setEditable(true);
        jtfMaxMotifSimilarityFDR.setVisible(true);
        
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(jtfMaxMotifSimilarityFDR, c);
		yPos += 1;
		
		// Choose attribute as name
        jtl = new JLabel("Choose the attribute that is the gene name");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the attribute that represents the gene name. </html>");
        
        c.gridx = 0;
		c.gridy = yPos;
		c.gridwidth = 2;
        jtl.setVisible(true);
        panel.add(jtl, c);
        
        //jcbGeneName = new JComboBox(values);
        //jcbGeneName.setModel(new javax.swing.DefaultComboBoxModel(Cytoscape.getNodeAttributes().getAttributeNames()));
        jcbGeneName = new AttributeComboBox();
        jcbGeneName.setFont(f);
        jcbGeneName.setVisible(true);
        c.gridx = 2;
		c.gridy = yPos;
		c.gridwidth = 3;
		panel.add(jcbGeneName, c);
		yPos += 1;
		
		//Button
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
				if (CisTargetXNodes.nodesSelected()){
					generateInput();
					CisTargetXInput input = getInput();
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
        
        //Change the kind of cisTarget action
        jtl = new JLabel("Choose the type of the cisTarget action: ");
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

        detailpanel = CreateClassicalInputView();
        //Register a listener for the radio buttons.
        predictedRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.PREDICTED_REGULATORS;
				detailpanel = CreateClassicalInputView();
			}
		});
        databaseForRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_FOR_REGULATORS;
				detailpanel = new JPanel();
			}
		});
        databaseForTargetome.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_FOR_TARGETOME;
				detailpanel = new JPanel();
			}
		});
        databaseNetworkAnnotations.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_NETWORK_ANNOTATIONS;
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
	public CisTargetXInput getInput(){
		return m_input;
	}
	
	
	
	/*
	 * Generating all needed input variabels
	 */
	
	/**
	 * 
	 * @return the e score as a float
	 */
	public float getNESThreshold(){
		return Float.parseFloat(((String) jtfEscore.getText()));
	}
	
	/**
	 * 
	 * @return the ROC threshold for AUC
	 */
	public float getAUCThreshold(){
		return Float.parseFloat((String) jtfROC.getText());
	}
	
	/**
	 * 
	 * @return the Threshold for visualisation.
	 */
	public int getRankThreshold(){
		return Integer.parseInt((String) jtfVisualisation.getText());
	}
	
	/**
	 * 
	 * @return the species and the nomenclature of this network
	 */
	public SpeciesNomenclature getSpeciesNomenclature(){
		return (SpeciesNomenclature) jcbSpecieAndNomenclature.getSelectedItem();
	}
	
	/**
	 * @return the type of the cisTarget that must be executed
	 */
	public CisTargetType getCisTargetType(){
		return this.cisTargetType;
	}
	
	/**
	 * 
	 * @return the name of the run
	 */
	private String getName(){
		return (String) jtfName.getText();
	}
	
	
	/**
	 * @post a new input class is created
	 * 			getInput() == new input()
	 * 			
	 */
	public void generateInput(){
		m_input = new CisTargetXInput(CisTargetXNodes.getGenes(this.getAttributeName(), this.getSpeciesNomenclature()), 
				this.getNESThreshold(), this.getAUCThreshold(), this.getRankThreshold(), 
				this.getSpeciesNomenclature(), this.cisTargetType, this.getName(), 
				this.getMinOrthologous(), this.getMaxMotifSimilarityFDR());
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
		return (String) jcbGeneName.getSelectedItem();
	}
	
	
	
	
}
