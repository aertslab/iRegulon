package view.resultspanel.actions;

import giny.view.NodeView;

import view.CytoscapeNetworkUtilities;
import view.resultspanel.*;

import java.awt.event.ActionEvent;
import java.util.*;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class CreateNewCombinedRegulatoryNetworkAction extends NetworkDrawAction implements Refreshable {
    private static final String NAME = "action_draw_merged_edges_network";

	public CreateNewCombinedRegulatoryNetworkAction(final String attributeName,
                                                    final Refreshable view){
		super(NAME, view, attributeName);
		refresh();
	}

    @Override
    public void refresh() {
        setEnabled(!Cytoscape.getCurrentNetworkView().equals(Cytoscape.getNullNetworkView()));
    }

    @Override
	public void actionPerformed(ActionEvent e) {
		final CyNetwork oldNetwork = Cytoscape.getCurrentNetwork();
		final CyNetworkView oldView = Cytoscape.getCurrentNetworkView();
		
		// Create a new network ...
		final CyNetwork network = Cytoscape.createNetwork(
                CytoscapeNetworkUtilities.getAllNodes(),
                CytoscapeNetworkUtilities.getAllEdges(),
                "Merged iRegulon network",
                Cytoscape.getCurrentNetwork());
		Cytoscape.setCurrentNetwork(network.getIdentifier());
		final CyNetworkView view = Cytoscape.createNetworkView(network, "Merged iRegulon network view");
		
		// Copy the place of all nodes on the old network to the new network ...
		final Iterator nodesIterator = oldNetwork.nodesIterator();
		while(nodesIterator.hasNext()) {
			final CyNode node = (CyNode) nodesIterator.next();
			final NodeView nodeView = oldView.getNodeView(node);
			if (nodeView == null) continue;
      		final NodeView newNodeView = view.getNodeView(node);
			newNodeView.setXPosition(nodeView.getXPosition());
			newNodeView.setYPosition(nodeView.getYPosition());
			network.addNode(node);
		}

		// Run over all edges ...
		final Iterator edgesIterator = network.edgesIterator();
		final CyAttributes attributes = Cytoscape.getEdgeAttributes();
        final HashMap<String, EdgeAttributes> name2edgeAttributes = new HashMap<String, EdgeAttributes>();
		while(edgesIterator.hasNext()) {
			final CyEdge edge = (CyEdge) edgesIterator.next();
			if (isRegulatoryEdge(edge)) {
                final String TF = attributes.getStringAttribute(edge.getIdentifier(), CytoscapeNetworkUtilities.REGULATOR_GENE_ATTRIBUTE_NAME);
				final String TG = attributes.getStringAttribute(edge.getIdentifier(), CytoscapeNetworkUtilities.TARGET_GENE_ATTRIBUTE_NAME);
                final String name = TF + " regulates " + TG;
                @SuppressWarnings("unchecked")
                final List<String> motifs = (List<String>) attributes.getListAttribute(edge.getIdentifier(), CytoscapeNetworkUtilities.MOTIF_ATTRIBUTE_NAME);

                if (name2edgeAttributes.containsKey(name)) {
					name2edgeAttributes.get(name).addMotifNames(motifs);
				} else {
					name2edgeAttributes.put(name, new EdgeAttributes(TF, TG, motifs));
				}

                network.removeEdge(edge.getRootGraphIndex(), true);
			}
		}

		// Draw all edges ...
        @SuppressWarnings("unchecked")
        final Map<String,List<CyNode>> name2nodes = CytoscapeNetworkUtilities.getNodeMap(getAttributeName(), network.nodesList());
		for (String key : name2edgeAttributes.keySet()) {
			final EdgeAttributes edgeAttributes = name2edgeAttributes.get(key);
            if (!name2nodes.containsKey(edgeAttributes.getFactorName()) || !name2nodes.containsKey(edgeAttributes.getTargetName()))
                continue;
            for (final CyNode sourceNode : name2nodes.get(edgeAttributes.getFactorName())) {
                for (final CyNode targetNode : name2nodes.get(edgeAttributes.getTargetName())) {
                    final CyEdge edge = addEdge(sourceNode, targetNode, network, view, "");
	    			setEdgeAttribute(edge, CytoscapeNetworkUtilities.REGULATOR_GENE_ATTRIBUTE_NAME, edgeAttributes.getFactorName());
		    		setEdgeAttribute(edge, CytoscapeNetworkUtilities.TARGET_GENE_ATTRIBUTE_NAME, edgeAttributes.getTargetName());
                    setEdgeAttribute(edge, CytoscapeNetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME, CytoscapeNetworkUtilities.REGULATORY_FUNCTION_PREDICTED);
                    for (final String motifName : edgeAttributes.getMotifNames()) {
                        addEdgeAttribute(edge, CytoscapeNetworkUtilities.MOTIF_ATTRIBUTE_NAME, motifName);
                    }
                }
            }
		}

		view.updateView();

        getView().refresh();

        CytoscapeNetworkUtilities.activeSidePanel();
	}

    private boolean isRegulatoryEdge(CyEdge edge) {
        final CyAttributes attributes = Cytoscape.getEdgeAttributes();
        return attributes.getAttribute(edge.getIdentifier(), "interaction").toString().contains("regulates")
                && attributes.getStringAttribute(edge.getIdentifier(), CytoscapeNetworkUtilities.REGULATOR_GENE_ATTRIBUTE_NAME) != null
                && attributes.getStringAttribute(edge.getIdentifier(), CytoscapeNetworkUtilities.TARGET_GENE_ATTRIBUTE_NAME) != null;
    }

    private static class EdgeAttributes {
		private final String factorName;
		private final String targetName;
		private final Set<String> motifNames;

		public EdgeAttributes(String factorName, String targetName, List<String> motifNames){
			this.factorName = factorName;
			this.targetName = targetName;
			this.motifNames = new HashSet<String>();
			this.motifNames.addAll(motifNames);
		}

		public void addMotifNames(List<String> motifNames){
			this.motifNames.addAll(motifNames);
		}

		public String getFactorName(){
			return this.factorName;
		}

		public String getTargetName(){
			return this.targetName;
		}

		public Collection<String> getMotifNames(){
			return this.motifNames;
		}
	}
}


