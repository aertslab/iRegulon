package view.actions;

import org.cytoscape.util.swing.FileChooserFilter;


public final class FileTypes {
    public static final FileChooserFilter BED = new FileChooserFilter("UCSC BED files (*.bed)", "bed");

    public static final FileChooserFilter IRF = new FileChooserFilter("iRegulon files (*.irf)", "irf");
    public static final FileChooserFilter TSV = new FileChooserFilter("iRegulon tab-delimited files (*.tsv)", "tsv");

    public static final FileChooserFilter PNG = new FileChooserFilter("PNG files (*.png)", "png");

    private FileTypes() {
    }
}
