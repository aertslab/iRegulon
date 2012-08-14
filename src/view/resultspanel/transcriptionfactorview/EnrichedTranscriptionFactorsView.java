package view.resultspanel.transcriptionfactorview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifPopUpMenu;
import view.resultspanel.SelectedMotif;
import view.resultspanel.TFComboBox;
import view.resultspanel.renderers.FloatRenderer;


public class EnrichedTranscriptionFactorsView {
    private final Results results;
	private final List<EnrichedTranscriptionFactor> transcriptionFactors;

	public EnrichedTranscriptionFactorsView(final Results results) {
        this.results = results;
		this.transcriptionFactors = transform(results);
	}

    public Results getResults() {
        return results;
    }

    private List<EnrichedTranscriptionFactor> transform(final Results results) {
        final List<EnrichedTranscriptionFactor> transcriptionFactors = new ArrayList<EnrichedTranscriptionFactor>();
        for (Motif motif: results.getMotifs()) {
            for (TranscriptionFactor tf: motif.getTranscriptionFactors()){
                transcriptionFactors.add(new EnrichedTranscriptionFactor(tf, motif));
            }
        }
        Collections.sort(transcriptionFactors);
        return Collections.unmodifiableList(transcriptionFactors);
    }

    public List<EnrichedTranscriptionFactor> getTranscriptionFactors() {
        return transcriptionFactors;
    }

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB,
                                  final JComboBox filterAttributeTF, final JTextField filterValueTF) {
		final JTable table = new JTable(new EnrichedTranscriptionFactorTableModel(this.transcriptionFactors));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
        table.addMouseListener(new MotifPopUpMenu(selectedMotif, filterValueTF, getResults().isRegionBased()));
        table.setDefaultRenderer(Float.class, new FloatRenderer("0.###E0"));
		return new JScrollPane(table);
	}
}
