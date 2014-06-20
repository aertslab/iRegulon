package view.actions;

import view.ResourceAction;

import java.awt.event.ActionEvent;
import java.net.URL;


public class SaveLogoAction extends ResourceAction {
    private static final String NAME = "action_save_logo";
    private final java.net.URL fullSizedLogoFileURL;

    public SaveLogoAction(final URL fullSizedLogoFileURL) {
        super(NAME);
        this.fullSizedLogoFileURL = fullSizedLogoFileURL;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PersistenceViewUtilities.saveLogo(fullSizedLogoFileURL);
    }
}
