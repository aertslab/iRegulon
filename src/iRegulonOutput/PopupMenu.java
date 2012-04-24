package iRegulonOutput;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.*;

public class PopupMenu extends JPopupMenu {
	
    public PopupMenu() {
    	super();}
    
    public void addAction(Action action) {
    	add(new JMenuItem(action));
    }
}
