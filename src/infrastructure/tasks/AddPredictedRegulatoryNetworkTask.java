package infrastructure.tasks;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public final class AddPredictedRegulatoryNetworkTask extends NetworkTask {
    private final GeneIdentifier transcriptionFactor;
    private final AbstractMotifAndTrack motifOrTrack;

    public AddPredictedRegulatoryNetworkTask(final NetworkResult network, String attributeName, GeneIdentifier transcriptionFactor, AbstractMotifAndTrack motifOrTrack, boolean createNodesIfNecessary) {
        super(network, createNodesIfNecessary, attributeName);
        this.transcriptionFactor = transcriptionFactor;
        this.motifOrTrack = motifOrTrack;
    }

    public GeneIdentifier getTranscriptionFactor() {
        return transcriptionFactor;
    }

    public AbstractMotifAndTrack getMotifOrTrack() {
        return motifOrTrack;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        taskMonitor.setStatusMessage("Adding nodes and edges ...");
        taskMonitor.setProgress(0.0);

        final Map<String, List<CyNode>> name2nodes = !createNodesIfNecessary
                ? NetworkUtilities.getInstance().getID2NodesMap(getNetwork(), attributeName)
                : Collections.<String, List<CyNode>>emptyMap();

        final List<CyNode> sourceNodes = createNodesIfNecessary
                ? Collections.singletonList(NetworkUtilities.getInstance().createSourceNode(getNetwork(), attributeName, transcriptionFactor, motifOrTrack))
                : findCyNodes(transcriptionFactor, name2nodes);
        if (sourceNodes.isEmpty()) return;

        final int totalCount = sourceNodes.size() * motifOrTrack.getCandidateTargetGenes().size();
        int count = 0;
        for (final CyNode sourceNode : sourceNodes) {
            if (!createNodesIfNecessary) {
                NetworkUtilities.getInstance().adjustSourceNode(getNetwork(), sourceNode, attributeName, transcriptionFactor, motifOrTrack);
            }

            for (CandidateTargetGene targetGene : motifOrTrack.getCandidateTargetGenes()) {
                if (cancelled) return;
                taskMonitor.setProgress(((double) count) / totalCount);
                final GeneIdentifier geneID = targetGene.getGeneID();

                final List<CyNode> targetNodes = createNodesIfNecessary
                        ? Collections.singletonList(NetworkUtilities.getInstance().createTargetNode(getNetwork(), attributeName, targetGene, motifOrTrack))
                        : findCyNodes(geneID, name2nodes);

                for (final CyNode targetNode : targetNodes) {
                    if (!createNodesIfNecessary) {
                        NetworkUtilities.getInstance().adjustTargetNode(getNetwork(), targetNode, attributeName, targetGene, motifOrTrack);
                    }
                    NetworkUtilities.getInstance().createPredictedEdge(getNetwork(), sourceNode, targetNode, transcriptionFactor, motifOrTrack, targetGene);
                }
                count++;
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
