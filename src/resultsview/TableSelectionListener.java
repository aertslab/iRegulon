package resultsview;

import resultsview.motifview.tablemodels.GlobalMotifTableModel;
import resultsview.motifview.tablemodels.MotifTableModel;

import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import domainmodel.Motif;


public class TableSelectionListener implements ListSelectionListener {

	private JTable table;
	private SelectedMotif selectedTFRegulons;
	
	
	public TableSelectionListener(JTable table, SelectedMotif selectedTFRegulons){
		// TODO Auto-generated constructor stub
		if (table == null || selectedTFRegulons == null){
			throw new IllegalArgumentException();
		}
		this.table = table;
		this.selectedTFRegulons = selectedTFRegulons;
	}
	
	/**
	 * 
	 * @return all the regulons shown in the table
	 */
	public List<Motif> getAllRegulonsInTable(){
		MotifTableModel model = (MotifTableModel) this.table.getModel();
		return model.getRegulatoryTree();
	}
	
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		//System.out.println("Regulons updated");
		this.selectedTFRegulons.setMotif(this.getTFRegulonSelected());
	}
	
	/**
	 * 
	 * @param table
	 * @return all the regulon targetGenes that are selected in the given JTable
	 * 		this is returned as a list of arrays of Objects. the first element of the array is the 
	 * 		regulatoryTree, the second element of the array is the regulatoryLink
	 */
	public Motif getTFRegulonSelected(JTable table){
		final GlobalMotifTableModel model = (GlobalMotifTableModel) table.getModel();
		//Object[] selected = new Object[2];
		int[] rowsSelected = table.getSelectedRows();
		if (rowsSelected.length == 0){
			return null;
		} else {
			int regTreeSelected = (table.convertRowIndexToModel(rowsSelected[0]));
			Motif motif = model.getRegulatoryTreeAtRow(regTreeSelected);
			return motif;
			
			
			/*List<Object[]> result = new ArrayList<Object[]>();
			for (int row = 0; row < rowsSelected.length; row++) {
				System.out.println(rowsSelected[row]);
				selected[0] = model.getRegulatoryTreeAtRow(rowsSelected[row]);
				selected[1] = model.getRegulatoryLinkAtRow(rowsSelected[row]);
				System.out.println(model.getRegulatoryTreeAtRow(rowsSelected[row]).getParentName());
				System.out.println(model.getRegulatoryLinkAtRow(rowsSelected[row]).getGeneName());
				result.add(selected);
			}
			System.out.println();
			for (Object[] res : result){
				RegulatoryTree tree = (RegulatoryTree) res[0];
				RegulatoryLink link = (RegulatoryLink) res[1];
				System.out.println(tree.getParentName());
				System.out.println(link.getGeneName());
			}
			return result;*/
		}
	}
	
	/**
	 * 
	 * @return all the regulon targetGenes that are selected in the given JTable
	 * 		this is returned as a list of arrays of Objects. the first element of the array is the 
	 * 		regulatoryTree, the second element of the array is the regulatoryLink
	 */
	public Motif getTFRegulonSelected(){
		return this.getTFRegulonSelected(this.table);
	}

}
