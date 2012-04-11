package cisTargetX;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import saveCisTarget.SaveLoadDialogs;
import saveCisTarget.SaveResults;

import java.util.*;

import cisTargetAnalysis.*;
import cisTargetOutput.CisTargetXOutputView;

import cytoscape.view.CyHelpBroker;
import javax.help.HelpSet;
import java.net.URL;


import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.view.cytopanels.*;
import domainModel.Motif;


public class CisTargetXStart extends CytoscapePlugin {
	
	/*
	 * Start the plugin
	 */
	public CisTargetXStart() {
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
		public MyPluginMenuAction(CisTargetXStart myPlugin) {
			//super(ResourceBundle.getBundle("cistargetx").getString("plugin_visual_name"));
			//setPreferredMenu("Plugins");
			final CisTargetVisualStyle style = new CisTargetVisualStyle();
			
			

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
						ArrayList<Motif> motifs = results.loadResults(xml);
						CisTargetXOutputView output = new CisTargetXOutputView(dia.getSaveName());
						output.drawPanel(motifs);
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
					// TODO Auto-generated method stub
					
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
					// TODO Auto-generated method stub
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							"<html> " +
							"<body>" +
							"" +
							"<br/> <br/>" +
							ResourceBundle.getBundle("cistargetx").getString("plugin_name") +
							"<br/>" +
							"Version 0.1" + 
							"<br/>" + 
							"Build ID 1" +
							"<br/> <br/> <br/>" +
							ResourceBundle.getBundle("cistargetx").getString("plugin_name") + 
							" was developed in the Laboratory of Computational Biology (S. Aerts lab), University of Leuven, Belgium." +
							"<br/>" +
							"Developers: Koen Herten & Bram Van de Sande." + "<br/>" +
							"Copyright 2012 Katholieke Universiteit Leuven." + "<br/>" +
							"</body>" +
							"</html>");
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
			final CisTargetVisualStyle style = new CisTargetVisualStyle();
			//style.createVizMap();
			//Draws the input window (Already working!)
			final CisTargetXInputViewSidePanel inputView = new CisTargetXInputViewSidePanel();
			inputView.DrawWindow();
			
			//Test and debug
			

			
		}
		
		
		
	}
	
	
}
