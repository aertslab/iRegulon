package view.actions;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import view.CytoscapeNetworkUtilities;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class OpenQueryMetatargetomeFormAction extends ResourceAction {
    private static final String NAME = "action_open_query_metatargetome_frame";

    public OpenQueryMetatargetomeFormAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Map<SpeciesNomenclature,Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature,Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, QueryMetatargetomeAction.getAvailableFactors(speciesNomenclature));
        }

        final JDialog frame = new MetatargetomeParameterFrame(getSelectedFactor(), speciesNomenclature2factors);
        frame.setVisible(true);
    }

    private GeneIdentifier getSelectedFactor() {
        final java.util.List<CyNode> nodes = CytoscapeNetworkUtilities.getSelectedNodes();
        if (nodes == null || nodes.isEmpty()) return null;
        final CyNode node = nodes.iterator().next();
        return new GeneIdentifier(node.getIdentifier(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
    }
}
