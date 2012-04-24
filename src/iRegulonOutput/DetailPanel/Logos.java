package iRegulonOutput.DetailPanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Logos {

	private double imageHeight = 50.0;
	private double imagewidth = 100.0;
	
	public Logos(){
		
	}
	
	private String getImagePath(String motifName){
		String path = "/logos/" + motifName + ".png";
	    return path;
	}
	
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 * @return an ImageIcon
	 */
	protected ImageIcon createSizedImageIcon(String motifName) {
		String path = this.getImagePath(motifName);
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	    	ImageIcon fullLogo = new ImageIcon(imgURL);
	    	int w = fullLogo.getIconWidth();
	    	int h = fullLogo.getIconHeight();
	    	double scale = 1;
	    	if (w >= this.imagewidth){
	    		scale = w / this.imagewidth;
	    		w = (int) Math.floor(w / scale);
	    		h = (int) Math.floor(h / scale);
	    		
	    	}
	    	if (h >= this.imageHeight){
	    		scale = h / this.imageHeight;
	    		w = (int) Math.floor(w / scale);
	    		h = (int) Math.floor(h / scale);
	    		
	    	}
	    	int type = BufferedImage.TYPE_INT_RGB;
	        BufferedImage dst = new BufferedImage(w, h, type);
	        Graphics2D g2 = dst.createGraphics();
	        g2.drawImage(fullLogo.getImage(), 0, 0, w, h, null, null);
	        g2.dispose();
	        ImageIcon logo = new ImageIcon(dst);
	        return logo;
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 * @return an ImageIcon
	 */
	protected ImageIcon createImageIcon(String motifName) {
		String path = this.getImagePath(motifName);
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	    	ImageIcon fullLogo = new ImageIcon(imgURL);
	        return fullLogo;
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}
