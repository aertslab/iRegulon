package cisTargetOutput;

import java.awt.event.ActionEvent;

import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import cisTargetX.CisTargetXAction;

import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.SpeciesNomenclature;
import domainModel.TranscriptionFactor;

public abstract class ComboboxAction extends CisTargetXAction implements ListSelectionListener, DocumentListener, MotifListener {

	private SelectedMotif selectedRegulatoryTree;
	private TranscriptionFactor transcriptionFactor;
	
	public ComboboxAction(SelectedMotif selectedRegulatoryTree){
		this.setEnabled(false);
		this.selectedRegulatoryTree = selectedRegulatoryTree;
		this.transcriptionFactor = null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// nothing
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		
		setEnabled(! model.isSelectionEmpty());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			this.setEnabled(true);
			try {
				this.addNewTF(e.getDocument().getText(0, e.getDocument().getLength()));
				//System.out.println(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		//System.out.println("insert");
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			try {
				this.addNewTF(e.getDocument().getText(0, e.getDocument().getLength()));
				//System.out.println(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		//System.out.println("remove " + e.getDocument().getLength());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			this.setEnabled(true);
			try {
				//this.addNewTF(e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getEndPosition().getOffset() - e.getDocument().getStartPosition().getOffset()));
				this.addNewTF(e.getDocument().getText(0, e.getDocument().getLength()));
				//System.out.println(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		//System.out.println("update");
	}
	
	
	private void addNewTF(String geneName){
		GeneIdentifier geneID = new GeneIdentifier(geneName, SpeciesNomenclature.UNKNOWN);
		TranscriptionFactor tf = new TranscriptionFactor(geneID, Float.NaN, Float.NaN, null, null, null, null);
		this.transcriptionFactor = tf;
	}
	
	public TranscriptionFactor getTranscriptionFactor(){
		return this.transcriptionFactor;
	}

	@Override
	public void newRegTree(Motif currentSelection) {
		//this.setEnabled(true);
		//System.out.println("comboboxaction enabled");
	}

	
	/**
	 * 
	 * @return the container of the selectedTFRegulons
	 */
	public SelectedMotif getSelectedRegulatoryTree(){
		return this.selectedRegulatoryTree;
	}
	
	
	
	
	
	
	
	
	
}
