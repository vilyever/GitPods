import Dependency.DependencyCellRender;
import Dependency.DependencyModel;
import Dependency.DependencyTableModel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import util.BaseDialog;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.event.*;
import java.util.ArrayList;

public class ImportDialog extends BaseDialog {
    final ImportDialog self = this;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane dependenciesScrollPane;
    private JButton addDependencyButton;

    public ImportDialog(@NotNull Project project) {
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

        this.addDependencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.getDependencyModels().add(new DependencyModel());
                self.dependenciesTableView.updateUI();
            }
        });

        this.dependenciesScrollPane.setViewportView(getDependenciesTableView());
        getDependenciesTableView().setModel(getDependencyTableModel());
    }

    private JBTable dependenciesTableView;
    protected JBTable getDependenciesTableView() {
        if (this.dependenciesTableView == null) {
            this.dependenciesTableView = new JBTable();
            this.dependenciesTableView.setShowVerticalLines(true);
            this.dependenciesTableView.setShowHorizontalLines(true);
        }
        return this.dependenciesTableView;
    }
    
    private DependencyTableModel dependencyTableModel;
    protected DependencyTableModel getDependencyTableModel() {
        if (this.dependencyTableModel == null) {
            this.dependencyTableModel = new DependencyTableModel(getDependencyModels());
        }
        return this.dependencyTableModel;
    }

    private ArrayList<DependencyModel> dependencyModels;
    protected ArrayList<DependencyModel> getDependencyModels() {
        if (this.dependencyModels == null) {
            this.dependencyModels = new ArrayList<>();
        }
        return this.dependencyModels;
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
