package resultsview.motifview.detailpanel;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

class ImageMouseListener  implements MouseListener{
	
	private ImageIcon fullIcon;
	private ImageMagnifierLabel im;
	private JLabel label;
	private Timer timer;
	
	public ImageMouseListener(ImageIcon fullIcon, JLabel label){
		this.fullIcon = fullIcon;
		this.label = label;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// nothinh
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// nothing
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		Point p = e.getLocationOnScreen();
		ImageTask task = new ImageTask(this, p);
		timer = new Timer();
		timer.schedule(task, 700);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (timer != null){
			timer.cancel();
			timer = null;
		}
		
		if (this.im != null){
			//int expand = 5;
			//Rectangle rectangle = new Rectangle((int) (im.getX() - expand), 
			//		(int) (im.getY() - expand), 
			//		(int) (im.getBounds().getWidth() + (expand * 2)), 
			//		(int) (im.getBounds().getHeight() + (expand * 2)));
			//System.out.println("X on screen  " + e.getXOnScreen());
			//System.out.println("y on screen " + e.getYOnScreen());
			//System.out.println("X rect " + (im.getBounds().getX() - expand));
			//System.out.println("Y rect " + (im.getBounds().getY() - expand));
			//System.out.println("width rect " + (im.getBounds().getWidth() + (expand * 2)));
			//System.out.println("height rect " + (im.getBounds().getHeight() + (expand * 2)));
			
			boolean isIn =false;
			if (e.getXOnScreen() >= im.getBounds().getX() 
					&& e.getXOnScreen() <= (im.getBounds().getX() + im.getBounds().getWidth())
					&& e.getYOnScreen() >= im.getBounds().getY()
					&& e.getYOnScreen() <= (im.getBounds().getY() + im.getBounds().getHeight())){
				isIn = true;
			}
			//System.out.println("isIn: " + isIn);
			if (label.contains(e.getXOnScreen(), e.getYOnScreen()) || isIn){
				//System.out.println("label: " + label.contains(e.getXOnScreen(), e.getYOnScreen()));
				//System.out.println("rect: " + rectangle.contains(e.getLocationOnScreen()));
				//System.out.println("rect x: " + rectangle.getX());
				//System.out.println("rect y: " + rectangle.getY());
			}else{
				im.dispose();
				im = null;
			}
		}
	}
	
	
	static class ImageMagnifierLabel extends JFrame{
		private ImageIcon temp;

		public ImageMagnifierLabel(ImageIcon imageFile,int width,int height,int x,int y){
			setUndecorated(true);
			temp=imageFile;
			setLocation(x - (temp.getIconWidth() / 2),y);
			setSize(temp.getIconWidth(),temp.getIconHeight());
			setVisible(true);
		}

		public void paint(Graphics g){
			super.paint(g);
			if (temp != null){
				g.drawImage(temp.getImage(),0,0,temp.getIconWidth(),temp.getIconHeight(),this);
			}
		}
	}
	
	class ImageTask extends TimerTask{
		
		private ImageMouseListener imListener;
		private Point p;

		public ImageTask(ImageMouseListener imListener, Point location){
			this.imListener = imListener;
			this.p = location;
		}
		
		@Override
		public void run() {
			if (fullIcon != null && im == null){
				im = new ImageMagnifierLabel(fullIcon, fullIcon.getIconWidth(), 
						fullIcon.getIconHeight(), (int) p.getX(), (int) p.getY());
				im.addMouseListener(imListener);
				timer = null;
			}
		}
		
	}
	
	
}



