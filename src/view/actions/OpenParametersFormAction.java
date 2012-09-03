package view.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;
import view.parametersform.PredictedRegulatorsForm;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;


public class OpenParametersFormAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_form";

    public OpenParametersFormAction() {
        super(NAME);
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
        final JFrame frame = new JFrame("Predict regulators");
		frame.setAlwaysOnTop(true);

		final PredictedRegulatorsForm input = new PredictedRegulatorsForm(frame);
		frame.add(input.createClassicalInputView());
		
		frame.pack();
        frame.setLocationRelativeTo(Cytoscape.getDesktop());
        frame.setAlwaysOnTop(true);

		frame.setVisible(true);
	}
}
