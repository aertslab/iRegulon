package view.resultspanel.guiwidgets;

import view.actions.CopyLogoAction;
import view.actions.SaveLogoAction;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LogoMouseListener implements MouseListener {
    private final String motifName;
    private final java.net.URL fullSizedLogoFileURL;

    public LogoMouseListener(final String motifName) {
        this.motifName = motifName;
        this.fullSizedLogoFileURL = LogoUtilities.getImageFileURL(motifName);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (fullSizedLogoFileURL != null) {
                RightClickPopupMenu menu = new RightClickPopupMenu();

                final CopyLogoAction copyLogoAction = new CopyLogoAction(motifName);
                menu.addAction(copyLogoAction);

                final SaveLogoAction saveLogoAction = new SaveLogoAction(fullSizedLogoFileURL);
                menu.addAction(saveLogoAction);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private static class RightClickPopupMenu extends JPopupMenu {

        public RightClickPopupMenu() {
            super();
        }

        public void addAction(Action action) {
            add(new JMenuItem(action));
        }
    }
}

