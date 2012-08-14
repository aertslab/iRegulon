package view.resultspanel.renderers;

import javax.swing.*;

import java.awt.*;
import javax.swing.table.*;


public class ColorRenderer extends JLabel implements TableCellRenderer, CanvasUpdater
    {
     private String columnName;
     private String[] COLORS = {"44B4D5","8BFEA8","B4D1B6","DBEBFF","5757FF","FF97E8","63E9FC","6CA870",
                       "D7F445","7DE98D","ECA65B","E97D89","B17DE9","A2684B","9D8F9C","956EA1",
                       "6EA189","9E7862","5CBB74","AFB5B1","AF9DBA","55D4E2","4985D6","FFBBBB"};
     
     
     
     public ColorRenderer(String column)
         {
         this.columnName = column;
         setOpaque(true);
     }
     
     
     public Component getTableCellRendererComponent(JTable table, Object value, 
    		 boolean isSelected, boolean hasFocus, int row, int column)
         {
	     this.updateCanvas(table, value, this, isSelected, row);
	     if (value != null) setText(value.toString());
         return this;
     }


	@Override
	public JLabel updateCanvas(JTable table, Object value, JLabel canvas, boolean isSelected, int row) {
		Object columnValue=table.getValueAt(row,table.getColumnModel().getColumnIndex(columnName));
        
        
        if(isSelected)
            {
            canvas.setBackground(table.getSelectionBackground());
            canvas.setForeground(table.getSelectionForeground());
        }
        else
            {
        	canvas.setBackground(table.getBackground());
        	canvas.setForeground(table.getForeground());
            int ColorNumber = (Integer) columnValue % this.COLORS.length;
            int intValue = Integer.parseInt(this.COLORS[ColorNumber],16);
            canvas.setBackground(new Color(intValue));
        }
        canvas.setOpaque(true);
        return canvas;
	}
}
