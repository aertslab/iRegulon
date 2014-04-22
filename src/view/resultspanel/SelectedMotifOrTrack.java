package view.resultspanel;


import domainmodel.AbstractMotifAndTrack;

import java.util.ArrayList;
import java.util.List;


public final class SelectedMotifOrTrack {
    private final List<MotifAndTrackListener> listeners;

    private AbstractMotifAndTrack motifOrTrack;

    public SelectedMotifOrTrack(final String attributeName) {
        this.listeners = new ArrayList<MotifAndTrackListener>();
        this.motifOrTrack = null;
    }

    public void registerListener(MotifAndTrackListener l) {
        this.listeners.add(l);
    }

    public void unregisterListener(MotifAndTrackListener l) {
        this.listeners.remove(l);
    }

    public AbstractMotifAndTrack getMotifOrTrack() {
        return this.motifOrTrack;
    }

    public void setMotifOrTrack(AbstractMotifAndTrack motifOrTrack) {
        this.motifOrTrack = motifOrTrack;
        refresh();
    }

    public void refresh() {
        fireListeners();
    }

    private void fireListeners() {
        for (final MotifAndTrackListener l : new ArrayList<MotifAndTrackListener>(listeners)) {
            l.newMotifOrTrackSelected(this.motifOrTrack);
        }
    }
}
