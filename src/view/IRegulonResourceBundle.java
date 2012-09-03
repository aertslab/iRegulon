package view;

import java.util.ResourceBundle;


public abstract class IRegulonResourceBundle {
	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("iRegulon");
	
	protected ResourceBundle getBundle() {
		return BUNDLE;
	}
}
