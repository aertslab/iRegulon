package view.actions;

import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsCytoPanelComponent;

import java.awt.event.ActionEvent;


public final class ExportResultsAction extends ResourceAction {
    private static final String NAME = "action_export_results_view";
    private ResultsCytoPanelComponent view;

    public ExportResultsAction(final ResultsCytoPanelComponent view) {
        super(NAME);
        this.view = view;
    }

    public ResultsCytoPanelComponent getView() {
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String data = PersistenceUtilities.convertResultsToTSV(getView().getResults());
        PersistenceViewUtilities.saveToSelectedFile(data, FileTypes.TSV);
    }
}
