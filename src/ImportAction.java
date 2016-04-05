import ModuleTable.ModuleModel;
import Task.PodsLoader;
import UI.ErrorDialogBuilder;
import UI.ImportDialog;
import com.intellij.execution.ExecutionException;
import com.intellij.lang.properties.create.CreateResourceBundleDialogComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapperDialog;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.PsiUtilBase;
import git4idea.GitVcs;
import git4idea.actions.GitAction;
import git4idea.config.GitVersion;

import javax.jnlp.FileSaveService;
import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.FileSystem;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeoutException;

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

        ApplicationManager.getApplication().saveAll();

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
