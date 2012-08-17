package view.resultspanel.transcriptionfactorview;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifPopUpMenu;
import view.resultspanel.MotifView;
import view.resultspanel.SelectedMotif;
import view.resultspanel.TFComboBox;
import view.resultspanel.renderers.FloatRenderer;


public class EnrichedTranscriptionFactorsView extends JPanel implements MotifView {
    private JTable table;

    private final Results results;
	private final List<EnrichedTranscriptionFactor> transcriptionFactors;

	public EnrichedTranscriptionFactorsView(final Results results) {
        this.results = results;
		this.transcriptionFactors = transform(results);
        setLayout(new BorderLayout());
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

    public Motif getSelectedMotif() {
        final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final EnrichedTranscriptionFactorTableModel model = (EnrichedTranscriptionFactorTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx).getMotif();
		}
    }

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB,
                                  final JComboBox filterAttributeTF, final JTextField filterValueTF) {
		table = new JTable(new EnrichedTranscriptionFactorTableModel(this.transcriptionFactors));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        TableMotifSelectionConnector.connect(table, selectedMotif);
        table.addMouseListener(new MotifPopUpMenu(selectedMotif, transcriptionFactorCB, getResults().isRegionBased()));

        //TODO: Use different selectedMotif object OR TODO: connect all other elements.

        table.setDefaultRenderer(Float.class, new FloatRenderer("0.###E0"));

        //TODO: Change Table Width

        add(new JScrollPane(table), BorderLayout.CENTER);
        return this;
	}
}
