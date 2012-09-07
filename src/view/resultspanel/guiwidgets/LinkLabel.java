package view.resultspanel.guiwidgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;


public final class LinkLabel extends JLabel {
    private final MouseListener mouseListener;

    private String originalText;
    private URI uri;

    public LinkLabel() {
        super();
        mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    open(uri);
                }
                public void mouseEntered(MouseEvent e) {
                    setText(originalText,false);
                }
                public void mouseExited(MouseEvent e) {
                    setText(originalText,true);
                }
        };
    }

    private void registerListener() {
        addMouseListener(mouseListener);
    }

    private void unregisterListener() {
        removeMouseListener(mouseListener);
    }

    public String getOriginalText() {
        return originalText;
    }

    private void setOriginalText(final String text) {
        this.originalText = text;
    }

    public URI getUri() {
        return uri;
    }

    private void setUri(URI uri) {
        this.uri = uri;
    }

    public void disableLink(final String text) {
        unregisterListener();
        setOriginalText(text);
        setUri(null);
        setText(text);
        setToolTipText("");
    }

    public void enableLink(final String text, final URI link){
        setOriginalText(text);
        setUri(link);
        setText(text, true);
        setToolTipText(link.toString());
        registerListener();
    }

    private void setText(String text, boolean ul) {
        if (text == null) {
            super.setText("");
        } else {
            final String link = ul ? "<u>"+text+"</u>" : text;
            super.setText("<html><span style=\"color: #000099;\">"+link+"</span></html>");
        }
    }

    private static void open(final URI uri) {
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to launch the link, " +
                                "your computer is likely misconfigured.",
                        "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Java is not able to launch links on your computer.",
                    "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
        }
    }
}