package view.parametersform;


import cytoscape.Cytoscape;
import domainmodel.Delineation;
import domainmodel.PredictRegulatorsParameters;
import domainmodel.RankingsDatabase;
import domainmodel.SpeciesNomenclature;
import infrastructure.CytoscapeNetworkUtilities;
import infrastructure.IRegulonResourceBundle;
import view.Refreshable;
import view.actions.PredictRegulatorsAction;
import view.parametersform.databaseselection.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PredictedRegulatorsForm extends IRegulonResourceBundle
        implements PredictedRegulatorsParameters, Refreshable {
    private static final float DEFAULT_NES_THRESHOLD = Float.parseFloat(RESOURCE_BUNDLE.getString("standard_escore"));
    private static final float DEFAULT_ROC_THRESHOLD = Float.parseFloat(RESOURCE_BUNDLE.getString("standard_ROC"));
    private static final int DEFAULT_RANK_THRESHOLD = Integer.parseInt(RESOURCE_BUNDLE.getString("standard_visualisation"));
    private static final float DEFAULT_MIN_ORTHOLOGOUS_IDENTITY = Float.parseFloat(RESOURCE_BUNDLE.getString("standard_minOrthologous"));
    private static final float DEFAULT_MAX_MOTIF_SIMILARITY_FDR = Float.parseFloat(RESOURCE_BUNDLE.getString("standard_maxMotifSimilarityFDR"));
    private static final int MAX_NAME_LENGTH = 50;

    private JTextField jobNameTF;
    private JComboBox attributeNameCB;
    private JTextField numberOfNodesTF;

    private JTextField jtfEscore;
    private JComboBox jcbSpecieAndNomenclature;
    private JTextField jtfROC;
    private JTextField jtfVisualisation;

    private JLabel labelMinOrthologous;
    private JTextField jtfMinOrthologous;
    private JLabel labelMaxMotifSimilarityFDR;
    private JTextField jtfMaxMotifSimilarityFDR;
    private MotifCollectionComboBox motifCollectionCB;
    private TrackCollectionComboBox trackCollectionCB;
    private PutativeRegulatoryRegionComboBox genePutativeRegulatoryRegionCB;
    private SearchSpaceTypeComboBox searchSpaceTypeCB;
    private RankingsDBCombobox motifRankingsDatabaseCB;
    private RankingsDBCombobox trackRankingsDatabaseCB;
    private JTextField txtOverlap;
    private JComboBox jcbDelation;
    private JTextField txtUpStream;
    private JTextField txtDownStream;
    private JRadioButton rbtnDelineation;
    private JRadioButton rbtnConversion;
    private DatabaseListener dbListener;

    private final JDialog frame;

    public PredictedRegulatorsForm() {
        this(null);
    }

    public PredictedRegulatorsForm(final JDialog frame) {
        this.frame = frame;
        this.dbListener = null;
    }

    public JPanel createForm() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();
        int yPos = 0;

        JLabel jtl;

        // Name of the analysis ...
        jtl = new JLabel("Name for analysis:");
        jtl.setToolTipText("Choose a name for your analysis.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 2;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.LINE_START;
        panel.add(jtl, cc);

        this.jobNameTF = new JTextField(deriveDefaultJobName());
        cc.gridx = 2;
        cc.gridy = yPos;
        cc.gridwidth = 3;
        cc.gridheight = 1;
        cc.weighty = 0.0;
        cc.weightx = 1.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.anchor = GridBagConstraints.LINE_START;
        panel.add(this.jobNameTF, cc);

        yPos += 1;

        // Species and nomenclature
        jtl = new JLabel("Species and gene nomenclature:");
        jtl.setToolTipText("Choose the species and the nomenclature of the genes.");

        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridheight = 1;
        cc.gridwidth = 2;
        cc.weighty = 0.0;
        cc.weightx = 0.0;
        cc.fill = GridBagConstraints.NONE;
        panel.add(jtl, cc);

        this.jcbSpecieAndNomenclature = new JComboBox();
        this.jcbSpecieAndNomenclature.setModel(new javax.swing.DefaultComboBoxModel(SpeciesNomenclature.getSelectableNomenclatures().toArray()));
        this.jcbSpecieAndNomenclature.setSelectedItem(SpeciesNomenclature.getSelectableNomenclatures().iterator().next());
        cc.gridx = 2;
        cc.gridy = yPos;
        cc.gridheight = 1;
        cc.gridwidth = 3;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.jcbSpecieAndNomenclature, cc);

        yPos += 1;

        int lineY = 0;

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
        cNode.weightx = 0;
        cNode.fill = GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelNode.add(jtl, cNode);

        this.attributeNameCB = new JComboBox();
        cNode.gridx = 1;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx = 0.5;
        cNode.fill = GridBagConstraints.HORIZONTAL;
        panelNode.add(this.attributeNameCB, cNode);
        lineY += 1;


        jtl = new JLabel("Number of selected genes (nodes):");
        jtl.setToolTipText("<html>The number of selected nodes that have an ID usable for the analysis." + "<p>" +
                "This does not mean that all nodes have valid names!</html>");
        cNode.gridx = 0;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx = 0;
        cNode.fill = GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelNode.add(jtl, cNode);

        numberOfNodesTF = new JTextField("0");
        numberOfNodesTF.setEditable(false);

        cNode.gridx = 1;
        cNode.gridy = lineY;
        cNode.gridwidth = 1;
        cNode.weightx = 0.5;
        cNode.fill = GridBagConstraints.HORIZONTAL;
        panelNode.add(numberOfNodesTF, cNode);
        lineY += 1;

        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 5;
        cNode.weightx = 0.5;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelNode, cc);
        yPos += 1;

        //Choose the database system
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 5;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createDatabaseSubPanel(), cc);
        yPos += 1;

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
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(overlapJtl, cRegion);

        this.txtOverlap = new JTextField();
        this.txtOverlap.setText(RESOURCE_BUNDLE.getString("standard_overlap"));
        cRegion.gridx = 2;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 3;
        cRegion.weightx = 0.5;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
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
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.rbtnDelineation, cRegion);

        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 4;
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.jcbDelation, cRegion);
        lineY += 1;

        cRegion.gridx = 0;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 1;
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.rbtnConversion, cRegion);

        JLabel labelUp = new JLabel("Upstream region:");
        labelUp.setToolTipText("Choose the amount of bp upstream of the TSS of a gene to use in the mapping to predefined regions.");
        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(labelUp, cRegion);

        this.txtUpStream = new JTextField();
        this.txtUpStream.setText(RESOURCE_BUNDLE.getString("standard_upstream"));
        cRegion.gridx = 3;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx = 0.5;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.txtUpStream, cRegion);
        lineY += 1;

        JLabel labelDown = new JLabel("Downstream region:");
        labelDown.setToolTipText("Choose the amount of bp downstream of the TSS of a gene to use in the mapping to predefined regions.");
        cRegion.gridx = 1;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx = 0;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(labelDown, cRegion);

        this.txtDownStream = new JTextField();
        this.txtDownStream.setText(RESOURCE_BUNDLE.getString("standard_downstream"));
        cRegion.gridx = 3;
        cRegion.gridy = lineY;
        cRegion.gridwidth = 2;
        cRegion.weightx = 0.5;
        cRegion.fill = GridBagConstraints.HORIZONTAL;
        panelRegion.add(this.txtDownStream, cRegion);
        lineY += 1;


        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 5;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelRegion, cc);
        yPos += 1;

        GridBagLayout layoutRecovery = new GridBagLayout();
        GridBagConstraints cRecovery = new GridBagConstraints();
        JPanel panelRecovery = new JPanel(layoutRecovery);
        TitledBorder borderRecovery = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Recovery");
        borderRecovery.setTitleJustification(TitledBorder.LEFT);
        borderRecovery.setTitlePosition(TitledBorder.CENTER);
        panelRecovery.setBorder(borderRecovery);
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
        cRecovery.gridx = 0;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        jtl.setToolTipText("<html>Choose the minimal score threshold to consider a motif as being relevant.</html>");
        panelRecovery.add(jtl, cRecovery);

        this.jtfEscore = new JTextField(Float.toString(DEFAULT_NES_THRESHOLD));
        this.jtfEscore.setVisible(true);
        cRecovery.gridx = 1;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0.5;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        this.jtfEscore.setVisible(true);
        panelRecovery.add(this.jtfEscore, cRecovery);
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

        cRecovery.gridx = 0;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelRecovery.add(jtl, cRecovery);

        this.jtfROC = new JTextField(Float.toString(DEFAULT_ROC_THRESHOLD));
        this.jtfROC.setEditable(true);

        cRecovery.gridx = 1;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0.5;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        this.jtfROC.setVisible(true);
        panelRecovery.add(this.jtfROC, cRecovery);
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

        cRecovery.gridx = 0;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        jtl.setVisible(true);
        panelRecovery.add(jtl, cRecovery);

        this.jtfVisualisation = new JTextField(Integer.toString(DEFAULT_RANK_THRESHOLD));
        this.jtfVisualisation.setEditable(true);

        cRecovery.gridx = 1;
        cRecovery.gridy = lineY;
        cRecovery.gridwidth = 1;
        cRecovery.weightx = 0.5;
        cRecovery.fill = GridBagConstraints.HORIZONTAL;
        this.jtfVisualisation.setVisible(true);
        panelRecovery.add(this.jtfVisualisation, cRecovery);
        lineY += 1;

        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 5;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelRecovery, cc);
        yPos += 1;

        //Minimal Orthologous id
        /* OOOOOOO
		 * OO	OO
		 * OO	OO
		 * OO	OO
		 * OOOOOOO
		 */
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 5;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createMotif2TFSubPanel(), cc);
        yPos += 1;

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
                    final PredictRegulatorsParameters predictRegulatorsParameters = deriveParameters();
                    if (predictRegulatorsParameters.parametersAreValid()) {
                        if (frame != null) frame.dispose();
                        super.actionPerformed(arg0);
                    } else {
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), predictRegulatorsParameters.getErrorMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No nodes are selected!");
                }
            }
        });
        submitButton.setText("Submit");
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(new JPanel(new FlowLayout()) {
            {
                if (frame != null) add(cancelButton);
                add(submitButton);
            }
        }, BorderLayout.SOUTH);


        this.dbListener = new DatabaseListener(this.jobNameTF,
                this.jtfEscore, this.jtfROC, this.jtfVisualisation, this.labelMinOrthologous,
                this.jtfMinOrthologous, this.labelMaxMotifSimilarityFDR,
                this.jtfMaxMotifSimilarityFDR, this.jcbSpecieAndNomenclature,
                this.motifCollectionCB,
                this.trackCollectionCB, this.genePutativeRegulatoryRegionCB, this.searchSpaceTypeCB,
                this.motifRankingsDatabaseCB, this.trackRankingsDatabaseCB, overlapJtl, this.txtOverlap, rbtnDelineation,
                this.jcbDelation, rbtnConversion, this.txtUpStream, labelUp, this.txtDownStream, labelDown,
                this.attributeNameCB, numberOfNodesTF, submitButton);
        registerListeners();

        return mainPanel;
    }

    private void registerListeners() {
        this.jobNameTF.addActionListener(this.dbListener);
        this.jobNameTF.getDocument().addDocumentListener(this.dbListener);
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
        this.genePutativeRegulatoryRegionCB.addActionListener(this.dbListener);
        this.trackCollectionCB.addActionListener(this.dbListener);
        this.motifCollectionCB.addActionListener(this.dbListener);
        this.searchSpaceTypeCB.addActionListener(this.dbListener);
        this.trackRankingsDatabaseCB.addActionListener(this.dbListener);
        this.motifRankingsDatabaseCB.addActionListener(this.dbListener);
        this.txtOverlap.addActionListener(this.dbListener);
        this.txtOverlap.getDocument().addDocumentListener(this.dbListener);
        rbtnDelineation.addActionListener(this.dbListener);
        this.jcbDelation.addActionListener(this.dbListener);
        rbtnConversion.addActionListener(this.dbListener);
        this.txtUpStream.addActionListener(this.dbListener);
        this.txtUpStream.getDocument().addDocumentListener(this.dbListener);
        this.txtDownStream.addActionListener(this.dbListener);
        this.txtDownStream.getDocument().addDocumentListener(this.dbListener);
        this.attributeNameCB.addActionListener(this.dbListener);
        numberOfNodesTF.addActionListener(this.dbListener);
    }

    private void unregisterListeners() {
        this.jobNameTF.removeActionListener(this.dbListener);
        this.jobNameTF.getDocument().removeDocumentListener(this.dbListener);
        this.jtfEscore.removeActionListener(this.dbListener);
        this.jtfEscore.getDocument().removeDocumentListener(this.dbListener);
        this.jtfROC.removeActionListener(this.dbListener);
        this.jtfROC.getDocument().removeDocumentListener(this.dbListener);
        this.jtfVisualisation.removeActionListener(this.dbListener);
        this.jtfVisualisation.getDocument().removeDocumentListener(this.dbListener);
        this.jtfMinOrthologous.removeActionListener(this.dbListener);
        this.jtfMinOrthologous.getDocument().removeDocumentListener(this.dbListener);
        this.jtfMaxMotifSimilarityFDR.removeActionListener(this.dbListener);
        this.jtfMaxMotifSimilarityFDR.getDocument().removeDocumentListener(this.dbListener);
        this.jcbSpecieAndNomenclature.removeActionListener(this.dbListener);
        this.genePutativeRegulatoryRegionCB.removeActionListener(this.dbListener);
        this.trackCollectionCB.removeActionListener(this.dbListener);
        this.motifCollectionCB.removeActionListener(this.dbListener);
        this.searchSpaceTypeCB.removeActionListener(this.dbListener);
        this.motifRankingsDatabaseCB.removeActionListener(this.dbListener);
        this.txtOverlap.removeActionListener(this.dbListener);
        this.txtOverlap.getDocument().removeDocumentListener(this.dbListener);
        rbtnDelineation.removeActionListener(this.dbListener);
        this.jcbDelation.removeActionListener(this.dbListener);
        rbtnConversion.removeActionListener(this.dbListener);
        this.txtUpStream.removeActionListener(this.dbListener);
        this.txtUpStream.getDocument().removeDocumentListener(this.dbListener);
        this.txtDownStream.removeActionListener(this.dbListener);
        this.txtDownStream.getDocument().removeDocumentListener(this.dbListener);
        this.attributeNameCB.removeActionListener(this.dbListener);
        numberOfNodesTF.removeActionListener(this.dbListener);
    }

    private JPanel createDatabaseSubPanel() {
        final GridBagConstraints cc = new GridBagConstraints();
        final JPanel panel = new JPanel(new GridBagLayout());
        final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Ranking");
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.CENTER);
        panel.setBorder(border);

        int yPos = 0;

        final JLabel typeLB = new JLabel("Type of search space:");
        typeLB.setToolTipText("Choose the type of search space to use in the analysis.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(typeLB, cc);

        this.searchSpaceTypeCB = new SearchSpaceTypeComboBox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.searchSpaceTypeCB, cc);

        yPos += 1;

        final JLabel motifCollectionLB = new JLabel("Motif collection:");
        motifCollectionLB.setToolTipText("Choose the motif collection to use in the enrichment analysis.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(motifCollectionLB, cc);

        this.motifCollectionCB = new MotifCollectionComboBox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(motifCollectionCB, cc);

        yPos += 1;

        final JLabel tracksLB = new JLabel("Track collection:");
        tracksLB.setToolTipText("Choose the track collection to use in the enrichment analysis.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(tracksLB, cc);

        this.trackCollectionCB = new TrackCollectionComboBox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(trackCollectionCB, cc);

        yPos += 1;

        final JLabel regRegionLB = new JLabel("Putative regulatory region:");
        regRegionLB.setToolTipText("Choose the putative regulatory region for genes used in the enrichment analysis.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(regRegionLB, cc);

        genePutativeRegulatoryRegionCB = new PutativeRegulatoryRegionComboBox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(genePutativeRegulatoryRegionCB, cc);

        yPos += 1;

        final JLabel motifRankingsDatabaseLB = new JLabel("Motif rankings database:");
        motifRankingsDatabaseLB.setToolTipText("Choose the motif rankings database.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(motifRankingsDatabaseLB, cc);

        this.motifRankingsDatabaseCB = new RankingsDBCombobox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.motifRankingsDatabaseCB, cc);

        yPos += 1;

        final JLabel trackdbLB = new JLabel("Track rankings database:");
        trackdbLB.setToolTipText("Choose the track rankings database.");
        cc.gridx = 0;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        panel.add(trackdbLB, cc);

        this.trackRankingsDatabaseCB = new RankingsDBCombobox();
        cc.gridx = 1;
        cc.gridy = yPos;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(this.trackRankingsDatabaseCB, cc);

        return panel;
    }

    private JPanel createMotif2TFSubPanel() {
        GridBagLayout layoutMotif2TF = new GridBagLayout();
        GridBagConstraints cMotif2TF = new GridBagConstraints();
        JPanel panelMotif2TF = new JPanel(layoutMotif2TF);
        TitledBorder borderMotif2TF = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "TF prediction");
        borderMotif2TF.setTitleJustification(TitledBorder.LEFT);
        borderMotif2TF.setTitlePosition(TitledBorder.CENTER);
        panelMotif2TF.setBorder(borderMotif2TF);
        int lineY = 0;

        this.labelMinOrthologous = new JLabel("Minimum identity between orthologous genes:");
        this.labelMinOrthologous.setToolTipText("<html>Choose the minimum identity between orthologous genes.<br/>" +
                "How closer to 0, how more orthologous the transcription factor will be to the gene for which an enriched motif was found.<br/>" +
                "(Value must be between 0 and 1).</html>");
        cMotif2TF.gridx = 0;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx = 0;
        cMotif2TF.fill = GridBagConstraints.HORIZONTAL;
        this.labelMinOrthologous.setVisible(true);
        panelMotif2TF.add(this.labelMinOrthologous, cMotif2TF);

        this.jtfMinOrthologous = new JTextField(Float.toString(DEFAULT_MIN_ORTHOLOGOUS_IDENTITY));
        this.jtfMinOrthologous.setEditable(true);
        this.jtfMinOrthologous.setVisible(true);
        cMotif2TF.gridx = 1;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx = 0.5;
        cMotif2TF.fill = GridBagConstraints.HORIZONTAL;
        panelMotif2TF.add(this.jtfMinOrthologous, cMotif2TF);
        lineY += 1;

        //max motif similarity FDR
		/* SSSSSSS
		 * SS
		 * SSSSSSS
		 * 		SS
		 * SSSSSSS
		 */
        this.labelMaxMotifSimilarityFDR = new JLabel("Maximum false discovery rate (FDR) on motif similarity:");
        this.labelMaxMotifSimilarityFDR.setToolTipText("<html>Choose the Maximum false discovery rate (FDR) on motif similarity.<br/>" +
                "How closer to 0, how similar the motif annotated for a displayed TF will be to the enriched motif.<br/>" +
                "(Value must be between 0 and 1).</html>");
        cMotif2TF.gridx = 0;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx = 0;
        cMotif2TF.fill = GridBagConstraints.HORIZONTAL;
        this.labelMaxMotifSimilarityFDR.setVisible(true);
        panelMotif2TF.add(this.labelMaxMotifSimilarityFDR, cMotif2TF);

        this.jtfMaxMotifSimilarityFDR = new JTextField(Float.toString(DEFAULT_MAX_MOTIF_SIMILARITY_FDR));
        this.jtfMaxMotifSimilarityFDR.setEditable(true);
        this.jtfMaxMotifSimilarityFDR.setVisible(true);

        cMotif2TF.gridx = 1;
        cMotif2TF.gridy = lineY;
        cMotif2TF.gridwidth = 1;
        cMotif2TF.weightx = 0.5;
        cMotif2TF.fill = GridBagConstraints.HORIZONTAL;
        panelMotif2TF.add(this.jtfMaxMotifSimilarityFDR, cMotif2TF);

        return panelMotif2TF;
    }

    public String getJobName() {
        return jobNameTF.getText();
    }

    public void setJobName(final String name) {
        if (name == null) {
            jobNameTF.setText(deriveDefaultJobName());
        } else if (name.length() > MAX_NAME_LENGTH) {
            jobNameTF.setText(name.substring(0, MAX_NAME_LENGTH - 3) + "...");
        } else {
            jobNameTF.setText(name);
        }
    }

    public static String deriveDefaultJobName() {
        final String name = Cytoscape.getCurrentNetwork().getTitle();
        if (name == null || name.equals("0")) {
            return PLUGIN_NAME + " name";
        } else if (name.length() > MAX_NAME_LENGTH) {
            return name.substring(0, MAX_NAME_LENGTH - 3) + "...";
        } else {
            return name;
        }
    }

    public String getMotifCollection() {
        return this.motifCollectionCB.getSelectedItem().toString();
    }

    public String getTrackCollection() {
        return this.trackCollectionCB.getSelectedItem().toString();
    }

    public float getNESThreshold() {
        return Float.parseFloat((this.jtfEscore.getText()));
    }

    public float getAUCThreshold() {
        return Float.parseFloat(this.jtfROC.getText());
    }

    public int getRankThreshold() {
        return Integer.parseInt(this.jtfVisualisation.getText());
    }

    public SpeciesNomenclature getSpeciesNomenclature() {
        return (SpeciesNomenclature) this.jcbSpecieAndNomenclature.getSelectedItem();
    }

    public float getMinOrthologousIdentity() {
        return Float.parseFloat(this.jtfMinOrthologous.getText());
    }

    public float getMaxMotifSimilarityFDR() {
        return Float.parseFloat(this.jtfMaxMotifSimilarityFDR.getText());
    }

    public String getAttributeName() {
        return (String) this.attributeNameCB.getSelectedItem();
    }

    public boolean isRegionBasedDatabase() {
        return this.searchSpaceTypeCB.isRegionBased();
    }

    public RankingsDatabase getMotifRankingsDatabase() {
        return (RankingsDatabase) this.motifRankingsDatabaseCB.getSelectedItem();
    }

    public RankingsDatabase getTrackRankingsDatabase() {
        return (RankingsDatabase) this.trackRankingsDatabaseCB.getSelectedItem();
    }

    public Float getOverlapFraction() {
        if (this.isRegionBasedDatabase()) {
            return Float.parseFloat(this.txtOverlap.getText());
        } else {
            return -1f;
        }
    }

    public Delineation getDelineation() {
        if (this.isRegionBasedDatabase() && this.rbtnDelineation.isSelected()) {
            return (Delineation) this.jcbDelation.getSelectedItem();
        } else {
            return new Delineation(null, null);
        }
    }

    public int getUpstreamRegionInBp() {
        if (this.isRegionBasedDatabase() && this.rbtnConversion.isSelected()) {
            return Integer.parseInt(this.txtUpStream.getText());
        } else {
            return -1;
        }
    }

    public int getDownstreamInBp() {
        if (this.isRegionBasedDatabase() && this.rbtnConversion.isSelected()) {
            return Integer.parseInt(this.txtDownStream.getText());
        } else {
            return -1;
        }
    }

    private int getNumberOfSelectedNodes() {
        return CytoscapeNetworkUtilities.getGenes(getAttributeName(), getSpeciesNomenclature()).size();
    }

    @Override
    public void refresh() {
        unregisterListeners();
        setJobName(Cytoscape.getNullNetwork().equals(Cytoscape.getCurrentNetwork())
                ? deriveDefaultJobName()
                : Cytoscape.getCurrentNetwork().getTitle());
        final String selectedAttributeName = getAttributeName();
        attributeNameCB.removeAllItems();
        final java.util.List<String> attributeNames = CytoscapeNetworkUtilities.getPossibleGeneIDAttributesWithDefault();
        for (String name : attributeNames) attributeNameCB.addItem(name);
        if (attributeNames.contains(selectedAttributeName)) attributeNameCB.setSelectedItem(selectedAttributeName);
        numberOfNodesTF.setText(Integer.toString(getNumberOfSelectedNodes()));
        registerListeners();
        if (dbListener != null) dbListener.refresh();
    }

    public PredictRegulatorsParameters deriveParameters() {
        return new PredictRegulatorsParameters(CytoscapeNetworkUtilities.getGenes(
                getAttributeName(),
                getSpeciesNomenclature()),
                getNESThreshold(),
                getAUCThreshold(),
                getRankThreshold(),
                getSpeciesNomenclature(),
                IRegulonType.PREDICTED_REGULATORS,
                getJobName(),
                getMotifCollection(),
                getTrackCollection(),
                getMinOrthologousIdentity(),
                getMaxMotifSimilarityFDR(),
                isRegionBasedDatabase(),
                getMotifRankingsDatabase(),
                getTrackRankingsDatabase(),
                getOverlapFraction(),
                getDelineation(),
                getUpstreamRegionInBp(),
                getDownstreamInBp(),
                getAttributeName());
    }
}
