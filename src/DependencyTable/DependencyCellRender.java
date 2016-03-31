package DependencyTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyCellRender extends DefaultTableCellRenderer {

    public DependencyCellRender() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBorder(new EmptyBorder(0, 16, 0, 16));
        return label;
    }
}
