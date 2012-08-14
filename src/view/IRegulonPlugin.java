package view;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import view.actions.LoadResultsAction;
import view.actions.QueryMetatargetomeAction;
import view.parametersform.IRegulonVisualStyle;
import view.parametersform.actions.OpenParametersFormAction;
import view.parametersform.actions.AddParametersFormToSidePanelAction;

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
        final JMenu menu = new JMenu(bundle.getString("plugin_name"));
        menu.setToolTipText(bundle.getString("plugin_description"));

        menu.add(new JMenuItem(new OpenParametersFormAction()));
        menu.add(new JMenuItem(new AddParametersFormToSidePanelAction()));

        menu.addSeparator();

        menu.add(new JMenuItem(new QueryMetatargetomeAction()));

        menu.addSeparator();

        menu.add(new JMenuItem(new LoadResultsAction()));

        menu.addSeparator();

        final JMenuItem item = new JMenuItem("Help");
        item.setToolTipText("Get some help");
        menu.add(item);
        CyHelpBroker.getHelpBroker().enableHelpOnButton(item, "Topic", null);

        menu.add(new JMenuItem(new AboutAction()));

        return menu;
    }

    private final class AboutAction extends ResourceAction {
        private static final String NAME = "action_about";

        public AboutAction() {
            super(NAME);
        }

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
