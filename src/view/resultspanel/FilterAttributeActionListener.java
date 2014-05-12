package view.resultspanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FilterAttributeActionListener implements ActionListener {
    private final AbstractFilterMotifAndTrackTableModel model;

    public FilterAttributeActionListener(AbstractFilterMotifAndTrackTableModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox box = (JComboBox) e.getSource();
        this.model.setFilterAttribute((FilterAttribute) box.getSelectedItem());
    }
}
