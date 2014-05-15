package domainmodel;

import java.util.List;


public interface MetaTargetomeParameters {
    String getAttributeName();

    GeneIdentifier getTranscriptionFactor();

    List<TargetomeDatabase> getTargetomeDatabases();

    int getOccurrenceCountThreshold();

    int getMaxNumberOfNodes();

    boolean createNewNetwork();
}
