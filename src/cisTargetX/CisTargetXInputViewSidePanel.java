package cisTargetX;


import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cytoscape.*;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;

public class CisTargetXInputViewSidePanel extends CisTargetResourceBundle{
	
	
	public CisTargetXInputViewSidePanel(){
		
	}
	
	/*
	 * Draws a window on the screen, that will be the input of the plugin
	 */
	public void DrawWindow(){
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
		//cytoPanel.setState(CytoPanelState.DOCK);
		//addCytoPanelListener(CytoPanelListener);

		
		InputView input = new InputView();
		JPanel panel = input.CreateGeneralInputView();
		//JPanel panel = new JPanel(layout);
		//JPanel panel = new JPanel();
		//Dimension preferredSize = new Dimension(600, fontPoints*37);
		//panel.setPreferredSize(preferredSize);
		//Container contentPane = frame.getContentPane();
		if (cytoPanel.indexOfComponent(getBundle().getString("plugin_visual_name")) == -1){
			cytoPanel.add(getBundle().getString("plugin_visual_name"), panel);
		}
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(getBundle().getString("plugin_visual_name")));
	}
	
}
