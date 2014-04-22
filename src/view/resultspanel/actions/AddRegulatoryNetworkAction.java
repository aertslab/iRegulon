package view.resultspanel.actions;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import domainmodel.TranscriptionFactor;
import infrastructure.CytoscapeNetworkUtilities;
import view.resultspanel.Refreshable;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class AddRegulatoryNetworkAction extends TranscriptionFactorDependentAction implements Refreshable {
    private static final String NAME = "action_draw_nodes_and_edges";

    public AddRegulatoryNetworkAction(SelectedMotifOrTrack selectedMotifOrTrack, final TranscriptionFactorComboBox selectedTranscriptionFactor, final Refreshable view, final String attributeName) {
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

        final Map<String, List<CyNode>> name2nodes = CytoscapeNetworkUtilities.getNodeMap(getAttributeName(), CytoscapeNetworkUtilities.getAllNodes());

        final List<CyNode> sourceNodes = name2nodes.containsKey(factor.getName())
                ? name2nodes.get(factor.getName())
                : Collections.singletonList(CytoscapeNetworkUtilities.createSourceNode(network, view, getAttributeName(), factor.getGeneID(), motifOrTrack));
        if (sourceNodes.isEmpty()) return;

        for (final CyNode sourceNode : sourceNodes) {
            CytoscapeNetworkUtilities.adjustSourceNode(sourceNode, getAttributeName(), factor.getGeneID(), motifOrTrack);

            for (CandidateTargetGene targetGene : motifOrTrack.getCandidateTargetGenes()) {
                final GeneIdentifier geneID = targetGene.getGeneID();

                final List<CyNode> targetNodes = name2nodes.containsKey(targetGene.getGeneName())
                        ? name2nodes.get(targetGene.getGeneName())
                        : Collections.singletonList(CytoscapeNetworkUtilities.createTargetNode(network, view, getAttributeName(), targetGene, motifOrTrack));
                for (final CyNode targetNode : targetNodes) {
                    CytoscapeNetworkUtilities.adjustTargetNode(targetNode, getAttributeName(), targetGene, motifOrTrack);
                    final CyEdge edge = createEdge(sourceNode, targetNode, factor, motifOrTrack, geneID);
                    setEdgeAttribute(edge, CytoscapeNetworkUtilities.RANK_ATTRIBUTE_NAME, targetGene.getRank());
                }
            }
        }

        Cytoscape.getEdgeAttributes().setUserVisible(CytoscapeNetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);

        view.applyLayout(CyLayouts.getDefaultLayout());

        view.redrawGraph(true, true);

        getView().refresh();

        CytoscapeNetworkUtilities.activeSidePanel();
    }
}
