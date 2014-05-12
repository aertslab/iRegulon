package view.resultspanel.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class CombinedRenderer extends JLabel implements TableCellRenderer {

    List<CanvasUpdater> renderList;

    public CombinedRenderer() {
        this.renderList = new ArrayList<CanvasUpdater>();
    }

    public void addRenderer(CanvasUpdater updater) {
        this.renderList.add(updater);
    }

    public boolean containsRenderer(CanvasUpdater updater) {
        return this.renderList.contains(updater);
    }

    public void removeRenderer(CanvasUpdater updater) {
        if (this.containsRenderer(updater)) {
            this.renderList.remove(updater);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        //int i=0;
        //setText(value == null ? "" : value.toString());
        for (CanvasUpdater updater : this.renderList) {
            //i++;
            updater.updateCanvas(table, value, this, isSelected, row);
        }
        return this;
    }
}
