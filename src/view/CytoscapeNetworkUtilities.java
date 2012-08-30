package view;


import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.*;

import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;


public final class CytoscapeNetworkUtilities {
	private CytoscapeNetworkUtilities() {
	}
	
	/**
	 * @return returns true if there are nodes selected in the current network
     *         else false is returned.
	 */
	public static boolean hasSelectedNodes() {
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		return !currentNetwork.getSelectedNodes().isEmpty();
	}

	/**
	 * Get all the selected nodes in the current network
	 */
	public static List<CyNode> getSelectedNodes() {
        if (!hasSelectedNodes()) return Collections.emptyList();
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
        @SuppressWarnings("unchecked")
        final Set<CyNode> selectedNodes = currentNetwork.getSelectedNodes();
        return Collections.unmodifiableList(new ArrayList<CyNode>(selectedNodes));
    }
	
	/**
	 * Get all the nodes in the current network.
     */
	public static List<CyNode> getAllNodes() {
        if (Cytoscape.getCurrentNetworkView().getNetwork() == null)
            return Collections.emptyList();

        final CyNetwork currentNetwork = Cytoscape.getCurrentNetworkView().getNetwork();
        @SuppressWarnings("unchecked")
		final Iterator<CyNode> it = currentNetwork.nodesIterator();
		final List<CyNode> result = new ArrayList<CyNode>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Get all the edges in the current network.
	 */
	public static List<CyEdge> getAllEdges(){
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
        @SuppressWarnings("unchecked")
		final Iterator<CyEdge> it = currentNetwork.edgesIterator();
		final List<CyEdge> result = new ArrayList<CyEdge>();
		while(it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}
	
	/**
	 * @return the nodes selected in the current network as a collection of GeneIdentifiers.
	 */
	public static Collection<GeneIdentifier> getGenes(final String attributeName,
                                                      final SpeciesNomenclature speciesNomenclature){
		if (!hasSelectedNodes()) return Collections.emptyList();
		final Collection<GeneIdentifier> result = new ArrayList<GeneIdentifier>();
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        for (final CyNode node : getSelectedNodes()) {
		    final String geneName = attributes.getStringAttribute(node.getIdentifier(), attributeName);
			if (geneName != null) {
				result.add(new GeneIdentifier(geneName, speciesNomenclature));
			}
		}
		return result;
	}
}

