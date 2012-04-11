package cisTargetX;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.event.ListSelectionListener;

public abstract class CisTargetXAction extends AbstractAction {

	private final ResourceBundle bundle = ResourceBundle.getBundle("cistargetx");

	public CisTargetXAction() {
		super();
	}

	public CisTargetXAction(String name) {
		super(name);
	}

	public CisTargetXAction(String name, Icon icon) {
		super(name, icon);
	}

	protected ResourceBundle getBundle() {
		return bundle;
	}
}