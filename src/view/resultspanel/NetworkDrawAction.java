package view.resultspanel;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import domainmodel.TranscriptionFactor;
import view.ResourceAction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class NetworkDrawAction extends ResourceAction {
    protected static final String PLUGIN_VISUAL_NAME = getBundle().getString("plugin_visual_name");
    protected static final String FEATURE_ID_ATTRIBUTE_NAME = "featureID";
    protected static final String MOTIF_ATTRIBUTE_NAME = "Motif";
    protected static final String REGULATORY_FUNCTION_ATTRIBUTE_NAME = "Regulatory function";
    protected static final String TARGET_GENE_ATTRIBUTE_NAME = "Target Gene";
    protected static final String REGULATOR_GENE_ATTRIBUTE_NAME = "Regulator Gene";
    protected static final String ID_ATTRIBUTE_NAME = "ID";
    protected static final String REGULATORY_FUNCTION_REGULATOR = "Regulator";
    protected static final String REGULATORY_FUNCTION_TARGET_GENE = "Regulated";
    protected static final String REGULATORY_FUNCTION_PREDICTED = "Predicted";


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
        this.attributeName = attributeName == null ? ID_ATTRIBUTE_NAME : attributeName;

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

    protected void addNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        final List<String> listAttribute = getListOfStringsAttributeForNode(node, attributeName, attributes);
        if (listAttribute.indexOf(attributeValue) < 0) {
            listAttribute.add(attributeValue);
            attributes.setListAttribute(node.getIdentifier(), attributeName, listAttribute);
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
        }
    }

    private List<String> getListOfStringsAttributeForNode(CyNode node, String attributeName, CyAttributes attributes) {
        if (attributes.getType(attributeName) == CyAttributes.TYPE_UNDEFINED)
            return new ArrayList<String>();
        if (attributes.getType(attributeName) != CyAttributes.TYPE_SIMPLE_LIST)
            throw new IllegalArgumentException();
        @SuppressWarnings("unchecked")
        final List<String> result = attributes.getListAttribute(node.getIdentifier(), attributeName);
        return result == null ? new ArrayList<String>() : result;
    }

    protected void setEdgeAttribute(CyEdge edge, String attributeName, String attributeValue){
		final CyAttributes attributes = Cytoscape.getEdgeAttributes();
		attributes.setAttribute(edge.getIdentifier(), attributeName, attributeValue);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}

    protected void setNodeAttribute(CyNode node, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        attributes.setAttribute(node.getIdentifier(), attributeName, attributeValue);
        Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
    }

    protected CyNode addNode(String nodeID, CyNetwork network, CyNetworkView view) {
        final CyNode node = Cytoscape.getCyNode(nodeID, true);
        network.addNode(node);
        view.addNodeView(node.getRootGraphIndex());
        //view.updateView();
        return node;
    }

    protected CyEdge addEdge(CyNode node1, CyNode node2, CyNetwork network, CyNetworkView view, String motif){
		final CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "regulates " + motif, true);
		network.addEdge(edge);
        view.addEdgeView(edge.getRootGraphIndex());
		//view.updateView();
		return edge;
	}

    protected CyEdge addEdge(CyNode node1, CyNode node2, String motif) {
		return addEdge(node1, node2, Cytoscape.getCurrentNetwork(), Cytoscape.getCurrentNetworkView(), motif);
	}

    protected void addEdgeAttribute(CyEdge edge, String attributeName, String attributeValue) {
        final CyAttributes attributes = Cytoscape.getEdgeAttributes();
        final List<String> listAttribute = getListOfStringsAttributeForEdge(edge, attributeName, attributes);
        if (listAttribute.indexOf(attributeValue) < 0) {
            listAttribute.add(attributeValue);
            attributes.setListAttribute(edge.getIdentifier(), attributeName, listAttribute);
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
        }
    }

    private List<String> getListOfStringsAttributeForEdge(CyEdge edge, String attributeName, CyAttributes attributes) {
        if (attributes.getType(attributeName) == CyAttributes.TYPE_UNDEFINED)
            return new ArrayList<String>();
        if (attributes.getType(attributeName) != CyAttributes.TYPE_SIMPLE_LIST)
            throw new IllegalArgumentException();
        @SuppressWarnings("unchecked")
        final List<String> result = attributes.getListAttribute(edge.getIdentifier(), attributeName);
        return result == null ? new ArrayList<String>() : result;
    }

    protected String getNodeStringAttribute(CyNode edge, String attributeName) {
        final CyAttributes attributes = Cytoscape.getNodeAttributes();
        try {
            return (String) attributes.getAttribute(edge.getIdentifier(), attributeName);
        } catch (ClassCastException e) {
            return null;
        }
	}

    protected CyEdge createEdge(final CyNode sourceNode, final CyNode targetNode, final TranscriptionFactor factor, final AbstractMotif motif, final GeneIdentifier targetGene) {
        final CyEdge edge = addEdge(sourceNode, targetNode, motif.getName());
		setEdgeAttribute(edge, REGULATOR_GENE_ATTRIBUTE_NAME, factor.getGeneID().getGeneName());
		setEdgeAttribute(edge, TARGET_GENE_ATTRIBUTE_NAME, targetGene.getGeneName());
	    setEdgeAttribute(edge, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_PREDICTED);
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addEdgeAttribute(edge, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
		setEdgeAttribute(edge, FEATURE_ID_ATTRIBUTE_NAME, "" + motif.getDatabaseID());
        return edge;
    }

    protected CyNode createSourceNode(final CyNetwork network, final CyNetworkView view, final GeneIdentifier factorID, final AbstractMotif motif) {
        final CyNode node = addNode(factorID.getGeneName(), network, view);
		setNodeAttribute(node, ID_ATTRIBUTE_NAME, factorID.getGeneName());
        if (!getAttributeName().equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(node, getAttributeName(), factorID.getGeneName());
        }
	    setNodeAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_REGULATOR);
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addNodeAttribute(node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
        return node;
    }

    protected CyNode createSourceNode(final CyNetwork network, final CyNetworkView view, final TranscriptionFactor factor, final AbstractMotif motif) {
        return createSourceNode(network, view, factor.getGeneID(), motif);
    }

    protected CyNode createTargetNode(final CyNetwork network, final CyNetworkView view, final CandidateTargetGene targetGene, final AbstractMotif motif) {
        final CyNode node = addNode(targetGene.getGeneName(), network, view);
		setNodeAttribute(node, ID_ATTRIBUTE_NAME, targetGene.getGeneName());
        if (!getAttributeName().equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(node, getAttributeName(), targetGene.getGeneName());
        }
        //if the node is a regulator and target at the same time, it must stay a regulator ...
        if (!REGULATORY_FUNCTION_REGULATOR.equals(getNodeStringAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME))) {
		    setNodeAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_TARGET_GENE);
        }
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addNodeAttribute(node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
        return node;
    }

    protected void activeSidePanel() {
        final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);
        if (cytoPanel.indexOfComponent(PLUGIN_VISUAL_NAME) < 0) {
            cytoPanel.setSelectedIndex(0);
        } else {
            cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(PLUGIN_VISUAL_NAME));
        }
    }

    protected Map<String, List<CyNode>> getNodeMap(final String attributeName, final List<CyNode> nodes) {
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
}
