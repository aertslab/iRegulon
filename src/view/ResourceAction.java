package view;

import infrastructure.IRegulonResourceBundle;
import org.cytoscape.application.swing.AbstractCyAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;


public abstract class ResourceAction extends AbstractCyAction {
    public static final String NAME_SUFFIX = "_name";
    public static final String DESCRIPTION_SUFFIX = "_description";
    public static final String ICON_SUFFIX = "_icon";

    public ResourceAction(final String actionName) {
        super(actionName);
        final String nameKey = actionName + NAME_SUFFIX;
        final String descriptionKey = actionName + DESCRIPTION_SUFFIX;
        final String iconKey = actionName + ICON_SUFFIX;

        final String name = getBundle().containsKey(nameKey) ? getBundle().getString(nameKey).trim() : "";
        if (!name.equals("")) putValue(Action.NAME, name);

        final String description = getBundle().containsKey(descriptionKey) ? getBundle().getString(descriptionKey).trim() : "";
        if (!description.equals("")) putValue(Action.SHORT_DESCRIPTION, description);

        if (getBundle().containsKey(iconKey)) putValue(Action.SMALL_ICON, loadIcon(getBundle().getString(iconKey)));
    }

    private ImageIcon loadIcon(final String resourceName) {
        return new ImageIcon(getClass().getResource(resourceName));
    }

    public static ResourceBundle getBundle() {
        return IRegulonResourceBundle.getBundle();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
