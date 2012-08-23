package view.resultspanel.guiwidgets;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public final class LogoUtilities {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");
    private static final CyLogHandler logger = ConsoleLogger.getLogger();

    private static final String LOGO_FOLDERNAME = bundle.getString("logo_folder");
    private static final String LOGO_EXTENSION = bundle.getString("logo_extension");
    private static final String LOGO_NOT_AVAILABLE = bundle.getString("logo_not_available");

	private static final double IMAGE_HEIGHT = 50.0;
	private static final double IMAGE_WIDTH = 100.0;
	
	private LogoUtilities() {
	}
	
	private static String getImagePath(String motifName) {
	    return LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
	}

    private static ImageIcon notAvailableImage() {
        final java.net.URL imageUrl = LogoUtilities.class.getResource(LOGO_NOT_AVAILABLE);
	    if (imageUrl != null) {
	        return new ImageIcon(imageUrl);
	    } else {
            logger.handleLog(LogLevel.LOG_ERROR, "Couldn't find file: " + LOGO_NOT_AVAILABLE);
            return null;
        }
    }

	public static ImageIcon createImageIcon(final String motifName) {
		final java.net.URL imageUrl = LogoUtilities.class.getResource(getImagePath(motifName));
	    if (imageUrl != null) {
	        return new ImageIcon(imageUrl);
	    } else {
	        logger.handleLog(LogLevel.LOG_ERROR, "Couldn't find file: " + getImagePath(motifName));
	        return notAvailableImage();
	    }
	}

    public static ImageIcon createResizedImageIcon(final String motifName) {
        final ImageIcon fullIcon = createImageIcon(motifName);
        int w = fullIcon.getIconWidth();
        int h = fullIcon.getIconHeight();
        double scale = 1.0;
        if (w >= IMAGE_WIDTH) {
            scale = w / IMAGE_WIDTH;
            w = (int) Math.floor(w / scale);
            h = (int) Math.floor(h / scale);
        }
        if (h >= IMAGE_HEIGHT) {
            scale = h / IMAGE_HEIGHT;
            w = (int) Math.floor(w / scale);
            h = (int) Math.floor(h / scale);
        }
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        g2.drawImage(fullIcon.getImage(), 0, 0, w, h, null, null);
        g2.dispose();
        return new ImageIcon(dst);
    }
}
