package iRegulonOutput.DetailPanel;

import iRegulonOutput.PopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;


public class TFMouseListener extends MouseAdapter{

	private PopupMenu menu;
	
	public TFMouseListener(JTable table, TFandMotifSelected tfMotif){
		if (table == null){
			System.err.println("Couldn't create TFMouseListener");
		}
		menu = new PopupMenu();
		final TFDetailFrameAction tfDetailFrameAction = new TFDetailFrameAction(tfMotif);
		table.getSelectionModel().addListSelectionListener(tfDetailFrameAction);
		menu.addAction(tfDetailFrameAction);

	}
	
	
	
	/**
	 * the mouseclicking
	 */
	public void mouseClicked(MouseEvent e){
		if (e.getButton() == e.BUTTON3){
	        menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	
}
