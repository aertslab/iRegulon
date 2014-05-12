package view.parametersform;


import cytoscape.Cytoscape;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import view.actions.QueryMetatargetomeAction;
import view.resultspanel.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;


public final class MetatargetomeParameterFrame extends JDialog {
    private static final String TITLE = "Query TF-target database for a factor";

    public MetatargetomeParameterFrame(final MetatargetomeParameters parameters,
                                       final Map<SpeciesNomenclature, java.util.Set<GeneIdentifier>> speciesNomenclature2factors,
                                       final Refreshable view) {
        super(Cytoscape.getDesktop(), TITLE, true);
        setContentPane(new ContentPane(parameters, speciesNomenclature2factors, view));
        pack();
        setLocationRelativeTo(Cytoscape.getDesktop());
        setAlwaysOnTop(true);
    }

    private class ContentPane extends JPanel {
        private ContentPane(final MetatargetomeParameters parameters,
                            final Map<SpeciesNomenclature, java.util.Set<GeneIdentifier>> speciesNomenclature2factors,
                            final Refreshable view) {
            super(new BorderLayout());

            final MetatargetomeParameterForm parameterForm = new MetatargetomeParameterForm(parameters, speciesNomenclature2factors);
            final QueryMetatargetomeAction submitAction = new QueryMetatargetomeAction(parameterForm, view) {
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
