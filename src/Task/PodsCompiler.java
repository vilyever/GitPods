package Task;

import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import UI.ErrorDialogBuilder;
import Util.Configure;
import Util.WriteActionUtil;
import com.android.tools.idea.gradle.eclipse.GradleImport;
import com.android.tools.idea.gradle.project.GradleProjectImporter;
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
import org.apache.commons.io.IOUtils;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.GradleBuild;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.settings.GradleSettings;
import org.jetbrains.plugins.gradle.util.GradleUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
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
                final VirtualFile gitPodsDir = PodsCompilerCreateDir.createGitPodsDir(getProject());
                if (gitPodsDir == null) {
                    return;
                }

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
                                return;
                            }
                        }
                    }
                }

                progressIndicator.setText("Removing redundant dependencies");
                PodsCompilerRemoveRedundant.removeRedundantDependencies(gitPodsDir, moduleModels);

//                for (VirtualFile dir : gitPodsDir.getChildren()) {
//                    final ArrayList<VirtualFile> gitPodDirs = PodsCompilerFindModuleDir.findDependencyModuleDirs(gitPodsDir);
//
//                }

                // TODO: 2016/3/31 find out library modules and import to project
                // TODO: 2016/3/31 modify build.gradle to make module as dependency
                progressIndicator.setText("Generating build files");
                if (!PodsCompilerGenerateBuild.generateBuild(getProject(), gitPodsDir, moduleModels)) {
                    return;
                }

                // TODO: 2016/3/31 sync gradle
                GradleProjectImporter.getInstance().requestProjectSync(getProject(), null);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                System.out.println("cancel");
            }
        });
    }








}
