package view.resultspanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.*;
import view.CytoscapeNetworkUtilities;
import view.ResourceAction;

import java.util.*;


public abstract class TranscriptionFactorDependentAction extends ResourceAction
        implements ListSelectionListener, DocumentListener {
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


    private SelectedMotif selectedMotif;
	private TranscriptionFactor transcriptionFactor;
    private Refreshable view;
	
	public TranscriptionFactorDependentAction(final String actionName,
                                              final SelectedMotif selectedMotif,
                                              final TFComboBox selectedTranscriptionFactor,
                                              final Refreshable view) {
        super(actionName);
		setEnabled(false);
		this.selectedMotif = selectedMotif;
		transcriptionFactor = null;
        this.view = view;

        selectedTranscriptionFactor.registerAction(this);

        //TODO: Implementation of undo functionality can be done via cytoscape.util.undo.CyUndo: a tiny class
        // for supporting undo in the Cytoscape context. If you want to post an edit, use
        // CyUndo.getUndoableEditSupport().postEdit(yourEdit) where yourEdit implements the UndoableEdit.
        // Every implementing action needs to use basic operations like addNode, addEdge which
        // are implemented in this abstract class. Adding undo functionality is just adding
        // the creation of UndoableEdit objects, combining them in a composite UndoableEdit and
        // registering this compound object with Cytoscape.
	}

    protected Refreshable getView() {
        return view;
    }

    public SelectedMotif getSelectedMotif() {
        return this.selectedMotif;
    }

	public TranscriptionFactor getTranscriptionFactor() {
		return this.transcriptionFactor;
	}

	private void setTranscriptionFactorByName(String geneName){
		GeneIdentifier geneID = new GeneIdentifier(geneName, SpeciesNomenclature.UNKNOWN);
		this.transcriptionFactor = new TranscriptionFactor(geneID, Float.NaN, Float.NaN, null, null, null, null);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(!model.isSelectionEmpty());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		} else {
			this.setEnabled(true);
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			this.setEnabled(true);
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
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
        view.updateView();
        return node;
    }

    protected CyEdge addEdge(CyNode node1, CyNode node2, CyNetwork network, CyNetworkView view, String motif){
		final CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "regulates " + motif, true);
		network.addEdge(edge);
        view.addEdgeView(edge.getRootGraphIndex());
		view.updateView();
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

    protected boolean addEdges(final CyNetwork network, final CyNetworkView view,
                               final TranscriptionFactor factor, final AbstractMotif motif,
                               final boolean createNodesIfNecessary) {
        final Map<String,List<CyNode>> name2nodes = getNodeMap(selectedMotif.getAttributeName(), CytoscapeNetworkUtilities.getAllNodes());

        final List<CyNode> sourceNodes = createNodesIfNecessary
                ? Collections.singletonList(createSourceNode(network, view, factor, motif))
                : findCyNodes(factor.getGeneID(), name2nodes);
        if (sourceNodes.isEmpty()) return false;

        for (final CyNode sourceNode : sourceNodes) {
            for (CandidateTargetGene targetGene : motif.getCandidateTargetGenes()) {
                final GeneIdentifier geneID = targetGene.getGeneID();

                final List<CyNode> targetNodes = createNodesIfNecessary
                    ? Collections.singletonList(createTargetNode(network, view, targetGene, motif))
                    : findCyNodes(geneID, name2nodes);

                for (final CyNode targetNode : targetNodes) {
                    createEdge(sourceNode, targetNode, motif, geneID);
                }
		    }
        }

        return true;
    }

	private List<CyNode> findCyNodes(final GeneIdentifier geneIdentifier, final Map<String,List<CyNode>> name2nodes) {
        if (name2nodes.containsKey(geneIdentifier.getGeneName())) {
            return name2nodes.get(geneIdentifier.getGeneName());
        } else {
            return Collections.emptyList();
        }
    }

    protected CyEdge createEdge(final CyNode sourceNode, final CyNode targetNode, final AbstractMotif motif, final GeneIdentifier targetGene) {
        final CyEdge edge = addEdge(sourceNode, targetNode, motif.getName());
		setEdgeAttribute(edge, REGULATOR_GENE_ATTRIBUTE_NAME, getTranscriptionFactor().getGeneID().getGeneName());
		setEdgeAttribute(edge, TARGET_GENE_ATTRIBUTE_NAME, targetGene.getGeneName());
	    setEdgeAttribute(edge, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_PREDICTED);
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addEdgeAttribute(edge, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
		setEdgeAttribute(edge, FEATURE_ID_ATTRIBUTE_NAME, "" + motif.getDatabaseID());
        return edge;
    }

    protected CyNode createSourceNode(final CyNetwork network, final CyNetworkView view, final TranscriptionFactor factor, final AbstractMotif motif) {
        final CyNode node = addNode(factor.getName(), network, view);
		setNodeAttribute(node, ID_ATTRIBUTE_NAME, factor.getName());
	    setNodeAttribute(node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_REGULATOR);
        for (AbstractMotif curMotif : motif.getMotifs()) {
		    addNodeAttribute(node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
        }
        return node;
    }

    protected CyNode createTargetNode(final CyNetwork network, final CyNetworkView view, final CandidateTargetGene targetGene, final AbstractMotif motif) {
        final CyNode node = addNode(targetGene.getGeneName(), network, view);
		setNodeAttribute(node, ID_ATTRIBUTE_NAME, targetGene.getGeneName());
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
