package resultsview.actions;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import resultsview.ResourceAction;
import resultsview.ResultsView;
import saveActions.SaveLoadDialogs;
import saveActions.SaveResults;

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
                final SaveResults converter = new SaveResults();
                SaveLoadDialogs.showDialog(converter.convertResultsToXML(view.getResults()),
                        view.getRunName(),
                        SaveResults.NATIVE_FILE_EXTENSION);
            }
        }
        panel.remove(view.getMainPanel());
        if (panel.getCytoPanelComponentCount() == 0) {
            panel.setState(CytoPanelState.HIDE);
        }
    }
}
