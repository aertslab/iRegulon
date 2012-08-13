package iRegulonInput;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import iRegulonOutput.ResultsView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import saveActions.SaveLoadDialogs;
import saveActions.SaveResults;

import java.util.*;


import cytoscape.view.CyHelpBroker;
import javax.help.HelpSet;

import java.net.URL;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;
import domainmodel.Results;


public class IRegulonPlugin extends CytoscapePlugin {
    private static final String HELP_SET_NAME = "/help/jhelpset.hs";
    private static final String IREGULON_LINK_OUT = "edgelinkouturl.iRegulon";


    private final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");
    private final CyLogHandler logger = ConsoleLogger.getLogger();


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
		final IRegulonVisualStyle style = new IRegulonVisualStyle();
	}
	
	private void addHelp() {
		try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final URL helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			if (!CyHelpBroker.addHelpSet(newHelpSet))
				logger.handleLog(LogLevel.LOG_ERROR, "iRegulon: Failed to add help set.");
		} catch (final Exception e) {
			logger.handleLog(LogLevel.LOG_ERROR, "iRegulon: Could not find help set: \"" + HELP_SET_NAME + "\".");
		}
	}

    private JMenu createMenu() {
        //iRegulon submenu
        final JMenu submenu = new JMenu(bundle.getString("plugin_name"));
        submenu.setToolTipText("Plugin for prediction of motifs, there transcription factors and there target genes.");

        //iRegulon panel
        JMenuItem item;
        item = new JMenuItem("Classical");
        item.setToolTipText("Do a classical  analysis.");
        item.addActionListener(new StartClasicalFrameAction());
        submenu.add(item);

        item = new JMenuItem("Other iRegulon options");
        item.setToolTipText("Do other options of iRegulon: like iRegulonDB");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "<html> " +
                                "<body>" +
                                "Comming soon." +
                                "</body>" +
                                "</html>");
            }
        });
        submenu.add(item);

        submenu.addSeparator();

        item = new JMenuItem("Get the panel");
        item.setToolTipText("Get the control panel.");
        item.addActionListener(new StartSidePanelAction());
        submenu.add(item);
        submenu.addSeparator();

        item = new JMenuItem("Load");
        item.setToolTipText("Load some previous results.");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveResults results = new SaveResults();
                SaveLoadDialogs dia = new SaveLoadDialogs();
                String xml = dia.openDialogue();
                if (xml != null){
                    try {
                        final Results result = results.loadResultsFromXML(xml);
                        final ResultsView output = new ResultsView(dia.getSaveName());
                        output.drawPanel(result);
                    } catch(Exception exception){
                        logger.handleLog(LogLevel.LOG_ERROR, exception.getMessage());
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), exception.getMessage());
                    }
                }
            }
        });
        submenu.add(item);
        submenu.addSeparator();

        //Help box
        item = new JMenuItem("Help");
        item.setToolTipText("Get some help");
        submenu.add(item);
        CyHelpBroker.getHelpBroker().enableHelpOnButton(item, "Topic", null);

        //About box
        item = new JMenuItem("About...");
        item.setToolTipText("About the plugin");
        item.addActionListener(new AboutAction());
        submenu.add(item);
        return submenu;
    }

    private final class AboutAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "<html> " +
                            "<body>" +
                            "" +
                            "<br/> <br/>" +
                            bundle.getString("plugin_name") +
                            "<br/>" +
                            "Version " +
                            bundle.getString("version") +
                            "<br/>" +
                            "Build ID " + bundle.getString("build_ID") +
                            "<br/>" +
                            "Released: " + bundle.getString("release_date") +
                            "<br/> <br/> <br/>" +
                            bundle.getString("plugin_name") +
                            " was developed in the Laboratory of Computational Biology (S. Aerts lab), University of Leuven, Belgium." +
                            "<br/>" +
                            "<br/>" +
                            "This work is under writing process. For the moment please cite the webpage (lab description) and the Methods paper." +
                            "<br/>" +
                            "<br/>" +
                            bundle.getString("labsite") +
                            "<br/>" +
                            "<br/>" +
                            bundle.getString("paper") +
                            "<br/>" +
                            "<br/>" +
                            "<br/>" +
                            "Developers: Koen Herten & Bram Van de Sande." + "<br/>" +
                            "Copyright 2012 KU Leuven." + "<br/>" +
                            "<br/>" +
                            "<br/>" +
                            "Contact: " + bundle.getString("contact") +
                            "<br/>" +
                            "</body>" +
                            "</html>", "About...", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
