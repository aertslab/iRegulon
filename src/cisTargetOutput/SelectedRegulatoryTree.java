package cisTargetOutput;

import cisTargetAnalysis.*;

import java.util.*;

import domainModel.Motif;
import domainModel.TranscriptionFactor;


public class SelectedRegulatoryTree {
	private List<SelectedRegulatoryListener> listeners;
	private Motif motifs;
	
	public SelectedRegulatoryTree(){
		this.motifs = null;
		this.listeners = new ArrayList<SelectedRegulatoryListener>();
	}
	
	/**
	 * 
	 * @param l
	 * @post a listener is added to the group of listeners
	 */
	public void registerListener(SelectedRegulatoryListener l) {
		this.listeners.add(l);
	}
	
	/**
	 * @post all the listerners gets the comment to do there action
	 */
	protected void fireListeners() {
		for (SelectedRegulatoryListener l : new ArrayList<SelectedRegulatoryListener>(listeners)) {
			l.newRegTree(this.motifs);
		}
	}
	
	
	
	
	
	/**
	 * 
	 *@return all regulatoryTree that is selected in the JTable
	 */
	public Motif getMotifs(){
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
