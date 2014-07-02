package view.actions;

import infrastructure.CytoscapeEnvironment;
import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.events.CytoPanelStateChangedEvent;
import org.cytoscape.application.swing.events.CytoPanelStateChangedListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import view.Refreshable;
import view.ResourceAction;
import view.parametersform.ParametersCytoPanelComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Properties;


public final class AddParametersFormToSidePanelAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_open_parameters_side_panel";

    private ParametersCytoPanelComponent sidePanel;

    public AddParametersFormToSidePanelAction() {
        super(NAME);
    }

    public boolean alreadyAdded() {
        return sidePanel != null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addSidePanel();
    }

    @Override
    public void refresh() {
        if (alreadyAdded()) sidePanel.refresh();
    }

    private void addSidePanel() {
        final CytoPanel cytoPanel = CytoscapeEnvironment.getInstance().getCytoPanel(CytoPanelName.WEST);
        final int idx = findComponent(cytoPanel, ParametersCytoPanelComponent.NAME);
        if (idx < 0) {
            sidePanel = new ParametersCytoPanelComponent();
            final CyServiceRegistrar serviceRegistrar = CytoscapeEnvironment.getInstance().getServiceRegistrar();
            serviceRegistrar.registerService(sidePanel, CytoPanelComponent.class, new Properties());

            // Update when cytopanel is selected ...
            serviceRegistrar.registerService(new CytoPanelStateChangedListener() {
                @Override
                public void handleEvent(CytoPanelStateChangedEvent event) {
                    if (CytoPanelName.WEST.equals(event.getCytoPanel().getCytoPanelName()))
                        refresh();
                }
            }, CytoPanelStateChangedListener.class, new Properties());

            // Update when nodes are selected ...
            serviceRegistrar.registerService(new RowsSetListener() {
                @Override
                public void handleEvent(RowsSetEvent event) {
                    if (event.getPayloadCollection().isEmpty()) return;
                    if (payloadContains(event.getPayloadCollection(), CyNetwork.SELECTED)) refresh();
                }

                private boolean payloadContains(final Collection<RowSetRecord> records, final String columnName) {
                    for (RowSetRecord record : records) {
                        if (columnName.equals(record.getColumn())) return true;
                    }
                    return false;
                }
            }, RowsSetListener.class, new Properties());

            //Update when views are removed or created ...
            //TODO: No refresh of suggested analysis name and number of selected genes when last network is destroyed?
            serviceRegistrar.registerService(new NetworkViewDestroyedListener() {
                @Override
                public void handleEvent(NetworkViewDestroyedEvent networkViewDestroyedEvent) {
                    refresh();
                }
            }, NetworkViewDestroyedListener.class, new Properties());
            serviceRegistrar.registerService(new NetworkViewAddedListener() {
                @Override
                public void handleEvent(NetworkViewAddedEvent networkViewAddedEvent) {
                    refresh();
                }
            }, NetworkViewAddedListener.class, new Properties());
            serviceRegistrar.registerService(new SetCurrentNetworkViewListener() {
                @Override
                public void handleEvent(SetCurrentNetworkViewEvent setCurrentNetworkViewEvent) {
                    refresh();
                }
            }, SetCurrentNetworkViewListener.class, new Properties());


            cytoPanel.setState(CytoPanelState.DOCK);
            cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(sidePanel));
        } else {
            cytoPanel.setSelectedIndex(idx);
        }

        refresh();
    }

    private int findComponent(final CytoPanel panel, final String name) {
        for (int idx = 0; idx < panel.getCytoPanelComponentCount(); idx++) {
            final Component component = panel.getComponentAt(idx);
            if (name.equals(component.getName())) return idx;
        }
        return -1;
    }
}
