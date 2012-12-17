package view.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import domainmodel.AbstractMotif;
import domainmodel.EnhancerRegion;
import infrastructure.Logger;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.resultspanel.MotifListener;
import view.resultspanel.Refreshable;
import view.resultspanel.SelectedMotif;

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
	private final SelectedMotif selectedMotif;

    private static final String BED_FILE_EXTENSION = "bed";

	public BedExportAction(final SelectedMotif selectedMotif) {
        super(NAME);
		this.selectedMotif = selectedMotif;

        final MotifListener enablenessListener = new MotifListener() {
            @Override
            public void newMotifSelected(AbstractMotif currentSelection) {
                refresh();
            }
        };
        this.selectedMotif.registerListener(enablenessListener);

        refresh();
	}

    @Override
    public void refresh() {
        setEnabled(getSelectedMotif().getMotif() != null);
    }

    public SelectedMotif getSelectedMotif() {
        return selectedMotif;
    }

    private String getPathSuggestion() {
        final File location = CytoscapeInit.getMRUD();
        return (location == null) ? "user.home" : location.getPath();
    }

    private String getFilenameSuggestion() {
        final AbstractMotif motif = getSelectedMotif().getMotif();
        return motif != null ? motif.getName() + "." + BED_FILE_EXTENSION : "";
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
                : new File(fc.getSelectedFile().getAbsoluteFile() + "." + BED_FILE_EXTENSION);

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
                for (EnhancerRegion region : service.getEnhancerRegions(selectedMotif.getMotif())) {
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
