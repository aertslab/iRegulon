package saveActions;

import java.awt.event.ActionEvent;

import view.resultspanel.ResourceAction;
import view.resultspanel.SelectedMotif;

public class BedLinkToBrowserAction extends ResourceAction {
    private static final String NAME = "action_link_to_UCSC";

	private final SaveBed bedSaver;
	
	public BedLinkToBrowserAction(SelectedMotif motif) {
        super(NAME);
		this.bedSaver = new SaveBed(motif);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.bedSaver.openUCSC();
	}

}
