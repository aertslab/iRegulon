package infrastructure;

import java.util.ResourceBundle;


public abstract class IRegulonResourceBundle {
    protected static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("iRegulon");

    public static final String PLUGIN_NAME = getBundle().getString("plugin_name");
    public static final String PLUGIN_VISUAL_NAME = getBundle().getString("plugin_visual_name");
    public static final String PLUGIN_VISUAL_NAME_HTML = "</html>" + PLUGIN_VISUAL_NAME + "</html>";

    public static ResourceBundle getBundle() {
        return RESOURCE_BUNDLE;
    }
}
