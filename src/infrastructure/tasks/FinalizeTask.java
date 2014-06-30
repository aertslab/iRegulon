package infrastructure.tasks;

import infrastructure.CytoscapeEnvironment;
import infrastructure.IRegulonResourceBundle;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import view.Refreshable;

import java.awt.*;


public final class FinalizeTask extends AbstractTask {
    private final NetworkResult network;
    private final Refreshable resultsPanel;
    private final boolean applyStyle;

    public FinalizeTask(NetworkResult network, Refreshable resultsPanel, boolean applyStyle) {
        this.network = network;
        this.resultsPanel = resultsPanel;
        this.applyStyle = applyStyle;
    }

    public Refreshable getResultsPanel() {
        return resultsPanel;
    }

    public boolean isApplyStyle() {
        return applyStyle;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        network.getView().updateView();
        //TODO: Active this when Visual Style really works ...
        //if (isApplyStyle()) IRegulonVisualStyle.applyVisualStyle(network.getView());
        if (getResultsPanel() != null) getResultsPanel().refresh();
        activeSidePanel();
    }

    private void activeSidePanel() {
        final CytoPanel cytoPanel = CytoscapeEnvironment.getInstance().getCytoPanel(CytoPanelName.WEST);
        final int idx = findComponent(cytoPanel, IRegulonResourceBundle.PLUGIN_NAME);
        cytoPanel.setSelectedIndex((idx < 0) ? 0 : idx);
    }

    private int findComponent(final CytoPanel panel, final String name) {
        for (int idx = 0; idx < panel.getCytoPanelComponentCount(); idx++) {
            final Component component = panel.getComponentAt(idx);
            if (name.equals(component.getName())) return idx;
        }
        return -1;
    }
}
