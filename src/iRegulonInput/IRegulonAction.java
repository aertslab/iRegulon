package iRegulonInput;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class IRegulonAction extends AbstractAction {

	private final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");

	public IRegulonAction() {
		super();
	}

	public IRegulonAction(String name) {
		super(name);
	}

	public IRegulonAction(String name, Icon icon) {
		super(name, icon);
	}

	protected ResourceBundle getBundle() {
		return bundle;
	}
}