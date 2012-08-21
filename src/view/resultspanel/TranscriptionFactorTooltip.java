package view.resultspanel;

import domainmodel.TranscriptionFactor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;


public class TranscriptionFactorTooltip extends MouseMotionAdapter {
    private final JTable table;

    public TranscriptionFactorTooltip(JTable table) {
        this.table = table;
    }

    public void mouseMoved(MouseEvent e) {
        final Point p = e.getPoint();
        final int row = table.rowAtPoint(p);
        final int column = table.columnAtPoint(p);

        final TranscriptionFactorTableModel model = (TranscriptionFactorTableModel) table.getModel();
        final TranscriptionFactor tf = model.getTranscriptionFactorAtRow(row);

        if (column == 1) {
            String orthology = "<html>";
            if (tf.getOrthologousGeneName() != null) {
                orthology = orthology + "Orthologous Gene Name = " + tf.getOrthologousGeneName();
            }
            if (tf.getOrthologousSpecies() != null) {
                orthology = orthology + "<br/> Orthologous Species = " + tf.getOrthologousSpecies();
            }
            orthology = orthology + "</html>";
            if (orthology.equalsIgnoreCase("<html></html>")) {
                table.setToolTipText(null);
            } else {
                table.setToolTipText(orthology);
            }

        } else if (column == 2) {
            String motifSimilarity = "<html>";
            if (tf.getSimilarMotifName() != null) {
                motifSimilarity = motifSimilarity + "Similar motif name = " + tf.getSimilarMotifName();
            }
            if (tf.getSimilarMotifDescription() != null) {
                motifSimilarity = motifSimilarity + "<br/> Similar motif description = " + tf.getSimilarMotifDescription();
            }
            motifSimilarity = motifSimilarity + "</html>";
            if (motifSimilarity.equalsIgnoreCase("<html></html>")) {
                table.setToolTipText(null);
            } else {
                table.setToolTipText(motifSimilarity);
            }

        }
    }
}
