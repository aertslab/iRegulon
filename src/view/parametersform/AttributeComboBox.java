package view.parametersform;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JComboBox;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import view.CytoscapeNetworkUtilities;


public class AttributeComboBox  extends JComboBox implements FocusListener {
    public static final String ID_ATTRIBUTE_NAME = "ID";
    private static final String HIDDEN_LABEL_ATTRIBUTE_NAME = "hiddenLabel";

    public static List<String> getPossibleGeneIDAttributes() {
        final List<String> results = new ArrayList<String>();

        final List<CyNode> nodes = CytoscapeNetworkUtilities.getSelectedNodes();
        final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        final float minFraction = Float.parseFloat(ResourceBundle.getBundle("iRegulon").getString("percentage_nodes_not_null"));
		for (String attributeName : nodeAttributes.getAttributeNames()){
			if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_STRING
					&& !attributeName.equals(ID_ATTRIBUTE_NAME)
                    && !attributeName.equals(HIDDEN_LABEL_ATTRIBUTE_NAME)){

                int nullCount = 0;
				for (CyNode node : nodes) {
					if (nodeAttributes.getStringAttribute(node.getIdentifier(), attributeName) == null) {
						nullCount++;
					}
				}
				if (nullCount < (nodes.size() * minFraction)) {
					results.add(attributeName);
				}
			}
		}

        return results;
    }

	public AttributeComboBox(){
		super();
		focusGained(null);
		addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		final String selectedAttributeName = (String) getSelectedItem();
		removeAllItems();

        final List<String> attributeNames = getPossibleGeneIDAttributes();
        for (String name: attributeNames) {
            addItem(name);
        }
		
		if (attributeNames.contains(selectedAttributeName)) {
			setSelectedItem(selectedAttributeName);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
}
