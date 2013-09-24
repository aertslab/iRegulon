package view;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;
import view.actions.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;


public class IRegulonPlugin extends CytoscapePlugin {
    private static final String IREGULON_LINK_OUT = "edgelinkouturl.iRegulon";

    private final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");

    /*
    * Start the plugin.
    */
    public IRegulonPlugin() {
        /* 1. Add menu item ... */
        final JMenu menu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
        menu.add(createMenu());

        /* 2. Add linkOut item ... */
        CytoscapeInit.getProperties().put(IREGULON_LINK_OUT, bundle.getString("URL_UCSC_LinkOut"));

        /* 3. Install visual style ... */
        IRegulonVisualStyle.installVisualStyle();
    }

    private JMenu createMenu() {
        final JMenu menu = new JMenu(bundle.getString("plugin_name"));
        menu.setToolTipText(bundle.getString("plugin_description"));

        menu.add(new JMenuItem(new OpenParametersFormAction()));
        final JMenuItem menuItem = new JMenuItem(new OpenQueryMetatargetomeFormAction(QueryMetatargetomeAction.DEFAULT_PARAMETERS, null));
        menu.add(menuItem);

        menu.addSeparator();

        menu.add(new JMenuItem(new AddParametersFormToSidePanelAction()));

        menu.addSeparator();

        menu.add(new JMenuItem(new LoadResultsAction()));

        menu.addSeparator();

        menu.add(new JMenuItem(new HelpAction()));

        menu.add(new JMenuItem(new AboutAction()));

        return menu;
    }

    private final class HelpAction extends ResourceAction {
        private static final String NAME = "action_help";

        public HelpAction() {
            super(NAME);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Desktop.getDesktop().browse(URI.create(bundle.getString("help_page")));
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private final class AboutAction extends ResourceAction {
        private static final String NAME = "action_about";

        public AboutAction() {
            super(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedImage wPic = null;

            try {
                wPic = ImageIO.read(this.getClass().getResource(bundle.getString("logo_iregulon")));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            JLabel wIcon = new JLabel(new ImageIcon(wPic));


            JEditorPane aboutText = new JEditorPane();

            aboutText.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        if (Desktop.isDesktopSupported()) {
                            final Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(URI.create(e.getURL().toString()));
                            } catch (IOException e2) {
                                JOptionPane.showMessageDialog(null,
                                        "Failed to open the requested URL in a web browser.",
                                        "Launching web browser failed", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Opening a web browser on this platform is not supported.",
                                    "Launching web browser not supported", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            });

            aboutText.setContentType("text/html");
            aboutText.setText(
                    "<html>" +
                            "<h1>" + bundle.getString("plugin_name") + "</h1>" +
                            "<p><b>Version:</b> " + bundle.getString("version") + "<br/>" +
                            "<b>Build ID:</b> " + bundle.getString("build_ID") + "<br/>" +
                            "<b>Released:</b> " + bundle.getString("release_date") + "<br/>" +
                            "<b>Developers:</b> Koen Herten, Bram Van de Sande, Gert Hulselmans and Rekin's Janky." + "<br/>" +
                            "<b>Copyright:</b> 2012-2013 KU Leuven." + "<br/>" +
                            "<b>Website:</b> " + bundle.getString("website") + "<br/>" +
                            "<b>Download:</b> " + bundle.getString("download_page") + "<br/>" +
                            "<b>Contact:</b> " + bundle.getString("contact") + "<br/>" +
                            "<b>Citation:</b> Rekin’s Janky, Annelien Verfaillie, Bram Van de Sande, Laura Standaert, Valerie Christiaens, Gert Hulselmans, Koen Herten, Marina Naval Sanchez, Delphine Potier, Dmitry\n" +
                            "Svetlichnyy, Hana Imrichová, Zeynep Kalender Atak, Mark Fiers, Jean-Christophe Marine, and Stein Aerts. <u>Detection of cis-regulatory master regulators enables reverse\n" +
                            "engineering human regulons from cancer gene signatures.</u> <i>Manuscript submitted.</i>" + "<br/>" +
                            "<b>Comment:</b> A full version of the plugin including TRANSFAC Professional motifs (<a href=\"http://www.gene-regulation.com/pub/databases.html\">http://www.gene-regulation.com/pub/databases.html</a>) is provided from the website  " + bundle.getString("download_page") + ". To download the TRANSFAC PRO version, the user will need to have a valid subscription to TRANSFAC Professional." +
                            "</p>" +
                            "<h2>Software License Agreement (hereinafter \"Agreement\")</h2>" +
                            "<p>This Software is developed by and on behalf of the Laboratory of Computational Biology of the KU Leuven and is owned by the Katholieke Universiteit Leuven (hereinafter referred to as \"KU LEUVEN\"). By downloading or installing the Software you agree with the terms and conditions below.</p>" +
                            "<h3>Article 1 - Definitions</h3>" +
                            "<p><b>1.1</b> \"Software\" shall mean the iRegulon Cytoscape plugin as available on the website " + bundle.getString("website") + " and via the Cytoscape App Store as jar file. The software that you install is the client of a client-server system whereby the client sends a request to a web server (for example, running at the KU Leuven). The request consists of a list of genes (i.e., a list of nodes in a Cytoscape network), along with parameters. The client receives the results of the motif discovery and motif2TF mapping as a table with motifs, candidate transcription factors, and predicted target genes. These results can be used to 'annotate' the active Cytoscape networks, and can be saved as text or xml format.</p>" +
                            "<p><b>1.2</b> \"Effective Date\" shall mean the date on which you download or install the iRegulon Cytoscape plugin (as available on " + bundle.getString("website") + " and via the Cytoscape App store) on your system and which provide you access to the iRegulon webapplication.</p>" +
                            "<p><b>1.3</b> \"Non-commercial User\" shall mean a person using the Software for non-commercial research purposes on its own behalf or on behalf of a legal entity. In case the Non-commercial User is acting on behalf of an entity, use of the Software is only permitted by the Non-Commercial User in the event the Software License Agreement is approved and signed by the authorized signatories of such legal entity and returned to KU LEUVEN.</p>" +
                            "<h3>Article 2 - License</h3>" +
                            "<p><b>2.1</b> As long as you qualify as a Non-commercial User, KU LEUVEN hereby grants you a royalty-free, non-exclusive, non-transferable license to the Software supplied by KU LEUVEN for strictly internal, non-commercial research use only.\n</p>" +
                            "<p><b>2.2</b> You acknowledge and agree that you may not use the Software for non-research and/or commercial purposes without first obtaining an appropriate license from KU LEUVEN. For the purposes of this Agreement \"use for commercial purposes\" shall include the use or transfer of the Software for a consideration as well as the use of the Software to support commercial activities including providing services with the Software to third parties. Any use of Software for commercial purposes shall be deemed a breach of this Agreement for which KU LEUVEN shall be entitled to whatever remedies it may have under law or equity, including recovery of any damages.</p>" +
                            "<p><b>2.3</b> You shall not sublicense any of your rights to the Software. Neither will you transfer the Software to a third party, unless prior written agreement of KU LEUVEN has been obtained. Furthermore, you are not allowed to reverse engineer, decompile or disassemble the Software.</p>" +
                            "<p><b>2.4</b> This Agreement does not grant or imply any right, title or license into or under any third party software (including but not limited to e.g. Cytoscape software) or intellectual property rights relating thereto.</p>" +
                            "<h3>Article 3 - Ownership</h3>" +
                            "<p>The Software is copyrighted and KU LEUVEN retains all title and ownership to the Software. Nothing in this Agreement shall preclude KU LEUVEN from entering into agreements with third parties concerning the Software.\n</p>" +
                            "<h3>Article 4 - Publication</h3>" +
                            "<p>Non-commercial User shall acknowledge KU LEUVEN as the owner of the Software and shall include a reference to the iRegulon publication and to KU LEUVEN in any manuscript describing data obtained using the Software.</p>" +
                            "<h3>Article 5 - No Support</h3>" +
                            "<p>This license does not entitle you to receive from KU LEUVEN technical support, telephone assistance, or enhancements or updates to the Software. Any support that may be given by KU LEUVEN on voluntary basis is provided \"as is\" and KU LEUVEN makes no representations or warranties of any type whatsoever, express or implied, regarding the provided support.</p>" +
                            "<h3>Article 6 - Warranty</h3>" +
                            "<p><b>6.1</b> The Software is provided \"as is\" by KU LEUVEN without warranty of any kind, whether express or implied. KU LEUVEN specifically disclaims the implied warranties of merchantability and fitness for a particular purpose or that the use of the Software will not infringe any patents, copyrights or trademarks or other rights of third parties. The entire risk as to the quality and performance of the Software is borne by you.</p>" +
                            "<p><b>6.2</b> Without prejudice to KU LEUVEN's liability for damages caused by KU LEUVEN's willful misconduct, KU LEUVEN shall not be liable for any loss, direct or indirect damage or other liability incurred by you or any third party in connection with the Software licensed by KU LEUVEN under this Agreement. Under no circumstances shall KU LEUVEN be liable for any direct, indirect, special, incidental, or consequential damages arising out of any performance of this Agreement, whether such damages are based on contract, tort or any other legal theory.</p>" +
                            "<h3>Article 7 - Indemnification</h3>" +
                            "<p>You will indemnify, defend and hold harmless KU LEUVEN, its directors, officers, employees and agents from and against all liability, losses, damages and expenses (including attorney's fees and costs) arising out of any claims, demands, actions or other proceedings made or instituted by any third party against any of them and arising out of or relating to any breach of this Agreement by you, or any use or disclosure of the Software by you, unless such claims or liability result from KU LEUVEN's willful misconduct.</p>" +
                            "<h3>Article 8 - Term</h3>" +
                            "<p><b>8.1</b> This Agreement is effective from the Effective Date until you delete the Software and any and all related files from your computing system. This Agreement will terminate immediately without notice from KU LEUVEN if you fail to comply with any provision of this Agreement. KU LEUVEN may terminate this Agreement at any time by inactivating your access to the Software.</p>" +
                            "<p><b>8.2</b> In case of termination the provisions of Article 3, 4, 6, and 7 shall remain in full force and effect.</p>" +
                            "<h3>Article 9 - Miscellaneous</h3>" +
                            "<p><b>9.1</b> Any notice authorized or required to be given to KU LEUVEN under this Agreement shall be in writing and shall be deemed to be duly given if sent by registered post to: KU Leuven Research & Development, Waaistraat 6, B-3000 Leuven, Belgium with attention to IP Department.</p>" +
                            "<p><b>9.2</b> The terms and conditions herein contained constitute the entire agreement between the Parties and supersede all previous agreements and understandings, whether oral or written, between the parties hereto with respect to the subject matter thereof.</p>" +
                            "<h3>Article 10 - Disputes</h3>" +
                            "<p>In the event of disputes in the interpretation and/or performance of this Agreement, the parties shall first undertake to settle their differences amicably. If no amicable settlement can be reached concerning the execution and/or interpretation of this Agreement, such conflict shall be brought before the courts of Leuven and Belgian Law shall be applicable.</p>" +
                            "</html>");

            aboutText.setEditable(false);
            aboutText.setBackground(null);
            aboutText.setBorder(null);
            aboutText.setCaretPosition(0);
            aboutText.setPreferredSize(new Dimension(550, 1600));

            JScrollPane scrollPane = new JScrollPane(aboutText);
            scrollPane.setPreferredSize(new Dimension(700, 580));
            scrollPane.getVerticalScrollBar().setValue(0);

            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(710, 710));
            panel.add(wIcon, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JOptionPane optionpane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

            JDialog dialog = optionpane.createDialog(null, "About...");
            dialog.setVisible(true);
        }
    }
}
