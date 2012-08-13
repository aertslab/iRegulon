package resultsview.renderers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class BooleanRenderer extends JLabel implements TableCellRenderer, CanvasUpdater{

	private static ImageIcon checkIcon;
	private static ImageIcon crossIcon;
	
	static {
		String checkpath = "/icons/check.gif";
	    java.net.URL checkimgURL = BooleanRenderer.class.getResource(checkpath);
	    if (checkimgURL != null) {
	    	checkIcon = new ImageIcon(checkimgURL);
	    } else {
	        System.err.println("Couldn't find file: " + checkpath);
	    }
	    String crosspath = "/icons/cross.png";
	    java.net.URL crossimgURL = BooleanRenderer.class.getResource(crosspath);
	    if (crossimgURL != null) {
	    	crossIcon = new ImageIcon(crossimgURL);
	    } else {
	        System.err.println("Couldn't find file: " + crosspath);
	    }
	}
	
	public BooleanRenderer(){
		
	}
	
	@Override
	public JLabel updateCanvas(JTable table, Object value, JLabel canvas,
			boolean isSelected, int row) {
		boolean checked;
		if (value == null){
			checked = false;
		}else{
			checked  = (Boolean) value;
		}
		if (checked){
			canvas.setIcon(this.checkIcon);
		}else{
			canvas.setIcon(this.crossIcon);
		}
		canvas.setText("");
		canvas.setOpaque(true);
		return canvas;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return this.updateCanvas(table, value, this, isSelected, row);
	}

}
