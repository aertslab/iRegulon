package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;

import java.util.ArrayList;
import java.util.List;


public class DefaultMetatargetomeParameters implements MetatargetomeParameters {
    private String attributeName;
    private GeneIdentifier transcriptionFactor;
    private List<TargetomeDatabase> databases;
    private int occurrenceCountThreshold;
    private int maxNumberOfNodes;
    private boolean createNewNetwork;

    public DefaultMetatargetomeParameters(final MetatargetomeParameters parameters) {
        this.attributeName = parameters.getAttributeName();
        this.transcriptionFactor = parameters.getTranscriptionFactor();
        this.databases = new ArrayList<TargetomeDatabase>(parameters.getDatabases());
        this.occurrenceCountThreshold = parameters.getOccurrenceCountThreshold();
        this.maxNumberOfNodes = parameters.getMaxNumberOfNodes();
        this.createNewNetwork = parameters.createNewNetwork();
    }

    public DefaultMetatargetomeParameters() {
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
    public List<TargetomeDatabase> getDatabases() {
        return databases;
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

    public void setDatabases(List<TargetomeDatabase> databases) {
        this.databases = databases;
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
