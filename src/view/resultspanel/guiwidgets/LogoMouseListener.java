package view.resultspanel.guiwidgets;

import view.actions.SaveLoadDialogs;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LogoMouseListener implements MouseListener {
    private final String MotifName;
    private final java.net.URL fullSizedLogoFileURL;

    public LogoMouseListener(final String MotifName){
        this.MotifName = MotifName;
        this.fullSizedLogoFileURL = LogoUtilities.getImageFileURL(MotifName);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Save the current motif logo to a file when a right click on the logo is detected.
        if(e.getButton() == MouseEvent.BUTTON3)
        {
            if (fullSizedLogoFileURL != null) {
                SaveLoadDialogs.saveLogo(fullSizedLogoFileURL, MotifName);
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
}
