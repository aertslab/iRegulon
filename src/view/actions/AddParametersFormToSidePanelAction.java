package view.actions;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;
import infrastructure.CytoscapeNetworkUtilities;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterForm;
import view.parametersform.ParameterChangeListener;
import view.parametersform.PredictedRegulatorsForm;
import view.resultspanel.Refreshable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddParametersFormToSidePanelAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_open_parameters_side_panel";

    private static final String PLUGIN_NAME = getBundle().getString("plugin_visual_name");

    private PredictedRegulatorsForm predictedRegulatorsForm;
    private MetatargetomeForm metatargetomeForm;

    public AddParametersFormToSidePanelAction() {
        super(NAME);
    }

    public boolean alreadyAdded() {
        return predictedRegulatorsForm != null && metatargetomeForm != null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!alreadyAdded()) addSidePanel();
    }

    @Override
    public void refresh() {
        if (alreadyAdded()) {
            predictedRegulatorsForm.refresh();
            metatargetomeForm.getForm().refresh();
            if (getSelectedFactor() != null) {
                metatargetomeForm.getForm().setSpeciesNomenclature(getSelectedFactor().getSpeciesNomenclature());
                metatargetomeForm.getForm().setTranscriptionFactor(getSelectedFactor());
            }
        }
    }

    private void addSidePanel() {
        final CytoscapeDesktop desktop = Cytoscape.getDesktop();
        final CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);

        final JPanel sidePanel = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();

        final JLabel titleLabel = new JLabel(PLUGIN_NAME);
        titleLabel.setFont(new Font("Serif", 0, 45));

        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        sidePanel.add(titleLabel, cc);


        final JTabbedPane tabbedPane = new JTabbedPane();
        predictedRegulatorsForm = new PredictedRegulatorsForm();
        final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature, Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, QueryMetatargetomeAction.getAvailableFactors(speciesNomenclature));
        }
        metatargetomeForm = new MetatargetomeForm(getSelectedFactor(), speciesNomenclature2factors);
        tabbedPane.addTab("Predict regulators and targets", null, new JScrollPane(predictedRegulatorsForm.createForm()), null);
        tabbedPane.addTab("Query TF-target database", null, metatargetomeForm, null);

        cc.gridx = 0;
        cc.gridy = 1;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.weightx = 1.0;
        cc.weighty = 1.0;
        cc.fill = GridBagConstraints.BOTH;
        sidePanel.add(tabbedPane, cc);

        if (cytoPanel.indexOfComponent(PLUGIN_NAME) == -1) {
            cytoPanel.add(PLUGIN_NAME, sidePanel);
        }

        final int idx = cytoPanel.indexOfComponent(PLUGIN_NAME);
        cytoPanel.setSelectedIndex(idx);

        cytoPanel.addCytoPanelListener(new CytoPanelListener() {
            @Override
            public void onStateChange(CytoPanelState newState) {
                refresh();
            }

            @Override
            public void onComponentSelected(int componentIndex) {
                // nothing to do
            }

            @Override
            public void onComponentRemoved(int count) {
                // nothing to do
            }

            @Override
            public void onComponentAdded(int count) {
                // nothing to do
            }
        });
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh();
            }
        });

        final PropertyChangeListener viewCreatedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                installSelectionListener((CyNetworkView) evt.getNewValue());
            }
        };
        final PropertyChangeListener viewDestroyedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                uninstallSelectionListener((CyNetworkView) evt.getNewValue());
            }
        };
        final PropertyChangeListener viewFocusedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        };
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED, viewCreatedListener);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, viewDestroyedListener);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, viewFocusedListener);
        installSelectionListener(Cytoscape.getCurrentNetworkView());

        refresh();
    }

    private final GraphViewChangeListener selectionListener = new GraphViewChangeListener() {
        @Override
        public void graphViewChanged(GraphViewChangeEvent graphViewChangeEvent) {
            if (graphViewChangeEvent.isNodesSelectedType() || graphViewChangeEvent.isNodesUnselectedType()) {
                refresh();
            }
        }
    };

    public void installSelectionListener(final CyNetworkView view) {
        view.addGraphViewChangeListener(selectionListener);
    }

    public void uninstallSelectionListener(final CyNetworkView view) {
        view.removeGraphViewChangeListener(selectionListener);
    }

    private GeneIdentifier getSelectedFactor() {
        if (!alreadyAdded()) return null;
        final java.util.List<CyNode> nodes = CytoscapeNetworkUtilities.getSelectedNodes();
        if (nodes == null || nodes.isEmpty()) return null;
        final CyNode node = nodes.iterator().next();
        final SpeciesNomenclature species = metatargetomeForm.getForm().getSpeciesNomenclature();
        return new GeneIdentifier(node.getIdentifier(), species == null ? SpeciesNomenclature.HOMO_SAPIENS_HGNC : species);
    }

    private static class MetatargetomeForm extends JPanel {
        private final MetatargetomeParameterForm parameterForm;

        private MetatargetomeForm(final GeneIdentifier factor, final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors) {
            super(new BorderLayout());

            parameterForm = new MetatargetomeParameterForm(QueryMetatargetomeAction.DEFAULT_PARAMETERS, speciesNomenclature2factors);
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
                parameterForm.setSpeciesNomenclature(SpeciesNomenclature.HOMO_SAPIENS_HGNC);
            }
            parameterForm.setTargetomeDatabases(TargetomeDatabase.getAllTargetomeDatabases());
        }

        public MetatargetomeParameterForm getForm() {
            return parameterForm;
        }
    }
}
