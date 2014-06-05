package view.actions;


import persistence.PersistenceUtilities;
import view.ResourceAction;
import view.resultspanel.ResultsView;

import java.awt.event.ActionEvent;

public class SaveResultsAction extends ResourceAction {
    private static final String NAME = "action_save_results_view";
    private ResultsView view;

    public SaveResultsAction(final ResultsView view) {
        super(NAME);
        this.view = view;
    }

    public ResultsView getView() {
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean saved = SaveLoadDialogs.showDialog(PersistenceUtilities.convertResultsToXML(getView().getResults()),
                getView().getRunName(),
                PersistenceUtilities.NATIVE_FILE_EXTENSION);
        if (saved && !view.isSaved()) {
            getView().setSaved();
        }
    }
}
