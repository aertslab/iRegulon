package view.actions;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsView;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class CloseResultsViewAction extends ResourceAction {
    private static final String NAME = "action_close_results_view";

    private CytoPanel panel;
    private ResultsView view;

    public CloseResultsViewAction(final CytoPanel panel, final ResultsView view) {
        super(NAME);
        this.panel = panel;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!view.isSaved()) {
            final int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
                    "Do you want to save this file?",
                    "Save?",
                    JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                SaveLoadDialogs.showDialog(PersistenceUtilities.convertResultsToXML(view.getResults()),
                        view.getRunName(),
                        PersistenceUtilities.NATIVE_FILE_EXTENSION);
            }
        }
        view.unregisterRefreshListeners();
        panel.remove(view.getMainPanel());
        if (panel.getCytoPanelComponentCount() == 0) {
            panel.setState(CytoPanelState.HIDE);
        }
    }
}
