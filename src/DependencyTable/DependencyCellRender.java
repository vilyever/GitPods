package DependencyTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyCellRender extends DefaultTableCellRenderer {

    private static final String ASTERISKS = "************************";

    public DependencyCellRender() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(new EmptyBorder(0, 8, 0, 8));

        if (column == DependencyColumnType.Password.ordinal()) {
            int length =0;
            if (value instanceof String) {
                length =  ((String) value).length();
            } else if (value instanceof char[]) {
                length = ((char[])value).length;
            }
            setText(asterisks(length));
        }

        return this;
    }

    private String asterisks(int length) {
        if (length > ASTERISKS.length()) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append('*');
            }
            return sb.toString();
        } else {
            return ASTERISKS.substring(0, length);
        }
    }
}
