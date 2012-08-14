package parameterform;


import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cytoscape.*;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;

public class InputViewSidePanel extends IRegulonResourceBundle{
	
	
	public InputViewSidePanel(){
		
	}
	
	/*
	 * Draws a window on the screen, that will be the input of the plugin
	 */
	public void DrawWindow(){
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
		//cytoPanel.setState(CytoPanelState.DOCK);
		//addCytoPanelListener(CytoPanelListener);

		
		final InputView input = new InputView();
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
		cytoPanel.addCytoPanelListener(new CytoPanelListener() {
			
			@Override
			public void onStateChange(CytoPanelState newState) {
				// This will update the panel when it becomes selected
				input.getListenerForClassicInput().refresh();
			}
			
			@Override
			public void onComponentSelected(int componentIndex) {
				// nothing to do
			}
			
			@Override
			public void onComponentRemoved(int count) {
				// nothing to do
			}
			
			@Override
			public void onComponentAdded(int count) {
				// nothing to do
			}
		});
	}
	
}
