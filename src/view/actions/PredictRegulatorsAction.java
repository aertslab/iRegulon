package view.actions;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.PredictRegulatorsParameters;
import domainmodel.Results;
import infrastructure.CytoscapeEnvironment;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceFactory;
import servercommunication.tasks.EnrichedMotifsAndTracksResults;
import view.ResourceAction;
import view.parametersform.PredictedRegulatorsParameters;
import view.resultspanel.ResultsCytoPanelComponent;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;


public class PredictRegulatorsAction extends ResourceAction {
    private static final String NAME = "action_submit_analysis";

    private final ComputationalService service = ComputationalServiceFactory.getInstance().getService();
    private final PredictedRegulatorsParameters parameters;

    public PredictRegulatorsAction(final PredictedRegulatorsParameters parameters) {
        super(NAME);
        this.parameters = parameters;
    }

    public PredictedRegulatorsParameters getParameters() {
        return parameters;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final PredictRegulatorsParameters predictRegulatorsParameters = this.parameters.deriveParameters();

        final TaskIterator tasks = new TaskIterator();

        final EnrichedMotifsAndTracksResults predictRegulatorsTask = service.createPredictRegulatorsTask(predictRegulatorsParameters);
        tasks.append(predictRegulatorsTask);
        tasks.append(new AbstractTask() {
            @Override
            public void run(TaskMonitor taskMonitor) throws IllegalStateException {
                final List<AbstractMotifAndTrack> motifsAndTracks = predictRegulatorsTask.getMotifsAndTracks();
                if (motifsAndTracks.isEmpty()) {
                    throw new IllegalStateException("Not a single motif or track is enriched for your input gene signature.");
                }
                final ResultsCytoPanelComponent outputView = new ResultsCytoPanelComponent(predictRegulatorsParameters.getName(), new Results(motifsAndTracks, predictRegulatorsParameters));
                CytoscapeEnvironment.getInstance().getServiceRegistrar().registerService(outputView, CytoPanelComponent.class, new Properties());
                final CytoPanel cytoPanel = CytoscapeEnvironment.getInstance().getCytoPanel(CytoPanelName.EAST);
                cytoPanel.setState(CytoPanelState.DOCK);
                cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(outputView));
            }
        });

        final TaskManager taskManager = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(DialogTaskManager.class);
        taskManager.execute(tasks);
    }
}

/*

                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;

                JLabel errorMessageLabel = new JLabel(errorMessageLostGenes);
                panel.add(errorMessageLabel, c);

                // Create a textarea for displaying the lost genes.
                JTextArea textArea = new JTextArea(10, 20);
                textArea.setText(lostGenes);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setCaretPosition(0);
                textArea.setEditable(false);


                c.gridwidth = 3;
                c.gridx = 0;
                c.gridy = 1;

                // Put textarea in a scrollpane.
                JScrollPane scrollPane = new JScrollPane(textArea);

                panel.add(scrollPane, c);

                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(), panel, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(), deriveMessage(e), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (! motifsAndTracks.isEmpty()) {
            final ResultsCytoPanelComponent outputView = new ResultsCytoPanelComponent(predictRegulatorsParameters.getName(), new Results(motifsAndTracks, predictRegulatorsParameters));
            final CytoPanel panel = CytoscapeEnvironment.getInstance().getJFrame().getCytoPanel(SwingConstants.EAST);
            panel.setState(CytoPanelState.DOCK);
            outputView.addToPanel(panel);
        } else {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame)(),
                    "Not a single motif or track is enriched for your input gene signature.");
        }
    }


}
*/