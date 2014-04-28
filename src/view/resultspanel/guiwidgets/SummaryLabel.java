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

        final boolean hasMotifCollection = results.hasMotifCollection();
        final boolean hasTrackCollection = results.hasTrackCollection();

        final StringBuilder builder = new StringBuilder();
        builder.append("<html>");

        builder.append("<b>Name:</b> ");
        builder.append(results.getName());
        builder.append("<br/>");


        builder.append("<b>Species and nomenclature:</b> ");
        builder.append(results.getSpeciesNomenclature().toString());
        builder.append("<br/>");

        builder.append("<b>Minimum NEScore:</b> ");
        builder.append(results.getEScore());
        builder.append("<br/>");

        if (hasMotifCollection) {
            builder.append("<b>Motif collection:</b> ");
            builder.append(results.getMotifCollection());
            builder.append("<br/>");
        }

        if (hasTrackCollection) {
            builder.append("<b>Track collection:</b> ");
            builder.append(results.getTrackCollection());
            builder.append("<br/>");
        }

        builder.append("<b>Rank threshold for visualisation:</b> ");
        builder.append(results.getThresholdForVisualisation());
        builder.append("<br/>");

        builder.append("<b>ROC threshold for AUC calculation (%):</b> ");
        builder.append(results.getROCthresholdAUC());
        builder.append("<br/>");

        if (hasMotifCollection) {
            builder.append("<b>Minimum identity between orthologous genes:</b> ");
            builder.append(results.getMinOrthologous());
            builder.append("<br/>");

            builder.append("<b>Maximum false discovery rate (FDR) on motif similarity:</b> ");
            builder.append(results.getMaxMotifSimilarityFDR());
            builder.append("<br/>");

            builder.append("<b>Motif rankings database:</b> ");
            builder.append(results.getMotifRankingsDatabaseName());
            builder.append("<br/>");
        }

        if (hasTrackCollection) {
            builder.append("<b>Track rankings database:</b> ");
            builder.append(results.getTrackRankingsDatabaseName());
            builder.append("<br/>");
        }

        builder.append("<b>Number of valid nodes:</b> ");
        builder.append(results.getGenes().size());
        builder.append("<br/>");

        if (results.isRegionBased()) {
            builder.append("<b>Overlap fraction:</b> ");
            builder.append(results.getOverlap());
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
