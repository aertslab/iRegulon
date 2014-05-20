package domainmodel;

import infrastructure.IRegulonResourceBundle;

import java.util.List;


public interface MetaTargetomeParameters {
    public static final int DEFAULT_THRESHOLD = Integer.parseInt(IRegulonResourceBundle.getBundle().getString("occurrence_count_threshold"));
    public static final int DEFAULT_MAX_NODE_COUNT = Integer.parseInt(IRegulonResourceBundle.getBundle().getString("max_node_number"));

    String getAttributeName();

    GeneIdentifier getTranscriptionFactor();

    List<TargetomeDatabase> getTargetomeDatabases();

    int getOccurrenceCountThreshold();

    int getMaxNumberOfNodes();

    boolean createNewNetwork();
}
