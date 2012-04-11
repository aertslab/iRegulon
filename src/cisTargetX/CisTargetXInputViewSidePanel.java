package cisTargetX;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import cisTargetAnalysis.CisTargetXInput;
import cisTargetAnalysis.SubmitAction;

import cytoscape.*;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainModel.SpeciesNomenclature;

public class CisTargetXInputViewSidePanel extends CisTargetResourceBundle implements Parameters {

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
	
	
	
	public CisTargetXInputViewSidePanel(){
		
	}
	
	/*
	 * Draws a window on the screen, that will be the input of the plugin
	 */
	public void DrawWindow(){
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
		//cytoPanel.setState(CytoPanelState.DOCK);
		//addCytoPanelListener(CytoPanelListener);

		
		SpringLayout layout = new SpringLayout();
		InputView input = new InputView();
		JPanel panel = input.CreateGeneralInputView();
		//JPanel panel = new JPanel(layout);
		//JPanel panel = new JPanel();
		//Dimension preferredSize = new Dimension(600, fontPoints*37);
		//panel.setPreferredSize(preferredSize);
		//Container contentPane = frame.getContentPane();
		cytoPanel.add(getBundle().getString("plugin_visual_name"), panel);
		
		//this.addComponentsToPane(panel);
		
		/*if (CisTargetXNodes.nodesSelected()){
			JFrame.setDefaultLookAndFeelDecorated(true);

			frame = new JFrame("CisTargetX");
			frame.setSize(600, fontPoints*37);
			frame.setVisible(true);
			frame.setLocation(100, 100);
			this.addComponentsToPane(frame.getContentPane());


			
		}else{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No nodes are selected!");
		}*/
	}
	
	
	/*
	 * drawing the input screen
	 */
	private void addComponentsToPane(Container pane) {
		pane.setLayout(null);
		int yPos = 1;
		

		//Change font and size of the title and add title
        JLabel jtl = new JLabel(getBundle().getString("plugin_visual_name"));
        Font f = new Font("Serif", 0, 50); 
        jtl.setFont(f);
        
        jtl.setBounds(1, yPos, 697, 70);
        jtl.setVisible(true);
        pane.add(jtl);
        yPos += 100;
        //parameters
        f = new Font("Serif", 0, fontPoints);
        
        //Name of the 
        jtl = new JLabel("Choose a name:");
        jtl.setFont(f);
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jtfName = new JTextField(Cytoscape.getCurrentNetwork().getTitle());
        jtfName.setFont(f);
        jtfName.setVisible(true);
        jtfName.setBounds(fontPoints*10, yPos, 75 + fontPoints*15, fontPoints*2);
        jtfName.setVisible(true);
        pane.add(jtfName);
        
        yPos+=fontPoints*2 + fontPoints;
        
        
        //Change the kind of cisTarget action
        jtl = new JLabel("Choose the type of the cisTarget action: ");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the type for your analysis. </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        yPos+=fontPoints*2;
        
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

        //Register a listener for the radio buttons.
        predictedRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.PREDICTED_REGULATORS;
				jtfEscore.setEnabled(true);
				jtfROC.setEnabled(true);
				jtfVisualisation.setEnabled(true);
			}
		});
        databaseForRegulators.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_FOR_REGULATORS;
				jtfEscore.setEnabled(true);
				jtfROC.setEnabled(false);
				jtfVisualisation.setEnabled(false);
			}
		});
        databaseForTargetome.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_FOR_TARGETOME;
				jtfEscore.setEnabled(true);
				jtfROC.setEnabled(false);
				jtfVisualisation.setEnabled(false);
			}
		});
        databaseNetworkAnnotations.addActionListener(new SubmitAction(this) {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cisTargetType = CisTargetType.DATABASE_NETWORK_ANNOTATIONS;
				jtfEscore.setEnabled(true);
				jtfROC.setEnabled(false);
				jtfVisualisation.setEnabled(false);
			}
		});
        predictedRegulators.setFont(f);
        predictedRegulators.setBounds(10, yPos, fontPoints*25, fontPoints*2);
        pane.add(predictedRegulators);
        yPos+=fontPoints*2;
        
        databaseForRegulators.setFont(f);
        databaseForRegulators.setBounds(10, yPos, fontPoints*25, fontPoints*2);
        pane.add(databaseForRegulators);
        yPos+=fontPoints*2;
        
        databaseForTargetome.setFont(f);
        databaseForTargetome.setBounds(10, yPos, fontPoints*25, fontPoints*2);
        pane.add(databaseForTargetome);
        yPos+=fontPoints*2;
        
        databaseNetworkAnnotations.setFont(f);
        databaseNetworkAnnotations.setBounds(10, yPos, fontPoints*25, fontPoints*2);
        pane.add(databaseNetworkAnnotations);
        yPos+=fontPoints*2 + fontPoints;
        
        

        // Escore
        jtl = new JLabel("Choose an Enrichment score threshold");
        jtl.setFont(f);
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        jtl.setToolTipText("<html> Choose the minimal score threshold to consider " +
        		"a motif as being relevant </html>");
        pane.add(jtl);
        
        String[] values = {"2.5", "2.0"};
        jtfEscore = new JTextField("2.5");
        jtfEscore.setFont(f);
        jtfEscore.setVisible(true);
        jtfEscore.setBounds(fontPoints*25, yPos, 75, fontPoints*2);
        jtfEscore.setVisible(true);
        pane.add(jtfEscore);
        
        yPos+=fontPoints*2 + fontPoints;
        
        // ROCthresholdAUC
        jtl = new JLabel("Choose the ROC threshold for AUC.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
        jtl.setToolTipText("<html> The x-axis (region rank) cut-off at which <P> " +
				"to calculate the Area Under the Curve. This <P>" +
				"measure is used to compare and rank all motifs. </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jtfROC = new JTextField("0.01");
        jtfROC.setFont(f);
        jtfROC.setEditable(true);
        
        jtfROC.setBounds(fontPoints*25, yPos, 75, fontPoints*2);
        jtfROC.setVisible(true);
        pane.add(jtfROC);
        yPos+=fontPoints*2 + fontPoints/2;
        
        // threshold for visualisation
        jtl = new JLabel("Choose the Threshold for visualisation.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>The x-axis cut-off for visualisation. " + "<P>" +
        		"When set to 5000, the recovery curve on the top 5000 " + "<P>" +
        		"regions is shown. </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jtfVisualisation = new JTextField("5000");
        jtfVisualisation.setFont(f);
        jtfVisualisation.setEditable(true);
        
        jtfVisualisation.setBounds(fontPoints*25, yPos, 75, fontPoints*2);
        jtfVisualisation.setVisible(true);
        pane.add(jtfVisualisation);
        yPos+=fontPoints*2 + fontPoints/2;

        
        // Species and nomenclature
        jtl = new JLabel("Choose the species and the nomenclature");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the species and the nomenclature of the genes </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jcbSpecieAndNomenclature = new JComboBox(values);
        jcbSpecieAndNomenclature.setModel(new javax.swing.DefaultComboBoxModel(SpeciesNomenclature.getAllNomenclatures().toArray()));
        jcbSpecieAndNomenclature.setFont(f);
        jcbSpecieAndNomenclature.setVisible(true);
        jcbSpecieAndNomenclature.setBounds(fontPoints*25, yPos, fontPoints*20, fontPoints*2);
        pane.add(jcbSpecieAndNomenclature);
        
        yPos+=fontPoints*2 + fontPoints/2;
        
        //Minimal orthologous
        jtl = new JLabel("Choose the minimal orthologous.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>Choose the minimal orthologous. " + "<P>" +
        		"How closer to 0, how more orthologous will be shown." + "<P>" +
        		"Maximum 1.</html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jtfMinOrthologous = new JTextField("0");
        jtfMinOrthologous.setFont(f);
        jtfMinOrthologous.setEditable(true);
        
        jtfMinOrthologous.setBounds(fontPoints*25, yPos, 75, fontPoints*2);
        jtfMinOrthologous.setVisible(true);
        pane.add(jtfMinOrthologous);
        yPos+=fontPoints*2 + fontPoints/2;
        
        
        //Maximal motif similarity FDR
        jtl = new JLabel("Choose the maximal motif similarity FDR.");
        f = new Font("Serif", 0, fontPoints);
        jtl.setFont(f);
		jtl.setToolTipText("<html>Choose the maximal motif similarity FDR." + "<P>" +
        		"How closer to 0, the motif will be more similar. " + "<P>" +
        		"Maximum 1. </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jtfMaxMotifSimilarityFDR = new JTextField("0.05");
        jtfMaxMotifSimilarityFDR.setFont(f);
        jtfMaxMotifSimilarityFDR.setEditable(true);
        
        jtfMaxMotifSimilarityFDR.setBounds(fontPoints*25, yPos, 75, fontPoints*2);
        jtfMaxMotifSimilarityFDR.setVisible(true);
        pane.add(jtfMaxMotifSimilarityFDR);
        yPos+=fontPoints*4 + fontPoints/2;
        
        // Choose attribute as name
        jtl = new JLabel("Choose the attribute that is the gene name");
        jtl.setFont(f);
        jtl.setToolTipText("<html> Choose the attribute that represents the gene name. </html>");
        
        jtl.setBounds(1, yPos, fontPoints*25, fontPoints*2);
        jtl.setVisible(true);
        pane.add(jtl);
        
        jcbGeneName = new AttributeComboBox();
        jcbGeneName.setFont(f);
        jcbGeneName.setVisible(true);
        jcbGeneName.setBounds(fontPoints*25, yPos, fontPoints*20, fontPoints*2);
        jcbGeneName.setVisible(true);
        pane.add(jcbGeneName);
        yPos+=fontPoints*4 + fontPoints/2;
        
        //Button
        JButton jbtn = new JButton("Submit");
        jbtn.setFont(f);
        
        jbtn.setBounds(10, yPos, fontPoints*30, fontPoints*2);
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
						super.actionPerformed(arg0);
					}else{
						JOptionPane.showMessageDialog(Cytoscape.getDesktop(), input.getErrorMessage());
						
					}
				}else{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No nodes are selected!");
				}
	
			}
		});
        pane.add(jbtn);
        
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
