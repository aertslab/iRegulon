package view.resultspanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;


public class FilterAttributeActionListener implements ActionListener {
	private final AbstractFilterMotifTableModel model;
	
	public FilterAttributeActionListener(AbstractFilterMotifTableModel model) {
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox box = (JComboBox) e.getSource();
		this.model.setFilterAttribute((FilterAttribute) box.getSelectedItem());
	}
}
