package view.resultspanel.motifview.detailpanel;

import view.actions.ShowMotif2TFDetailAction;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class PopupMouseListener extends MouseAdapter {
    private PopupMenu menu;

    public PopupMouseListener(JTable table, TFandMotifSelected tfMotif) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        menu = new PopupMenu();
        final ShowMotif2TFDetailAction tfDetailFrameAction = new ShowMotif2TFDetailAction(tfMotif);
        table.getSelectionModel().addListSelectionListener(tfDetailFrameAction);
        menu.addAction(tfDetailFrameAction);
    }

    public void mouseClicked(MouseEvent e) {
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
