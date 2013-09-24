package view.resultspanel.guiwidgets;

import domainmodel.Motif;

import javax.swing.*;
import java.awt.*;


public class LogoThumbnail extends JLabel {
    public static final int THUMBNAIL_HEIGHT = 50;
    public static final int THUMBNAIL_WIDTH = 200;

    private Motif motif;
    private LogoThumbnailMouseListener curMouseListener;

    public LogoThumbnail() {
        this(null);
    }

    public LogoThumbnail(final Motif motif) {
        super();
        setMotif(motif);
    }

    public boolean hasMotif() {
        return motif != null;
    }

    public Motif getMotif() {
        return motif;
    }

    public void setMotif(Motif motif) {
        this.motif = motif;
        refresh();
    }

    private void refresh() {
        // Center thumbnail.
        setHorizontalAlignment(JLabel.CENTER);

        if (curMouseListener != null) removeMouseListener(curMouseListener);

        if (hasMotif()) {
            String motifName = getMotif().getName();
            ImageIcon thumbIcon = LogoUtilities.createResizedImageIcon(motifName);
            if (thumbIcon != null) {
                setIcon(thumbIcon);
                setText("");
                curMouseListener = new LogoThumbnailMouseListener(motifName, this);
            } else {
                setIcon(null);
                setText("<html><i>This motif cannot be shown as it is part of TRANSFAC Pro.</i></html>");
                curMouseListener = null;
            }
        } else {
            setIcon(null);
            setText("");
            curMouseListener = null;
        }

        final Dimension dim = new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        setMaximumSize(dim);
        setMinimumSize(dim);

        if (curMouseListener != null) addMouseListener(curMouseListener);
    }
}

