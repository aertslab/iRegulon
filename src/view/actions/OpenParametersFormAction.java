package view.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;
import view.parametersform.InputView;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class OpenParametersFormAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_form";

    public OpenParametersFormAction() {
        super(NAME);
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
        final JFrame frame = new JFrame(getBundle().getString("plugin_name"));
		frame.setAlwaysOnTop(true);

		final InputView input = new InputView(frame);
		frame.add(input.createClassicalInputView());
		
		frame.pack();
        frame.setLocationRelativeTo(Cytoscape.getDesktop());
        frame.setAlwaysOnTop(true);

		frame.setVisible(true);
	}
}
