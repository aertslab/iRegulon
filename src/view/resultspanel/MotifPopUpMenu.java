package view.resultspanel;


import java.awt.event.*;

import javax.swing.*;

import view.actions.BedExportAction;
import view.actions.BedLinkToBrowserAction;

import view.resultspanel.actions.AddRegulatoryInteractionsAction;
import view.resultspanel.actions.AddRegulatoryNetworkWithCombinedEdgesAction;
import view.resultspanel.actions.CreateNewRegulatoryNetworkAction;
import view.resultspanel.actions.AddRegulatoryNetworkAction;


public class MotifPopUpMenu extends MouseAdapter {
	private PopupMenu menu;
	
	
	public MotifPopUpMenu(SelectedMotif selectedMotif, TFComboBox transcriptionFactorComboBox, boolean isRegionBased){
		if (selectedMotif == null || transcriptionFactorComboBox == null) {
			throw new IllegalArgumentException();
		}

		menu = new PopupMenu();

		final CreateNewRegulatoryNetworkAction networkAction = new CreateNewRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox);
		menu.addAction(networkAction);
			
		final AddRegulatoryNetworkAction drawRegulonsAndEdgesAction = new AddRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox);
		menu.addAction(drawRegulonsAndEdgesAction);
			
		final AddRegulatoryInteractionsAction drawEdgesAction = new AddRegulatoryInteractionsAction(selectedMotif, transcriptionFactorComboBox);
		menu.addAction(drawEdgesAction);
			
		final AddRegulatoryNetworkWithCombinedEdgesAction drawMergedAction = new AddRegulatoryNetworkWithCombinedEdgesAction(selectedMotif, transcriptionFactorComboBox);
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
