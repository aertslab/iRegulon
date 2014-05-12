package view.resultspanel.renderers;

import javax.swing.*;

public interface CanvasUpdater {
    public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row);
}
