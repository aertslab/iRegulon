package view.actions;

import view.ResourceAction;
import view.resultspanel.guiwidgets.LogoTransferable;
import view.resultspanel.guiwidgets.LogoUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CopyLogoAction extends ResourceAction {
    private static final String NAME = "action_copy_clipboard_logo";
    private final String motifName;

    public CopyLogoAction(final String motifName) {
        super(NAME);
        this.motifName = motifName;
    }

    private static void setClipboard(ImageIcon image) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new LogoTransferable(image), null);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ImageIcon fullSizedLogo = LogoUtilities.createImageIcon(motifName);
        setClipboard(fullSizedLogo);
    }
}
