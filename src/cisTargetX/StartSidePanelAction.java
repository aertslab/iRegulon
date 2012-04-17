package cisTargetX;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartSidePanelAction implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		final CisTargetXInputViewSidePanel inputView = new CisTargetXInputViewSidePanel();
		inputView.DrawWindow();
		
	}

}
