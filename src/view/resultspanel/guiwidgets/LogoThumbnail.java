package view.resultspanel.guiwidgets;

import domainmodel.Motif;

import java.awt.*;

import javax.swing.JLabel;


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

