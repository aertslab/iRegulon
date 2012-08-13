package resultsview.motifview.detailpanel;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TFDetailFrameAction extends AbstractAction implements ListSelectionListener{

	private TFandMotifSelected tfMotif;
	
	public TFDetailFrameAction(TFandMotifSelected tfMotif){
		putValue(Action.NAME, ResourceBundle.getBundle("iRegulon").getString("action_detail_frame"));
		putValue(Action.SHORT_DESCRIPTION, ResourceBundle.getBundle("iRegulon").getString("action_detail_frame"));
		setEnabled(false);
		this.tfMotif = tfMotif;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DetailFrameTFandMotif frame = new DetailFrameTFandMotif(tfMotif);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(! model.isSelectionEmpty());
	}

	

}
