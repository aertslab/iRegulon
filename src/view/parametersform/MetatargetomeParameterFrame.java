package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.MetaTargetomeParameters;
import domainmodel.SpeciesNomenclature;
import infrastructure.CytoscapeEnvironment;
import org.cytoscape.application.swing.AbstractCyAction;
import view.Refreshable;
import view.actions.QueryMetatargetomeAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;


public final class MetatargetomeParameterFrame extends JDialog {
    private static final String TITLE = "Query TF-target database for a factor";

    public MetatargetomeParameterFrame(final MetaTargetomeParameters parameters,
                                       final Map<SpeciesNomenclature, java.util.Set<GeneIdentifier>> speciesNomenclature2factors,
                                       final Refreshable view) {
        super(CytoscapeEnvironment.getInstance().getJFrame(), TITLE, true);
        setContentPane(new ContentPane(parameters, speciesNomenclature2factors, view));
        pack();
        setLocationRelativeTo(CytoscapeEnvironment.getInstance().getJFrame());
        setAlwaysOnTop(true);
    }

    private class ContentPane extends JPanel {
        private ContentPane(final MetaTargetomeParameters parameters,
                            final Map<SpeciesNomenclature, java.util.Set<GeneIdentifier>> speciesNomenclature2factors,
                            final Refreshable view) {
            super(new BorderLayout());

            final MetaTargetomeParameterForm parameterForm = new MetaTargetomeParameterForm(parameters, speciesNomenclature2factors);
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

    private class CancelAction extends AbstractCyAction {
        private CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MetatargetomeParameterFrame.this.dispose();
        }
    }
}
