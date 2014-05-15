package domainmodel;

import java.util.ArrayList;
import java.util.List;


public class DefaultMetaTargetomeParameters implements MetaTargetomeParameters {
    private String attributeName;
    private GeneIdentifier transcriptionFactor;
    private List<TargetomeDatabase> targetomeDatabases;
    private int occurrenceCountThreshold;
    private int maxNumberOfNodes;
    private boolean createNewNetwork;

    public DefaultMetaTargetomeParameters(final MetaTargetomeParameters parameters) {
        this.attributeName = parameters.getAttributeName();
        this.transcriptionFactor = parameters.getTranscriptionFactor();
        this.targetomeDatabases = new ArrayList<TargetomeDatabase>(parameters.getTargetomeDatabases());
        this.occurrenceCountThreshold = parameters.getOccurrenceCountThreshold();
        this.maxNumberOfNodes = parameters.getMaxNumberOfNodes();
        this.createNewNetwork = parameters.createNewNetwork();
    }

    public DefaultMetaTargetomeParameters() {
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public GeneIdentifier getTranscriptionFactor() {
        return transcriptionFactor;
    }

    @Override
    public List<TargetomeDatabase> getTargetomeDatabases() {
        return targetomeDatabases;
    }

    @Override
    public int getOccurrenceCountThreshold() {
        return occurrenceCountThreshold;
    }

    @Override
    public int getMaxNumberOfNodes() {
        return maxNumberOfNodes;
    }

    @Override
    public boolean createNewNetwork() {
        return createNewNetwork;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setTranscriptionFactor(GeneIdentifier transcriptionFactor) {
        this.transcriptionFactor = transcriptionFactor;
    }

    public void setTranscriptomeDatabases(List<TargetomeDatabase> transcriptomeDatabases) {
        this.targetomeDatabases = transcriptomeDatabases;
    }

    public void setOccurrenceCountThreshold(int occurrenceCountThreshold) {
        this.occurrenceCountThreshold = occurrenceCountThreshold;
    }

    public void setMaxNumberOfNodes(int maxNumberOfNodes) {
        this.maxNumberOfNodes = maxNumberOfNodes;
    }

    public void setCreateNewNetwork(boolean createNewNetwork) {
        this.createNewNetwork = createNewNetwork;
    }
}
