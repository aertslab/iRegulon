package view.resultspanel;


import java.awt.event.*;

import javax.swing.*;

import view.actions.BedExportAction;
import view.actions.BedLinkToBrowserAction;

import view.resultspanel.actions.AddRegulatoryInteractionsAction;
import view.resultspanel.actions.CreateNewRegulatoryNetworkAction;
import view.resultspanel.actions.AddRegulatoryNetworkAction;


public class MotifPopUpMenu extends MouseAdapter implements Refreshable {
	private PopupMenu menu;

    private final AddRegulatoryInteractionsAction drawEdgesAction;
	
	public MotifPopUpMenu(final SelectedMotif selectedMotif,
                          final TranscriptionFactorComboBox transcriptionFactorComboBox,
                          final boolean isRegionBased,
                          final Refreshable view,
                          final String attributeName) {
		if (selectedMotif == null || transcriptionFactorComboBox == null || view == null) {
			throw new IllegalArgumentException();
		}

		menu = new PopupMenu();

		final CreateNewRegulatoryNetworkAction networkAction = new CreateNewRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, view, attributeName);
		menu.addAction(networkAction);
			
		final AddRegulatoryNetworkAction drawRegulonsAndEdgesAction = new AddRegulatoryNetworkAction(selectedMotif, transcriptionFactorComboBox, view, attributeName);
		menu.addAction(drawRegulonsAndEdgesAction);
			
		drawEdgesAction = new AddRegulatoryInteractionsAction(selectedMotif, transcriptionFactorComboBox, view, attributeName);
		menu.addAction(drawEdgesAction);

		if (isRegionBased) {
			final BedExportAction bedExportAction = new BedExportAction(selectedMotif);
			menu.addAction(bedExportAction);
			
			final BedLinkToBrowserAction bedLinkToBrowserAction = new BedLinkToBrowserAction(selectedMotif);
			menu.addAction(bedLinkToBrowserAction);
		}
	}

    public void refresh() {
        drawEdgesAction.refresh();
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
