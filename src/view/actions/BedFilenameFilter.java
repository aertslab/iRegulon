package view.actions;


import javax.swing.filechooser.FileFilter;
import java.io.File;


final class BedFilenameFilter extends FileFilter {
    static final String BED_FILE_EXTENSION = "bed";

    @Override
    public boolean accept(final File f) {
        return (f != null) && f.getName().endsWith("." + BED_FILE_EXTENSION);
    }

    @Override
    public String getDescription() {
        return "UCSC BED files";
    }
}