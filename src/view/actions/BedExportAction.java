package view.actions;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.EnhancerRegion;
import infrastructure.CytoscapeEnvironment;
import infrastructure.Logger;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceFactory;
import servercommunication.ServerCommunicationException;
import view.Refreshable;
import view.ResourceAction;
import view.resultspanel.MotifAndTrackListener;
import view.resultspanel.SelectedMotifOrTrack;

import javax.swing.*;
import java.awt.event.ActionEvent;


public final class BedExportAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_save_bed";

    private final ComputationalService service = ComputationalServiceFactory.getInstance().getService();
    private final SelectedMotifOrTrack selectedMotifOrTrack;

    public BedExportAction(final SelectedMotifOrTrack selectedMotifOrTrack) {
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

    @Override
    public void refresh() {
        setEnabled(getSelectedMotifOrTrack().getMotifOrTrack() != null);
    }

    public SelectedMotifOrTrack getSelectedMotifOrTrack() {
        return selectedMotifOrTrack;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final StringBuilder bed_output = new StringBuilder();

        try {
            for (EnhancerRegion region : service.getEnhancerRegions(selectedMotifOrTrack.getMotifOrTrack())) {
                bed_output.append(region.toString() + "\n");
            }
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e.getMessage());
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "An error has occurred while communicating with server.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        PersistenceViewUtilities.saveToSelectedFile(bed_output.toString(), FileTypes.BED);
    }
}
