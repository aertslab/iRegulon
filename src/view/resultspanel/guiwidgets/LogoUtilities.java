package view.resultspanel.guiwidgets;

import infrastructure.Logger;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public final class LogoUtilities {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");

    private static final String LOGO_FOLDERNAME = bundle.getString("logo_folder");
    private static final String LOGO_EXTENSION = bundle.getString("logo_extension");
    private static final String LOGO_NOT_AVAILABLE = bundle.getString("logo_not_available");
    private static final String LOGO_NOT_AVAILABLE_THUMB = bundle.getString("logo_not_available_thumb");

	private LogoUtilities() {
	}
	
	private static String getImagePath(String motifName) {
	    return LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
	}

    public static java.net.URL getImageFileURL(final String motifName) {
        java.net.URL imageUrl = LogoUtilities.class.getResource(getImagePath(motifName));
        if (imageUrl != null) {
            return imageUrl;
        } else {
            Logger.getInstance().error("Couldn't find file: " + getImagePath(motifName));
            imageUrl = LogoUtilities.class.getResource(LOGO_NOT_AVAILABLE);
            return imageUrl;
        }
    }

    private static ImageIcon notAvailableImage() {
        final java.net.URL imageUrl = LogoUtilities.class.getResource(LOGO_NOT_AVAILABLE);
	    if (imageUrl != null) {
	        return new ImageIcon(imageUrl);
	    } else {
            Logger.getInstance().error("Couldn't find file: " + LOGO_NOT_AVAILABLE);
            return null;
        }
    }

    private static ImageIcon notAvailableThumbImage() {
        final java.net.URL imageUrl = LogoUtilities.class.getResource(LOGO_NOT_AVAILABLE_THUMB);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        } else {
            Logger.getInstance().error("Couldn't find file: " + LOGO_NOT_AVAILABLE_THUMB);
            return null;
        }
    }

	public static ImageIcon createImageIcon(final String motifName) {
		final java.net.URL imageUrl = LogoUtilities.class.getResource(getImagePath(motifName));
	    if (imageUrl != null) {
	        return new ImageIcon(imageUrl);
	    } else {
	        Logger.getInstance().error("Couldn't find file: " + getImagePath(motifName));
	        return notAvailableImage();
	    }
	}

    public static ImageIcon createResizedImageIcon(final String motifName) {
        final ImageIcon fullIcon = createImageIcon(motifName);
        int w = fullIcon.getIconWidth();
        int h = fullIcon.getIconHeight();

        if (h == 256) {
            // We got the notAvailable PNG image, use the thumbnail version (48x48 pixels).
            return notAvailableThumbImage();
        } else {
            // When we have a real logo, the height is 188 pixels.
            // The thumbnail will have a height of 47 pixels.
            double scale = 4.0;
            h = (int) Math.floor(h / scale);
            w = (int) Math.floor(w / scale);
            int type = BufferedImage.TYPE_INT_RGB;
            BufferedImage dst = new BufferedImage(w, h, type);
            Graphics2D g2 = dst.createGraphics();
            g2.drawImage(fullIcon.getImage(), 0, 0, w, h, null, null);
            g2.dispose();
            return new ImageIcon(dst);
        }
    }
}
