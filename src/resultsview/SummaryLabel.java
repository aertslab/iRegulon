package resultsview;

import domainmodel.Results;

import javax.swing.*;


public class SummaryLabel extends JLabel {
    public SummaryLabel(final Results results) {
        setText(results.getName());
        setToolTipText(createToolTipText(results));
    }

    private String createToolTipText(Results results) {
        if (results.hasParameters()){
            String parameters = "<html>"
                        + "Name:  " + results.getName()
                        + "<br/>"
                        + "Species and nomenclature: " + results.getSpeciesNomenclature().toString()
                        + "<br/>"
                        + "Minimal NEscore: " + results.getEScore()
                        + "<br/>"
                        + "Threshold for visualisation: " + results.getThresholdForVisualisation()
                        + "<br/>"
                        + "ROC threshold AUC: " + results.getROCthresholdAUC()
                        + "<br/>"
                        + "minimal orthologous: " + results.getMinOrthologous()
                        + "<br/>"
                        + "maximal motif similarity: " + results.getMaxMotifSimilarityFDR()
                        + "<br/>"
                        + "<br/>"
                        + "database: " + results.getDatabaseName()
                        + "<br/>";
            if (results.isRegionBased()){
                parameters += "overlap: " + results.getOverlap() + "<br/>";
                if (results.isDelineationBased()){
                    parameters += "Delineation: " + results.getDelineationName();
                } else {
                    parameters += "Upstream: " + results.getUpstream() + " kb <br/>";
                    parameters += "Downstream: " + results.getDownstream() + " kb";
                }
            }
            parameters += "</html>";
            return parameters;
        } else {
            return "";
        }
    }
}
