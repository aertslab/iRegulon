package view.resultspanel.guiwidgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;


public final class LinkTextField extends JTextField {
    private final MouseListener mouseListener;

    private Boolean hasLink;
    private URI uri;

    public LinkTextField() {
        super();

        mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (hasLink == true) {
                        open(uri);
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
                if (hasLink == true) {
                    // Change link color to Dodger blue on hover.
                    setForeground(new Color(30,144,255));
                }
            }

            public void mouseExited(MouseEvent e) {
                if (hasLink == true) {
                    setForeground(Color.BLUE);
                }
            }
        };
    }

    private void registerListener() {
        addMouseListener(mouseListener);
    }

    private void unregisterListener() {
        removeMouseListener(mouseListener);
    }

    public URI getUri() {
        return uri;
    }

    private void setUri(URI uri) {
        this.uri = uri;
    }

    public void disableLink(final String text) {
        this.hasLink = false;
        unregisterListener();
        setUri(null);
        setForeground(Color.BLACK);
        setText(text);
        setToolTipText("");
    }

    public void enableLink(final String text, final URI link) {
        this.hasLink = true;
        setUri(link);
        setForeground(Color.BLUE);
        setText(text);
        setToolTipText(link.toString());
        unregisterListener();
        registerListener();
    }

    private static void open(final URI uri) {
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to open the requested URL in a web browser.",
                        "Launching web browser failed", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Opening a web browser on this platform is not supported.",
                    "Launching web browser not supported", JOptionPane.WARNING_MESSAGE);
        }
    }
}
