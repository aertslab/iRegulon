package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;

import java.util.ArrayList;
import java.util.List;


public class DefaultMetatargetomeParameters implements MetatargetomeParameters {
    private final String attributeName;
    private final GeneIdentifier transcriptionFactor;
    private final List<TargetomeDatabase> databases;
    private final int occurenceCountThreshold;
    private final int maxNumberOfNodes;
    private final boolean createNewNetwork;

    public DefaultMetatargetomeParameters(final MetatargetomeParameters parameters) {
        this.attributeName = parameters.getAttributeName();
        this.transcriptionFactor = parameters.getTranscriptionFactor();
        this.databases = new ArrayList<TargetomeDatabase>(parameters.getDatabases());
        this.occurenceCountThreshold = parameters.getOccurenceCountThreshold();
        this.maxNumberOfNodes = parameters.getMaxNumberOfNodes();
        this.createNewNetwork = parameters.createNewNetwork();
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
    public int getOccurenceCountThreshold() {
        return occurenceCountThreshold;
    }

    @Override
    public int getMaxNumberOfNodes() {
        return maxNumberOfNodes;
    }

    @Override
    public boolean createNewNetwork() {
        return createNewNetwork;
    }
}
