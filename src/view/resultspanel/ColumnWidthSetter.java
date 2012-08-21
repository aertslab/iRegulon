package view.resultspanel;

import javax.swing.JTable;
import java.util.List;


public class ColumnWidthSetter {
	private final JTable table;
	
	public ColumnWidthSetter(final JTable table){
		this.table = table;
	}
	
	public void setWidth() {
		final ColumnImportances model = (ColumnImportances) this.table.getModel();

        final List<Integer> columnImportance = model.getColumnImportances();
        for (int i = 0, columnImportanceSize = columnImportance.size(); i < columnImportanceSize; i++) {
            final int curImportance = columnImportance.get(i);
            this.table.getColumnModel().getColumn(i).setPreferredWidth(convertToWidth(curImportance));
        }
	}

    private int convertToWidth(int curImportance) {
        int width = 0;
        switch (curImportance) {
            case 1:
                width = 250;
                break;
            case 2:
                width = 60;
                break;
            case 3:
                width = 25;
                break;
            default:
                width = 20;
        }
        return width;
    }
}
