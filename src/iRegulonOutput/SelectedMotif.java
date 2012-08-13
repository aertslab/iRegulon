package iRegulonOutput;


import java.util.*;

import domainmodel.Motif;


public class SelectedMotif {
	private List<MotifListener> listeners;
	private Motif motifs;
	private String attributeName;
	
	public SelectedMotif(String attributeName){
		this.motifs = null;
		this.listeners = new ArrayList<MotifListener>();
		this.attributeName = attributeName;
	}
	
	/**
	 * 
	 * @param l
	 * @post a listener is added to the group of listeners
	 */
	public void registerListener(MotifListener l) {
		this.listeners.add(l);
	}
	
	/**
	 * @post all the listerners gets the comment to do there action
	 */
	protected void fireListeners() {
		for (MotifListener l : new ArrayList<MotifListener>(listeners)) {
			l.newRegTree(this.motifs);
		}
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	
	
	/**
	 * 
	 *@return all regulatoryTree that is selected in the JTable
	 */
	public Motif getMotif(){
		return this.motifs;
	}
	
	/**
	 * 
	 * @param motif the motif that is selected in the given JTable
	 */
	protected void setRegulatoryTree(Motif motifs){
		this.motifs = motifs;
		fireListeners();
	}
	
	

	
	
	
	
}
