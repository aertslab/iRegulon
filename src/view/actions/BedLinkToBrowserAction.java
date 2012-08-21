package view.actions;

import java.awt.event.ActionEvent;

import view.ResourceAction;
import view.resultspanel.SelectedMotif;

public class BedLinkToBrowserAction extends ResourceAction {
    private static final String NAME = "action_link_to_UCSC";

	private final BedUtilities bedSaver;
	
	public BedLinkToBrowserAction(SelectedMotif motif) {
        super(NAME);
		this.bedSaver = new BedUtilities(motif);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.bedSaver.openUCSC();
	}
}
