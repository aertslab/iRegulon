package view.resultspanel.transcriptionfactorview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import domainmodel.Results;

public class EnrichedTranscriptionFactorsView {

	private EnrichedTranscriptionFactorCollection calTFs;

	public EnrichedTranscriptionFactorsView(final Results result){
		this.calTFs = new EnrichedTranscriptionFactorCollection(result);
	}
	
	public JComponent createPanel(){
		List<EnrichedTranscriptionFactor> tfs = new ArrayList<EnrichedTranscriptionFactor>(this.calTFs.getCollectionPredictedTFs());
		Collections.sort(tfs);
		EnrichedTranscriptionFactorCollectionTableModel predictedTFModel = new EnrichedTranscriptionFactorCollectionTableModel(tfs);
		JTable table = new JTable(predictedTFModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		return new JScrollPane(table);
	}
	
}
