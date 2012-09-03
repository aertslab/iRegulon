package view.actions;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import view.ResourceAction;
import view.parametersform.PredictedRegulatorsForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
		tabbedPane.addTab("Predict regulators", null, predictedRegulatorsForm.createClassicalInputView(), null);
        tabbedPane.addTab("Query metatargetome", null, new JPanel(), null);

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
                predictedRegulatorsForm.getListenerForClassicInput().refresh();
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
	}

}
