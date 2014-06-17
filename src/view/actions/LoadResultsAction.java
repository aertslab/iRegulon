package view.actions;

import domainmodel.Results;
import infrastructure.CytoscapeEnvironment;
import infrastructure.Logger;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import persistence.LoadException;
import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsCytoPanelComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Properties;


public final class LoadResultsAction extends ResourceAction {
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
                final ResultsCytoPanelComponent output = new ResultsCytoPanelComponent(dia.getSaveName(), result);
                CytoscapeEnvironment.getInstance().getServiceRegistrar().registerService(output, CytoPanelComponent.class, new Properties());
                final CytoPanel cytoPanel = CytoscapeEnvironment.getInstance().getCytoPanel(CytoPanelName.EAST);
                cytoPanel.setState(CytoPanelState.DOCK);
                cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(output));
            } catch (LoadException exception) {
                Logger.getInstance().error(exception);
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(), exception.getMessage());
            }
        }
    }
}
