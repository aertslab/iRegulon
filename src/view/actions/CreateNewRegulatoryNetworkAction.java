package view.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;
import infrastructure.NetworkUtilities;
import view.Refreshable;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import java.awt.event.ActionEvent;


public class CreateNewRegulatoryNetworkAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_create_new_network";

    public CreateNewRegulatoryNetworkAction(SelectedMotifOrTrack selectedRegulatoryTree,
                                            final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                            final Refreshable view, final String attributeName) {
        super(NAME, selectedRegulatoryTree, selectedTranscriptionFactor, view, attributeName);
        if (selectedRegulatoryTree == null) throw new IllegalArgumentException();
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final AbstractMotifAndTrack motifOrTrack = this.getSelectedMotifOrTrack().getMotifOrTrack();
        final TranscriptionFactor factor = this.getTranscriptionFactor();

        final CyNetwork network = Cytoscape.createNetwork(createTitle(motifOrTrack, factor));
        final CyNetworkView view = Cytoscape.createNetworkView(network, createTitle(motifOrTrack, factor));

        addEdges(network, view, factor, motifOrTrack, true);

        Cytoscape.getEdgeAttributes().setUserVisible(NetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);

        view.applyLayout(CyLayouts.getDefaultLayout());

        NetworkUtilities.applyVisualStyle();

        view.redrawGraph(true, true);

        getView().refresh();

        NetworkUtilities.activeSidePanel();
    }

    private String createTitle(AbstractMotifAndTrack motifOrTrack, TranscriptionFactor factor) {
        String trackTypeString;
        if (motifOrTrack.isMotif() || motifOrTrack.isMotifCluster()) {
            trackTypeString = " with motif ";
        } else {
            trackTypeString = " with track ";
        }
        return factor.getName() + trackTypeString + motifOrTrack.getName();

    }
}
