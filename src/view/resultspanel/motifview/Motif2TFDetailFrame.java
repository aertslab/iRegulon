package view.resultspanel.motifview;

import view.resultspanel.CandidateTargetGeneTableModel;
import view.resultspanel.motifview.detailpanel.LogoUtilities;
import view.resultspanel.motifview.detailpanel.TFandMotifSelected;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class Motif2TFDetailFrame {
	private TFandMotifSelected tfMotif;

	public Motif2TFDetailFrame(TFandMotifSelected tfMotif){
		this.tfMotif = tfMotif;
		String title = tfMotif.getMotif().getName() + " " + tfMotif.getTranscriptionFactor().getName();
		JFrame frame = new JFrame(title);
		frame.add(this.getDetailPane());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public JPanel getDetailPane(){
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(layout);
		int ypos = 0;
		int yBgnLogo = 0;
		
		JLabel parametersTitleLabel = new JLabel();
		Font f = new Font("Serif", 0, 30);
		parametersTitleLabel.setFont(f);
		parametersTitleLabel.setText("Parameters");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 2;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(parametersTitleLabel, c);
		ypos+=1;
		
		
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Job name: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(nameLabel, c);
		
		JLabel nnameLabel = new JLabel();
		nnameLabel.setText(this.tfMotif.getInput().getName());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 3;
		panel.add(nnameLabel, c);
		c.gridwidth = 1;
		ypos+=1;
		
		JLabel SpeciesLabel = new JLabel();
		SpeciesLabel.setText("Species and Nomenclature: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(SpeciesLabel, c);
		
		JLabel speciesNameLabel = new JLabel();
		speciesNameLabel.setText(this.tfMotif.getInput().getSpeciesNomenclature().toString());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 3;
		panel.add(speciesNameLabel, c);
		c.gridwidth = 1;
		ypos+=1;
		
		int yDatabase = ypos;
		
		JLabel minEscoreLabel = new JLabel();
		minEscoreLabel.setText("Minimal E-score: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(minEscoreLabel, c);
		
		JLabel minEscoreValueLabel = new JLabel();
		minEscoreValueLabel.setText("" + this.tfMotif.getInput().getEScore());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(minEscoreValueLabel, c);
		ypos+=1;
		
		JLabel visualisationLabel = new JLabel();
		visualisationLabel.setText("Threshold for visualisation: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(visualisationLabel, c);
		
		JLabel visualisationValueLabel = new JLabel();
		visualisationValueLabel.setText("" + this.tfMotif.getInput().getThresholdForVisualisation());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(visualisationValueLabel, c);
		ypos+=1;
		
		JLabel aucLabel = new JLabel();
		aucLabel.setText("ROC threshold for AUC: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(aucLabel, c);
		
		JLabel aucValueLabel = new JLabel();
		aucValueLabel.setText("" + this.tfMotif.getInput().getROCthresholdAUC());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(aucValueLabel, c);
		ypos+=1;
		
		JLabel orthologousLabel = new JLabel();
		orthologousLabel.setText("minimal ortholgous: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(orthologousLabel, c);
		
		JLabel orthologousValueLabel = new JLabel();
		orthologousValueLabel.setText("" + this.tfMotif.getInput().getMinOrthologous());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(orthologousValueLabel, c);
		ypos+=1;
		
		JLabel similarityLabel = new JLabel();
		similarityLabel.setText("maximal motif similarity: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(similarityLabel, c);
		
		JLabel similarityValueLabel = new JLabel();
		similarityValueLabel.setText("" + this.tfMotif.getInput().getMaxMotifSimilarityFDR());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(similarityValueLabel, c);
		ypos+=1;
		
		JLabel databaseLabel = new JLabel();
		databaseLabel.setText("Database: ");
		c.gridx = 2;
		c.gridy = yDatabase;
		c.gridwidth = 1;
		panel.add(databaseLabel, c);
		
		JLabel databaseNameLabel = new JLabel();
		databaseNameLabel.setText("" + this.tfMotif.getInput().getDatabase());
		c.gridx = 3;
		c.gridy = yDatabase;
		c.gridwidth = 1;
		panel.add(databaseNameLabel, c);
		yDatabase+=1;
		
		if (this.tfMotif.getInput().isRegionBased()){
			JLabel overlapLabel = new JLabel();
			overlapLabel.setText("Overlap: ");
			c.gridx = 2;
			c.gridy = yDatabase;
			c.gridwidth = 1;
			panel.add(overlapLabel, c);
			
			JLabel overlapValueLabel = new JLabel();
			overlapValueLabel.setText("" + this.tfMotif.getInput().getOverlap());
			c.gridx = 3;
			c.gridy = yDatabase;
			c.gridwidth = 1;
			panel.add(overlapValueLabel, c);
			yDatabase+=1;
			if (this.tfMotif.getInput().isDelineationBased()){
				JLabel delineationLabel = new JLabel();
				delineationLabel.setText("Delineation: ");
				c.gridx = 2;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(delineationLabel, c);
				
				JLabel delineationNameLabel = new JLabel();
				delineationNameLabel.setText("" + this.tfMotif.getInput().getDelineation());
				c.gridx = 3;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(delineationNameLabel, c);
				yDatabase+=1;
			}else{
				JLabel upstreamLabel = new JLabel();
				upstreamLabel.setText("Amount of bases upstream: ");
				c.gridx = 2;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(upstreamLabel, c);
				
				JLabel upstreamValueLabel = new JLabel();
				upstreamValueLabel.setText("" + this.tfMotif.getInput().getUpstream() + " kb");
				c.gridx = 3;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(upstreamValueLabel, c);
				yDatabase+=1;
				
				JLabel downstreamLabel = new JLabel();
				downstreamLabel.setText("Amount of bases downstream: ");
				c.gridx = 2;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(downstreamLabel, c);
				
				JLabel downstreamValueLabel = new JLabel();
				downstreamValueLabel.setText("" + this.tfMotif.getInput().getDownstream() + " kb");
				c.gridx = 3;
				c.gridy = yDatabase;
				c.gridwidth = 1;
				panel.add(downstreamValueLabel, c);
				yDatabase+=1;
			}
		}
		
		if (yDatabase > ypos){
			ypos = yDatabase;
		}
		
		/*
		 * Motif
		 * 
		 * 
		 * 
		 */
		JLabel motifTitleLabel = new JLabel();
		//Font f = new Font("Serif", 0, 30);
		motifTitleLabel.setFont(f);
		motifTitleLabel.setText("Motif");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 2;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(motifTitleLabel, c);
		ypos+=1;
		yBgnLogo = ypos;
		
		
		JLabel motifLabel = new JLabel();
		motifLabel.setText("Enriched motif: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(motifLabel, c);
		
		JLabel motifNameLabel = new JLabel();
		motifNameLabel.setText(this.tfMotif.getMotif().getName());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(motifNameLabel, c);
		ypos+=1;
		
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText("Description: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(descriptionLabel, c);
		
		JLabel descriptionTextLabel = new JLabel();
		descriptionTextLabel.setText(this.tfMotif.getMotif().getDescription());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(descriptionTextLabel, c);
		ypos += 1;
		
		JLabel NESLabel = new JLabel();
		NESLabel.setText("NEScore: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(NESLabel, c);
		
		JLabel NEScoreLabel = new JLabel();
		NEScoreLabel.setText("" + this.tfMotif.getMotif().getNEScore());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(NEScoreLabel, c);
		ypos += 1;
		
		JLabel AUCLabel = new JLabel();
		AUCLabel.setText("AUC value: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(AUCLabel, c);
		
		JLabel AUCValueLabel = new JLabel();
		AUCValueLabel.setText("" + this.tfMotif.getMotif().getAUCValue());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(AUCValueLabel, c);
		ypos += 1;
		
		JLabel RankLabel = new JLabel();
		RankLabel.setText("Motif rank: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(RankLabel, c);
		
		JLabel RankValueLabel = new JLabel();
		RankValueLabel.setText("" + this.tfMotif.getMotif().getRank());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(RankValueLabel, c);
		ypos += 1;
		
		JLabel ClusterLabel = new JLabel();
		ClusterLabel.setText("Cluster rank: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(ClusterLabel, c);
		
		JLabel ClusterValueLabel = new JLabel();
		ClusterValueLabel.setText("" + this.tfMotif.getMotif().getClusterCode());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(ClusterValueLabel, c);
		ypos += 1;
		
		//empty label to pack the above for the same size as the logo
		JLabel emptyLabel = new JLabel();
		emptyLabel.setText("");
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(emptyLabel, c);
		ypos += 1;
		
		JLabel logoLabel = new JLabel(LogoUtilities.createImageIcon(this.tfMotif.getMotif().getName()));
		c.gridx = 2;
		c.gridy = yBgnLogo;
		c.gridwidth = 2;
		c.gridheight = ypos - yBgnLogo;
		panel.add(logoLabel, c);
		c.gridheight = 1;
		
		
		//TTTTTTT
		//	TT
		//	TT		
		//	TT
		//	TT		transcription factor
		JLabel TFTitleLabel = new JLabel();
		f = new Font("Serif", 0, 30);
		TFTitleLabel.setFont(f);
		TFTitleLabel.setText("Transcription Factor");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 2;
		panel.add(TFTitleLabel, c);
		ypos+=1;
		yBgnLogo = ypos;
		
		JLabel tfLabel = new JLabel();
		tfLabel.setText("Transcription factor: ");
		c.gridx = 0;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(tfLabel, c);
		
		JLabel tfNameLabel = new JLabel();
		tfNameLabel.setText(this.tfMotif.getTranscriptionFactor().getName());
		c.gridx = 1;
		c.gridy = ypos;
		c.gridwidth = 1;
		panel.add(tfNameLabel, c);
		ypos+=1;
		
		
		
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getMaxMotifSimilarityFDR())){
			
			if (this.tfMotif.getTranscriptionFactor().getSimilarMotifName() != null){
				JLabel tfsimMotifLabel = new JLabel();
				tfsimMotifLabel.setText("Similar motif: ");
				c.gridx = 0;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfsimMotifLabel, c);
			
				JLabel tfSimilarMotifLabel = new JLabel();
				tfSimilarMotifLabel.setText(this.tfMotif.getTranscriptionFactor().getSimilarMotifName());
				c.gridx = 1;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfSimilarMotifLabel, c);
				ypos+=1;
			}
			if (this.tfMotif.getTranscriptionFactor().getSimilarMotifDescription() != null){
				JLabel tfDescrLabel = new JLabel();
				tfDescrLabel.setText("Similar motif description: ");
				c.gridx = 0;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfDescrLabel, c);
				
				JLabel tfDescriptionLabel = new JLabel();
				tfDescriptionLabel.setText(this.tfMotif.getTranscriptionFactor().getSimilarMotifDescription());
				c.gridx = 1;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfDescriptionLabel, c);
				ypos+=1;
			}
			JLabel tfSMotifFDRLabel = new JLabel();
			tfSMotifFDRLabel.setText("Similar motif FDR: ");
			c.gridx = 0;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfSMotifFDRLabel, c);
		
			JLabel tfSimilarMotifFDRLabel = new JLabel();
			tfSimilarMotifFDRLabel.setText("" + this.tfMotif.getTranscriptionFactor().getMaxMotifSimilarityFDR());
			c.gridx = 1;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfSimilarMotifFDRLabel, c);
			ypos+=1;
		}
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getMinOrthologousIdentity())){
			if (this.tfMotif.getTranscriptionFactor().getOrthologousGeneName() != null){
				JLabel tfOrthGeneNameLabel = new JLabel();
				tfOrthGeneNameLabel.setText("Orthologous gene name: ");
				c.gridx = 0;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfOrthGeneNameLabel, c);
		
				JLabel tfOrthologousGeneNameLabel = new JLabel();
				tfOrthologousGeneNameLabel.setText("" + this.tfMotif.getTranscriptionFactor().getOrthologousGeneName());
				c.gridx = 1;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfOrthologousGeneNameLabel, c);
				ypos+=1;
			}
			if (this.tfMotif.getTranscriptionFactor().getOrthologousSpecies() != null){
				JLabel tfOrthSpeciesLabel = new JLabel();
				tfOrthSpeciesLabel.setText("Orthologous species: ");
				c.gridx = 0;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfOrthSpeciesLabel, c);
				
				JLabel tfOrthologousSpeciesLabel = new JLabel();
				tfOrthologousSpeciesLabel.setText("" + this.tfMotif.getTranscriptionFactor().getOrthologousSpecies());
				c.gridx = 1;
				c.gridy = ypos;
				c.gridwidth = 1;
				panel.add(tfOrthologousSpeciesLabel, c);
				ypos+=1;
			}
			JLabel tfOrthIdentifierLabel = new JLabel();
			tfOrthIdentifierLabel.setText("Orthologous identifier: ");
			c.gridx = 0;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfOrthIdentifierLabel, c);
		
			JLabel tfOrthologousIdentifierLabel = new JLabel();
			tfOrthologousIdentifierLabel.setText("" + this.tfMotif.getTranscriptionFactor().getMinOrthologousIdentity());
			c.gridx = 1;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfOrthologousIdentifierLabel, c);
			ypos+=1;
			
		}
		JTable targetGeneTable = new JTable(new CandidateTargetGeneTableModel(this.tfMotif.getMotif()));
		//Dimension preferredSizes = new Dimension(10, 10);
		//targetGeneTable.setPreferredSize(preferredSizes);
		c.gridx = 0;
		c.gridy = ypos;
		c.weightx=0.3;
		c.weighty=1;
		c.gridwidth = 2;
		c.gridheight = 1;
		ypos+=1;
		JScrollPane pane = new JScrollPane(targetGeneTable);
		Dimension preferredSize = new Dimension(100, 100);
		pane.setPreferredSize(preferredSize);
		//panel.add(new JScrollPane(targetGeneTable), c);
		panel.add(pane, c);
		
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getMaxMotifSimilarityFDR()) && this.tfMotif.getTranscriptionFactor().getSimilarMotifName() != null){
			//JLabel empty2Label = new JLabel();
			//empty2Label.setText("");
			//c.gridx = 1;
			//c.gridy = ypos;
			//c.gridwidth = 1;
			//panel.add(empty2Label, c);
			//ypos += 1;
			JLabel logoTFLabel = new JLabel(LogoUtilities.createImageIcon(this.tfMotif.getTranscriptionFactor().getSimilarMotifName()));
			c.gridx = 2;
			c.gridy = yBgnLogo;
			c.gridwidth = 2;
			c.gridheight = ypos - yBgnLogo;
			panel.add(logoTFLabel, c);
			c.gridheight = 1;
			c.gridwidth = 1;
		}
		
		
		
		
		
		return panel;
	}

}
