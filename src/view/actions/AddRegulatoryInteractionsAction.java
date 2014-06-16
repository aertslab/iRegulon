package view.actions;


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;
import infrastructure.NetworkUtilities;
import view.Refreshable;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import java.awt.event.ActionEvent;


public class AddRegulatoryInteractionsAction extends TranscriptionFactorDependentAction implements Refreshable {
    private static final String NAME = "action_draw_edges";

    public AddRegulatoryInteractionsAction(SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                           final Refreshable view, final String attributeName) {
        super(NAME, selectedMotifOrTrack, selectedTranscriptionFactor, view, attributeName);
        if (selectedMotifOrTrack == null) throw new IllegalArgumentException();
        refresh();
    }

    @Override
    public void refresh() {
        setEnabled(checkEnabled());
    }

    @Override
    protected boolean checkEnabled() {
        return super.checkEnabled() && !Cytoscape.getCurrentNetworkView().equals(Cytoscape.getNullNetworkView());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final AbstractMotifAndTrack motifOrTrack = this.getSelectedMotifOrTrack().getMotifOrTrack();
        final TranscriptionFactor factor = this.getTranscriptionFactor();

        final CyNetwork network = Cytoscape.getCurrentNetwork();
        final CyNetworkView view = Cytoscape.getCurrentNetworkView();

        if (!addEdges(network, view, factor, motifOrTrack, false)) return;

        Cytoscape.getEdgeAttributes().setUserVisible(NetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);

        view.redrawGraph(true, true);

        getView().refresh();

        NetworkUtilities.activeSidePanel();
    }
}
