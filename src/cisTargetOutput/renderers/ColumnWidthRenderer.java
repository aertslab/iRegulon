package cisTargetOutput.renderers;

import javax.swing.JTable;

import cisTargetOutput.MotifTableModels.GlobalMotifTableModel;



public class ColumnWidthRenderer{

	private JTable table;
	
	public ColumnWidthRenderer(JTable table){
		this.table = table;
	}
	
	public void widthSetter(){
		GlobalMotifTableModel model = (GlobalMotifTableModel) this.table.getModel();
		int[] imp = model.getCollumnImportance();
		int totImp = 0;
		int maxImp = 0;
		for (int index = 0; index < imp.length; index++){
			totImp += imp[index];
			if (imp[index] > maxImp){
				maxImp = imp[index];
			}
		}
		for (int index = 0; index < imp.length; index++){
			int width = 20;
			switch(imp[index]){
			case 1 : width = 250;
			break;
			case 2 : width = 60;
			break;
			case 3 : width = 25;
			break;
			}
			this.table.getColumnModel().getColumn(index).setPreferredWidth(width);
		}
		
	}

}
