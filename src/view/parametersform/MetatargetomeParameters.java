package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: u0043358
 * Date: 29/08/12
 * Time: 08:16
 * To change this template use File | Settings | File Templates.
 */
public interface MetatargetomeParameters {
    GeneIdentifier getTranscriptionFactor();

    List<TargetomeDatabase> getDatabases();
}
