package view.actions;

import domainmodel.GeneIdentifier;
import domainmodel.MetaTargetomeParameters;
import domainmodel.SpeciesNomenclature;
import servercommunication.MetaTargetomes;
import view.Refreshable;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class OpenQueryMetatargetomeFormAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_open_query_metatargetome_frame";

    private MetaTargetomeParameters parameters;
    private final Refreshable view;

    public OpenQueryMetatargetomeFormAction(final MetaTargetomeParameters parameters, final Refreshable view) {
        super(NAME);
        this.parameters = parameters;
        this.view = view;
    }

    public MetaTargetomeParameters getParameters() {
        return parameters;
    }

    public void setParameters(MetaTargetomeParameters parameters) {
        this.parameters = parameters;
        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature, Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, MetaTargetomes.getAvailableFactors(speciesNomenclature));
        }

        final JDialog frame = new MetatargetomeParameterFrame(getParameters(), speciesNomenclature2factors, view);
        frame.setVisible(true);
    }

    @Override
    public void refresh() {
        setEnabled(checkEnabled());
    }

    private boolean checkEnabled() {
        if (getParameters() == null) return false;
        if (getParameters().getTargetomeDatabases().isEmpty()) return false;
        final GeneIdentifier factor = getParameters().getTranscriptionFactor();
        if (factor == null) return false;
        if (getParameters().getOccurrenceCountThreshold() < 0) return false;
        if (getParameters().getMaxNumberOfNodes() < 0) return false;
        return MetaTargetomes.getAvailableFactors(factor.getSpeciesNomenclature()).contains(factor);
    }
}
