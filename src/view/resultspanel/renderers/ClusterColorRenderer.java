package view.resultspanel.renderers;

import domainmodel.ClusterColors;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class ClusterColorRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private String columnName;

    private static final ClusterColors clusterColors = new ClusterColors();


    public ClusterColorRenderer(String column) {
        this.columnName = column;
        setOpaque(true);
    }


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.updateCanvas(table, value, this, isSelected, row);
        if (value != null) setText(value.toString());
        return this;
    }


    @Override
    public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
        final Object columnValue = table.getValueAt(row, table.getColumnModel().getColumnIndex(columnName));

        final int clusterNumber = (Integer) columnValue;
        canvas.setBackground(clusterColors.getClusterColor(clusterNumber));

        if (isSelected) {
            // Set white text for the current selected row (actually cell).
            canvas.setForeground(table.getSelectionForeground());
            // Set white border (top and bottom) for the current selected row (actually cell).
            canvas.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, Color.WHITE));
        } else {
            // Set black text for the current row (actually cell).
            canvas.setForeground(table.getForeground());
            // Remove border (in case this row was selected last time).
            canvas.setBorder(BorderFactory.createEmptyBorder());
        }
        canvas.setOpaque(true);
        return canvas;
    }
}
