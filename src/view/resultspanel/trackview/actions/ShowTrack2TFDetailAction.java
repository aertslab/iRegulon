package view.resultspanel.trackview.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;
import view.resultspanel.trackview.Track2TFDetailFrame;
import view.resultspanel.trackview.detailpanel.TFandTrackSelected;

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ShowTrack2TFDetailAction extends ResourceAction implements ListSelectionListener {
    private static final String NAME = "action_detail_frame";

	private TFandTrackSelected tfTrack;
	
	public ShowTrack2TFDetailAction(final TFandTrackSelected tfTrack) {
        super(NAME);
		setEnabled(false);
		this.tfTrack = tfTrack;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final JDialog frame = new Track2TFDetailFrame(tfTrack);
        frame.setLocationRelativeTo(Cytoscape.getDesktop());
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(!model.isSelectionEmpty());
	}
}
