import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class ImportAction extends AnAction {

    public ImportAction() {
        super("ImportAction");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        setProject(anActionEvent.getData(PlatformDataKeys.PROJECT));

        getImportDialog().setVisible(true);
    }

    private Project project;
    protected ImportAction setProject(Project project) {
        this.project = project;
        return this;
    }
    protected Project getProject() {
        return this.project;
    }

    private ImportDialog importDialog;
    protected ImportDialog getImportDialog() {
        if (this.importDialog == null) {
            this.importDialog = new ImportDialog(getProject());
            this.importDialog.setSize(800, 700);
            this.importDialog.setLocationRelativeTo(null);
            this.importDialog.setResizable(false);
        }
        return this.importDialog;
    }
}
