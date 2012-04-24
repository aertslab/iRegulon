package iRegulonOutput;

import iRegulonOutput.MotifTableModels.FilteredMotifModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;


public class FilteringOnComboBoxAction implements ActionListener {

	private final FilteredMotifModel model;
	
	public FilteringOnComboBoxAction(FilteredMotifModel model){
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComboBox box = (JComboBox) e.getSource();
		this.model.setFilteringOn((FilteringOn) box.getSelectedItem());
	}

	
	
}
