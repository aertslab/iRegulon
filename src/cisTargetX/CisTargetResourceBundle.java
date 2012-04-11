/**
 * 
 */
package cisTargetX;

import java.util.ResourceBundle;

/**
 * @author koen
 *
 */
public abstract class CisTargetResourceBundle {

	private final ResourceBundle bundle = ResourceBundle.getBundle("cistargetx");
	
	protected ResourceBundle getBundle() {
		return bundle;
	}
	
}
