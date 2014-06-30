package view.actions;

import domainmodel.AbstractMotifAndTrack;
import infrastructure.CytoscapeEnvironment;
import infrastructure.Logger;
import org.cytoscape.util.swing.OpenBrowser;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import view.Refreshable;
import view.ResourceAction;
import view.resultspanel.MotifAndTrackListener;
import view.resultspanel.SelectedMotifOrTrack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;


public final class OpenLinkToGenomeBrowserAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_link_to_UCSC";

    private final ComputationalService service = new ComputationalServiceHTTP();
    private final SelectedMotifOrTrack selectedMotifOrTrack;

    public OpenLinkToGenomeBrowserAction(SelectedMotifOrTrack selectedMotifOrTrack) {
        super(NAME);
        this.selectedMotifOrTrack = selectedMotifOrTrack;

        final MotifAndTrackListener enablenessListener = new MotifAndTrackListener() {
            @Override
            public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
                refresh();
            }
        };
        this.selectedMotifOrTrack.registerListener(enablenessListener);

        refresh();
    }

    public SelectedMotifOrTrack getSelectedMotifOrTrack() {
        return selectedMotifOrTrack;
    }

    @Override
    public void refresh() {
        setEnabled(enable());
    }

    private boolean enable() {
        if (getSelectedMotifOrTrack().getMotifOrTrack() == null || !java.awt.Desktop.isDesktopSupported()) return false;
        final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        return desktop.isSupported(java.awt.Desktop.Action.BROWSE);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final URI link = service.getLink2GenomeBrowser4EnhancerRegions(getSelectedMotifOrTrack().getMotifOrTrack());
        try {
            final OpenBrowser openBrowser = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(OpenBrowser.class);
            if (!openBrowser.openURL(link.toString())) {
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                        "An error has occurred while opening the UCSC browser.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException e) {
            final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            try {
                desktop.browse(link);
            } catch (Exception e2) {
                Logger.getInstance().error(e2);
                JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                        "An error has occurred while opening the UCSC browser.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
