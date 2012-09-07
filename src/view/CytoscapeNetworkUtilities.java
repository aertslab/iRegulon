package view;


import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.*;

import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import domainmodel.*;

import javax.swing.*;


public final class CytoscapeNetworkUtilities {
    public static final String PLUGIN_VISUAL_NAME = ResourceBundle.getBundle("iRegulon").getString("plugin_visual_name");

    public static final String FEATURE_ID_ATTRIBUTE_NAME = "featureID";
    public static final String MOTIF_ATTRIBUTE_NAME = "Motif";
    public static final String REGULATORY_FUNCTION_ATTRIBUTE_NAME = "Regulatory function";
    public static final String TARGET_GENE_ATTRIBUTE_NAME = "Target Gene";
    public static final String REGULATOR_GENE_ATTRIBUTE_NAME = "Regulator Gene";
    public static final String ID_ATTRIBUTE_NAME = "ID";
    public static final String STRENGTH_ATTRIBUTE_NAME = "Strength";
    public static final String RANK_ATTRIBUTE_NAME = "Rank";
    private static final String HIDDEN_LABEL_ATTRIBUTE_NAME = "hiddenLabel";

    public static final String REGULATORY_FUNCTION_REGULATOR = "Regulator";
    public static final String REGULATORY_FUNCTION_TARGET_GENE = "Regulated";
    public static final String REGULATORY_FUNCTION_PREDICTED = "Predicted";
    public static final String REGULATORY_FUNCTION_METATARGETOME = "Metatargetome";

    private static final Set<String> EXCLUDED_ATTRIBUTE_NAMES = new HashSet<String>();
    static {
        EXCLUDED_ATTRIBUTE_NAMES.add(HIDDEN_LABEL_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(FEATURE_ID_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(ID_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(REGULATORY_FUNCTION_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(STRENGTH_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(RANK_ATTRIBUTE_NAME);
    }

	private CytoscapeNetworkUtilities() {
	}
	
	/**
	 * @return returns true if there are nodes selected in the current network
     *         else false is returned.
	 */
	public static boolean hasSelectedNodes() {
        if (Cytoscape.getCurrentNetworkView().getNetwork() == null) return false;
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetworkView().getNetwork();
		return !currentNetwork.getSelectedNodes().isEmpty();
	}

	/**
	 * Get all the selected nodes in the current network
	 */
	public static List<CyNode> getSelectedNodes() {
        if (Cytoscape.getCurrentNetworkView().getNetwork() == null)
            return Collections.emptyList();

        if (!hasSelectedNodes()) return Collections.emptyList();
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetworkView().getNetwork();
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
        if (Cytoscape.getCurrentNetworkView().getNetwork() == null)
            return Collections.emptyList();

		final CyNetwork currentNetwork = Cytoscape.getCurrentNetworkView().getNetwork();
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

    public static void addNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        final List<String> listAttribute = getListOfStringsAttributeForNode(node, attributeName, attributes);
        if (listAttribute.indexOf(attributeValue) < 0) {
            listAttribute.add(attributeValue);
            attributes.setListAttribute(node.getIdentifier(), attributeName, listAttribute);
            //Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
        }
    }

    private static List<String> getListOfStringsAttributeForNode(CyNode node, String attributeName, CyAttributes attributes) {
        if (attributes.getType(attributeName) == CyAttributes.TYPE_UNDEFINED)
            return new ArrayList<String>();
        if (attributes.getType(attributeName) != CyAttributes.TYPE_SIMPLE_LIST)
            throw new IllegalArgumentException();
        @SuppressWarnings("unchecked")
        final List<String> result = attributes.getListAttribute(node.getIdentifier(), attributeName);
        return result == null ? new ArrayList<String>() : result;
    }

    public static void setEdgeAttribute(CyEdge edge, String attributeName, String attributeValue){
		final CyAttributes attributes = Cytoscape.getEdgeAttributes();
		attributes.setAttribute(edge.getIdentifier(), attributeName, attributeValue);
		//Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}

    public static void setEdgeAttribute(CyEdge edge, String attributeName, int attributeValue){
		final CyAttributes attributes = Cytoscape.getEdgeAttributes();
		attributes.setAttribute(edge.getIdentifier(), attributeName, attributeValue);
		//Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}

    public static void setNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        attributes.setAttribute(node.getIdentifier(), attributeName, attributeValue);
        //Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
    }

    public static CyNode addNode(String nodeID, CyNetwork network, CyNetworkView view) {
        final CyNode node = Cytoscape.getCyNode(nodeID, true);
        network.addNode(node);
        view.addNodeView(node.getRootGraphIndex());
        //view.updateView();
        return node;
    }

    public static CyEdge addEdge(CyNode node1, CyNode node2, CyNetwork network, CyNetworkView view, String motif){
		final String interaction = (motif == null) ? "regulates" : "regulates via " + motif;
        final CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, interaction, true);
		network.addEdge(edge);
        view.addEdgeView(edge.getRootGraphIndex());
		//view.updateView();
		return edge;
	}

    public static CyEdge addEdge(CyNode node1, CyNode node2, String motif) {
		return addEdge(node1, node2, Cytoscape.getCurrentNetwork(), Cytoscape.getCurrentNetworkView(), motif);
	}

    public static void addEdgeAttribute(CyEdge edge, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getEdgeAttributes();
        final List<String> listAttribute = getListOfStringsAttributeForEdge(edge, attributeName, attributes);
        if (listAttribute.indexOf(attributeValue) < 0) {
            listAttribute.add(attributeValue);
            attributes.setListAttribute(edge.getIdentifier(), attributeName, listAttribute);
            //Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
        }
    }

    private static List<String> getListOfStringsAttributeForEdge(CyEdge edge, String attributeName, CyAttributes attributes) {
        if (attributes.getType(attributeName) == CyAttributes.TYPE_UNDEFINED)
            return new ArrayList<String>();
        if (attributes.getType(attributeName) != CyAttributes.TYPE_SIMPLE_LIST)
            throw new IllegalArgumentException();
        @SuppressWarnings("unchecked")
        final List<String> result = attributes.getListAttribute(edge.getIdentifier(), attributeName);
        return result == null ? new ArrayList<String>() : result;
    }

    public static String getNodeStringAttribute(CyNode edge, String attributeName) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        try {
            return (String) attributes.getAttribute(edge.getIdentifier(), attributeName);
        } catch (ClassCastException e) {
            return null;
        }
	}

    public static CyEdge createEdge(CyNetwork network, CyNetworkView view, final CyNode sourceNode, final CyNode targetNode, final GeneIdentifier factor, final AbstractMotif motif, final GeneIdentifier targetGene, final String regulatoryFunction) {
        final CyEdge edge = addEdge(sourceNode, targetNode, network, view, motif == null ? null : motif.getName());
		setEdgeAttribute(edge, REGULATOR_GENE_ATTRIBUTE_NAME, factor.getGeneName());
		setEdgeAttribute(edge, TARGET_GENE_ATTRIBUTE_NAME, targetGene.getGeneName());
	    setEdgeAttribute(edge, REGULATORY_FUNCTION_ATTRIBUTE_NAME, regulatoryFunction);
        if (motif != null) {
            for (AbstractMotif curMotif : motif.getMotifs()) {
		        addEdgeAttribute(edge, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
            }
            setEdgeAttribute(edge, FEATURE_ID_ATTRIBUTE_NAME, motif.getDatabaseID());
        } else {
            addEdgeAttribute(edge, MOTIF_ATTRIBUTE_NAME, "?metatargetome for " + factor.getGeneName() + "?");
        }
        return edge;
    }

    public static CyEdge createEdge(final CyNode sourceNode, final CyNode targetNode, final TranscriptionFactor factor, final AbstractMotif motif, final GeneIdentifier targetGene) {
        return createEdge(Cytoscape.getCurrentNetwork(), Cytoscape.getCurrentNetworkView(), sourceNode, targetNode, factor.getGeneID(), motif, targetGene, REGULATORY_FUNCTION_PREDICTED);
    }

    public static CyNode createSourceNode(final CyNetwork network, final CyNetworkView view, final String attributeName, final GeneIdentifier factorID, final AbstractMotif motif) {
        final CyNode node = addNode(factorID.getGeneName(), network, view);
		adjustSourceNode(node, attributeName, factorID, motif);
        return node;
    }

    public static void adjustSourceNode(final CyNode node, final String attributeName, final GeneIdentifier factorID, final AbstractMotif motif) {
        setNodeAttribute(node, ID_ATTRIBUTE_NAME, factorID.getGeneName());
        if (!attributeName.equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(node, attributeName, factorID.getGeneName());
        }
	    setNodeAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_REGULATOR);
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addNodeAttribute(node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
    }

    public static CyNode createTargetNode(final CyNetwork network, final CyNetworkView view, final String attributeName, final CandidateTargetGene targetGene, final AbstractMotif motif) {
        final CyNode node = addNode(targetGene.getGeneName(), network, view);
		adjustTargetNode(node, attributeName, targetGene, motif);
        return node;
    }

    public static void adjustTargetNode(final CyNode node, final String attributeName, final CandidateTargetGene targetGene, final AbstractMotif motif) {
        setNodeAttribute(node, ID_ATTRIBUTE_NAME, targetGene.getGeneName());
        if (!attributeName.equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(node, attributeName, targetGene.getGeneName());
        }
        //if the node is a regulator and target at the same time, it must stay a regulator ...
        if (!REGULATORY_FUNCTION_REGULATOR.equals(getNodeStringAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME))) {
		    setNodeAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_TARGET_GENE);
        }
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addNodeAttribute(node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
    }

    public static Map<String, List<CyNode>> getNodeMap(final String attributeName, final List<CyNode> nodes) {
        final Map<String, List<CyNode>> result = new HashMap<String, List<CyNode>>();
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        for (CyNode node : nodes) {
            String attributeValue = (String) attributes.getAttribute(node.getIdentifier(), attributeName);
            if (attributeValue == null) continue;
            if (result.containsKey(attributeValue)) {
                 result.get(attributeValue).add(node);
            } else {
                 final List<CyNode> list = new ArrayList<CyNode>();
                 list.add(node);
                 result.put(attributeValue, list);
            }
        }
        return result;
    }

    public static void activeSidePanel() {
        final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
        if (cytoPanel.indexOfComponent(PLUGIN_VISUAL_NAME) < 0) {
            cytoPanel.setSelectedIndex(0);
        } else {
            cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(PLUGIN_VISUAL_NAME));
        }
    }

    public static void applyVisualStyle() {
        final VisualStyle visualStyle = IRegulonVisualStyle.getVisualStyle();
        final VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        if (visualStyle != null) manager.setVisualStyle(visualStyle);
    }

    public static List<String> getPossibleGeneIDAttributes() {
        final List<String> results = new ArrayList<String>();

        final List<CyNode> nodes = getAllNodes();
        final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        final float minFraction = Float.parseFloat(ResourceBundle.getBundle("iRegulon").getString("percentage_nodes_not_null"));
		for (String attributeName : nodeAttributes.getAttributeNames()){
			if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_STRING
					&& !EXCLUDED_ATTRIBUTE_NAMES.contains(attributeName) ) {
                int nullCount = 0;
				for (CyNode node : nodes) {
					if (nodeAttributes.getStringAttribute(node.getIdentifier(), attributeName) == null) {
						nullCount++;
					}
				}
				if (nullCount < (nodes.size() * minFraction)) {
					results.add(attributeName);
				}
			}
		}
        Collections.sort(results);
        return results;
    }

    public static List<String> getPossibleGeneIDAttributesWithDefault() {
        final List<String> names = getPossibleGeneIDAttributes();
        if (names.isEmpty()) return Collections.singletonList(ID_ATTRIBUTE_NAME);
        else return names;
    }
}

