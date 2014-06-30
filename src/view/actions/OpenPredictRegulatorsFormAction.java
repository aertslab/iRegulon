package view.actions;

import infrastructure.CytoscapeEnvironment;
import view.ResourceAction;
import view.parametersform.PredictedRegulatorsForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;


public class OpenPredictRegulatorsFormAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_form";

    public OpenPredictRegulatorsFormAction() {
        super(NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JDialog frame = new JDialog(CytoscapeEnvironment.getInstance().getJFrame(), "Predict regulators and targets", true);
        frame.setAlwaysOnTop(true);

        final PredictedRegulatorsForm predictedRegulatorsForm = new PredictedRegulatorsForm(frame);
        frame.add(predictedRegulatorsForm.createForm());
        predictedRegulatorsForm.refresh();

        frame.pack();
        frame.setLocationRelativeTo(CytoscapeEnvironment.getInstance().getJFrame());
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
