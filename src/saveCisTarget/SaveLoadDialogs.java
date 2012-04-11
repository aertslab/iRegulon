package saveCisTarget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public static void saveDialogue(String xml, String name){
		JFrame frame = new JFrame();

		// Create a file chooser
		String filename = System.getProperty("user.home");
		JFileChooser fc = new JFileChooser(new File(filename));
		ctfFileFilter ctfFilter = new ctfFileFilter();
		fc.addChoosableFileFilter(ctfFilter);
		fc.setSelectedFile(new File(name + ".ctf"));
		fc.showSaveDialog(frame);
		File selFile = fc.getSelectedFile();
		
		if (! ctfFilter.accept(fc.getSelectedFile())){
			selFile = new File(selFile.getAbsoluteFile() + ".ctf");
		}
		//System.out.println(selFile.getName());
		//System.out.println(selFile.getAbsolutePath());
		BufferedWriter output;
		boolean success;
		boolean write;
		try {
			success = selFile.createNewFile();
			if (success) {
		        // File did not exist and was created
				write = true;
			} else {
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
					//System.out.println("file saved");
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html> " +
					"<body>" +
					"An error has occured while creating your file." +
					"</body>" +
					"</html>");
		}
	}
	
	
	/**
	 * Gives the user a pane for choosing the file they want to open.
	 * The file is returned as a string
	 * @return String xml
	 */
	public String openDialogue(){
		JFrame frame = new JFrame();

		// Create a file chooser
		String filename = System.getProperty("user.home");
		JFileChooser fc = new JFileChooser(new File(filename));
		ctfFileFilter ctfFilter = new ctfFileFilter();
		fc.addChoosableFileFilter(ctfFilter);
		fc.showOpenDialog(frame);
		File selFile = fc.getSelectedFile();
		//System.out.println("file found");
		if (selFile != null){
			if (ctfFilter.accept(selFile)){
				String name = selFile.getName();
				//System.out.println(name);
				String[] names = name.split("\\.");
				//System.out.println(names);
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
			//System.out.println(name);
			String[] names = name.split("\\.");
			//System.out.println(names);
			if (names[names.length -1].equals("ctf")){
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "CisTarget files";
		}
		
	}

}
