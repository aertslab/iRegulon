package view.resultspanel.guiwidgets;

import domainmodel.Motif;

import java.awt.*;

import javax.swing.*;


public class LogoThumbnail extends JLabel {
    public static final int THUMBNAIL_HEIGHT = 50;
	public static final int THUMBNAIL_WIDTH  = 200;

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
            setIcon(LogoUtilities.createResizedImageIcon(getMotif().getName()));
            ImageIcon fullSizedLogo = LogoUtilities.createImageIcon(getMotif().getName());
            // Left click + Ctrl-C will copy the logo to the clipboard.
            this.addMouseListener(new CopyToClipboardMouseListener(fullSizedLogo));
            // Right click will save the logo to a PNG file.
            curMouseListener = new LogoThumbnailMouseListener(getMotif().getName(), this);
        } else {
            setIcon(null);
            curMouseListener = null;
        }

        final Dimension dim = new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        setMaximumSize(dim);
		setMinimumSize(dim);

		if (curMouseListener != null) addMouseListener(curMouseListener);
    }
}

