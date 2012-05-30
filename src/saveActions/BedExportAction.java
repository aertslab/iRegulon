package saveActions;

import iRegulonInput.IRegulonAction;
import iRegulonOutput.SelectedMotif;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class BedExportAction extends IRegulonAction{

	private final SaveBed bedSaver;
	
	public BedExportAction(SelectedMotif motif){
		this.bedSaver = new SaveBed(motif);
		putValue(Action.NAME, getBundle().getString("action_save_bed"));
		putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_save_bed"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		this.bedSaver.saveBed();
	}

}
