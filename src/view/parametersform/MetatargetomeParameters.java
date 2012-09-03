package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;

import java.util.List;


public interface MetatargetomeParameters {
    String getAttributeName();

    GeneIdentifier getTranscriptionFactor();

    List<TargetomeDatabase> getDatabases();

    int getOccurenceCountThreshold();

    int getMaxNumberOfNodes();

    boolean createNewNetwork();
}
