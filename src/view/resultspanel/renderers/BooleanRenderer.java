package view.resultspanel.renderers;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class BooleanRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");
    private static final CyLogHandler logger = ConsoleLogger.getLogger();

	private static final ImageIcon TRUE_ICON;
	private static final ImageIcon FALSE_ICON;

    private static final ImageIcon loadIcon(final String resourceName) {
        final String resource = bundle.getString(resourceName);
        final java.net.URL imageURL = BooleanRenderer.class.getResource(resource);
	    if (imageURL != null) {
	    	return new ImageIcon(imageURL);
	    } else {
	        logger.handleLog(LogLevel.LOG_ERROR, "Missing resource. Couldn't find file: \"" + resource + "\".");
            return null;
	    }
    }
	
	static {
        TRUE_ICON = loadIcon("renderer_boolean_true_icon");
        FALSE_ICON = loadIcon("renderer_boolean_false_icon");
	}
	
	public BooleanRenderer() {
        super();
	}

    @Override
    public JLabel updateCanvas(JTable table, Object value, JLabel canvas,
                               boolean isSelected, int row) {
        if(isSelected) {
            canvas.setBackground(table.getSelectionBackground());
            canvas.setForeground(table.getSelectionForeground());
        } else {
        	canvas.setBackground(table.getBackground());
        	canvas.setForeground(table.getForeground());
        }
        final boolean checked = (value == null) ? Boolean.FALSE : (Boolean) value;
        if (checked) {
            canvas.setIcon(TRUE_ICON);
        } else {
            canvas.setIcon(FALSE_ICON);
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
