package resultsview.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class FloatRenderer extends JLabel implements TableCellRenderer, CanvasUpdater{

	String pattern;
	MyFormat format;
	
	public FloatRenderer(String pattern){
		this.pattern = pattern;
		this.format = new MyFormat(pattern);
	}
	
	public String getPattern(){
		return this.pattern;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		updateCanvas(table, value, this, isSelected, row);
		return this;
	}
	
	public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
		final Float number =(Float) value;
		if (value == null){
			canvas.setText("");
		}
		if (Float.isNaN(number)) { 
		    canvas.setText("PERFECT");
		} else {
			canvas.setText(format.formatFloat(number));
			if (number == 0){
				canvas.setText("0");
			}
		}
		return canvas;
	}

}
