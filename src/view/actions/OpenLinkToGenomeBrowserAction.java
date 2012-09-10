package view.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import domainmodel.AbstractMotif;
import infrastructure.Logger;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import view.ResourceAction;
import view.resultspanel.MotifListener;
import view.resultspanel.Refreshable;
import view.resultspanel.SelectedMotif;

import javax.swing.*;

public class OpenLinkToGenomeBrowserAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_link_to_UCSC";

    private final ComputationalService service = new ComputationalServiceHTTP();
	private final SelectedMotif selectedMotif;

	public OpenLinkToGenomeBrowserAction(SelectedMotif selectedMotif) {
        super(NAME);
        this.selectedMotif = selectedMotif;

        final MotifListener enablenessListener = new MotifListener() {
            @Override
            public void newMotifSelected(AbstractMotif currentSelection) {
                refresh();
            }
        };
        this.selectedMotif.registerListener(enablenessListener);

        refresh();
	}

    public SelectedMotif getSelectedMotif() {
        return selectedMotif;
    }

    @Override
    public void refresh() {
        setEnabled(enable());
    }

    private boolean enable() {
        if (getSelectedMotif().getMotif() == null || !java.awt.Desktop.isDesktopSupported()) return false;
        final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        return desktop.isSupported(java.awt.Desktop.Action.BROWSE);
    }
	
	@Override
	public void actionPerformed(ActionEvent event) {
        final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            desktop.browse(service.getLink2GenomeBrowser4EnhancerRegions(getSelectedMotif().getMotif()));
        } catch (Exception e ) {
            Logger.getInstance().error(e);
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "An error has occurred while opening the UCSC browser.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
