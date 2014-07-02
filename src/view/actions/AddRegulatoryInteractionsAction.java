package view.actions;

import view.Refreshable;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import java.awt.event.ActionEvent;


public final class AddRegulatoryInteractionsAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_draw_edges";

    public AddRegulatoryInteractionsAction(SelectedMotifOrTrack selectedMotifOrTrack,
                                           final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                           final Refreshable view,
                                           final String attributeName) {
        super(NAME, attributeName, selectedMotifOrTrack, selectedTranscriptionFactor, view, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        draw(true);
    }

    @Override
    protected String getTaskDescription() {
        return "Add regulatory interactions to network";
    }

    @Override
    protected String createTitle() {
        return "";
    }
}
