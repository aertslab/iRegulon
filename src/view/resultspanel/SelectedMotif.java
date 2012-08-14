package view.resultspanel;


import java.util.*;

import domainmodel.Motif;


public class SelectedMotif {
	private List<MotifListener> listeners;
	private Motif motif;
	private String attributeName;
	
	public SelectedMotif(String attributeName) {
		this.motif = null;
		this.listeners = new ArrayList<MotifListener>();
		this.attributeName = attributeName;
	}
	
	public void registerListener(MotifListener l) {
		this.listeners.add(l);
	}
	
	protected void fireListeners() {
		for (MotifListener l : new ArrayList<MotifListener>(listeners)) {
			l.newMotifSelected(this.motif);
		}
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}

	public Motif getMotif() {
		return this.motif;
	}
	
	public void setMotif(Motif motif){
		this.motif = motif;
		fireListeners();
	}
}
