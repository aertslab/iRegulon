package view.actions;

import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterFrame;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.Refreshable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class OpenQueryMetatargetomeFormAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_open_query_metatargetome_frame";

    private MetatargetomeParameters parameters;
    private final Refreshable view;

    public OpenQueryMetatargetomeFormAction(final MetatargetomeParameters parameters, final Refreshable view) {
        super(NAME);
        this.parameters = parameters;
        this.view = view;
    }

    public MetatargetomeParameters getParameters() {
        return parameters;
    }

    public void setParameters(MetatargetomeParameters parameters) {
        this.parameters = parameters;
        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Map<SpeciesNomenclature,Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature,Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, QueryMetatargetomeAction.getAvailableFactors(speciesNomenclature));
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
        if (getParameters().getDatabases().isEmpty()) return false;
        final GeneIdentifier factor = getParameters().getTranscriptionFactor();
        if (factor == null) return false;
        if (getParameters().getOccurenceCountThreshold() < 0) return false;
        if (getParameters().getMaxNumberOfNodes() < 0) return false;
        return QueryMetatargetomeAction.getAvailableFactors(factor.getSpeciesNomenclature()).contains(factor);
    }
}
