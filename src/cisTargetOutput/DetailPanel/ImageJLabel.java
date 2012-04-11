package cisTargetOutput.DetailPanel;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

public class ImageJLabel extends JLabel{
	
	private ImageIcon fullIcon;
	private ImageMouseListener imlistener;
	
	public ImageJLabel(ImageIcon icon){
		super(icon);
		this.fullIcon = icon;
		this.imlistener = new ImageMouseListener(this.fullIcon, this);
		this.addMouseListener(this.imlistener);
	}
	
	public ImageJLabel(ImageIcon icon, ImageIcon fullIcon){
		super(fullIcon);
		this.fullIcon = fullIcon;
		this.imlistener = new ImageMouseListener(this.fullIcon, this);
		this.addMouseListener(this.imlistener);
	}
	
	public void setIcon(ImageIcon icon){
		super.setIcon(icon);
		if (icon == null){
			this.fullIcon = null;
		}else{
			if (this.fullIcon == null){
				this.fullIcon = icon;
			}
		}
		this.paint(this.getGraphics());
		this.removeMouseListener(this.imlistener);
		this.imlistener = new ImageMouseListener(fullIcon, this);
		this.addMouseListener(this.imlistener);
	}
	
	public void setFullIcon(ImageIcon icon){
		this.fullIcon = icon;
		this.paint(this.getGraphics());
		this.removeMouseListener(this.imlistener);
		this.imlistener = new ImageMouseListener(fullIcon, this);
		this.addMouseListener(this.imlistener);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		//if (this.icon != null){
			//g.drawImage(this.icon.getImage(),0,0,getSize().width,getSize().height,this);
		//}
	}
	

	

}

