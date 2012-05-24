package saveActions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;




import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

public class SaveLoadDialogs {
	
	private String saveName;
	
	public SaveLoadDialogs(){
		this.saveName = null;
	}

	/**
	 * Gives the user a panel for choosing where to save there results
	 * and saves the result in the chosen file
	 * @param xml
	 * @param name of the job
	 */
	public static boolean saveDialogue(String xml, String name, String extension){
		JFrame frame = new JFrame();

		// Create a file chooser
		File locFile = CytoscapeInit.getMRUD();
		String location = "";
		if (locFile == null){
			location = "user.home";
		}else{
			location = locFile.getPath();
		}
		JFileChooser fc = new JFileChooser(new File(location));
		irfFileFilter irfFilter = new irfFileFilter();
		fc.addChoosableFileFilter(irfFilter);
		fc.setSelectedFile(new File(name + extension));
		fc.showSaveDialog(frame);
		File selFile = fc.getSelectedFile();
		if (! irfFilter.accept(fc.getSelectedFile())){
			if (extension == ".irf"){
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
					output.write(xml);
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
		return write;
	}
	
	
	/**
	 * Gives the user a pane for choosing the file they want to open.
	 * The file is returned as a string
	 * @return String xml
	 */
	public String openDialogue(){
		JFrame frame = new JFrame();

		// Create a file chooser
		File locFile = CytoscapeInit.getMRUD();
		String location = "";
		if (locFile == null){
			location = "user.home";
		}else{
			location = locFile.getPath();
		}
		JFileChooser fc = new JFileChooser(new File(location));
		ctfFileFilter ctfFilter = new ctfFileFilter();
		irfFileFilter irfFilter = new irfFileFilter();
		irfOrctfFileFilter irfOrctfFilter = new irfOrctfFileFilter();
		fc.addChoosableFileFilter(ctfFilter);
		fc.addChoosableFileFilter(irfFilter);
		fc.addChoosableFileFilter(irfOrctfFilter);
		fc.showOpenDialog(frame);
		File selFile = fc.getSelectedFile();
		if (selFile != null){
			if (irfOrctfFilter.accept(selFile)){
				String name = selFile.getName();
				String[] names = name.split("\\.");
				this.saveName = names[0];
				try {
					/*
					FileInputStream fstream = new FileInputStream(selFile);
					System.out.println("reading file");
			
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					//Read File Line By Line
					String xml = "";
					while ((strLine = br.readLine()) != null)   {
					// Print the content on the console
						xml += strLine + "\n";
						//System.out.println(strLine);
					}	
					//Close the input stream
					in.close();
					return xml;
					*/
					Charset charset = Charset.forName("ISO-8859-15");
				    CharsetDecoder decoder = charset.newDecoder();

					FileInputStream fis = new FileInputStream(selFile);
					FileChannel fileChannel = fis.getChannel();

					// Get the file's size and then map it into memory
					int sz = (int)fileChannel.size();
					MappedByteBuffer bb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, sz);

					// Decode the file into a char buffer
					CharBuffer cb = decoder.decode(bb);
					
					String xml = cb.toString();
					return xml;


			
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"<html> " +
						"<body>" +
						"An error has occured while opening your file." +
						"</body>" +
						"</html>");
				}catch (IOException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"<html> " +
						"<body>" +
						"An error has occured while opening your file." +
						"</body>" +
						"</html>");
				}
			}else{
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"<html> " +
						"<body>" +
						"Wrong file type selected." +
						"</body>" +
						"</html>");
			}
		}
		return null;
	}
	
	public String getSaveName(){
		return this.saveName;
	}
	
	
	private static class ctfFileFilter extends FileFilter{

		@Override
		public boolean accept(File f) {
			String name = f.getName();
			String[] names = name.split("\\.");
			if (names[names.length -1].equals("ctf")){
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "old iRegulon files (ctf)";
		}
		
	}
	
	private static class irfFileFilter extends FileFilter{

		@Override
		public boolean accept(File f) {
			String name = f.getName();
			String[] names = name.split("\\.");
			if (names[names.length -1].equals("irf")){
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "iRegulon files (irf)";
		}
		
	}
	
	private static class irfOrctfFileFilter extends FileFilter{

		@Override
		public boolean accept(File f) {
			String name = f.getName();
			String[] names = name.split("\\.");
			if (names[names.length -1].equals("irf") || names[names.length -1].equals("ctf")){
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "all kind of iRegulon files (ctf and irf)";
		}
		
	}

}
