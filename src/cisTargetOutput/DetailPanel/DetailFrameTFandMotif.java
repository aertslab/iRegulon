package cisTargetOutput.DetailPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cisTargetX.InputView;

public class DetailFrameTFandMotif {
	
	private TFandMotifSelected tfMotif;
	
	public DetailFrameTFandMotif(TFandMotifSelected tfMotif){
		this.tfMotif = tfMotif;
		System.out.println(tfMotif.getMotif().getEnrichedMotifID());
		System.out.println(tfMotif.getTranscriptionFactor().getName());
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
		
		JLabel motifNameLabel = new JLabel();
		motifNameLabel.setText(this.tfMotif.getMotif().getEnrichedMotifID());
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(motifNameLabel, c);
		
		
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText("Description: ");
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(descriptionLabel, c);
		
		JLabel descriptionTextLabel = new JLabel();
		descriptionTextLabel.setText(this.tfMotif.getMotif().getDescription());
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(descriptionTextLabel, c);
		
		JLabel AUCLabel = new JLabel();
		AUCLabel.setText("AUC value: ");
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(AUCLabel, c);
		
		JLabel AUCValueLabel = new JLabel();
		AUCValueLabel.setText("" + this.tfMotif.getMotif().getAucValue());
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(AUCValueLabel, c);
		
		
		
		
		return panel;
	}

}
