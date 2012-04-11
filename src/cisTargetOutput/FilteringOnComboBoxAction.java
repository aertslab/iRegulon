package cisTargetOutput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cisTargetOutput.MotifTableModels.FilteredMotifModel;

public class FilteringOnComboBoxAction implements ActionListener {

	private final FilteredMotifModel model;
	
	public FilteringOnComboBoxAction(FilteredMotifModel model){
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComboBox box = (JComboBox) e.getSource();
		if (box.getSelectedItem().equals("motif")){
			this.model.setFilteringOnMotif();
		}else{
			this.model.setFilteringOnTF();
		}
	}

	
	
}
