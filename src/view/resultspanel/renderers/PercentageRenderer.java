package view.resultspanel.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

public class PercentageRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private static final String NOT_APPLICABLE_STRING = "N/A";

    private final NumberFormat formatter;
    private String naLabel;

    public PercentageRenderer() {
        this(NOT_APPLICABLE_STRING);
    }

    public PercentageRenderer(final String naLabel) {
        this.naLabel = naLabel;
        this.formatter = NumberFormat.getPercentInstance();
    }

    public String getNaLabel() {
        return naLabel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        updateCanvas(table, value, this, isSelected, row);
        return this;
    }

    public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
        if (isSelected) {
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
            canvas.setText(getNaLabel());
        } else {
            canvas.setText(formatter.format(number));
        }
        return canvas;
    }
}

