package iRegulonInput;

import iRegulonOutput.IRegulonOutputView;

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
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import domainModel.Motif;
import domainModel.Results;


public class IRegulonStart extends CytoscapePlugin {
	
	/*
	 * Start the plugin
	 */
	public IRegulonStart() {
		MyPluginMenuAction menuAction = new MyPluginMenuAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);
		this.addHelp();
	}
	
	/**
	*  Hook plugin help into the Cytoscape main help system:                                                                                   
    */
	private void addHelp() {
		final String HELP_SET_NAME = "/help/jhelpset.hs";
		final ClassLoader classLoader = this.getClass().getClassLoader();
		URL helpSetURL;
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			helpSetURL = getClass().getResource(HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			if (!CyHelpBroker.addHelpSet(newHelpSet))
				System.err.println("iRegulon: Failed to add help set!");
		}catch (final Exception e) {
			System.err.println("iRegulon: Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}
	
	
	
	
	/*
	 * Action of Starting the plugin
	 */
	public class MyPluginMenuAction extends CytoscapeAction {

		/*
		 * Show the plugin in the plugins menu in Cytoscape
		 */
		public MyPluginMenuAction(IRegulonStart myPlugin) {
			//super(ResourceBundle.getBundle("cistargetx").getString("plugin_visual_name"));
			//setPreferredMenu("Plugins");
			final IRegulonVisualStyle style = new IRegulonVisualStyle();
			
			

			 //set-up menu options in plugin menu
	        JMenu menu=Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
	        JMenuItem item;
	        //CisTarget submenu
	        JMenu submenu = new JMenu(ResourceBundle.getBundle("cistargetx").getString("plugin_name"));
	        submenu.setToolTipText("Plugin for prediction of motifs, there transcription factors and there target genes.");

	        //CisTarget panel
	        item = new JMenuItem("Classical");
	        item.setToolTipText("Do a classical  analysis.");
	        item.addActionListener(new StartClasicalFrameAction());
	        submenu.add(item);
	        
	        item = new JMenuItem("Other flavors");
	        item.setToolTipText("Do other flavors of CisTarget: like CisTargetDB");
	        item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
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
						Results result = results.loadResultsFromXML(xml);
						IRegulonOutputView output = new IRegulonOutputView(dia.getSaveName());
						output.drawPanel(result);
					}
				}
			});
	        submenu.add(item);
	        submenu.addSeparator();
	        
	        //Help box
	        item = new JMenuItem("Help");
	        item.setToolTipText("Get some help");
	        item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// The help is already added to this menuitem
					
				}
			});
	        submenu.add(item);
	        CyHelpBroker.getHelpBroker().enableHelpOnButton(item, "Topic", null);


	        //About box
	        item = new JMenuItem("About...");
	        item.setToolTipText("About the plugin");
	        item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							"<html> " +
							"<body>" +
							"" +
							"<br/> <br/>" +
							ResourceBundle.getBundle("cistargetx").getString("plugin_name") +
							"<br/>" +
							"Version " + 
							ResourceBundle.getBundle("cistargetx").getString("version") +
							"<br/>" + 
							"Build ID " + ResourceBundle.getBundle("cistargetx").getString("build_ID") +
							"<br/>" + 
							"Released: " + ResourceBundle.getBundle("cistargetx").getString("release_date") +
							"<br/> <br/> <br/>" +
							ResourceBundle.getBundle("cistargetx").getString("plugin_name") + 
							" was developed in the Laboratory of Computational Biology (S. Aerts lab), University of Leuven, Belgium." +
							"<br/>" +
							"<br/>" +
							"This work is under writing process. For the moment please cite the webpage (lab description) and the Methods paper." +
							"<br/>" +
							"<br/>" +
							ResourceBundle.getBundle("cistargetx").getString("labsite") +
							"<br/>" +
							"<br/>" +
							ResourceBundle.getBundle("cistargetx").getString("paper") +
							"<br/>" +
							"<br/>" +
							"<br/>" +
							"Developers: Koen Herten & Bram Van de Sande." + "<br/>" +
							"Copyright 2012 KU Leuven." + "<br/>" +
							"<br/>" +
							"<br/>" +
							"Contact: " + ResourceBundle.getBundle("cistargetx").getString("contact") +
							"<br/>" +
							"</body>" +
							"</html>", "About...", JOptionPane.INFORMATION_MESSAGE);
				}
			});
	        submenu.add(item);
	        
	        //menu.add(submenu);
	        menu.add(submenu);
			
		}

		/*
		 * Actions to do
		 * (non-Javadoc)
		 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			//Debug: Draw a messagebox for showing that the plugin works
			//JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"CisTargetX is selected!");
			final IRegulonVisualStyle style = new IRegulonVisualStyle();
			//style.createVizMap();
			//Draws the input window (Already working!)
			final InputViewSidePanel inputView = new InputViewSidePanel();
			inputView.DrawWindow();
			
			//Test and debug
			

			
		}
		
		
		
	}
	
	
}
