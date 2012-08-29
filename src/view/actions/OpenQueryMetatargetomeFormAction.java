package view.actions;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
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
        final Map<SpeciesNomenclature,List<GeneIdentifier>> speciesNomenclature2factors;
        try {
            speciesNomenclature2factors = queryForFactors();
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final JFrame frame = new MetatargetomeParameterFrame(getSelectedFactor(), speciesNomenclature2factors);
        frame.setVisible(true);
    }

    private Map<SpeciesNomenclature,List<GeneIdentifier>> queryForFactors() throws ServerCommunicationException {
        final ComputationalService service = new ComputationalServiceHTTP();
        final Map<SpeciesNomenclature,List<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature,List<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, service.queryTranscriptionFactorsWithPredictedTargetome(speciesNomenclature));
        }
        return speciesNomenclature2factors;
    }

    private GeneIdentifier getSelectedFactor() {
        @SuppressWarnings("unchecked")
        final Set<CyNode> nodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
        if (nodes == null || nodes.isEmpty()) return null;
        final CyNode node = nodes.iterator().next();
        return new GeneIdentifier(node.getIdentifier(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
    }
}
