package view.resultspanel;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import domainmodel.TranscriptionFactor;
import infrastructure.NetworkUtilities;
import view.Refreshable;
import view.ResourceAction;


public abstract class NetworkDrawAction extends ResourceAction {
    private static final Refreshable NO_VIEW = new Refreshable() {
        @Override
        public void refresh() {
            //Nop ...
        }
    };

    private final String attributeName;
    private final Refreshable view;

    public NetworkDrawAction(final String actionName, final Refreshable view, final String attributeName) {
        super(actionName);
        this.view = view == null ? NO_VIEW : view;
        this.attributeName = attributeName == null ? NetworkUtilities.ID_ATTRIBUTE_NAME : attributeName;

        //TODO: Implementation of undo functionality can be done via cytoscape.util.undo.CyUndo: a tiny class
        // for supporting undo in the Cytoscape context. If you want to post an edit, use
        // CyUndo.getUndoableEditSupport().postEdit(yourEdit) where yourEdit implements the UndoableEdit.
        // Every implementing action needs to use basic operations like addNode, addEdge which
        // are implemented in this abstract class. Adding undo functionality is just adding
        // the creation of UndoableEdit objects, combining them in a composite UndoableEdit and
        // registering this compound object with Cytoscape.
    }

    public Refreshable getView() {
        return view;
    }

    public String getAttributeName() {
        return attributeName;
    }

    protected CyNode addNode(String nodeID, CyNetwork network, CyNetworkView view) {
        return NetworkUtilities.addNode(nodeID, network, view);
    }

    protected void addNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        NetworkUtilities.addNodeAttribute(node, attributeName, attributeValue);
    }

    protected void setNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        NetworkUtilities.setNodeAttribute(node, attributeName, attributeValue);
    }

    protected CyEdge addEdge(CyNode node1, CyNode node2, CyNetwork network, CyNetworkView view, String motif) {
        return NetworkUtilities.addEdge(node1, node2, network, view, motif);
    }

    protected void setEdgeAttribute(CyEdge edge, String attributeName, String attributeValue) {
        NetworkUtilities.setEdgeAttribute(edge, attributeName, attributeValue);
    }

    protected void setEdgeAttribute(CyEdge edge, String attributeName, int attributeValue) {
        NetworkUtilities.setEdgeAttribute(edge, attributeName, attributeValue);
    }

    protected void addEdgeAttribute(CyEdge edge, String attributeName, String attributeValue) {
        NetworkUtilities.addEdgeAttribute(edge, attributeName, attributeValue);
    }

    protected CyEdge createEdge(final CyNode sourceNode, final CyNode targetNode, final TranscriptionFactor factor,
                                final AbstractMotifAndTrack motifOrTrack, final GeneIdentifier targetGene) {
        return NetworkUtilities.createEdge(sourceNode, targetNode, factor, motifOrTrack, targetGene);
    }

    protected CyNode createSourceNode(final CyNetwork network, final CyNetworkView view, final GeneIdentifier factorID,
                                      final AbstractMotifAndTrack motifOrTrack) {
        return NetworkUtilities.createSourceNode(network, view, getAttributeName(), factorID, motifOrTrack);
    }

    protected CyNode createSourceNode(final CyNetwork network, final CyNetworkView view,
                                      final TranscriptionFactor factor, final AbstractMotifAndTrack motifOrTrack) {
        return createSourceNode(network, view, factor.getGeneID(), motifOrTrack);
    }

    protected CyNode createTargetNode(final CyNetwork network, final CyNetworkView view,
                                      final CandidateTargetGene targetGene, final AbstractMotifAndTrack motifOrTrack) {
        return NetworkUtilities.createTargetNode(network, view, getAttributeName(), targetGene, motifOrTrack);
    }
}
