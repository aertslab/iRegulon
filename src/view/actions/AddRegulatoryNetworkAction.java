package view.actions;

import view.Refreshable;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import java.awt.event.ActionEvent;


public final class AddRegulatoryNetworkAction extends TranscriptionFactorDependentAction {
    private static final String ADD_NAME = "action_draw_nodes_and_edges";
    private static final String CREATE_NAME = "action_create_new_network";

    public static AddRegulatoryNetworkAction createAddRegulatoryNetworkAction(final String attributeName,
                                                                              final SelectedMotifOrTrack selectedMotifOrTrack,
                                                                              final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                                                              final Refreshable view) {
        return new AddRegulatoryNetworkAction(ADD_NAME, attributeName, selectedMotifOrTrack, selectedTranscriptionFactor, view, false);
    }

    public static AddRegulatoryNetworkAction createCreateNewRegulatoryNetworkAction(final String attributeName,
                                                                                    final SelectedMotifOrTrack selectedMotifOrTrack,
                                                                                    final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                                                                    final Refreshable view) {
        return new AddRegulatoryNetworkAction(CREATE_NAME, attributeName, selectedMotifOrTrack, selectedTranscriptionFactor, view, true);
    }

    private AddRegulatoryNetworkAction(final String name, final String attributeName,
                                       final SelectedMotifOrTrack selectedMotifOrTrack,
                                       final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                       final Refreshable view,
                                       final boolean createNewNetwork) {
        super(name, attributeName, selectedMotifOrTrack, selectedTranscriptionFactor, view, createNewNetwork);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        draw(false);
    }

    protected String createTitle() {
        if (getMotifOrTrack().isMotif() || getMotifOrTrack().isMotifCluster()) {
            return getTranscriptionFactor().getGeneName() + " with motif " + getMotifOrTrack().getName();
        } else {
            return getTranscriptionFactor().getGeneName() + " with track " + getMotifOrTrack().getName();
        }
    }

    @Override
    protected String getTaskDescription() {
        return "Add regulatory interactions to network";
    }
}
