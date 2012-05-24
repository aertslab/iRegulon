package iRegulonOutput.renderers;

import javax.swing.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.table.*;

import cytoscape.Cytoscape;


public class HighLightColorRenderer extends JLabel implements TableCellRenderer, CanvasUpdater
    {
     private final String columnName;
     private Set IDsToBeHighlighted = new HashSet();
    
     
     
     public HighLightColorRenderer(String column)
         {
         this.columnName = column;
         setOpaque(true);
     }
     
     public void setIDsToBeHighlighted(Set IDs){
    	 this.IDsToBeHighlighted = IDs;
     }
     
     
     public Component getTableCellRendererComponent(JTable table, Object value, 
    		 boolean isSelected, boolean hasFocus, int row, int column)
         {
         this.updateCanvas(table, value, this, isSelected, row);
         if (value != null) setText(value.toString());
         return this;
     }

	@Override
	public JLabel updateCanvas(JTable table, Object value, JLabel canvas,
			boolean isSelected, int row) {
		Object columnValue=table.getValueAt(row,table.getColumnModel().getColumnIndex(columnName));
        //if (value != null) setText(value.toString());
        
        //canvas.setBackground(table.getBackground());
        //canvas.setForeground(table.getForeground());
        //canvas.setBackground(Color.WHITE);
		if(IDsToBeHighlighted.contains(columnValue.toString())){
			canvas.setBackground(Color.LIGHT_GRAY);
			canvas.setForeground(Color.RED);
		}else{
			canvas.setBackground(Color.WHITE);
			canvas.setForeground(Color.BLACK);
		}
		if(isSelected){
        	canvas.setBackground(table.getSelectionBackground());
        	//canvas.setBackground(Color.BLUE);
            //canvas.setForeground(Color.RED);
        }
        //if the canvas isn't opaque, the background color isn't shown
        canvas.setOpaque(true);
        //canvas.setText(canvas.getText());
		return canvas;
	}
}
