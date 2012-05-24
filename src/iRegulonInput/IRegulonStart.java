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

import java.io.File;
import java.net.URL;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
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
		CytoscapeInit.getProperties().put("edgelinkouturl.iRegulon", "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&hgt.customText=http://med.kuleuven.be/lcb/iregulon/generatebed.php?featureIDandTarget=%featureID%:%Target Gene%:%Regulator Gene%");
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
	
	
	
	public void saveSessionStateFiles(List<File> pFileList){
		System.out.println("save session iRegulon");
		
	}
	
	public void restoreSessionState(List<File> pStateFileList){
		System.out.println("restore session iRegulon");
		
	}
	
	
	/*
	 * Action of Starting the plugin
	 */
	public class MyPluginMenuAction extends CytoscapeAction {

		/*
		 * Show the plugin in the plugins menu in Cytoscape
		 */
		public MyPluginMenuAction(IRegulonStart myPlugin) {
			//super(ResourceBundle.getBundle("iRegulon").getString("plugin_visual_name"));
			//setPreferredMenu("Plugins");
			final IRegulonVisualStyle style = new IRegulonVisualStyle();
			
			

			 //set-up menu options in plugin menu
	        JMenu menu=Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
	        JMenuItem item;
	        //iRegulon submenu
	        JMenu submenu = new JMenu(ResourceBundle.getBundle("iRegulon").getString("plugin_name"));
	        submenu.setToolTipText("Plugin for prediction of motifs, there transcription factors and there target genes.");

	        //iRegulon panel
	        item = new JMenuItem("Classical");
	        item.setToolTipText("Do a classical  analysis.");
	        item.addActionListener(new StartClasicalFrameAction());
	        submenu.add(item);
	        
	        item = new JMenuItem("Other iRegulon options");
	        item.setToolTipText("Do other options of iRegulon: like iRegulonDB");
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
						try{
							Results result = results.loadResultsFromXML(xml);
							IRegulonOutputView output = new IRegulonOutputView(dia.getSaveName());
							output.drawPanel(result);
						}catch(Exception exception){
							System.err.println(exception.getMessage());
							exception.printStackTrace();
							JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
									exception.getMessage());
						}
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
							ResourceBundle.getBundle("iRegulon").getString("plugin_name") +
							"<br/>" +
							"Version " + 
							ResourceBundle.getBundle("iRegulon").getString("version") +
							"<br/>" + 
							"Build ID " + ResourceBundle.getBundle("iRegulon").getString("build_ID") +
							"<br/>" + 
							"Released: " + ResourceBundle.getBundle("iRegulon").getString("release_date") +
							"<br/> <br/> <br/>" +
							ResourceBundle.getBundle("iRegulon").getString("plugin_name") + 
							" was developed in the Laboratory of Computational Biology (S. Aerts lab), University of Leuven, Belgium." +
							"<br/>" +
							"<br/>" +
							"This work is under writing process. For the moment please cite the webpage (lab description) and the Methods paper." +
							"<br/>" +
							"<br/>" +
							ResourceBundle.getBundle("iRegulon").getString("labsite") +
							"<br/>" +
							"<br/>" +
							ResourceBundle.getBundle("iRegulon").getString("paper") +
							"<br/>" +
							"<br/>" +
							"<br/>" +
							"Developers: Koen Herten & Bram Van de Sande." + "<br/>" +
							"Copyright 2012 KU Leuven." + "<br/>" +
							"<br/>" +
							"<br/>" +
							"Contact: " + ResourceBundle.getBundle("iRegulon").getString("contact") +
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
			//JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"iRegulon is selected!");
			final IRegulonVisualStyle style = new IRegulonVisualStyle();
			//style.createVizMap();
			//Draws the input window (Already working!)
			final InputViewSidePanel inputView = new InputViewSidePanel();
			inputView.DrawWindow();
			
			//Test and debug
			

			
		}
		
		
		
	}
	
	
}
