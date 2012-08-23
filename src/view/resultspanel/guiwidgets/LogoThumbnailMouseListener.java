package view.resultspanel.guiwidgets;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

class LogoThumbnailMouseListener extends MouseAdapter {
    private static final int WAITING_TIME_IN_MS = 700;

    private ImageIcon fullSizedLogo;
	private FullSizedLogoPopup popup;

    private final JLabel logoThumbnail;
	private Timer timer;
	
	public LogoThumbnailMouseListener(final ImageIcon fullSizedLogo, final JLabel logoThumbnail){
		this.fullSizedLogo = fullSizedLogo;
		this.logoThumbnail = logoThumbnail;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		timer = new Timer();
		timer.schedule(new ShowPopupTask(e.getLocationOnScreen()), WAITING_TIME_IN_MS);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

        if (this.popup != null) {
            final boolean isIn = (e.getXOnScreen() >= popup.getBounds().getX()
                    && e.getXOnScreen() <= (popup.getBounds().getX() + popup.getBounds().getWidth())
                    && e.getYOnScreen() >= popup.getBounds().getY()
                    && e.getYOnScreen() <= (popup.getBounds().getY() + popup.getBounds().getHeight()));
            if (!logoThumbnail.contains(e.getXOnScreen(), e.getYOnScreen()) && !isIn) {
                popup.dispose();
                popup = null;
            }
        }
	}

	private static class FullSizedLogoPopup extends JFrame {
		private final ImageIcon fullSizedLogo;

		public FullSizedLogoPopup(final ImageIcon fullSizedLogo, int x, int y){
			super();
            this.fullSizedLogo = fullSizedLogo;

            setUndecorated(true);
			setLocation(x - (fullSizedLogo.getIconWidth() / 2), y);
			setSize(fullSizedLogo.getIconWidth(), fullSizedLogo.getIconHeight());
			setVisible(true);
		}

		public void paint(Graphics g){
			super.paint(g);
			if (fullSizedLogo != null){
				g.drawImage(fullSizedLogo.getImage(), 0, 0, fullSizedLogo.getIconWidth(), fullSizedLogo.getIconHeight(),this);
			}
		}
	}
	
	private class ShowPopupTask extends TimerTask {
		private final Point location;

		public ShowPopupTask(final Point location){
			this.location = location;
		}
		
		@Override
		public void run() {
			if (fullSizedLogo != null && popup == null) {
				popup = new FullSizedLogoPopup(fullSizedLogo, (int) location.getX(), (int) location.getY());
				popup.addMouseListener(LogoThumbnailMouseListener.this);
				timer = null;
			}
		}
	}
}



