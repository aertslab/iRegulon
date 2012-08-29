package view.actions;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Set;


public class OpenQueryMetatargetomeFormAction extends ResourceAction {
    private static final String NAME = "action_open_query_metatargetome_frame";

    public OpenQueryMetatargetomeFormAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JFrame frame = new MetatargetomeParameterFrame(getSelectedFactor());
        frame.setVisible(true);
    }

    private GeneIdentifier getSelectedFactor() {
        @SuppressWarnings("unchecked")
        final Set<CyNode> nodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
        if (nodes == null || nodes.isEmpty()) return null;
        final CyNode node = nodes.iterator().next();
        return new GeneIdentifier(node.getIdentifier(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
    }
}
