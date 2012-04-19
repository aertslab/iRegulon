package cisTargetX;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JComboBox;

import cytoscape.CyNode;
import cytoscape.Cytoscape;


public class AttributeComboBox  extends JComboBox implements FocusListener{


	public AttributeComboBox(){
		super();
		this.focusGained(null);
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
		String selected = (String) this.getSelectedItem();
		boolean contains = false;
		this.removeAllItems();
		String [] attr = Cytoscape.getNodeAttributes().getAttributeNames();
		ArrayList<CyNode> nodes = CisTargetXNodes.getSelectedNodes();
		for (String atName : attr){
			if (Cytoscape.getNodeAttributes().getType(atName) == Cytoscape.getNodeAttributes().TYPE_STRING
					&& ! atName.equals("ID") && ! atName.equals("hiddenLabel")){
				int amountNull = 0;
				for (CyNode node : nodes){
					if (Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), atName) == null){
						amountNull++;
					}
				}
				if (amountNull < (nodes.size() * Float.parseFloat(ResourceBundle.getBundle("cistargetx").getString("percentage_nodes_not_null")))){
					this.addItem(atName);
					if (!contains && atName.equals(selected)){
						contains = true;
					}
				}
			}
		}
		
		if (contains){
			this.setSelectedItem(selected);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO nothing
		
	}

}
