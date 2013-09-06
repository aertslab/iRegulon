package view.resultspanel.guiwidgets;

import domainmodel.Results;

import javax.swing.*;


public class SummaryLabel extends JLabel {
    public SummaryLabel(final Results results) {
        super();
        setText(results.getName());
        setToolTipText(createToolTipText(results));
    }

    private String createToolTipText(final Results results) {
        if (!results.hasParameters()) return "";
        final StringBuilder builder = new StringBuilder();
        builder.append("<html>");

        builder.append("<b>Name:</b> ");
        builder.append(results.getName());
        builder.append("<br/>");


        builder.append("<b>Species and nomenclature:</b> ");
        builder.append(results.getSpeciesNomenclature().toString());
        builder.append("<br/>");

        builder.append("<b>Mimumum NEScore:</b> ");
        builder.append(results.getEScore());
        builder.append("<br/>");

        builder.append("<b>Rank threshold for visualisation:</b> ");
        builder.append(results.getThresholdForVisualisation());
        builder.append("<br/>");

        builder.append("<b>ROC threshold for AUC calculation (%):</b> ");
        builder.append(results.getROCthresholdAUC());
        builder.append("<br/>");

        builder.append("<b>Minimum identity between orthologous genes:</b> ");
        builder.append(results.getMinOrthologous());
        builder.append("<br/>");

        builder.append("<b>Maximum false discovery rate (FDR) on motif similarity:</b> ");
        builder.append(results.getMaxMotifSimilarityFDR());
        builder.append("<br/>");

        builder.append("<b>Database:</b> ");
        builder.append(results.getDatabaseName());
        builder.append("<br/>");

        if (results.isRegionBased()) {
            builder.append("<b>Overlap fraction:</b> ");
            builder.append(results.getOverlap() );
            builder.append("<br/>");

            if (results.isDelineationBased()) {
                 builder.append("<b>Putative regulatory region:</b> ");
                 builder.append(results.getDelineationName());
                 builder.append("<br/>");
            } else {
                 builder.append("<b>Putative regulatory region:</b> [TSS-");
                 builder.append(results.getUpstream());
                 builder.append("kb,TSS+");
                 builder.append(results.getDownstream());
                 builder.append("kb]<br/>");
            }
        }

        builder.append("</html>");
        return builder.toString();
    }
}
