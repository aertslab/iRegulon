package view.resultspanel;


import java.util.*;

import domainmodel.AbstractMotif;
import domainmodel.Motif;


public final class SelectedMotif {
	private final List<MotifListener> listeners;
    private final String attributeName;

	private AbstractMotif motif;

	public SelectedMotif(final String attributeName) {
		this.listeners = new ArrayList<MotifListener>();
		this.attributeName = attributeName;
        this.motif = null;
    }
	
	public void registerListener(MotifListener l) {
		this.listeners.add(l);
	}

    public void unregisterListener(MotifListener l) {
        this.listeners.remove(l);
    }

	public String getAttributeName(){
		return this.attributeName;
	}

	public AbstractMotif getMotif() {
		return this.motif;
	}
	
	public void setMotif(AbstractMotif motif) {
		this.motif = motif;
		refresh();
	}

    public void refresh() {
        fireListeners();
    }

    private void fireListeners() {
        for (final MotifListener l : new ArrayList<MotifListener>(listeners)) {
            l.newMotifSelected(this.motif);
        }
    }
}
