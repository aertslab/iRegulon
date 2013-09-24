package view.actions;

import view.ResourceAction;

import java.awt.event.ActionEvent;


public class SaveLogoAction extends ResourceAction {
    private static final String NAME = "action_save_logo";
    private final java.net.URL fullSizedLogoFileURL;
    private final String motifName;

    public SaveLogoAction(final java.net.URL fullSizedLogoFileURL, final String motifName) {
        super(NAME);
        this.fullSizedLogoFileURL = fullSizedLogoFileURL;
        this.motifName = motifName;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        SaveLoadDialogs.saveLogo(fullSizedLogoFileURL, motifName);
    }
}
