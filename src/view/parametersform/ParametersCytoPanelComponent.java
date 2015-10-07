package view.parametersform;

import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import infrastructure.IRegulonResourceBundle;
import infrastructure.NetworkUtilities;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import servercommunication.MetaTargetomes;
import view.Refreshable;
import view.actions.QueryMetatargetomeAction;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class ParametersCytoPanelComponent extends JPanel implements CytoPanelComponent, Refreshable {
    public static final String NAME = IRegulonResourceBundle.PLUGIN_NAME;

    private final PredictedRegulatorsForm predictedRegulatorsForm;
    private final MetaTargetomeForm metaTargetomeForm;

    public ParametersCytoPanelComponent() {
        super(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();

        final JLabel titleLabel = new JLabel(NAME);
        titleLabel.setFont(new Font("Serif", 0, 45));

        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        add(titleLabel, cc);

        final JTabbedPane tabbedPane = new JTabbedPane();
        predictedRegulatorsForm = new PredictedRegulatorsForm();
        final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature, Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, MetaTargetomes.getAvailableFactors(speciesNomenclature));
        }
        metaTargetomeForm = new MetaTargetomeForm(getSelectedFactor(), speciesNomenclature2factors);
        tabbedPane.addTab("Predict regulators and targets", null, new JScrollPane(predictedRegulatorsForm.createForm()), null);
        tabbedPane.addTab("Query TF-target database", null, metaTargetomeForm, null);

        cc.gridx = 0;
        cc.gridy = 1;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 1.0;
        cc.fill = GridBagConstraints.BOTH;
        add(tabbedPane, cc);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh();
            }
        });
    }

    @Override
    public void refresh() {
        predictedRegulatorsForm.refresh();
        metaTargetomeForm.getForm().refresh();
        if (getSelectedFactor() != null) {
            metaTargetomeForm.getForm().setSpeciesNomenclature(getSelectedFactor().getSpeciesNomenclature());
            metaTargetomeForm.getForm().setTranscriptionFactor(getSelectedFactor());
        }
    }

    private GeneIdentifier getSelectedFactor() {
        if (metaTargetomeForm == null || metaTargetomeForm.getForm() == null) return null;
        final SpeciesNomenclature species = metaTargetomeForm.getForm().getSpeciesNomenclature();
        final CyNetwork network = NetworkUtilities.getInstance().getCurrentNetwork();
        if (network == null) return null;
        final java.util.List<GeneIdentifier> ids = NetworkUtilities.getInstance().getSelectedNodesAsGeneIDs(network,
                metaTargetomeForm.getForm().getAttributeName(),
                species == null ? SpeciesNomenclature.HOMO_SAPIENS_HG19_HGNC : species);
        if (ids == null || ids.isEmpty()) return null;
        return ids.get(0);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    @Override
    public String getTitle() {
        return NAME;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    private static class MetaTargetomeForm extends JPanel {
        private final MetaTargetomeParameterForm parameterForm;

        private MetaTargetomeForm(final GeneIdentifier factor, final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors) {
            super(new BorderLayout());

            parameterForm = new MetaTargetomeParameterForm(QueryMetatargetomeAction.DEFAULT_PARAMETERS, speciesNomenclature2factors);
            final QueryMetatargetomeAction submitAction = new QueryMetatargetomeAction(parameterForm, null);
            add(parameterForm, BorderLayout.CENTER);
            add(new JPanel(new FlowLayout()) {
                {
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
                parameterForm.setSpeciesNomenclature(SpeciesNomenclature.HOMO_SAPIENS_HG19_HGNC);
            }
            parameterForm.setTargetomeDatabases(TargetomeDatabase.getAllTargetomeDatabases());
        }

        public MetaTargetomeParameterForm getForm() {
            return parameterForm;
        }
    }
}
