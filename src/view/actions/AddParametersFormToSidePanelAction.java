package view.actions;

import view.ResourceAction;
import view.parametersform.InputViewSidePanel;

import java.awt.event.ActionEvent;

public class AddParametersFormToSidePanelAction extends ResourceAction {
    private static final String NAME = "action_open_parameters_side_panel";

    public AddParametersFormToSidePanelAction() {
        super(NAME);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		final InputViewSidePanel inputView = new InputViewSidePanel();
		inputView.addSidePanel();
	}
}
