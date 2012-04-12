package cisTargetOutput.DetailPanel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cisTargetX.InputView;

public class DetailFrameTFandMotif {
	
	private TFandMotifSelected tfMotif;
	private static Logos logo = new Logos();
	
	public DetailFrameTFandMotif(TFandMotifSelected tfMotif){
		this.tfMotif = tfMotif;
		String title = tfMotif.getMotif().getEnrichedMotifID() + " " + tfMotif.getTranscriptionFactor().getName();
		JFrame frame = new JFrame(title);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
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
		
		JLabel motifTitleLabel = new JLabel();
		Font f = new Font("Serif", 0, 30);
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
		motifNameLabel.setText(this.tfMotif.getMotif().getEnrichedMotifID());
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
		NEScoreLabel.setText("" + this.tfMotif.getMotif().getNeScore());
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
		AUCValueLabel.setText("" + this.tfMotif.getMotif().getAucValue());
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
		
		JLabel logoLabel = new JLabel(this.logo.createImageIcon(this.tfMotif.getMotif().getEnrichedMotifID()));
		c.gridx = 2;
		c.gridy = yBgnLogo;
		c.gridwidth = 1;
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
		
		
		
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getMotifSimilarityFDR())){
			
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
			tfSimilarMotifFDRLabel.setText("" + this.tfMotif.getTranscriptionFactor().getMotifSimilarityFDR());
			c.gridx = 1;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfSimilarMotifFDRLabel, c);
			ypos+=1;
		}
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getOrthologousIdentifier())){
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
			tfOrthologousIdentifierLabel.setText("" + this.tfMotif.getTranscriptionFactor().getOrthologousIdentifier());
			c.gridx = 1;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(tfOrthologousIdentifierLabel, c);
			ypos+=1;
			
		}
		if (! Float.isNaN(this.tfMotif.getTranscriptionFactor().getMotifSimilarityFDR()) && this.tfMotif.getTranscriptionFactor().getSimilarMotifName() != null){
			JLabel empty2Label = new JLabel();
			empty2Label.setText("");
			c.gridx = 1;
			c.gridy = ypos;
			c.gridwidth = 1;
			panel.add(empty2Label, c);
			ypos += 1;
			JLabel logoTFLabel = new JLabel(this.logo.createImageIcon(this.tfMotif.getTranscriptionFactor().getSimilarMotifName()));
			c.gridx = 2;
			c.gridy = yBgnLogo;
			c.gridwidth = 1;
			c.gridheight = ypos - yBgnLogo;
			panel.add(logoTFLabel, c);
			c.gridheight = 1;
		}
		
		
		
		return panel;
	}

}
