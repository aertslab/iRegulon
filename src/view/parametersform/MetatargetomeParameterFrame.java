package view.parametersform;


import cytoscape.Cytoscape;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import view.actions.QueryMetatargetomeAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;


public final class MetatargetomeParameterFrame extends JFrame {
    private static final String TITLE = "Query metatargetome for a factor";

    public MetatargetomeParameterFrame(final GeneIdentifier factor, final Map<SpeciesNomenclature,java.util.List<GeneIdentifier>> speciesNomenclature2factors) {
        super(TITLE);
        setContentPane(new ContentPane(factor, speciesNomenclature2factors));
        pack();
        setLocationRelativeTo(Cytoscape.getDesktop());
        setAlwaysOnTop(true);
    }

    private class ContentPane extends JPanel {
        private ContentPane(final GeneIdentifier factor, final Map<SpeciesNomenclature,java.util.List<GeneIdentifier>> speciesNomenclature2factors) {
            super(new BorderLayout());

            final MetatargetomeParameterForm parameterForm = new MetatargetomeParameterForm(speciesNomenclature2factors);
            add(parameterForm, BorderLayout.CENTER);
            add(new JPanel(new FlowLayout()) {
                {
                    add(new JButton(new CancelAction()));
                    add(new JButton(new QueryMetatargetomeAction(parameterForm, null)));
                }
            }, BorderLayout.SOUTH);
            parameterForm.setSpeciesNomenclature(SpeciesNomenclature.HOMO_SAPIENS_HGNC);
            parameterForm.setTranscriptionFactor(factor);
            parameterForm.setDatabases(TargetomeDatabase.getAllDatabases());
        }
    }

    private class CancelAction extends AbstractAction {
        private CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MetatargetomeParameterFrame.this.dispose();
        }
    }
}
