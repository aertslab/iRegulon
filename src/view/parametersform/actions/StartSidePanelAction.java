package view.parametersform.actions;

import view.parametersform.InputViewSidePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartSidePanelAction implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		final InputViewSidePanel inputView = new InputViewSidePanel();
		inputView.DrawWindow();
		
	}

}
