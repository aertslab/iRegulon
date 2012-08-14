/**
 * 
 */
package parameterform;

import java.util.ResourceBundle;

/**
 * @author koen
 *
 */
public abstract class IRegulonResourceBundle {

	private final ResourceBundle bundle = ResourceBundle.getBundle("iRegulon");
	
	protected ResourceBundle getBundle() {
		return bundle;
	}
	
}
