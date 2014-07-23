package view.actions;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.GeneIdentifier;
import infrastructure.CytoscapeEnvironment;
import infrastructure.NetworkUtilities;
import infrastructure.tasks.*;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;
import view.Refreshable;
import view.ResourceAction;
import view.resultspanel.MotifAndTrackListener;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.guiwidgets.TranscriptionFactorListener;

import java.util.Properties;


abstract class TranscriptionFactorDependentAction extends ResourceAction implements Refreshable {
    private static final Refreshable NO_VIEW = new Refreshable() {
        @Override
        public void refresh() {
            //Nop ...
        }
    };

    private SelectedMotifOrTrack selectedMotifOrTrack;
    private TranscriptionFactorComboBox selectedTranscriptionFactor;
    private final String attributeName;
    private final Refreshable resultsPanel;
    private final boolean createNewNetwork;

    public TranscriptionFactorDependentAction(final String actionName,
                                              final String attributeName,
                                              final SelectedMotifOrTrack selectedMotifOrTrack,
                                              final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                              final Refreshable resultsPanel,
                                              final boolean createNewNetwork) {
        super(actionName);
        this.attributeName = attributeName == null ? NetworkUtilities.ID_ATTRIBUTE_NAME : attributeName;
        this.selectedMotifOrTrack = selectedMotifOrTrack;
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;
        this.resultsPanel = resultsPanel == null ? TranscriptionFactorDependentAction.NO_VIEW : resultsPanel;
        this.createNewNetwork = createNewNetwork;

        selectedTranscriptionFactor.addListener(new TranscriptionFactorListener() {
            @Override
            public void factorChanged() {
                refresh();
            }
        });

        selectedMotifOrTrack.registerListener(new MotifAndTrackListener() {
            @Override
            public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
                refresh();
            }
        });

        if (!createNewNetwork) {
            CytoscapeEnvironment.getInstance().getServiceRegistrar().registerService(new NetworkViewAddedListener() {
                @Override
                public void handleEvent(NetworkViewAddedEvent networkViewAddedEvent) {
                    refresh();
                }
            }, NetworkViewAddedListener.class, new Properties());
            CytoscapeEnvironment.getInstance().getServiceRegistrar().registerService(new NetworkViewDestroyedListener() {
                @Override
                public void handleEvent(NetworkViewDestroyedEvent networkViewDestroyedEvent) {
                    refresh();
                }
            }, NetworkViewDestroyedListener.class, new Properties());
        }

        refresh();
    }

    @Override
    public void refresh() {
        setEnabled(checkEnabled());
    }

    protected boolean checkEnabled() {
        if (createNewNetwork) return (getMotifOrTrack() != null && getTranscriptionFactor() != null);
        else return (getMotifOrTrack() != null && getTranscriptionFactor() != null
                && NetworkUtilities.getInstance().getCurrentNetworkView() != null);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public AbstractMotifAndTrack getMotifOrTrack() {
        return this.selectedMotifOrTrack.getMotifOrTrack();
    }

    public GeneIdentifier getTranscriptionFactor() {
        return selectedTranscriptionFactor.getTranscriptionFactor();
    }

    public Refreshable getResultsPanel() {
        return resultsPanel;
    }

    protected void draw(final boolean onlyInteractions) {
        final TaskIterator tasks = new TaskIterator();

        tasks.append(new AbstractTask() {
            @Override
            public void run(TaskMonitor taskMonitor) throws Exception {
                taskMonitor.setTitle(getTaskDescription());
            }
        });

        final NetworkResult network;
        final boolean oldNetworkHasEdges;

        if (createNewNetwork) {
            final CreateNewNetworkTask createNetworkTask = new CreateNewNetworkTask(createTitle());
            tasks.append(createNetworkTask);
            network = createNetworkTask;
            oldNetworkHasEdges = false;
        } else {
            network = NetworkResult.CURRENT;
            oldNetworkHasEdges = network.getNetwork().getEdgeCount() > 0 ? true : false;
        }

        final String attributeName = getAttributeName();
        final AbstractMotifAndTrack motifOrTrack = getMotifOrTrack();
        final GeneIdentifier factor = getTranscriptionFactor();
        final AddPredictedRegulatoryNetworkTask addPredictedRegulatoryNetworkTask = new AddPredictedRegulatoryNetworkTask(
                network,
                attributeName,
                factor,
                motifOrTrack,
                !onlyInteractions);
        tasks.append(addPredictedRegulatoryNetworkTask);

        if (! oldNetworkHasEdges) {
            /* Apply layout when the old network didn't have any edges yet (or when we have created a new network). */
            tasks.append(new ApplyDefaultLayoutTask(network));
        }

        tasks.append(new FinalizeTask(network, getResultsPanel()));

        final TaskManager taskManager = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(DialogTaskManager.class);
        taskManager.execute(tasks);
    }

    protected abstract String createTitle();

    protected abstract String getTaskDescription();
}
