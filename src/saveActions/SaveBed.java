package saveActions;

import iRegulonInput.IRegulonResourceBundle;
import iRegulonOutput.SelectedMotif;

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
import exceptions.BedException;

public class SaveBed extends IRegulonResourceBundle {

	private SelectedMotif motif;
	
	
	public SaveBed(SelectedMotif motif){
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
        		java.net.URI uri = new java.net.URI("http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&hgt.customText=" + this.getBundle().getString("URL_motifBedGenerator") + "?" + this.generateParameters());
        		desktop.browse(uri);
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
	
	
	private String generateParameters(){
		String transcr = "";
		List<TranscriptionFactor> tfs = this.motif.getMotif().getTranscriptionFactors();
		for (TranscriptionFactor tf : tfs){
			if (! transcr.equals("")){
				transcr += ",";
			}
			transcr +=  tf;
		}
		transcr = transcr.substring(0, transcr.length() - 2);
		String data = "featureIDandTarget=" + this.motif.getMotif().getFeatureID();
		if (this.motif.getMotif().getCandidateTargetGenes().size() >= 1){
			data += ":" + this.motif.getMotif().getCandidateTargetGenes().get(0).getGeneName();
		}else{
			data += ":";
		}
		data += ":" + transcr;
		return data;
	}
	
	
	private String getBed() throws BedException{
	    String bed = ""; 
	    String data = this.generateParameters();
		try {
			URL url = new URL(this.getBundle().getString("URL_motifBedGenerator") + "?" + data);
			
			URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    //Delete first 2 lines
		    int lineNr = 0;
		    while ((line = rd.readLine()) != null) {
		    	if (lineNr >= 2){
		    		System.out.println(line);
		    		bed+=line + '\n';
		    	}
		    	lineNr++;
		    }
		    rd.close();
			return bed;
		}
		catch (Exception e) {
			System.err.println(bed);
			System.out.println(e.getMessage());
			if (bed.isEmpty()){
				bed = e.getMessage();
			}
			throw new BedException(bed, e);
		}
	}
	
	public void saveBed(){
		JFrame frame = new JFrame();
		String name = this.motif.getMotif().getEnrichedMotifID();
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
		//System.out.println(selFile.getName());
		//System.out.println(selFile.getAbsolutePath());
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
					try{
						bed = this.getBed();
					}
					catch (BedException e){
						e.printStackTrace();
						System.out.println(e.getMessage());
						JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
								"<html> " +
								"<body>" +
								"An error has occured while generating your bed file." +
								"</body>" +
								"</html>");
					}
					output.write(bed);
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							"<html> " +
							"<body>" +
							"An error has occured while save your file." +
							"</body>" +
							"</html>");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html> " +
					"<body>" +
					"An error has occured while creating your file." +
					"</body>" +
					"</html>");
		}
	}
	
	
	private static class bedFileFilter extends FileFilter{

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
			return "UCSC bed files";
		}
		
	}
	
	
}
