package view.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import persistence.PersistenceUtilities;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class SaveLoadDialogs {

    private String saveName;

    public SaveLoadDialogs() {
        this.saveName = null;
    }

    /**
     * Gives the user a panel for choosing where to save their results
     * and saves the results in the chosen file.
     *
     * @param xml
     * @param name of the job
     */
    public static boolean showDialog(String xml, String name, String extension) {
        JFrame frame = new JFrame();

        // Create a file chooser.
        File locFile = CytoscapeInit.getMRUD();
        String location = "";
        if (locFile == null) {
            location = "user.home";
        } else {
            location = locFile.getPath();
        }
        JFileChooser fc = new JFileChooser(new File(location));

        // Display "All Files" filter at the bottom of the list.
        fc.setAcceptAllFileFilterUsed(false);
        if (extension == PersistenceUtilities.NATIVE_FILE_EXTENSION) {
            fc.addChoosableFileFilter(new FileNameExtensionFilter("iRegulon files (*.irf)", "irf"));
        } else {
            fc.addChoosableFileFilter(new FileNameExtensionFilter("iRegulon tab-delimited files (*.tsv)", "tsv"));
        }
        fc.setAcceptAllFileFilterUsed(true);

        fc.setSelectedFile(new File(name + extension));
        int result = fc.showSaveDialog(frame);
        boolean write = false;

        if (result == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
            FileFilter ff = fc.getFileFilter();
            if (!ff.accept(selFile)) {
                if (extension == PersistenceUtilities.NATIVE_FILE_EXTENSION || extension == PersistenceUtilities.TSV_FILE_EXTENSION) {
                    selFile = new File(selFile.getAbsolutePath() + extension);
                } else {
                    selFile = new File(selFile.getAbsolutePath());
                }
            }
            BufferedWriter output;
            boolean success;
            try {
                success = selFile.createNewFile();
                if (success) {
                    // File did not exist and was created.
                    write = true;
                } else {
                    // File already exists.
                    write = false;
                    int n = JOptionPane.showConfirmDialog(frame,
                            "Do you want to overwrite the file '" + selFile.getName() + "'?",
                            "Overwrite?",
                            JOptionPane.YES_NO_OPTION);
                    if (n == 0) {
                        write = true;
                    }

                }
                if (write) {
                    try {
                        output = new BufferedWriter(new FileWriter(selFile));
                        output.write(xml);
                        output.close();
                    } catch (IOException e) {
                        write = false;
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                "<html> " +
                                "<body>" +
                                "An error has occurred while trying to save the file '" + selFile.getName() + "'." +
                                "</body>" +
                                "</html>");
                    }
                }
            } catch (IOException e1) {
                write = false;
                e1.printStackTrace();
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "<html> " +
                        "<body>" +
                        "An error has occurred while creating the file '" + selFile.getName() + "'." +
                        "</body>" +
                        "</html>");
            }
        }

        return write;
    }


    /**
     * Gives the user a pane for choosing the file they want to open.
     * The file is returned as a string
     *
     * @return String xml
     */
    public String openDialogue() {
        JFrame frame = new JFrame();

        // Create a file chooser.
        File locFile = CytoscapeInit.getMRUD();
        String location = "";
        if (locFile == null) {
            location = "user.home";
        } else {
            location = locFile.getPath();
        }
        JFileChooser fc = new JFileChooser(new File(location));
        // Display "All Files" filter at the bottom of the list.
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("iRegulon files (*.irf)", "irf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Old iRegulon files (*.ctf)", "ctf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("All iRegulon files (*.ctf and *.irf)", "ctf", "irf"));
        fc.setAcceptAllFileFilterUsed(true);

        fc.showOpenDialog(frame);
        File selFile = fc.getSelectedFile();
        if (selFile != null) {
            String name = selFile.getName();
            if (!selFile.isFile()) {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "<html> " +
                                "<body>" +
                                "File '" + name + "' does not exist." +
                                "</body>" +
                                "</html>");
                return null;
            }
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

                // Get the file's size and then map it into memory.
                int sz = (int) fileChannel.size();
                MappedByteBuffer bb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, sz);

                // Decode the file into a char buffer.
                CharBuffer cb = decoder.decode(bb);

                String xml = cb.toString();
                return xml;

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "<html> " +
                                "<body>" +
                                "An error has occurred while opening the file '" + name + "'." +
                                "</body>" +
                                "</html>");
            }
        }
        return null;
    }

    /**
     * Gives the user a panel for choosing where to save the logo.
     *
     * @param fullSizedLogoFileURL
     * @param MotifName
     */
    public static boolean saveLogo(java.net.URL fullSizedLogoFileURL, String MotifName) {
        if (fullSizedLogoFileURL == null) {
            return false;
        }

        JFrame frame = new JFrame();

        // Create a file chooser.
        File locFile = CytoscapeInit.getMRUD();
        String location = "";
        if (locFile == null) {
            location = "user.home";
        } else {
            location = locFile.getPath();
        }
        JFileChooser fc = new JFileChooser(new File(location));

        // Display "All Files" filter at the bottom of the list.
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG files (*.png)", "png"));
        fc.setAcceptAllFileFilterUsed(true);

        fc.setSelectedFile(new File(MotifName + ".png"));
        int result = fc.showSaveDialog(frame);
        boolean write = false;

        if (result == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
            FileFilter ff = fc.getFileFilter();
            selFile = new File(selFile.getAbsolutePath());
            if (!ff.accept(selFile)) {
                selFile = new File(selFile + ".png");
            }
            boolean success;
            try {
                success = selFile.createNewFile();
                if (success) {
                    // File did not exist and was created.
                    write = true;
                } else {
                    // File already exists.
                    write = false;
                    int n = JOptionPane.showConfirmDialog(frame,
                            "Do you want to overwrite the file '" + selFile.getName() + "'?",
                            "Overwrite?",
                            JOptionPane.YES_NO_OPTION);
                    if (n == 0) {
                        write = true;
                    }

                }
                if (write) {
                    byte[] buffer = new byte[8 * 1024];
                    String LogoFilename = selFile.getAbsolutePath();
                    try {
                        InputStream input = fullSizedLogoFileURL.openStream();
                        OutputStream output = new FileOutputStream(LogoFilename);
                        try {
                            output.write(input.read());
                            int bytesRead;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } finally {
                            input.close();
                            output.close();
                        }
                    } catch (IOException e) {
                        write = false;
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                "<html> " +
                                        "<body>" +
                                        "An error has occurred while trying to save the logo file '" + selFile.getName() + "'." +
                                        "</body>" +
                                        "</html>");
                    }
                }
            } catch (IOException e1) {
                write = false;
                e1.printStackTrace();
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "<html> " +
                                "<body>" +
                                "An error has occurred while creating the file '" + selFile.getName() + "'." +
                                "</body>" +
                                "</html>");
            }
        }

        return write;
    }

    public String getSaveName() {
        return this.saveName;
    }
}

