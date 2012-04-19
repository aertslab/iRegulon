package cisTargetOutput;


import javax.swing.Icon;
import javax.swing.JButton;

public class DrawButton extends JButton{

	
	public DrawButton(){
		super();
		this.setEnabled(false);
	}
	
	public DrawButton(String text){
		super(text);
		this.setEnabled(false);
	}
	
	public DrawButton(Icon icon){
		super(icon);
		this.setEnabled(false);
	}
	
	public DrawButton(String text, Icon icon){
		super(text, icon);
		this.setEnabled(false);
	}

	

	
	
	
}
