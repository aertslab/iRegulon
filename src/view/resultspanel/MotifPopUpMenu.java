package view.resultspanel;


import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import view.actions.BedExportAction;
import view.actions.BedLinkToBrowserAction;

import view.resultspanel.actions.DrawEdgesAction;
import view.resultspanel.actions.DrawMergedEdgesNetworkAction;
import view.resultspanel.actions.CreateNewNetworkAction;
import view.resultspanel.actions.DrawNodesAndEdgesAction;


public class MotifPopUpMenu extends MouseAdapter {
	private PopupMenu menu;
	
	
	public MotifPopUpMenu(SelectedMotif selectedMotif, JTextComponent selectedTF, boolean isRegionBased){
		if (selectedMotif == null || selectedTF == null) {
			throw new IllegalArgumentException();
		}

		menu = new PopupMenu();

		final CreateNewNetworkAction networkAction = new CreateNewNetworkAction(selectedMotif);
		selectedTF.getDocument().addDocumentListener(networkAction);
		menu.addAction(networkAction);
			
		final DrawNodesAndEdgesAction drawRegulonsAndEdgesAction = new DrawNodesAndEdgesAction(selectedMotif);
		selectedTF.getDocument().addDocumentListener(drawRegulonsAndEdgesAction);
		menu.addAction(drawRegulonsAndEdgesAction);
			
		final DrawEdgesAction drawEdgesAction = new DrawEdgesAction(selectedMotif);
		selectedTF.getDocument().addDocumentListener(drawEdgesAction);
		menu.addAction(drawEdgesAction);
			
		final DrawMergedEdgesNetworkAction drawMergedAction = new DrawMergedEdgesNetworkAction(selectedMotif);
		selectedTF.getDocument().addDocumentListener(drawMergedAction);
		menu.addAction(drawMergedAction);
		
		if (isRegionBased) {
			final BedExportAction bedExportAction = new BedExportAction(selectedMotif);
			menu.addAction(bedExportAction);
			
			final BedLinkToBrowserAction bedLinkToBrowserAction = new BedLinkToBrowserAction(selectedMotif);
			menu.addAction(bedLinkToBrowserAction);
		}
	}


    public void mouseClicked(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON3) {
	        menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private static class PopupMenu extends JPopupMenu {

        public PopupMenu() {
            super();
        }

        public void addAction(Action action) {
            add(new JMenuItem(action));
        }
    }
}
