package view.resultspanel.renderers;

import javax.swing.*;

import java.awt.*;
import javax.swing.table.*;


public class ClusterColorRenderer extends JLabel implements TableCellRenderer, CanvasUpdater
    {
     private String columnName;
     /*  Generate visually distinct colors:
      *
      *  Look at the python3 code of Janus Troelsen at:
      *    http://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
      *
      *  Change:
      *    if __name__ == "__main__":
      *        print(list(itertools.islice(gethtmlcolors(), 100)))
      *
      *  With:
      *    if __name__ == "__main__":
      *        for rgb_color in list(itertools.islice(gethtmlcolors(), 100)):
      *           printf ("0x" + "".join([ '%02X' % int(x) for x in rgb_color[4:-1].split(',') ]), end=", ")
      */
     private int[] COLORS = { 0x7F3333, 0x51CCCC, 0x337F7F, 0x8ECC51, 0x597F33, 0x8E51CC, 0x59337F,
                              0xCCAD51, 0x7F6C33, 0xCC5151, 0x51CC70, 0x337F46, 0x5170CC, 0x33467F,
                              0xCC51AD, 0x7F336C, 0xCC7F51, 0x7F4F33, 0xBCCC51, 0x757F33, 0x60CC51,
                              0x3C7F33, 0x51CC9E, 0x337F62, 0x519ECC, 0x33627F, 0x6051CC, 0x3C337F,
                              0xBC51CC, 0x75337F, 0xCC517F, 0x7F334F, 0xCC6851, 0x7F4133, 0xCC9651,
                              0x7F5E33, 0xCCC451, 0x7F7A33, 0xA5CC51, 0x677F33, 0x77CC51, 0x4A7F33,
                              0x51CC59, 0x337F37, 0x51CC87, 0x337F54, 0x51CCB5, 0x337F71, 0x51B5CC,
                              0x33717F, 0x5187CC, 0x33547F, 0x5159CC, 0x33377F, 0x7751CC, 0x4A337F,
                              0xA551CC, 0x67337F, 0xCC51C4, 0x7F337A, 0xCC5196, 0x7F335E, 0xCC5168,
                              0x7F3341, 0xCC5D51, 0x7F3A33, 0xCC7451, 0x7F4833, 0xCC8A51, 0x7F5633,
                              0xCCA151, 0x7F6533, 0xCCB851, 0x7F7333, 0xC8CC51, 0x7D7F33, 0xB1CC51,
                              0x6E7F33, 0x9ACC51, 0x607F33, 0x83CC51, 0x527F33, 0x6CCC51, 0x437F33,
                              0x55CC51, 0x357F33, 0x51CC64, 0x337F3E, 0x51CC7B, 0x337F4D, 0x51CC92,
                              0x337F5B, 0x51CCA9, 0x337F69, 0x51CCC0, 0x337F78, 0x51C0CC ,0x33787F };

     
     public ClusterColorRenderer(String column)
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

        int ColorNumber = (Integer) columnValue % this.COLORS.length;
        int intValue = this.COLORS[ColorNumber];
        canvas.setBackground(new Color(intValue));

        if(isSelected) {
            // Set white text for the current selected row (actually cell).
            canvas.setForeground(table.getSelectionForeground());
            // Set white border (top and bottom) for the current selected row (actually cell).
            canvas.setBorder(BorderFactory.createMatteBorder(2,0,2,0,Color.WHITE));
        } else {
            // Set black text for the current row (actually cell).
            canvas.setForeground(table.getForeground());
            // Remove border (in case this row was selected last time).
            canvas.setBorder(BorderFactory.createEmptyBorder());
        }
        canvas.setOpaque(true);
        return canvas;
	}
}
