package cisTargetX;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

import cytoscape.CyNode;
import cytoscape.Cytoscape;


public class AttributeComboBox  extends JComboBox implements FocusListener{

	public AttributeComboBox(){
		super();
		String [] attr = Cytoscape.getNodeAttributes().getAttributeNames();
		ArrayList<CyNode> nodes = CisTargetXNodes.getAllNodes();
		for (String atName : attr){
			if (Cytoscape.getNodeAttributes().getType(atName) == Cytoscape.getNodeAttributes().TYPE_STRING){
				this.addItem(atName);
			}
		}
		this.addFocusListener(this);
	}
	
	public Object getItemAt(int index){
		return super.getItemAt(index);
	}
	
	public int getItemCount(){
		return super.getItemCount();
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		String selected = (String) this.getSelectedItem();
		boolean contains = false;
		this.removeAllItems();
		String [] attr = Cytoscape.getNodeAttributes().getAttributeNames();
		ArrayList<CyNode> nodes = CisTargetXNodes.getAllNodes();
		for (String atName : attr){
			if (Cytoscape.getNodeAttributes().getType(atName) == Cytoscape.getNodeAttributes().TYPE_STRING){
				this.addItem(atName);
				if (!contains && atName.equals(selected)){
					contains = true;
				}
			}
		}
		
		if (contains){
			this.setSelectedItem(selected);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

}
