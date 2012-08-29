package view.parametersform;


import domainmodel.GeneIdentifier;
import domainmodel.TargetomeDatabase;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import view.actions.QueryMetatargetomeAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public final class MetatargetomeParameterFrame extends JFrame {
    private static final String TITLE = "Query metatargetome for a factor";

    public MetatargetomeParameterFrame(final GeneIdentifier factor) {
        super(TITLE);
        setContentPane(new ContentPane(factor));
        pack();
    }

    private class ContentPane extends JPanel {
        private ContentPane(final GeneIdentifier factor) {
            super(new BorderLayout());
            final ComputationalService service = new ComputationalServiceHTTP();
            final MetatargetomeParameterForm parameterForm = new MetatargetomeParameterForm(service.queryTranscriptionFactorsWithPredictedTargetome());
            add(parameterForm, BorderLayout.CENTER);
            add(new JPanel(new FlowLayout()) {
                {
                    add(new JButton(new CancelAction()));
                    add(new JButton(new QueryMetatargetomeAction(parameterForm)));
                }
            }, BorderLayout.SOUTH);
            parameterForm.setTranscriptionFactor(factor);
            parameterForm.setDatabases(TargetomeDatabase.getAllDatabases());
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
