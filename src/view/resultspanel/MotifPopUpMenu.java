package view.resultspanel;


import java.awt.event.*;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import view.actions.BedExportAction;
import view.actions.BedLinkToBrowserAction;

import view.resultspanel.actions.DrawEdgesAction;
import view.resultspanel.actions.DrawMergedEdgesNetworkAction;
import view.resultspanel.actions.CreateNewNetworkAction;
import view.resultspanel.actions.DrawNodesAndEdgesAction;


public class MotifPopUpMenu extends MouseAdapter{
	
	
	private PopupMenu menu;
	
	
	public MotifPopUpMenu(JTable table, SelectedMotif selectedTFRegulons, JTextComponent tc, boolean isRegionBased){
		if (table == null || selectedTFRegulons == null){
			throw new IllegalArgumentException();
		}
		menu = new PopupMenu();
			
			
		final CreateNewNetworkAction networkAction = new CreateNewNetworkAction(selectedTFRegulons);
		//table.getSelectionModel().addListSelectionListener(networkAction);
		//the transcriptionfactor that is selected in the given text component, must be added as a listener 
		tc.getDocument().addDocumentListener(networkAction);
		menu.addAction(networkAction);
			
		final DrawNodesAndEdgesAction drawRegulonsAndEdgesAction = new DrawNodesAndEdgesAction(selectedTFRegulons);
		//table.getSelectionModel().addListSelectionListener(drawRegulonsAndEdgesAction);
		//the transcriptionfactor that is selected in the given text component, must be added as a listener 
		tc.getDocument().addDocumentListener(drawRegulonsAndEdgesAction);
		menu.addAction(drawRegulonsAndEdgesAction);
			
		final DrawEdgesAction drawEdgesAction = new DrawEdgesAction(selectedTFRegulons);
		//table.getSelectionModel().addListSelectionListener(drawEdgesAction);
		//the transcriptionfactor that is selected in the given text component, must be added as a listener 
		tc.getDocument().addDocumentListener(drawEdgesAction);
		menu.addAction(drawEdgesAction);
			
			
		final DrawMergedEdgesNetworkAction drawMergedAction = new DrawMergedEdgesNetworkAction(selectedTFRegulons);
		//table.getSelectionModel().addListSelectionListener(drawMergedAction);
		//the transcriptionfactor that is selected in the given text component, must be added as a listener 
		tc.getDocument().addDocumentListener(drawMergedAction);
		menu.addAction(drawMergedAction);
		
		if (isRegionBased){
			final BedExportAction bedExportAction = new BedExportAction(selectedTFRegulons);
			menu.addAction(bedExportAction);
			
			final BedLinkToBrowserAction bedLinkToBrowserAction = new BedLinkToBrowserAction(selectedTFRegulons);
			menu.addAction(bedLinkToBrowserAction);
		}
	}


	/**
	 * the mouseclicking
	 */
	public void mouseClicked(MouseEvent e){
		if (e.getButton() == e.BUTTON3){
	        menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	
	
}
