package Dependency;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyTableModel extends AbstractTableModel {

    public DependencyTableModel(ArrayList<DependencyModel> dependencyModels) {
        super();
        this.dependencyModels = dependencyModels;
    }
    
    private ArrayList<DependencyModel> dependencyModels;
    protected DependencyTableModel setDependencyModels(ArrayList<DependencyModel> dependencyModels) {
        this.dependencyModels = dependencyModels;
        return this; 
    }
    protected ArrayList<DependencyModel> getDependencyModels() {
        return this.dependencyModels;
    }

    @Override
    public int getColumnCount() {
        return DependencyColumnType.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return DependencyColumnType.values()[column].title();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return DependencyColumnType.values()[columnIndex].cellClass();
    }

    @Override
    public int getRowCount() {
        return getDependencyModels().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DependencyModel model = getDependencyModels().get(rowIndex);

        if (columnIndex == DependencyColumnType.Name.ordinal()) {
            return model.getName();
        }
        else if (columnIndex == DependencyColumnType.UseGlobalRepository.ordinal()) {
            return model.isUseGlobalRepository();
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DependencyModel model = getDependencyModels().get(rowIndex);

        if (columnIndex == DependencyColumnType.Name.ordinal()) {
            model.setName((String) aValue);
        }
        else if (columnIndex == DependencyColumnType.UseGlobalRepository.ordinal()) {
            model.setUseGlobalRepository((Boolean) aValue);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
