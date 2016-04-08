import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import Task.PodsLoader;
import UI.ErrorDialogBuilder;
import UI.ImportDialog;
import com.intellij.ProjectTopics;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons;
import com.intellij.lang.properties.create.CreateResourceBundleDialogComponent;
import com.intellij.notification.EventLog;
import com.intellij.notification.NotificationsManager;
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
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vcs.VcsRootChecker;
import com.intellij.openapi.vcs.VcsRootSettings;
import com.intellij.openapi.vcs.roots.VcsRootDetector;
import com.intellij.openapi.vcs.roots.VcsRootErrorsFinder;
import com.intellij.openapi.vcs.roots.VcsRootProblemNotifier;
import com.intellij.openapi.vcs.roots.VcsRootScanner;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.messages.MessageBus;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitVcs;
import git4idea.actions.GitAction;
import git4idea.config.GitVersion;
import org.jetbrains.annotations.NotNull;

import javax.jnlp.FileSaveService;
import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.FileSystem;
import java.text.ParseException;
import java.util.*;
import java.util.List;
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

        if (getProject() == null) {
            ErrorDialogBuilder.showMessage("Can not find opening project.");
            return;
        }

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
