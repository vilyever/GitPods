package Task;

import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import UI.ErrorDialogBuilder;
import Util.Configure;
import Util.WriteActionUtil;
import com.intellij.execution.ExecutableValidator;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessNotCreatedException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.*;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.vcs.VcsNotifier;
import com.intellij.openapi.vcs.ui.VcsBalloonProblemNotifier;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.actions.GitAction;
import git4idea.actions.GitCloneAction;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitHandler;
import git4idea.commands.GitHandlerUtil;
import git4idea.config.GitConfigUtil;
import git4idea.config.GitVcsApplicationSettings;
import git4idea.config.GitVersion;
import git4idea.i18n.GitBundle;
import git4idea.util.GitUIUtil;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Vilyever on 2016/3/31.
 */
public class PodsCompiler {


    public static void compilePods(Project project, final ArrayList<ModuleModel> moduleModels) {
        ProgressManager.getInstance().run(new Task.Modal(project, "Compiling pods", true) {

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                progressIndicator.setText("Check git pods dir");
                VirtualFile gitPodsDir = PodsCompilerCreateDir.createGitPodsDir(getProject());
                if (gitPodsDir == null) {
                    return;
                }

                // TODO: 2016/3/31 check git downloaded
                progressIndicator.setText("Check git available");
                if (!PodsCompilerCheckGitAvailable.checkGitAvailable(getProject())) {
                    return;
                }

                progressIndicator.setText("Fetching dependencies");

                for (ModuleModel moduleModel : moduleModels) {
                    for (DependencyModel dependencyModel : moduleModel.getDependencyModels()) {
                        if (!progressIndicator.isCanceled()) {
                            progressIndicator.setText("Fetching " + dependencyModel.getGitUrl());
                            boolean result = PodsCompilerFetchDependencies.fetchDependencies(getProject(), gitPodsDir, dependencyModel);
                            if (!result) {
                                break;
                            }
                        }
                    }
                }

                progressIndicator.setText("Removing redundant dependencies");
                VirtualFile[] repoDirs = gitPodsDir.getChildren();
                ArrayList<VirtualFile> willRemainDirs = new ArrayList<VirtualFile>();
                for (VirtualFile dir : repoDirs) {
                    for (ModuleModel moduleModel : moduleModels) {
                        for (DependencyModel dependencyModel : moduleModel.getDependencyModels()) {
                            if (dir.getName().equals(dependencyModel.getRepositoryName())) {
                                willRemainDirs.add(dir);
                            }
                        }
                    }
                }
                for (VirtualFile dir : repoDirs) {
                    if (!willRemainDirs.contains(dir)) {
                        WriteActionUtil.runWriteAction(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dir.delete(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                WriteActionUtil.runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        VirtualFileManager.getInstance().syncRefresh();
                    }
                });

//                for (VirtualFile dir : gitPodsDir.getChildren()) {
//                    for (VirtualFile subDir : dir.getChildren()) {
//                        if (subDir.isDirectory() && subDir.findChild("build.gradle") != null) {
//                            System.out.println("subDir " + subDir.getName());
//                            if (subDir.getName().equals("contextholder")) {
//
//                                WriteActionUtil.runWriteAction(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ModuleManager.getInstance(getProject()).newModule(subDir.getPath(), JavaModuleType.getModuleType().getId());
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }

//                ModuleManager.getInstance(getProject()).

                // TODO: 2016/3/31 update all git and checkout specify tag

                // TODO: 2016/3/31 find out library modules and import to project

                // TODO: 2016/3/31 modify build.gradle to make module as dependency

                // TODO: 2016/3/31 sync gradle

                // TODO: 2016/3/31 write .gitpods

                System.out.println("compile over");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                System.out.println("cancel");
            }
        });
    }








}
