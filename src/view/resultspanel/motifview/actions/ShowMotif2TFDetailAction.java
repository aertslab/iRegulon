package view.resultspanel.motifview.actions;

import cytoscape.Cytoscape;
import view.ResourceAction;
import view.resultspanel.motifview.Motif2TFDetailFrame;
import view.resultspanel.motifview.detailpanel.TFandMotifSelected;

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ShowMotif2TFDetailAction extends ResourceAction implements ListSelectionListener {
    private static final String NAME = "action_detail_frame";

	private TFandMotifSelected tfMotif;
	
	public ShowMotif2TFDetailAction(final TFandMotifSelected tfMotif) {
        super(NAME);
		setEnabled(false);
		this.tfMotif = tfMotif;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final JFrame frame = new Motif2TFDetailFrame(tfMotif);
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
