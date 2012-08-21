package view.actions;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import persistence.BedConversionUtilities;
import persistence.BedException;
import view.IRegulonResourceBundle;
import view.resultspanel.SelectedMotif;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import domainmodel.TranscriptionFactor;


public class BedUtilities extends IRegulonResourceBundle {
    private final CyLogHandler logger = ConsoleLogger.getLogger();

	private final SelectedMotif motif;
	
	
	public BedUtilities(SelectedMotif motif){
		this.motif = motif;
	}
	
	public void openUCSC(){
		boolean canconnect = true;
		if( !java.awt.Desktop.isDesktopSupported() ) {
            System.err.println( "Desktop is not supported (fatal)" );
            canconnect = false;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            canconnect = false;
        }
        try {
        	if (canconnect){
                desktop.browse(new java.net.URI(BedConversionUtilities.INSTANCE.createUCSCResourceLink(this.motif.getMotif())));
        	}
        }
        catch ( Exception e ) {
        	e.printStackTrace();
        	System.err.println(e.getMessage());
        	JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html> " +
					"<body>" +
					"An error has occured while linking to the UCSC browser or by creating your bed." +
					"</body>" +
					"</html>");
        }
        if (!canconnect){
        	JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html> " +
					"<body>" +
					"Your system doesn't support this link to the UCSC browser." +
					"</body>" +
					"</html>");
        }
	}
	
	public void saveBed(){
		JFrame frame = new JFrame();
		String name = this.motif.getMotif().getName();
		String extension = ".bed";

		// Create a file chooser
		File locFile = CytoscapeInit.getMRUD();
		String location = "";
		if (locFile == null){
			location = "user.home";
		}else{
			location = locFile.getPath();
		}
		JFileChooser fc = new JFileChooser(new File(location));
		bedFileFilter bedFilter = new bedFileFilter();
		fc.addChoosableFileFilter(bedFilter);
		fc.setSelectedFile(new File(name + extension));
		fc.showSaveDialog(frame);
		File selFile = fc.getSelectedFile();
		if (! bedFilter.accept(fc.getSelectedFile())){
			if (extension == ".bed"){
				selFile = new File(selFile.getAbsoluteFile() + extension);
			}else{
				selFile = new File(selFile.getAbsoluteFile() + "");
			}
		}

		BufferedWriter output;
		boolean success;
		boolean write = false;
		try {
			success = selFile.createNewFile();
			if (success) {
		        // File did not exist and was created
				write = true;
			} else {
				System.out.println(selFile.toString());
				// File already exists
				write = false;
				int n = JOptionPane.showConfirmDialog(frame,
					    "Do you want to overwrite this file?",
					    "Overwrite?",
					    JOptionPane.YES_NO_OPTION);
				if (n == 0){
					write = true;
				}

			}
			if (write){
				try {
					output = new BufferedWriter(new FileWriter(selFile));
					String bed = "";
					try {
						bed = BedConversionUtilities.INSTANCE.getRegionsBed(this.motif.getMotif());
					} catch (BedException e){
						logger.handleLog(LogLevel.LOG_ERROR, e.getCause().getMessage());
						JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "An error has occured while generating your bed file.");
					}
					output.write(bed);
					output.close();
				} catch (IOException e) {
                    logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "An error has occured while save your file.");
				}
			}
		} catch (IOException e1) {
			logger.handleLog(LogLevel.LOG_ERROR, e1.getMessage());
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "An error has occured while creating your file.");
		}
	}
	
	private static class bedFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			String name = f.getName();
			String[] names = name.split("\\.");
			if (names[names.length -1].equals("bed")){
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "UCSC BED files";
		}
	}
}
