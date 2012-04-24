package iRegulonOutput.MotifTableModels;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class FilteredPatternDocumentListener implements DocumentListener{

	private final FilteredMotifModel model;
	
	public FilteredPatternDocumentListener(FilteredMotifModel model){
		this.model = model;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		String pattern = "";
		if (e.getDocument().getLength() <= 0){
			pattern = "";
		}else{
			
			try {
				pattern = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		this.model.setPattern(pattern);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		String pattern = "";
		if (e.getDocument().getLength() <= 0){
			
		}else{
			try {
				pattern = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		this.model.setPattern(pattern);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		String pattern = "";
		if (e.getDocument().getLength() <= 0){
			
		}else{
			try {
				pattern = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		this.model.setPattern(pattern);
	}

}
