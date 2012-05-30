package saveActions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import iRegulonInput.IRegulonAction;
import iRegulonOutput.SelectedMotif;

public class BedLinkToBrowserAction extends IRegulonAction{

	private final SaveBed bedSaver;
	
	public BedLinkToBrowserAction(SelectedMotif motif){
		this.bedSaver = new SaveBed(motif);
		putValue(Action.NAME, getBundle().getString("action_link_to_UCSC"));
		putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_link_to_UCSC"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		this.bedSaver.openUCSC();
	}

}
