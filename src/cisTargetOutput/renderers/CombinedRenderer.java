package cisTargetOutput.renderers;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class CombinedRenderer extends JLabel implements TableCellRenderer{
	
	List<CanvasUpdater> renderList;
	
	public CombinedRenderer(){
		this.renderList = new ArrayList<CanvasUpdater>();
	}
	
	public void addRenderer(CanvasUpdater updater){
		this.renderList.add(updater);
	}
	
	public boolean containsRenderer(CanvasUpdater updater){
		return this.renderList.contains(updater);
	}
	
	public void removeRenderer(CanvasUpdater updater){
		if (this.containsRenderer(updater)){
			this.renderList.remove(updater);
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		int i=0;
		JLabel label = new JLabel(value.toString());
		for (CanvasUpdater updater : this.renderList){
			i++;
			label = updater.updateCanvas(table, value, label, isSelected, row);
		}
		return label;
	}

}
