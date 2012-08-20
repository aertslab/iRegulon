package view.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class QueryMetatargetomeAction extends ResourceAction {
    private static final String NAME = "action_query_metatargetome";

    public QueryMetatargetomeAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO: Implement the metatargetome functionality ...
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Coming soon.");
    }
}
