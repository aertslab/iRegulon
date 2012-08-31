package view.resultspanel.actions;

import cytoscape.*;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;

import domainmodel.AbstractMotif;
import view.CytoscapeNetworkUtilities;
import view.resultspanel.Refreshable;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;

import domainmodel.TranscriptionFactor;


public class AddRegulatoryNetworkAction extends TranscriptionFactorDependentAction implements Refreshable {
    private static final String NAME = "action_draw_nodes_and_edges";

	public AddRegulatoryNetworkAction(SelectedMotif selectedMotif, final TranscriptionFactorComboBox selectedTranscriptionFactor, final Refreshable view, final String attributeName) {
		super(NAME, selectedMotif, selectedTranscriptionFactor, view, attributeName);
		if (selectedMotif == null) throw new IllegalArgumentException();
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
		final AbstractMotif motif = this.getSelectedMotif().getMotif();
		final TranscriptionFactor factor = this.getTranscriptionFactor();

		final CyNetwork network =  Cytoscape.getCurrentNetwork();
		final CyNetworkView view = Cytoscape.getCurrentNetworkView();

        addEdges(network, view, factor, motif, true);

        Cytoscape.getEdgeAttributes().setUserVisible(CytoscapeNetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);

        view.applyLayout(CyLayouts.getDefaultLayout());

        view.redrawGraph(true, true);

        getView().refresh();

        CytoscapeNetworkUtilities.activeSidePanel();
	}
}
