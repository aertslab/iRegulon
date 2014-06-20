package view.actions;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.EnhancerRegion;
import infrastructure.CytoscapeEnvironment;
import infrastructure.Logger;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.Refreshable;
import view.ResourceAction;
import view.resultspanel.MotifAndTrackListener;
import view.resultspanel.SelectedMotifOrTrack;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class BedExportAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_save_bed";

    private final ComputationalService service = new ComputationalServiceHTTP();
    private final SelectedMotifOrTrack selectedMotifOrTrack;

    public BedExportAction(final SelectedMotifOrTrack selectedMotifOrTrack) {
        super(NAME);
        this.selectedMotifOrTrack = selectedMotifOrTrack;

        final MotifAndTrackListener enablenessListener = new MotifAndTrackListener() {
            @Override
            public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
                refresh();
            }
        };
        this.selectedMotifOrTrack.registerListener(enablenessListener);

        refresh();
    }

    @Override
    public void refresh() {
        setEnabled(getSelectedMotifOrTrack().getMotifOrTrack() != null);
    }

    public SelectedMotifOrTrack getSelectedMotifOrTrack() {
        return selectedMotifOrTrack;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final JFileChooser fc = new JFileChooser(new File(getPathSuggestion()));
        // Display "All Files" filter at the bottom of the list.
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("UCSC BED files (*.bed)", "bed"));
        fc.setAcceptAllFileFilterUsed(true);

        fc.setSelectedFile(new File(getFilenameSuggestion()));
        fc.showSaveDialog(Cytoscape.getDesktop());
        FileFilter ff = fc.getFileFilter();

        final File selectedFile = ff.accept(fc.getSelectedFile())
                ? fc.getSelectedFile()
                : new File(fc.getSelectedFile().getAbsoluteFile() + "." + FileTypes.BED.getExtensions()[0]);

        BufferedWriter output = null;
        try {
            if (!selectedFile.createNewFile()) {
                // File already exists ...
                int n = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
                        "Do you want to overwrite the file '" + selectedFile.getName() + "'?",
                        "Overwrite?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            output = new BufferedWriter(new FileWriter(selectedFile));
            try {
                for (EnhancerRegion region : service.getEnhancerRegions(selectedMotifOrTrack.getMotifOrTrack())) {
                    output.write(region.toString());
                    output.write('\n');
                }
                output.flush();
            } catch (ServerCommunicationException e) {
                Logger.getInstance().error(e.getMessage());
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "An error has occurred while communicating with server.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            Logger.getInstance().error(e.getMessage());
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "An error has occurred while creating and saving the BED file '" + selectedFile.getName() + ".",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    //Nop ...
                }
            }
        }
    }
}
