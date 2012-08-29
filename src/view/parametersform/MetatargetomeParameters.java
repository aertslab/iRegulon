package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;

import java.util.List;


public interface MetatargetomeParameters {
    GeneIdentifier getTranscriptionFactor();

    List<TargetomeDatabase> getDatabases();
}
