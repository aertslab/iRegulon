package view.resultspanel.guiwidgets;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CopyToClipboardMouseListener implements MouseListener {
    private final ImageIcon image;

    public CopyToClipboardMouseListener(ImageIcon image) {
        this.image = image;
    }

    public ImageIcon getImage() {
        return image;
    }

    private static void setClipboard(ImageIcon image) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new LogoTransferable(image), null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            setClipboard(getImage());
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
