package cisTargetOutput;


import java.util.*;

import domainModel.Motif;


public class SelectedMotif {
	private List<MotifListener> listeners;
	private Motif motifs;
	
	public SelectedMotif(){
		this.motifs = null;
		this.listeners = new ArrayList<MotifListener>();
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
