import ModuleTable.ModuleModel;
import Task.PodsLoader;
import UI.ImportDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

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

        PodsLoader.loadExistedPods(getProject(), new PodsLoader.LoaderDelegate() {
            @Override
            public void onPodsLoaded(ArrayList<ModuleModel> moduleModels) {
                new ImportDialog(getProject(), moduleModels).setVisible(true);
            }
        });

    }

    private Project project;
    protected ImportAction setProject(Project project) {
        this.project = project;
        return this;
    }
    protected Project getProject() {
        return this.project;
    }
}
