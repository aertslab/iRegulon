package view.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;
import view.parametersform.PredictedRegulatorsForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;


public class OpenParametersFormAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_form";

    public OpenParametersFormAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JDialog frame = new JDialog(Cytoscape.getDesktop(), "Predict regulators and targets", true);
        frame.setAlwaysOnTop(true);

        final PredictedRegulatorsForm predictedRegulatorsForm = new PredictedRegulatorsForm(frame);
        frame.add(predictedRegulatorsForm.createForm());
        predictedRegulatorsForm.refresh();

        frame.pack();
        frame.setLocationRelativeTo(Cytoscape.getDesktop());
        frame.setAlwaysOnTop(true);

        frame.setVisible(true);

        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                predictedRegulatorsForm.refresh();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                //Nop
            }
        });
    }
}
