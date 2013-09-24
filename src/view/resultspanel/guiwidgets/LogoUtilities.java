package view.resultspanel.guiwidgets;

import infrastructure.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public final class LogoUtilities {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");

    private static final String LOGO_FOLDERNAME = bundle.getString("logo_folder");
    private static final String LOGO_EXTENSION = bundle.getString("logo_extension");

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
            return null;
        }
    }

    public static ImageIcon createImageIcon(final String motifName) {
        final java.net.URL imageUrl = LogoUtilities.class.getResource(getImagePath(motifName));
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        } else {
            Logger.getInstance().error("Couldn't find file: " + getImagePath(motifName));
            return null;
        }
    }

    public static ImageIcon createResizedImageIcon(final String motifName) {
        final ImageIcon fullIcon = createImageIcon(motifName);

        /* Were we able to find the logo (or did we have a Transfac Pro logo)? */
        if (fullIcon == null) {
            return null;
        }

        int w = fullIcon.getIconWidth();
        int h = fullIcon.getIconHeight();

        /*
         * When we have a real logo, the height is 188 pixels.
         * The thumbnail will have a height of 47 pixels.
         */
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
