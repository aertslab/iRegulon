package view.resultspanel.motifview.detailpanel;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


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

