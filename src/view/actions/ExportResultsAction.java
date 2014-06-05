package view.actions;


import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsView;

import java.awt.event.ActionEvent;

public class ExportResultsAction extends ResourceAction {
    private static final String NAME = "action_export_results_view";
    private ResultsView view;

    public ExportResultsAction(final ResultsView view) {
        super(NAME);
        this.view = view;
    }

    public ResultsView getView() {
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String data = PersistenceUtilities.convertResultsToTSV(getView().getResults());
        SaveLoadDialogs.showDialog(data, getView().getRunName(), PersistenceUtilities.TSV_FILE_EXTENSION);
    }
}