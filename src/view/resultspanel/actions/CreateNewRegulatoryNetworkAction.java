package view.resultspanel.actions;

import domainmodel.AbstractMotif;
import view.CytoscapeNetworkUtilities;
import view.resultspanel.Refreshable;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;

import domainmodel.TranscriptionFactor;


public class CreateNewRegulatoryNetworkAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_create_new_network";

	public CreateNewRegulatoryNetworkAction(SelectedMotif selectedRegulatoryTree,
                                            final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                            final Refreshable view, final String attributeName){
		super(NAME, selectedRegulatoryTree, selectedTranscriptionFactor,view, attributeName);
		if (selectedRegulatoryTree == null)	throw new IllegalArgumentException();
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final AbstractMotif motif = this.getSelectedMotif().getMotif();
		final TranscriptionFactor factor = this.getTranscriptionFactor();

        final CyNetwork network = Cytoscape.createNetwork(createTitle(motif, factor));
		final CyNetworkView view = Cytoscape.createNetworkView(network, createTitle(motif, factor));

		addEdges(network, view, factor, motif, true);

        Cytoscape.getEdgeAttributes().setUserVisible(CytoscapeNetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);

        view.applyLayout(CyLayouts.getDefaultLayout());

        CytoscapeNetworkUtilities.applyVisualStyle();

        view.redrawGraph(true, true);

        getView().refresh();

        CytoscapeNetworkUtilities.activeSidePanel();
    }

    private String createTitle(AbstractMotif motif, TranscriptionFactor factor) {
        return factor.getName() + " with motif " + motif.getName();
    }
}
