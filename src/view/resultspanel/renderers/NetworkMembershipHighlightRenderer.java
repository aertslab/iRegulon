package view.resultspanel.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Collections;
import java.util.Set;


public class NetworkMembershipHighlightRenderer extends JLabel implements TableCellRenderer, CanvasUpdater {
    private final String columnName;
    private Set<String> IDsToBeHighlighted = Collections.emptySet();

    public NetworkMembershipHighlightRenderer(final String column) {
        this.columnName = column;
        setOpaque(true);
    }

    public String getColumnName() {
        return columnName;
    }

    public Set getIDsToBeHighlighted() {
        return IDsToBeHighlighted;
    }

    public void setIDsToBeHighlighted(final Set<String> IDs) {
        this.IDsToBeHighlighted = (IDs == null) ? Collections.<String>emptySet() : Collections.unmodifiableSet(IDs);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.updateCanvas(table, value, this, isSelected, row);
        if (value != null) setText(value.toString());
        return this;
    }

    @Override
    public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int rowIndex) {
        final int columnIndex = table.getColumnModel().getColumnIndex(columnName);
        final String ID = (String) table.getValueAt(rowIndex, columnIndex);

        if (IDsToBeHighlighted.contains(ID)) {
            canvas.setBackground(Color.LIGHT_GRAY);
            canvas.setForeground(Color.RED);
        } else {
            canvas.setBackground(Color.WHITE);
            canvas.setForeground(Color.BLACK);
        }

        if (isSelected) {
            canvas.setBackground(table.getSelectionBackground());
        }

        canvas.setOpaque(true);

        return canvas;
    }
}
