package view.resultspanel.renderers;

import java.awt.*;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class FloatRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private static final String NOT_APPLICABLE_STRING = "Direct";
    private static final String ZERO_STRING = "0";

    private final DecimalFormat formatter;
	private final String pattern;
	
	public FloatRenderer(final String pattern){
		this.pattern = pattern;
		this.formatter = new DecimalFormat(pattern);
	}
	
	public String getPattern() {
		return this.pattern;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		updateCanvas(table, value, this, isSelected, row);
		return this;
	}
	
	public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
        if(isSelected) {
            canvas.setBackground(table.getSelectionBackground());
            canvas.setForeground(table.getSelectionForeground());
        } else {
        	canvas.setBackground(table.getBackground());
        	canvas.setForeground(table.getForeground());
        }
        canvas.setOpaque(true);

		final Float number = (Float) value;
		if (number == null) {
			canvas.setText("");
		} else if (Float.isNaN(number)) {
		    canvas.setText(NOT_APPLICABLE_STRING);
        } else if (number == 0.0) {
            canvas.setText(ZERO_STRING);
		} else {
			canvas.setText(formatter.format(number).replace(',', '.'));
		}
		return canvas;
	}
}
