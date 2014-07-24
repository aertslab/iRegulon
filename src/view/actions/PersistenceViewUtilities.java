package view.actions;

import infrastructure.CytoscapeEnvironment;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collections;


public class PersistenceViewUtilities {

    private static final Charset CHARSET = Charset.forName("ISO-8859-15");

    private String iregulonJobName;

    public PersistenceViewUtilities() {
        this.iregulonJobName = null;
    }

    public static boolean saveToSelectedFile(final String data, FileChooserFilter fileChooserFilter) {
        /* Create a file chooser. */
        final FileUtil fileUtil = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(FileUtil.class);

        final String extension = fileChooserFilter.getExtensions()[0];

        File selectedFile = fileUtil.getFile(CytoscapeEnvironment.getInstance().getJFrame(),
                "Save " + extension + " file",
                FileUtil.SAVE,
                Collections.singleton(fileChooserFilter));

        if (selectedFile == null) {
            /* No file was selected. */
            return false;
        }

        if (!fileChooserFilter.accept(selectedFile)) {
            /* Add the right extension to the output filename, if it was not provided by the user. */
            selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
        }

        if (selectedFile.exists()) {
            final int dialogResult = JOptionPane.showConfirmDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "Do you want to overwrite " + selectedFile.getAbsolutePath() + "?",
                    "Overwrite?",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        /* Try to create the (new) file and if check if it has the right permissions. */
        try {
            selectedFile.createNewFile();
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "The file '" + selectedFile.getName() + "' has not the right permissions." +
                    "</body>" +
                    "</html>");
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "The file '" + selectedFile.getName() + "' could not be created." +
                    "</body>" +
                    "</html>");
            return false;
        }

        /* Write the data to the file. */
        try {
            final BufferedWriter output = new BufferedWriter(new FileWriter(selectedFile));
            output.write(data);
            output.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "An error has occurred while trying to save the file '" + selectedFile.getName() + "'." +
                    "</body>" +
                    "</html>");
            return false;
        }

        return true;
    }

    public String selectIRegulonFile() {
        final FileUtil fileUtil = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(FileUtil.class);
        final File selectedFile = fileUtil.getFile(CytoscapeEnvironment.getInstance().getJFrame(),
                "Select IRF file to load",
                FileUtil.LOAD, Collections.singleton(FileTypes.IRF));

        if (selectedFile != null) {
            if (FileTypes.IRF.accept(selectedFile)) {
                String filename = selectedFile.getName();
                /* Strip off the extension of the selected file. */
                this.iregulonJobName = filename.substring(0, filename.length() - FileTypes.IRF.getExtensions()[0].length() - 1);

                if (!selectedFile.isFile()) {
                    JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                            "<html> " +
                            "<body>" +
                            "File '" + filename + "' does not exist." +
                            "</body>" +
                            "</html>");
                    return null;
                }

                try {
                    CharsetDecoder decoder = CHARSET.newDecoder();

                    FileInputStream fis = new FileInputStream(selectedFile);
                    FileChannel fileChannel = fis.getChannel();

                    /* Get the file size and then map it into memory. */
                    int sz = (int) fileChannel.size();
                    MappedByteBuffer bb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, sz);

                    /* Decode the file into a char buffer. */
                    CharBuffer cb = decoder.decode(bb);

                    String xml = cb.toString();
                    return xml;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                            "<html> " +
                            "<body>" +
                            "File '" + filename + "' could not be found." +
                            "</body>" +
                            "</html>");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                            "<html> " +
                            "<body>" +
                            "An error has occurred while opening the file '" + filename + "'." +
                            "</body>" +
                            "</html>");
                }
            } else {
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                        "Wrong file type selected.");
            }
        }
        return null;
    }

    public static boolean saveLogo(URL fullSizedLogoFileURL) {
        if (fullSizedLogoFileURL == null) {
            return false;
        }

        /* Create a file chooser. */
        final FileUtil fileUtil = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(FileUtil.class);

        final String extension = FileTypes.PNG.getExtensions()[0];

        File selectedFile = fileUtil.getFile(CytoscapeEnvironment.getInstance().getJFrame(),
                "Save logo as PNG file",
                FileUtil.SAVE,
                Collections.singleton(FileTypes.PNG));

        if (selectedFile == null) {
            /* No file was selected. */
            return false;
        }

        if (!FileTypes.PNG.accept(selectedFile)) {
            /* Add the right extension to the output filename, if it was not provided by the user. */
            selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
        }

        if (selectedFile.exists()) {
            final int dialogResult = JOptionPane.showConfirmDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "Do you want to overwrite " + selectedFile.getAbsolutePath() + "?",
                    "Overwrite?",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        /* Try to create the (new) file and if check if it has the right permissions. */
        try {
            selectedFile.createNewFile();
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "The logo file '" + selectedFile.getName() + "' has not the right permissions." +
                    "</body>" +
                    "</html>");
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "The logo file '" + selectedFile.getName() + "' could not be created." +
                    "</body>" +
                    "</html>");
            return false;
        }

        final byte[] buffer = new byte[8 * 1024];
        final String LogoFilename = selectedFile.getAbsolutePath();

        /* Write the data to the file. */
        try {
            final InputStream input = fullSizedLogoFileURL.openStream();
            final OutputStream output = new FileOutputStream(LogoFilename);

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
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "<html> " +
                    "<body>" +
                    "An error has occurred while trying to save the logo file '" + selectedFile.getName() + "'." +
                    "</body>" +
                    "</html>");
            return false;
        }

        return true;
    }

    public String getIregulonJobName() {
        return this.iregulonJobName;
    }
}

