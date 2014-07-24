package view.resultspanel.guiwidgets;

import infrastructure.CytoscapeEnvironment;
import org.cytoscape.util.swing.OpenBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;


public final class LinkTextField extends JTextField {
    private final MouseListener mouseListener;

    private boolean hasLink;
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
                    setForeground(new Color(30, 144, 255));
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
        try {
            final OpenBrowser openBrowser = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(OpenBrowser.class);
            if (!openBrowser.openURL(uri.toString())) {
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                        "Failed to open " + uri.toString() + " in a web browser.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException e) {
            final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                        "Failed to open " + uri.toString() + " in a web browser.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
