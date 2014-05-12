package view.resultspanel.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DefaultRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {

    @Override
    public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
        if (isSelected) {
            canvas.setBackground(table.getSelectionBackground());
            canvas.setForeground(table.getSelectionForeground());
        } else {
            canvas.setBackground(table.getBackground());
            canvas.setForeground(table.getForeground());
        }
        canvas.setOpaque(true);

        if (value == null) {
            canvas.setText("");
        } else {
            canvas.setText(value.toString());
        }
        return canvas;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.updateCanvas(table, value, this, isSelected, row);
        return this;
    }
}
