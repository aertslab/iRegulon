package summaryTFs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import domainModel.Results;

public class TFOutputView {

	private CalculateTFs calTFs;
	private Results result;
	private String runName;
	
	public TFOutputView(String runName, Results result){
		this.runName = runName;
		this.result = result;
		this.calTFs = new CalculateTFs(result);
	}
	
	public JComponent createPanel(){
		//JPanel panel = new JPanel(layout);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
				
		JLabel label = new JLabel(this.runName);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=0;
		c.weighty=0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.ipadx = 0;
		c.ipady = 0;
		panel.add(label, c);
		if (this.result.hasParameters()){
			label.setText(this.result.getName());
			String parameters = "<html>" 
						+ "Name:  " + this.result.getName() 
						+ "<br/>"
						+ "Species and nomenclature: " + this.result.getSpeciesNomenclature().toString()
						+ "<br/>"
						+ "Minimal NEscore: " + this.result.getEScore()
						+ "<br/>"
						+ "Threshold for visualisation: " + this.result.getThresholdForVisualisation()
						+ "<br/>"
						+ "ROC threshold AUC: " + this.result.getROCthresholdAUC()
						+ "<br/>"
						+ "minimal orthologous: " + this.result.getMinOrthologous()
						+ "<br/>"
						+ "maximal motif similarity: " + this.result.getMaxMotifSimilarityFDR()
						+ "<br/>"
						+ "<br/>"
						+ "database: " + this.result.getDatabaseName()
						+ "<br/>";
			if (this.result.isRegionBased()){
				parameters += "overlap: " + this.result.getOverlap()
						+ "<br/>";
				if (this.result.isDelineationBased()){
					parameters += "Delineation: " + this.result.getDelineationName();
				}else{
					parameters += "Upstream: " + this.result.getUpstream() + " kb"
							+ "<br/>"
							+ "Downstream: " + this.result.getDownstream() + " kb";
				}
			}
			parameters += "</html>";
			label.setToolTipText(parameters);
		}
		
		
		List<PredictedTF> tfs = new ArrayList<PredictedTF>(this.calTFs.getCollectionPredictedTFs());
		Collections.sort(tfs);
		PredictedTFTableModel predictedTFModel = new PredictedTFTableModel(tfs);
		JTable table = new JTable(predictedTFModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(table);
	    panel.add(scrollPane);
	    c.gridx = 0;
		c.gridy = 1;
		c.weightx=1;
		c.weighty=0.8;
		c.gridwidth = 4;
		c.gridheight = 0;
		c.ipadx = 0;
		c.ipady = 100;
		panel.add(scrollPane, c);
		
		return panel;
	}
	
}
