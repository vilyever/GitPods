package UI;

import DependencyTable.DependencyCellRender;
import DependencyTable.DependencyColumnType;
import DependencyTable.DependencyModel;
import DependencyTable.DependencyTableDataModel;
import ModuleTable.ModuleModel;
import ModuleTable.ModuleTableDataModel;
import Task.PodsCompiler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ImportDialog extends BaseDialog {
    final ImportDialog self = this;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton addDependencyButton;
    private JButton deleteDependencyButton;
    private JTable moduleTableView;
    private JTable dependencyTableView;

    public ImportDialog(@NotNull Project project, ArrayList<ModuleModel> moduleModels) {
        super(project);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle("GitPods Import");
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.addDependencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.getDependencyTableDataModel().addNewDependency();
            }
        });

        this.deleteDependencyButton.setForeground(Color.RED);
        this.deleteDependencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = self.dependencyTableView.getSelectedRows();

                self.getDependencyTableDataModel().deleteRows(selectedRows);

                self.deleteDependencyButton.setEnabled(false);
            }
        });

        this.moduleTableView.setModel(getModuleTableDataModel());

        this.moduleTableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.moduleTableView.setDragEnabled(false);
        this.moduleTableView.getTableHeader().setReorderingAllowed(false);
        this.moduleTableView.setRowHeight(30);

        this.moduleTableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRows = self.moduleTableView.getSelectedRows();
                if (selectedRows.length > 0) {
                    self.getDependencyTableDataModel().setModuleModel(getModuleTableDataModel().getModuleModels().get(selectedRows[0]));
                    self.addDependencyButton.setEnabled(true);
                }
                else {
                    self.getDependencyTableDataModel().setModuleModel(null);
                    self.addDependencyButton.setEnabled(false);
                }
                self.deleteDependencyButton.setEnabled(false);
            }
        });

        this.dependencyTableView.setModel(getDependencyTableDataModel());
        this.dependencyTableView.setDefaultRenderer(Object.class, getDependencyCellRender());

        this.dependencyTableView.setDragEnabled(false);
        this.dependencyTableView.getTableHeader().setReorderingAllowed(false);
        this.dependencyTableView.setRowHeight(30);
        this.dependencyTableView.getColumn(DependencyColumnType.GitUrl.title()).setMinWidth(400);
        this.dependencyTableView.getColumn(DependencyColumnType.Tag.title()).setMaxWidth(120);
        this.dependencyTableView.getColumn(DependencyColumnType.Tag.title()).setMinWidth(120);
        this.dependencyTableView.getColumn(DependencyColumnType.Alias.title()).setMinWidth(280);
        this.dependencyTableView.getColumn(DependencyColumnType.UserName.title()).setMaxWidth(150);
        this.dependencyTableView.getColumn(DependencyColumnType.UserName.title()).setMinWidth(150);
        this.dependencyTableView.getColumn(DependencyColumnType.Password.title()).setMaxWidth(150);
        this.dependencyTableView.getColumn(DependencyColumnType.Password.title()).setMinWidth(150);

        JPasswordField password = new JPasswordField();
        password.setBorder(new LineBorder(Color.BLACK));
        TableCellEditor editor = new DefaultCellEditor(password);
        this.dependencyTableView.getColumnModel().getColumn(DependencyColumnType.Password.ordinal()).setCellEditor(editor);

        this.dependencyTableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRows = self.moduleTableView.getSelectedRows();
                if (selectedRows.length > 0) {
                    self.deleteDependencyButton.setEnabled(true);
                }
                else {
                    self.deleteDependencyButton.setEnabled(false);
                }
            }
        });

        getModuleTableDataModel().setModuleModels(moduleModels);
        this.moduleTableView.setRowSelectionInterval(0, 0);
    }

    private ModuleTableDataModel moduleTableDataModel;
    protected ModuleTableDataModel getModuleTableDataModel() {
        if (this.moduleTableDataModel == null) {
            this.moduleTableDataModel = new ModuleTableDataModel();
        }
        return this.moduleTableDataModel;
    }

    private DependencyCellRender dependencyCellRender;
    protected DependencyCellRender getDependencyCellRender() {
        if (this.dependencyCellRender == null) {
            this.dependencyCellRender = new DependencyCellRender();
        }
        return this.dependencyCellRender;
    }

    private DependencyTableDataModel dependencyTableDataModel;
    protected DependencyTableDataModel getDependencyTableDataModel() {
        if (this.dependencyTableDataModel == null) {
            this.dependencyTableDataModel = new DependencyTableDataModel();
        }
        return this.dependencyTableDataModel;
    }

    private void onOK() {
// add your code here
        TableCellEditor editor = this.dependencyTableView.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        boolean haveEmptyItem = false;
        for (ModuleModel moduleModel : getModuleTableDataModel().getModuleModels()) {
            ArrayList<DependencyModel> willRemovedModels = new ArrayList<>();
            for (DependencyModel dependencyModel : moduleModel.getDependencyModels()) {
                if (StringUtil.isEmpty(dependencyModel.getGitUrl())
                        || StringUtil.isEmpty(dependencyModel.getTag())) {
                    haveEmptyItem = true;
                    break;
                }
            }
            if (haveEmptyItem) {
                break;
            }
        }

        if (haveEmptyItem) {
            DialogBuilder dialogBuilder = new DialogBuilder(getProject());
            dialogBuilder.setTitle("Warning");
            dialogBuilder.setCenterPanel(new JLabel("Items with empty giturl or tag will be removed."));
            int result = dialogBuilder.show();
            if (result == DialogWrapper.OK_EXIT_CODE) {
                for (ModuleModel moduleModel : getModuleTableDataModel().getModuleModels()) {
                    ArrayList<DependencyModel> willRemovedModels = new ArrayList<>();
                    for (DependencyModel dependencyModel : moduleModel.getDependencyModels()) {
                        if (StringUtil.isEmpty(dependencyModel.getGitUrl())
                                || StringUtil.isEmpty(dependencyModel.getTag())) {
                            willRemovedModels.add(dependencyModel);
                        }
                    }
                    moduleModel.getDependencyModels().removeAll(willRemovedModels);
                }

                dispose();
                PodsCompiler.compilePods(getProject(), getModuleTableDataModel().getModuleModels());
            }
        }
        else {
            dispose();
            PodsCompiler.compilePods(getProject(), getModuleTableDataModel().getModuleModels());
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
