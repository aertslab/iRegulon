package parameterform.actions;

import parameterform.IRegulonResourceBundle;
import parameterform.InputView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class StartClasicalFrameAction extends IRegulonResourceBundle implements ActionListener{
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final JFrame frame = new JFrame(getBundle().getString("plugin_name"));
		
		InputView input = new InputView(frame);
		JPanel panel = input.CreateClassicalInputView();
		frame.add(panel);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	

}
