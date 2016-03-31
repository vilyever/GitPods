package UI;

import DependencyTable.DependencyCellRender;
import DependencyTable.DependencyColumnType;
import DependencyTable.DependencyTableDataModel;
import ModuleTable.ModuleModel;
import ModuleTable.ModuleTableDataModel;
import Task.PodsCompiler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import Task.PodsLoader;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

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


        setTitle("GitPods Import");
        setSize(this.contentPane.getPreferredSize());
        setLocationRelativeTo(null);

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
        this.moduleTableView.getTableHeader().setPreferredSize(new Dimension(-1, 30));
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
        this.dependencyTableView.getTableHeader().setPreferredSize(new Dimension(-1, 30));
        this.dependencyTableView.setRowHeight(30);
        this.dependencyTableView.getColumn(DependencyColumnType.Tag.title()).setMaxWidth(120);
        this.dependencyTableView.getColumn(DependencyColumnType.Tag.title()).setMinWidth(120);

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

//        PodsLoader.loadExistedPods(getProject(), new PodsLoader.LoaderDelegate() {
//            @Override
//            public void onPodsLoaded(ArrayList<ModuleModel> moduleModels) {
//                getModuleTableDataModel().setModuleModels(moduleModels);
//                self.moduleTableView.setRowSelectionInterval(0, 0);
//            }
//        });
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
        dispose();

        PodsCompiler.compilePods(getProject(), getModuleTableDataModel().getModuleModels(), new PodsCompiler.CompilerDelegate() {
            @Override
            public void onCompileSuccess() {

            }

            @Override
            public void onCompileFail(String reason) {
                new ErrorDialog(getProject(), reason).setVisible(true);
            }
        });
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
