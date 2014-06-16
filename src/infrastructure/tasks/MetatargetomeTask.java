package infrastructure.tasks;

import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import infrastructure.NetworkUtilities;
import infrastructure.IRegulonResourceBundle;
import view.Refreshable;

import java.util.Collections;
import java.util.List;


public abstract class MetatargetomeTask extends IRegulonResourceBundle implements Task {
    protected static final AbstractMotif NO_MOTIF = new AbstractMotif("NaN",
            Collections.<CandidateTargetGene>emptyList(),
            Collections.<TranscriptionFactor>emptyList()) {
        @Override
        public int getDatabaseID() {
            return Integer.MIN_VALUE;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public float getAUCValue() {
            return Float.NaN;
        }

        @Override
        public float getNEScore() {
            return Float.NaN;
        }

        @Override
        public Motif getBestMotif() {
            return null;
        }

        @Override
        public List<Motif> getMotifs() {
            return Collections.emptyList();
        }
    };

    private final CyNetwork network;
    private final CyNetworkView view;
    private final Refreshable resultsPanel;
    private final String attributeName;
    private final GeneIdentifier transcriptionFactor;
    private final List<CandidateTargetGene> targetome;

    private TaskMonitor monitor;
    private boolean interrupted = false;


    public MetatargetomeTask(CyNetwork network, CyNetworkView view,
                      final Refreshable resultsPanel, final String attributeName,
                      GeneIdentifier factor, List<CandidateTargetGene> targetome) {
        this.transcriptionFactor = factor;
        this.targetome = targetome;
        this.resultsPanel = resultsPanel;
        this.attributeName = attributeName == null ? NetworkUtilities.ID_ATTRIBUTE_NAME : attributeName;
        this.network = network;
        this.view = view;
    }

    public GeneIdentifier getTranscriptionFactor() {
        return transcriptionFactor;
    }

    public List<CandidateTargetGene> getTargetome() {
        return targetome;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public TaskMonitor getMonitor() {
        return monitor;
    }

    public CyNetwork getNetwork() {
        return network;
    }

    public CyNetworkView getView() {
        return view;
    }

    public Refreshable getResultsPanel() {
        return resultsPanel;
    }

    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getTitle() {
        return IRegulonResourceBundle.PLUGIN_NAME + ": Add metatargetome for " + getTranscriptionFactor().getGeneName();
    }

    @Override
    public void halt() {
        this.interrupted = true;
    }

    @Override
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.monitor = taskMonitor;
    }
}
