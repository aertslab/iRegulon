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


public final class MetatargetomeParameterFrame extends JDialog {
    private static final String TITLE = "Query metatargetome for a factor";

    public MetatargetomeParameterFrame(final GeneIdentifier factor, final Map<SpeciesNomenclature,java.util.Set<GeneIdentifier>> speciesNomenclature2factors) {
        super(Cytoscape.getDesktop(), TITLE, true);
        setContentPane(new ContentPane(factor, speciesNomenclature2factors));
        pack();
        setLocationRelativeTo(Cytoscape.getDesktop());
        setAlwaysOnTop(true);
    }

    private class ContentPane extends JPanel {
        private ContentPane(final GeneIdentifier factor, final Map<SpeciesNomenclature,java.util.Set<GeneIdentifier>> speciesNomenclature2factors) {
            super(new BorderLayout());

            final MetatargetomeParameterForm parameterForm = new MetatargetomeParameterForm(speciesNomenclature2factors);
            final QueryMetatargetomeAction submitAction = new QueryMetatargetomeAction(parameterForm, null) {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    super.actionPerformed(actionEvent);
                    dispose();
                }
            };
            add(parameterForm, BorderLayout.CENTER);
            add(new JPanel(new FlowLayout()) {
                {
                    add(new JButton(new CancelAction()));
                    final JButton submitButton = new JButton(submitAction);
                    submitButton.setText("Submit");
                    submitButton.setIcon(null);
                    add(submitButton);
                }
            }, BorderLayout.SOUTH);

            parameterForm.addParameterChangeListener(new ParameterChangeListener() {
                public void parametersChanged() {
                    submitAction.refresh();
                }
            });

            if (factor != null) {
                parameterForm.setSpeciesNomenclature(factor.getSpeciesNomenclature());
                parameterForm.setTranscriptionFactor(factor);
            } else {
                parameterForm.setSpeciesNomenclature(SpeciesNomenclature.HOMO_SAPIENS_HGNC);
            }
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
