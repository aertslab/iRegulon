package view.actions;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.Results;
import infrastructure.Logger;
import persistence.LoadException;
import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsView;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class LoadResultsAction extends ResourceAction {
    private static final String NAME = "action_load_results";

    public LoadResultsAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SaveLoadDialogs dia = new SaveLoadDialogs();
        String xml = dia.openDialogue();
        if (xml != null) {
            try {
                final Results result = PersistenceUtilities.loadResultsFromXML(xml);
                final ResultsView output = new ResultsView(dia.getSaveName(), result);
                final CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
                panel.setState(CytoPanelState.DOCK);
                output.addToPanel(panel);
            } catch (LoadException exception) {
                Logger.getInstance().error(exception);
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), exception.getMessage());
            }
        }
    }
}
