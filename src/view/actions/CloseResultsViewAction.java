package view.actions;

import infrastructure.CytoscapeEnvironment;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import view.ResourceAction;
import view.resultspanel.ResultsCytoPanelComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;


public final class CloseResultsViewAction extends ResourceAction {
    private static final String NAME = "action_close_results_view";

    private ResultsCytoPanelComponent view;

    public CloseResultsViewAction(final ResultsCytoPanelComponent view) {
        super(NAME);
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!view.isSaved()) {
            final int result = JOptionPane.showConfirmDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "Do you want to save this file?",
                    "Save?",
                    JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                PersistenceViewUtilities.saveToSelectedFile(
                        persistence.PersistenceUtilities.convertResultsToXML(view.getResults()),
                        FileTypes.IRF);
            }
        }
        view.unregisterRefreshListeners();

        final CytoPanel panel = CytoscapeEnvironment.getInstance().getCytoPanel(CytoPanelName.WEST);
        CytoscapeEnvironment.getInstance().getServiceRegistrar().unregisterService(view, CytoPanelComponent.class);

        if (panel.getCytoPanelComponentCount() == 0) {
            panel.setState(CytoPanelState.HIDE);
        }
    }
}
