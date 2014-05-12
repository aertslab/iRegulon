package view.resultspanel.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class ClusterColorRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private String columnName;

    private int[] COLORS = {0x51CC8C, 0x51CCCC, 0x337F7F, 0x8ECC51, 0x597F33, 0x8E51CC, 0xCCAD51, 0x7F6C33,
                            0x51CC70, 0x337F46, 0x5170CC, 0xCC51AD, 0x7F336C, 0xCC7F51, 0x7F4F33, 0xCC5151,
                            0xBCCC51, 0x757F33, 0x60CC51, 0x3C7F33, 0x51CC9E, 0x337F62, 0x519ECC, 0x33627F,
                            0x6051CC, 0xBC51CC, 0xCC517F, 0xCC6851, 0xCC9651, 0x7F5E33, 0xCCC451, 0x7F7A33,
                            0xA5CC51, 0x677F33, 0x77CC51, 0x4A7F33, 0x337F37, 0x51CC87, 0x337F54, 0x51CCB5,
                            0x337F71, 0x51B5CC, 0x33717F, 0x5187CC, 0x33547F, 0x5159CC, 0x7751CC, 0xA551CC,
                            0xCC51C4, 0x7F337A, 0xCC5196, 0xCC5168, 0xCC5D51, 0x7F3A33, 0xCC7451, 0x7F4833,
                            0xCC8A51, 0xCCB851, 0x9ACC51, 0x55CC51, 0x51CC92, 0x51C0CC, 0x517BCC, 0x6C51CC,
                            0xB151CC, 0xCC51A1, 0xCC515D, 0xCC6E51, 0xCC9051, 0xCCB351, 0xC2CC51, 0xA0CC51,
                            0x7DCC51, 0x5BCC51, 0x51C6CC, 0x51A3CC, 0x7F335E, 0x7F3341};


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
        Object columnValue = table.getValueAt(row, table.getColumnModel().getColumnIndex(columnName));

        int ColorNumber = (Integer) columnValue % this.COLORS.length;
        int intValue = this.COLORS[ColorNumber];
        canvas.setBackground(new Color(intValue));

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
