package infrastructure;


import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;

import javax.swing.*;

public final class CytoscapeEnvironment implements CySwingApplication {
    private static CytoscapeEnvironment INSTANCE;

    private final CySwingApplication application;
    private final CyServiceRegistrar serviceRegistrar;

    public static CytoscapeEnvironment getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public static void install(final CySwingApplication application, final CyServiceRegistrar serviceRegistrar) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new CytoscapeEnvironment(application, serviceRegistrar);
    }

    private CytoscapeEnvironment(CySwingApplication application, CyServiceRegistrar serviceRegistrar) {
        if (application == null) throw new IllegalArgumentException();
        if (serviceRegistrar == null) throw new IllegalArgumentException();
        this.application = application;
        this.serviceRegistrar = serviceRegistrar;
    }

    public CyServiceRegistrar getServiceRegistrar() {
        return serviceRegistrar;
    }

    @Override
    public JMenu getJMenu(String s) {
        return application.getJMenu(s);
    }

    @Override
    public JMenuBar getJMenuBar() {
        return application.getJMenuBar();
    }

    @Override
    public JToolBar getJToolBar() {
        return application.getJToolBar();
    }

    @Override
    public void addAction(CyAction cyAction) {
        application.addAction(cyAction);
    }

    @Override
    public void removeAction(CyAction cyAction) {
        application.removeAction(cyAction);
    }

    @Override
    public CytoPanel getCytoPanel(CytoPanelName cytoPanelName) {
        return application.getCytoPanel(cytoPanelName);
    }

    @Override
    public JFrame getJFrame() {
        return application.getJFrame();
    }

    @Override
    public JToolBar getStatusToolBar() {
        return application.getStatusToolBar();
    }
}
