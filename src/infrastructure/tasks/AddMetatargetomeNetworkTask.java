package infrastructure.tasks;

import domainmodel.*;
import infrastructure.IRegulonResourceBundle;
import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;
import servercommunication.tasks.TargetomeResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public final class AddMetatargetomeNetworkTask extends NetworkTask {
    private static final AbstractMotif NO_MOTIF = new AbstractMotif("NaN",
            Collections.<CandidateTargetGene>emptyList(),
            Collections.<TranscriptionFactor>emptyList()) {
        @Override
        public int getDatabaseID() {
            return Integer.MIN_VALUE;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public float getAUCValue() {
            return Float.NaN;
        }

        @Override
        public float getNEScore() {
            return Float.NaN;
        }

        @Override
        public Motif getBestMotif() {
            return null;
        }

        @Override
        public List<Motif> getMotifs() {
            return Collections.emptyList();
        }
    };

    private final GeneIdentifier transcriptionFactor;
    private final TargetomeResult targetome;

    public AddMetatargetomeNetworkTask(NetworkResult network, boolean createNodesIfNecessary, String attributeName, GeneIdentifier transcriptionFactor, TargetomeResult targetome) {
        super(network, createNodesIfNecessary, attributeName);
        this.transcriptionFactor = transcriptionFactor;
        this.targetome = targetome;
    }

    public GeneIdentifier getTranscriptionFactor() {
        return transcriptionFactor;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        if (getNetwork() == null) throw new IllegalStateException();

        taskMonitor.setStatusMessage(IRegulonResourceBundle.PLUGIN_NAME + ": Add metatargetome for " + getTranscriptionFactor().getGeneName());
        taskMonitor.setProgress(0.0);

        final Map<String, List<CyNode>> name2nodes = !createNodesIfNecessary
                ? NetworkUtilities.getInstance().getID2NodesMap(getNetwork(), getAttributeName())
                : Collections.<String, List<CyNode>>emptyMap();

        final List<CyNode> sourceNodes = createNodesIfNecessary
                ? Collections.singletonList(NetworkUtilities.getInstance().createSourceNode(getNetwork(), attributeName, transcriptionFactor, NO_MOTIF))
                : findCyNodes(transcriptionFactor, name2nodes);
        if (sourceNodes.isEmpty()) return;

        final int totalCount = sourceNodes.size() * targetome.getTargetome().size();
        int count = 0;
        for (final CyNode sourceNode : sourceNodes) {
            if (!createNodesIfNecessary) {
                NetworkUtilities.getInstance().adjustSourceNode(getNetwork(), sourceNode, attributeName, transcriptionFactor, NO_MOTIF);
            }

            for (final CandidateTargetGene targetGene : targetome.getTargetome()) {
                if (cancelled) return;
                taskMonitor.setProgress(((double) count) / totalCount);

                final List<CyNode> targetNodes = createNodesIfNecessary
                        ? Collections.singletonList(NetworkUtilities.getInstance().createTargetNode(getNetwork(), attributeName, targetGene, NO_MOTIF))
                        : findCyNodes(targetGene.getGeneID(), name2nodes);

                for (final CyNode targetNode : targetNodes) {
                    if (!createNodesIfNecessary) {
                        NetworkUtilities.getInstance().adjustTargetNode(getNetwork(), targetNode, attributeName, targetGene, NO_MOTIF);
                    }
                    NetworkUtilities.getInstance().createMetatargetomeEdge(getNetwork(), sourceNode, targetNode, transcriptionFactor, targetGene);
                }

                count++;

                taskMonitor.setProgress(count / totalCount);
            }
        }

        taskMonitor.setProgress(1.0);
    }

    private List<CyNode> findCyNodes(final GeneIdentifier geneIdentifier, final Map<String, List<CyNode>> name2nodes) {
        if (name2nodes.containsKey(geneIdentifier.getGeneName())) {
            return name2nodes.get(geneIdentifier.getGeneName());
        } else {
            return Collections.emptyList();
        }
    }
}
