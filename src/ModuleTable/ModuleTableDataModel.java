package ModuleTable;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class ModuleTableDataModel extends AbstractTableModel {

    public ModuleTableDataModel() {
        super();
    }

    private ArrayList<ModuleModel> moduleModels;
    public ArrayList<ModuleModel> getModuleModels() {
        if (this.moduleModels == null) {
            this.moduleModels = new ArrayList<>();
        }
        return this.moduleModels;
    }
    public ModuleTableDataModel setModuleModels(ArrayList<ModuleModel> moduleModels) {
        this.moduleModels = moduleModels;
        fireTableDataChanged();
        return this;
    }

    @Override
    public int getColumnCount() {
        return ModuleColumnType.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return ModuleColumnType.values()[column].title();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return ModuleColumnType.values()[columnIndex].cellClass();
    }

    @Override
    public int getRowCount() {
        return getModuleModels().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ModuleModel model = getModuleModels().get(rowIndex);

        if (columnIndex == ModuleColumnType.Name.ordinal()) {
            return model.getName();
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
