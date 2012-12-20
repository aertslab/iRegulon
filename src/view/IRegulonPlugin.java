package view;

import infrastructure.Logger;
import view.actions.*;
import view.actions.OpenParametersFormAction;
import view.actions.AddParametersFormToSidePanelAction;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import java.util.*;

import cytoscape.view.CyHelpBroker;
import javax.help.HelpSet;

import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;


public class IRegulonPlugin extends CytoscapePlugin {
    private static final String HELP_SET_NAME = "/help/jhelpset.hs";
    private static final String IREGULON_LINK_OUT = "edgelinkouturl.iRegulon";

    private final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");

    /*
    * Start the plugin.
    */
	public IRegulonPlugin() {
        // 1. Add menu item ...
        final JMenu menu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
        menu.add(createMenu());

        // 2. Hook plugin help into the Cytoscape main help system ...
        addHelp();

        // 3. Add linkOut item ...
        CytoscapeInit.getProperties().put(IREGULON_LINK_OUT, bundle.getString("URL_UCSC_LinkOut"));

        // 4. Install visual style ...
        IRegulonVisualStyle.installVisualStyle();
	}
	
	private void addHelp() {
		try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final URL helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			if (!CyHelpBroker.addHelpSet(newHelpSet))
				Logger.getInstance().error("iRegulon: Failed to add help set.");
		} catch (final Exception e) {
			Logger.getInstance().error("iRegulon: Could not find help set: \"" + HELP_SET_NAME + "\".");
		}
	}

    private JMenu createMenu() {
        final JMenu menu = new JMenu(bundle.getString("plugin_name"));
        menu.setToolTipText(bundle.getString("plugin_description"));

        menu.add(new JMenuItem(new OpenParametersFormAction()));
        final JMenuItem menuItem = new JMenuItem(new OpenQueryMetatargetomeFormAction(QueryMetatargetomeAction.DEFAULT_PARAMETERS, null));
        menuItem.setIcon(null);
        menu.add(menuItem);

        menu.addSeparator();

        menu.add(new JMenuItem(new AddParametersFormToSidePanelAction()));

        menu.addSeparator();

        menu.add(new JMenuItem(new LoadResultsAction()));

        menu.addSeparator();

        final JMenuItem helpItem = menu.add(new HelpAction());
        CyHelpBroker.getHelpBroker().enableHelpOnButton(helpItem, "Topic", null);

        menu.add(new JMenuItem(new AboutAction()));

        return menu;
    }

    private static final class HelpAction extends ResourceAction {
        private static final String NAME = "action_help";

        public HelpAction() {
            super(NAME);
        }
    }

    private final class AboutAction extends ResourceAction {
        private static final String NAME = "action_about";

        public AboutAction() {
            super(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextPane aboutText = new JTextPane();
            aboutText.setContentType("text/html");
            aboutText.setText(
                "<html>" +
                "<h1>" + bundle.getString("plugin_name") + "</h1>" +
                "<p><b>Version:</b> " + bundle.getString("version") + "<br/>" +
                "<b>Build ID:</b> " + bundle.getString("build_ID") + "<br/>" +
                "<b>Released:</b> " + bundle.getString("release_date") + "<br/>" +
                "<b>Developers:</b> Koen Herten, Bram Van de Sande and Gert Hulselmans." + "<br/>" +
                "<b>Copyright:</b> 2012 KU Leuven." + "<br/>" +
                "<b>Website:</b> " + bundle.getString("website") + "<br/>" +
                "<b>Contact:</b> " + bundle.getString("contact") + "<br/>" +
                "<b>Paper:</b> " + bundle.getString("paper") + "<br/>" +
                "<b>Citation:</b> Rekin’s Janky, Annelien Verfaillie, Bram Van de Sande, Valerie Christiaens, Laura Standaerdt, Gert Hulselmans, Koen Herten, Zeynep Kalender, Jean-Christophe Marine, and Stein Aerts. iRegulon: Sequence-based Discovery of Human Regulons. Manuscript submitted." +
                "</p>" +
                "<h2>Terms and conditions</h2>" +
                "<p>It is preliminary stated that KU Leuven, more specifically its Department of Human Genetics, for the purposes of this Agreement represented by KU Leuven RESEARCH &amp; DEVELOPMENT, Groot Begijnhof, Benedenstraat 59, B-3000 Leuven, Belgium, (\"hereinafter referred to as KU Leuven\"), owns the copyright of the iRegulon software. Therefore it is convened the following.</p>" +
                "<h3>Use of the iRegulon software</h3>" +
                "<p>If you qualify as an Academic User, KU Leuven grants you a royalty-free, non-exclusive and non-transferable limited license (with no right to sub-license) to use the iRegulon software for your non-commercial research use only. Academic User means a user of the iRegulon software :</p>" +
                "<ul>" +
                "<li>Who is employed by, or a student enrolled at, or a scientist legitimately affiliated with an academic, non-profit or government institution; and</li>" +
                "<li>Whose use of the iRegulon software is on behalf of and in the interest of such academic, non-profit or government institution and is not on behalf of a commercial entity.</li>" +
                "</ul>" +
                "<h3>In exchange for this license to use the iRegulon software you agree to the following</h3>" +
                "<ul>" +
                "<li>Access and use this iRegulon software only if you qualify as an Academic User;</li>" +
                "<li>Discontinue use if at any time you fail to qualify as an Academic User;</li>" +
                "<li>Access and use this iRegulon software for non-commercial research purposes only;</li>" +
                "<li>At all times, comply with all applicable laws, rules and regulations with respect to your use of this iRegulon software;</li>" +
                "<li>Not to use or access the iRegulon software <strong>(a)</strong> directly on the behalf of any commercial entity or <strong>(b)</strong> upon the direct or indirect request or inquiry of any commercial entity.</li>" +
                "</ul>" +
                "<h3>Warranty, Liability and Indemnity</h3>" +
                "<p>The iRegulon software is provided on an \"as is,\" \"as available\" basis without warranties of any kind either express or implied. To the fullest extent possible pursuant to the applicable law, KU Leuven disclaims all warranties, express or implied, including, but not limited to, implied warranties of merchantability, fitness for a particular purpose, title and non-infringement or other violation of rights. KU Leuven does not warrant or make any representations regarding the use, validity, accuracy, or reliability of, or the results of the use of, or otherwise respecting, the information and materials on the iRegulon software or any sites linked to the iRegulon software.</p>" +
                "<p>Under no circumstances, including, but not limited to, negligence, shall KU Leuven or its suppliers be liable for any direct, indirect, special, incidental or consequential damages, or any other damages of any kind, including, but not limited to, loss of data or loss of profits arising out of the use, or the inability to use the site or the materials contained herein or accessed through, the iRegulon software, even if KU Leuven, or a KU Leuven authorized representative has been advised of the possibility of such damages.</p>" +
                "<h3>Intellectual Property Rights</h3>" +
                "<p>By using the iRegulon software, you do not acquire any intellectual property right to the iRegulon software or any part thereof, nor do you acquire any license or other rights under any patents, patent applications, trade secrets or other proprietary rights of KU Leuven, except as expressly granted herein.</p>" +
                "<h3>Publication</h3>" +
                "<p>The Academic User shall acknowledge KU Leuven as the provider of software and shall include a reference to Rekin's Janky, Annelien Verfaillie, Bram Van de Sande, Valerie Christiaens, Laura Standaerdt, Gert Hulselmans, Koen Herten, Zeynep Kalender, Jean-Christophe Marine, and Stein Aerts. iRegulon: Sequence-based Discovery of Human Regulons. Manuscript in preparation; in any manuscript describing data obtained using Licensed Software.<br/><br/></p>" +
                "</html>");
            aboutText.setEditable(false);
            aboutText.setBackground(null);
            aboutText.setBorder(null);
            aboutText.setCaretPosition(0);
            aboutText.setPreferredSize(new Dimension(550,1600));

            JScrollPane scrollPane = new JScrollPane(aboutText);
            scrollPane.setPreferredSize(new Dimension(700, 700));

            JOptionPane pane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);

            JDialog dialog = pane.createDialog(null, "About...");
            dialog.setVisible(true);
        }
    }
}
