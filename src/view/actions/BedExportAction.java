package view.actions;

import view.ResourceAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;


public class BedExportAction extends ResourceAction {
    private static final String NAME = "action_save_bed";

	private final SaveBed bedSaver;
	
	public BedExportAction(SelectedMotif motif){
        super(NAME);
		this.bedSaver = new SaveBed(motif);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.bedSaver.saveBed();
	}
}
