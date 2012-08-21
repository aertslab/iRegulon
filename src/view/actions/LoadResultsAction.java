package view.actions;

import cytoscape.Cytoscape;
import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.Results;
import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsView;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class LoadResultsAction extends ResourceAction {
    private static final String NAME = "action_load_results";

    private final CyLogHandler logger = ConsoleLogger.getLogger();

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
            } catch (Exception exception) {
                logger.handleLog(LogLevel.LOG_ERROR, exception.getMessage());
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), exception.getMessage());
            }
        }
    }
}
