package view.actions;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameterForm;
import view.parametersform.ParameterChangeListener;
import view.parametersform.PredictedRegulatorsForm;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddParametersFormToSidePanelAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_side_panel";

    private static final String PLUGIN_NAME = getBundle().getString("plugin_visual_name");

    public AddParametersFormToSidePanelAction() {
        super(NAME);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		addSidePanel();
	}

    private void addSidePanel() {
		final CytoscapeDesktop desktop = Cytoscape.getDesktop();
		final CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);

        final JPanel sidePanel = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();

        final JLabel titleLabel = new JLabel(PLUGIN_NAME);
        titleLabel.setFont(new Font("Serif", 0, 45));

        cc.gridx = 0; cc.gridy = 0;
		cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 1.0; cc.weighty = 0.0;
		cc.fill = GridBagConstraints.HORIZONTAL;
        sidePanel.add(titleLabel, cc);


        final JTabbedPane tabbedPane = new JTabbedPane();
        final PredictedRegulatorsForm predictedRegulatorsForm = new PredictedRegulatorsForm();
        final Map<SpeciesNomenclature,Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature,Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, QueryMetatargetomeAction.getAvailableFactors(speciesNomenclature));
        }
        final MetatargetomeForm metatargetomeForm = new MetatargetomeForm(getSelectedFactor(), speciesNomenclature2factors);
		tabbedPane.addTab("Predict regulators", null, predictedRegulatorsForm.createForm(), null);
        tabbedPane.addTab("Query metatargetome", null, metatargetomeForm, null);

        cc.gridx = 0; cc.gridy = 1;
		cc.gridwidth = 1; cc.gridheight = 1;
        cc.weightx = 1.0; cc.weighty = 1.0;
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
                predictedRegulatorsForm.refresh();
                metatargetomeForm.getForm().setTranscriptionFactor(getSelectedFactor());
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
                predictedRegulatorsForm.refresh();
                metatargetomeForm.getForm().setTranscriptionFactor(getSelectedFactor());
            }
        });
	}

    private GeneIdentifier getSelectedFactor() {
        @SuppressWarnings("unchecked")
        final Set<CyNode> nodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
        if (nodes == null || nodes.isEmpty()) return null;
        final CyNode node = nodes.iterator().next();
        return new GeneIdentifier(node.getIdentifier(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
    }


    private static class MetatargetomeForm extends JPanel {
        private final MetatargetomeParameterForm parameterForm;

        private MetatargetomeForm(final GeneIdentifier factor, final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors) {
            super(new BorderLayout());

            parameterForm = new MetatargetomeParameterForm(speciesNomenclature2factors);
            final QueryMetatargetomeAction submitAction = new QueryMetatargetomeAction(parameterForm, null);
            add(parameterForm, BorderLayout.CENTER);
            add(new JPanel(new FlowLayout()) {
                {
                    final JButton submitButton = new JButton(submitAction);
                    submitButton.setIcon(null);
                    add(submitButton);
                }
            }, BorderLayout.SOUTH);

            parameterForm.addParameterChangeListener(new ParameterChangeListener() {
                public void parametersChanged() {
                    submitAction.refresh();
                }
            });

            parameterForm.setSpeciesNomenclature(SpeciesNomenclature.HOMO_SAPIENS_HGNC);
            if (factor != null) parameterForm.setTranscriptionFactor(factor);
            parameterForm.setDatabases(TargetomeDatabase.getAllDatabases());
        }

        public MetatargetomeParameterForm getForm() {
            return parameterForm;
        }
    }
}
