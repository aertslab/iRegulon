package iRegulonOutput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class TFComboBox extends JComboBox implements MotifListener, ActionListener{

	//private SelectedMotif selectedMotif;
	private Collection<TranscriptionFactor> tfCollection;
	
	public TFComboBox(SelectedMotif selectedTFRegulons){
		super();
		//this.selectedMotif = selectedTFRegulons;
		selectedTFRegulons.registerListener(this);
		this.tfCollection = Collections.EMPTY_LIST;
		this.setEnabled(false);
	}
	
	@Override
	public void newRegTree(Motif currentSelection) {
		// TODO Auto-generated method stub
		if (! this.tfCollection.equals(currentSelection) && currentSelection!=null){
			this.tfCollection = currentSelection.getTranscriptionFactors();
			Collections.sort((List<TranscriptionFactor>) this.tfCollection);
			this.removeAllItems();
			for (TranscriptionFactor tf : this.tfCollection){
				this.addItem(tf);
			}
			this.setEnabled(true);
		}
		if (currentSelection == null){
			this.setEnabled(false);
		}
	}
	
	 public void actionPerformed(ActionEvent e) {
	        //JComboBox cb = (JComboBox)e.getSource();
	    }

	
}
