package resultsview.renderers;

import javax.swing.JLabel;
import javax.swing.JTable;

public interface CanvasUpdater {

	public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row);
}
