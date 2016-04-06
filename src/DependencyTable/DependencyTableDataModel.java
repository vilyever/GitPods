package DependencyTable;

import ModuleTable.ModuleModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyTableDataModel extends AbstractTableModel {

    public DependencyTableDataModel() {
        super();
    }

    public void addNewDependency() {
        if (getModuleModel() == null) {
            return;
        }
        getModuleModel().getDependencyModels().add(new DependencyModel());
        fireTableDataChanged();
    }

    public void deleteRows(int[] rows) {
        if (getModuleModel() == null) {
            return;
        }

        ArrayList<DependencyModel> willDeleteDependencyModels = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            willDeleteDependencyModels.add(getModuleModel().getDependencyModels().get(rows[i]));
        }

        getModuleModel().getDependencyModels().removeAll(willDeleteDependencyModels);

        fireTableDataChanged();
    }

    private ModuleModel moduleModel;
    public DependencyTableDataModel setModuleModel(ModuleModel moduleModel) {
        this.moduleModel = moduleModel;
        fireTableDataChanged();
        return this;
    }
    public ModuleModel getModuleModel() {
        return this.moduleModel;
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
        if (getModuleModel() == null) {
            return 0;
        }
        return getModuleModel().getDependencyModels().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DependencyModel model = getModuleModel().getDependencyModels().get(rowIndex);

        if (columnIndex == DependencyColumnType.GitUrl.ordinal()) {
            return model.getGitUrl();
        }
        else if (columnIndex == DependencyColumnType.Tag.ordinal()) {
            return model.getTag();
        }
        else if (columnIndex == DependencyColumnType.Alias.ordinal()) {
            return model.getAliasName();
        }
        else if (columnIndex == DependencyColumnType.UserName.ordinal()) {
            return model.getUserName();
        }
        else if (columnIndex == DependencyColumnType.Password.ordinal()) {
            return model.getPassword();
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DependencyModel model = getModuleModel().getDependencyModels().get(rowIndex);

        if (columnIndex == DependencyColumnType.GitUrl.ordinal()) {
            String value = (String) aValue;
            model.setGitUrl(value.trim());
        }
        else if (columnIndex == DependencyColumnType.Tag.ordinal()) {
            String value = (String) aValue;
            model.setTag(value.trim());
        }
        else if (columnIndex == DependencyColumnType.Alias.ordinal()) {
            String value = (String) aValue;
            model.setAliasName(value.trim());
        }
        else if (columnIndex == DependencyColumnType.UserName.ordinal()) {
            String value = (String) aValue;
            model.setUserName(value.trim());
        }
        else if (columnIndex == DependencyColumnType.Password.ordinal()) {
            String value = (String) aValue;
            model.setPassword(value);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
