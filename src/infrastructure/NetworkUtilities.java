package infrastructure;

import domainmodel.*;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.*;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;

import java.util.*;


public final class NetworkUtilities {
    public static final String PLUGIN_VISUAL_NAME = IRegulonResourceBundle.PLUGIN_VISUAL_NAME_HTML;

    public static final String ASSEMBLY_ATTRIBUTE_NAME = "Assembly";
    public static final String FEATURE_ID_ATTRIBUTE_NAME = "featureID";
    public static final String MOTIF_ATTRIBUTE_NAME = "Motif";
    public static final String MOTIF_ID_ATTRIBUTE_NAME = "MotifID";
    public static final String TRACK_ATTRIBUTE_NAME = "Track";
    public static final String TRACK_ID_ATTRIBUTE_NAME = "TrackID";
    public static final String REGULATORY_FUNCTION_ATTRIBUTE_NAME = "Regulatory function";
    public static final String TARGET_GENE_ATTRIBUTE_NAME = "Target Gene";
    public static final String REGULATOR_GENE_ATTRIBUTE_NAME = "Regulator Gene";
    public static final String STRENGTH_ATTRIBUTE_NAME = "Strength";
    public static final String RANK_ATTRIBUTE_NAME = "Rank";
    public static final String INTERACTION_ATTRIBUTE_NAME = "interaction";

    public static final String ID_ATTRIBUTE_NAME = CyNetwork.NAME;
    public static final String SELECTED_ATTRIBUTE_NAME = CyNetwork.SELECTED;
    public static final String HIDDEN_LABEL_ATTRIBUTE_NAME = "hiddenLabel";

    public static final String REGULATORY_FUNCTION_REGULATOR = "Regulator";
    public static final String REGULATORY_FUNCTION_TARGET_GENE = "Regulated";
    public static final String REGULATORY_FUNCTION_PREDICTED = "Predicted";
    public static final String REGULATORY_FUNCTION_METATARGETOME = "Metatargetome";

    private static final float NON_NULL_FRACTION = Float.parseFloat(ResourceBundle.getBundle("iRegulon").getString("percentage_nodes_not_null"));

    private static final Set<String> EXCLUDED_ATTRIBUTE_NAMES = new HashSet<String>();

    static {
        EXCLUDED_ATTRIBUTE_NAMES.add(HIDDEN_LABEL_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(FEATURE_ID_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(REGULATORY_FUNCTION_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(SELECTED_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(STRENGTH_ATTRIBUTE_NAME);
        EXCLUDED_ATTRIBUTE_NAMES.add(RANK_ATTRIBUTE_NAME);
    }

    private static NetworkUtilities INSTANCE;

    private final CyApplicationManager applicationManager;
    private final CyServiceRegistrar serviceRegistrar;

    private NetworkUtilities(final CyApplicationManager manager, final CyServiceRegistrar serviceRegistrar) {
        if (manager == null) throw new IllegalArgumentException();
        if (serviceRegistrar == null) throw new IllegalArgumentException();
        applicationManager = manager;
        this.serviceRegistrar = serviceRegistrar;
    }

    public static NetworkUtilities getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public static void install(final CySwingAppAdapter adapter) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new NetworkUtilities(adapter.getCyApplicationManager(), adapter.getCyServiceRegistrar());
    }

    private <S> S getService(Class<S> clazz) {
        return serviceRegistrar.getService(clazz);
    }

    public CyNetwork getCurrentNetwork() {
        return applicationManager.getCurrentNetwork();
    }

    public String getCurrentNetworkName() {
        final CyNetwork network = NetworkUtilities.getInstance().getCurrentNetwork();
        if (network == null) return "";
        else return network.getDefaultNetworkTable().getRow(network.getSUID()).get(CyNetwork.NAME, String.class);
    }

    public void setCurrentNetwork(final CyNetwork network) {
        applicationManager.setCurrentNetwork(network);
    }

    public CyNetwork createNetwork(final String title) {
        final CyNetworkFactory factory = getService(CyNetworkFactory.class);
        final CyNetworkNaming naming = getService(CyNetworkNaming.class);
        final CyNetworkManager manager = getService(CyNetworkManager.class);

        final CyNetwork network = factory.createNetwork();
        network.getDefaultNetworkTable().getRow(network.getSUID())
                .set(CyNetwork.NAME, naming.getSuggestedNetworkTitle(title == null ? "" : title));

        manager.addNetwork(network);

        return network;
    }

    public CyNetworkView getCurrentNetworkView() {
        return applicationManager.getCurrentNetworkView();
    }

    public CyNetworkView createNetworkView(final CyNetwork network) {
        if (network == null) throw new IllegalArgumentException();
        final CyNetworkViewFactory factory = getService(CyNetworkViewFactory.class);
        final CyNetworkViewManager manager = getService(CyNetworkViewManager.class);

        final Collection<CyNetworkView> views = manager.getNetworkViews(network);
        if (views.isEmpty()) {
            final CyNetworkView view = factory.createNetworkView(network);
            manager.addNetworkView(view);
            return view;
        } else {
            return views.iterator().next();
        }
    }

    public TaskIterator applyLayout(final CyNetworkView view) {
        final CyLayoutAlgorithm layout = getService(CyLayoutAlgorithm.class);
        return layout.createTaskIterator(view, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
    }

    /**
     * @return Returns true if there are nodes selected in the current network.
     *         If no nodes are selected in the current network, false is returned.
     */
    public boolean hasSelectedNodes(final CyNetwork network) {
        return !getSelectedNodes(network).isEmpty();
    }

    /**
     * Get all the selected nodes in the current network.
     */
    public List<CyNode> getSelectedNodes(final CyNetwork network) {
        if (network == null) return Collections.emptyList();
        final List<CyNode> nodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
        return Collections.unmodifiableList(new ArrayList<CyNode>(nodes));
    }

    public List<GeneIdentifier> getSelectedNodesAsGeneIDs(final CyNetwork network,
                                                          final String attributeName,
                                                          final SpeciesNomenclature speciesNomenclature) {
        if (speciesNomenclature == null) throw new IllegalArgumentException();
        if (network == null) return Collections.emptyList();
        final List<GeneIdentifier> results = new ArrayList<GeneIdentifier>();
        for (CyNode node : getSelectedNodes(network)) {
            final String name = network.getDefaultNodeTable().getRow(node.getSUID()).get(attributeName == null ? ID_ATTRIBUTE_NAME : attributeName,
                    String.class);
            if (name != null) results.add(new GeneIdentifier(name, speciesNomenclature));
        }
        return results;
    }

    /**
     * Get all the nodes in the current network.
     */
    @Deprecated
    public List<CyNode> getAllNodes(final CyNetwork network) {
        if (network == null) return Collections.emptyList();
        return Collections.unmodifiableList(network.getNodeList());
    }

    /**
     * Get all the edges in the current network.
     */
    @Deprecated
    public static List<CyEdge> getAllEdges(final CyNetwork network) {
        if (network == null) return Collections.emptyList();
        return Collections.unmodifiableList(network.getEdgeList());
    }

    public void addNodeAttribute(final CyNetwork network, final CyNode node, final String attributeName, final String attributeValue) {
        if (network == null) throw new IllegalArgumentException();
        addAttribute(network.getDefaultEdgeTable(), node, attributeName, attributeValue);
    }

    public void addEdgeAttribute(final CyNetwork network, final CyEdge edge, final String attributeName, final String attributeValue) {
        if (network == null) throw new IllegalArgumentException();
        addAttribute(network.getDefaultEdgeTable(), edge, attributeName, attributeValue);
    }

    private void addAttribute(CyTable attributes, CyIdentifiable component, final String attributeName, final String attributeValue) {
        if (component == null) throw new IllegalArgumentException();
        if (attributeName == null) throw new IllegalArgumentException();

        final List<String> listAttribute = getListOfStringsAttribute(component, attributeName, attributes);
        if (listAttribute.indexOf(attributeValue) < 0) {
            listAttribute.add(attributeValue);
            try {
                attributes.getRow(component.getSUID()).set(attributeName, listAttribute);
            } catch (IllegalArgumentException e) {
                Logger.getInstance().error(e);
                attributes.getRow(component.getSUID()).set(attributeName, attributeValue);
            }
        }
    }

    private <S extends CyIdentifiable> List<String> getListOfStringsAttribute(S component, String attributeName, CyTable attributes) {
        final CyRow row = attributes.getRow(component.getSUID());
        final CyColumn column = attributes.getColumn(attributeName);
        if (column == null) {
            attributes.createListColumn(attributeName, String.class, false);
            return new ArrayList<String>();
        } else if (column.getType().equals(String.class)) {
            final List<String> result = new ArrayList<String>();
            result.add(row.get(attributeName, String.class));
            return result;
        } else if (column.getType().equals(List.class) && column.getListElementType().equals(String.class)) {
            @SuppressWarnings("unchecked")
            final List<String> result = row.get(attributeName, List.class);
            return result == null ? new ArrayList<String>() : result;
        } else throw new IllegalArgumentException();
    }

    public <S> void setNodeAttribute(final CyNetwork network, final CyNode node, final String attributeName, final S attributeValue) {
        if (network == null) throw new IllegalArgumentException();
        final CyTable attributes = network.getDefaultNodeTable();
        setAttribute(attributes, node, attributeName, attributeValue);
    }

    public <S> void setEdgeAttribute(final CyNetwork network, CyEdge edge, String attributeName, S attributeValue) {
        if (network == null) throw new IllegalArgumentException();
        final CyTable attributes = network.getDefaultEdgeTable();
        setAttribute(attributes, edge, attributeName, attributeValue);
    }

    private <S extends CyIdentifiable, T> void setAttribute(CyTable attributes, S component, String attributeName, T attributeValue) {
        try {
            if (attributes.getColumn(attributeName) == null) {
                attributes.createColumn(attributeName, attributeValue.getClass(), false);
            }
            attributes.getRow(component.getSUID()).set(attributeName, attributeValue);
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error(e);
            attributes.getRow(component.getSUID()).set(attributeName, attributeValue != null ? attributeValue.toString() : null);
        }
    }

    public CyNode findNode(final CyNetwork network, final String attributeName, final String id, final boolean createIfNecessary) {
        if (network == null) throw new IllegalArgumentException();
        final String columnName = attributeName == null ? ID_ATTRIBUTE_NAME : attributeName;
        final Collection<CyRow> rows;
        if (network.getDefaultNodeTable().getColumn(attributeName) == null) {
            network.getDefaultNodeTable().createColumn(attributeName, String.class, false);
            rows = Collections.emptyList();
        } else rows = network.getDefaultNodeTable().getMatchingRows(columnName, id);
        if (rows == null || rows.isEmpty()) {
            if (createIfNecessary) {
                final CyNode node = network.addNode();
                final CyRow row = network.getDefaultNodeTable().getRow(node.getSUID());
                row.set(attributeName, id);
                if (!ID_ATTRIBUTE_NAME.equals(attributeName)) row.set(ID_ATTRIBUTE_NAME, id);
                return node;
            } else {
                return null;
            }
        } else if (rows.size() == 1) {
            final Long SUID = rows.iterator().next().get(CyNetwork.SUID, Long.class);
            return network.getNode(SUID);
        } else {
            throw new IllegalStateException();
        }
    }

    private String getID(final CyIdentifiable component, final CyTable attributes) {
        return attributes.getRow(component.getSUID()).get(ID_ATTRIBUTE_NAME, String.class);
    }

    public CyEdge createMetatargetomeEdge(final CyNetwork network, final CyNode sourceNode, final CyNode targetNode,
                                          final GeneIdentifier factor, final CandidateTargetGene targetGene) {
        final CyEdge edge = createEdge(network, sourceNode, targetNode, factor, null, targetGene.getGeneID(), REGULATORY_FUNCTION_METATARGETOME);
        setEdgeAttribute(network, edge, STRENGTH_ATTRIBUTE_NAME, targetGene.getRank());
        return edge;
    }

    public CyEdge createPredictedEdge(final CyNetwork network, final CyNode sourceNode, final CyNode targetNode,
                                      final GeneIdentifier factor, final AbstractMotifAndTrack motifOrTrack, final CandidateTargetGene targetGene) {
        final CyEdge edge = createEdge(network, sourceNode, targetNode, factor, motifOrTrack, targetGene.getGeneID(), REGULATORY_FUNCTION_PREDICTED);
        setEdgeAttribute(network, edge, RANK_ATTRIBUTE_NAME, targetGene.getRank());
        return edge;
    }

    public CyEdge createEdge(final CyNetwork network, final CyNode node1, final CyNode node2, final String motifOrTrack) {
        if (network == null) throw new IllegalArgumentException();
        if (network.getNode(node1.getSUID()) == null || network.getNode(node2.getSUID()) == null) return null;

        final String interaction = (motifOrTrack == null) ? "regulates" : "regulates via " + motifOrTrack;
        final CyEdge edge = network.addEdge(node1, node2, true);
        final CyRow row = network.getDefaultEdgeTable().getRow(edge.getSUID());
        row.set(ID_ATTRIBUTE_NAME, getID(node1, network.getDefaultNodeTable()) + " " + interaction + " " + getID(node2, network.getDefaultNodeTable()));
        row.set(INTERACTION_ATTRIBUTE_NAME, interaction);
        return edge;
    }

    private CyEdge createEdge(final CyNetwork network, final CyNode sourceNode, final CyNode targetNode,
                              final GeneIdentifier factor, final AbstractMotifAndTrack motifOrTrack, final GeneIdentifier targetGene,
                              final String regulatoryFunction) {
        final CyEdge edge = createEdge(network, sourceNode, targetNode, motifOrTrack == null ? null : motifOrTrack.getName());

        setEdgeAttribute(network, edge, ASSEMBLY_ATTRIBUTE_NAME, targetGene.getSpeciesNomenclature().getAssembly());
        setEdgeAttribute(network, edge, REGULATOR_GENE_ATTRIBUTE_NAME, factor.getGeneName());
        setEdgeAttribute(network, edge, TARGET_GENE_ATTRIBUTE_NAME, targetGene.getGeneName());
        setEdgeAttribute(network, edge, REGULATORY_FUNCTION_ATTRIBUTE_NAME, regulatoryFunction);

        if (motifOrTrack != null) {
            if (motifOrTrack.isMotif()) {
                AbstractMotif motif = (AbstractMotif) motifOrTrack;
                for (AbstractMotif curMotif : motif.getMotifs()) {
                    addEdgeAttribute(network, edge, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
                }
                final long motifID = getListOfStringsAttribute(edge, MOTIF_ATTRIBUTE_NAME, network.getDefaultEdgeTable()).hashCode();
                setEdgeAttribute(network, edge, MOTIF_ID_ATTRIBUTE_NAME, motifID);
            } else if (motifOrTrack.isTrack()) {
                AbstractTrack track = (AbstractTrack) motifOrTrack;
                for (AbstractTrack curTrack : track.getTracks()) {
                    addEdgeAttribute(network, edge, TRACK_ATTRIBUTE_NAME, curTrack.getName());
                }
                final long trackID = getListOfStringsAttribute(edge, TRACK_ATTRIBUTE_NAME, network.getDefaultEdgeTable()).hashCode();
                setEdgeAttribute(network, edge, TRACK_ID_ATTRIBUTE_NAME, trackID);
            }
            setEdgeAttribute(network, edge, FEATURE_ID_ATTRIBUTE_NAME, motifOrTrack.getDatabaseID());
        } else {
            setEdgeAttribute(network, edge, MOTIF_ID_ATTRIBUTE_NAME, factor.hashCode());
        }

        return edge;
    }

    public CyNode createSourceNode(final CyNetwork network, final String attributeName, final GeneIdentifier factor, final AbstractMotifAndTrack motifOrTrack) {
        if (factor == null) throw new IllegalArgumentException();
        final CyNode node = findNode(network, attributeName, factor.getGeneName(), true);
        adjustSourceNode(network, node, attributeName, factor, motifOrTrack);
        return node;
    }

    public void adjustSourceNode(final CyNetwork network, final CyNode node, final String attributeName, final GeneIdentifier factor, final AbstractMotifAndTrack motifOrTrack) {
        setNodeAttribute(network, node, ID_ATTRIBUTE_NAME, factor.getGeneName());
        if (!attributeName.equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(network, node, attributeName, factor.getGeneName());
        }
        setNodeAttribute(network, node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_REGULATOR);

        if (motifOrTrack.isMotif()) {
            AbstractMotif motif = (AbstractMotif) motifOrTrack;
            for (AbstractMotif curMotif : motif.getMotifs()) {
                addNodeAttribute(network, node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
            }
        } else if (motifOrTrack.isTrack()) {
            AbstractTrack track = (AbstractTrack) motifOrTrack;
            for (AbstractTrack curTrack : track.getTracks()) {
                addNodeAttribute(network, node, TRACK_ATTRIBUTE_NAME, curTrack.getName());
            }
        }
    }

    public CyNode createTargetNode(final CyNetwork network, final String attributeName, final CandidateTargetGene targetGene, final AbstractMotifAndTrack motifOrTrack) {
        if (targetGene == null) throw new IllegalArgumentException();
        final CyNode node = findNode(network, attributeName, targetGene.getGeneName(), true);
        adjustTargetNode(network, node, attributeName, targetGene, motifOrTrack);
        return node;
    }

    public void adjustTargetNode(final CyNetwork network, final CyNode node, final String attributeName, final CandidateTargetGene targetGene, final AbstractMotifAndTrack motifOrTrack) {
        setNodeAttribute(network, node, ID_ATTRIBUTE_NAME, targetGene.getGeneName());
        if (!attributeName.equals(ID_ATTRIBUTE_NAME)) {
            setNodeAttribute(network, node, attributeName, targetGene.getGeneName());
        }
        /* If the node is a regulator and target at the same time, it must stay a regulator. */
        if (!REGULATORY_FUNCTION_REGULATOR.equals(getAttribute(network.getDefaultNodeTable(), node, REGULATORY_FUNCTION_ATTRIBUTE_NAME))) {
            setNodeAttribute(network, node, REGULATORY_FUNCTION_ATTRIBUTE_NAME, REGULATORY_FUNCTION_TARGET_GENE);
        }

        if (motifOrTrack.isMotif()) {
            AbstractMotif motif = (AbstractMotif) motifOrTrack;
            for (AbstractMotif curMotif : motif.getMotifs()) {
                addNodeAttribute(network, node, MOTIF_ATTRIBUTE_NAME, curMotif.getName());
            }
        } else if (motifOrTrack.isTrack()) {
            AbstractTrack track = (AbstractTrack) motifOrTrack;
            for (AbstractTrack curTrack : track.getTracks()) {
                addNodeAttribute(network, node, TRACK_ATTRIBUTE_NAME, curTrack.getName());
            }
        }
    }

    private String getAttribute(final CyTable attributes, CyIdentifiable component, final String attributeName) {
        final CyRow row = attributes.getRow(component.getSUID());
        return row.get(attributeName, String.class);
    }

    public Map<String, List<CyNode>> getID2NodesMap(final CyNetwork network, final String attributeName) {
        if (network == null) throw new IllegalArgumentException();
        final List<CyNode> nodes = network.getNodeList();
        final Map<String, List<CyNode>> result = new HashMap<String, List<CyNode>>();
        final CyTable attributes = network.getDefaultNodeTable();
        for (CyNode node : nodes) {
            final String attributeValue = attributes.getRow(node.getSUID()).get(attributeName, String.class);
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

    public List<String> getPossibleIDAttributes(final CyNetwork network) {
        final List<String> names = getPossibleGeneIDAttributes(network);
        if (names.isEmpty()) return Collections.singletonList(ID_ATTRIBUTE_NAME);
        else return names;
    }

    private List<String> getPossibleGeneIDAttributes(final CyNetwork network) {
        if (network == null) return Collections.singletonList(ID_ATTRIBUTE_NAME);
        final List<String> results = new ArrayList<String>();

        final List<CyNode> nodes = network.getNodeList();
        final CyTable attributes = network.getDefaultNodeTable();
        for (CyColumn column : attributes.getColumns()) {
            final String attributeName = column.getName();
            if (column.getType().equals(String.class)
                    && !column.getVirtualColumnInfo().isVirtual()
                    && !EXCLUDED_ATTRIBUTE_NAMES.contains(attributeName)) {
                int nullCount = 0;
                for (CyNode node : nodes) {
                    if (attributes.getRow(node.getSUID()).get(attributeName, String.class) == null) {
                        nullCount++;
                    }
                }
                if (nullCount < (nodes.size() * NON_NULL_FRACTION)) {
                    results.add(attributeName);
                }
            }
        }
        Collections.sort(results);
        return results;
    }
}

