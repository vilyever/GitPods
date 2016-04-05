package Task;

import DependencyTable.DependencyModel;
import UI.ErrorDialogBuilder;
import Util.Configure;
import Util.WriteActionUtil;
import com.android.ddmlib.TimeoutException;
import com.intellij.execution.ExecutableValidator;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.actions.GitCloneAction;
import git4idea.config.GitVcsApplicationSettings;

import java.io.IOException;

/**
 * Created by vilyever on 2016/4/5.
 */
public class PodsCompilerFetchDependencies {

    public static boolean fetchDependencies(Project project, VirtualFile gitPodsDir, DependencyModel dependencyModel) {

        VirtualFile authorDir = gitPodsDir.findChild(dependencyModel.getAuthor());
        VirtualFile repoDir = authorDir == null ? null : authorDir.findChild(dependencyModel.getRepositoryName());

        if (dependencyModel.getAuthor() == null
                || dependencyModel.getRepositoryName() == null) {
            repoDir = gitPodsDir.findChild(dependencyModel.getGitUrl());
        }

        GeneralCommandLine fetchCommandLine = new GeneralCommandLine();
        fetchCommandLine.setExePath(GitVcsApplicationSettings.getInstance().getPathToGit());
        fetchCommandLine.setCharset(CharsetToolkit.getDefaultSystemCharset());

        if (repoDir == null) {
            fetchCommandLine.setWorkDirectory(gitPodsDir.getPath());
            fetchCommandLine.addParameter("clone");
            fetchCommandLine.addParameter(dependencyModel.getGitUrl());
            if (dependencyModel.getAuthor() == null
                    || dependencyModel.getRepositoryName() == null) {
                fetchCommandLine.addParameter(dependencyModel.getGitUrl());
            }
            else {
                fetchCommandLine.addParameter(dependencyModel.getAuthor() + "/" + dependencyModel.getRepositoryName());
            }
        }
        else {
            final VirtualFile finalRepoDir = repoDir;
            WriteActionUtil.runWriteAction(new Runnable() {
                @Override
                public void run() {
                    VirtualFile gitDir = finalRepoDir.findChild(Configure.GitMaskDir);
                    try {
                        gitDir.rename(null, Configure.GitDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            fetchCommandLine.setWorkDirectory(repoDir.getPath());
            fetchCommandLine.addParameter("fetch");
            fetchCommandLine.addParameter("origin");
        }

        System.out.println("fetchCommandLine " + fetchCommandLine.getCommandLineString());

        String error = null;
        try {
            CapturingProcessHandler handler = new CapturingProcessHandler(fetchCommandLine);
            ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
            ProcessOutput result = handler.runProcessWithProgressIndicator(indicator);

            if (result.isTimeout()) {
                error = "Run command timeout.";
            } else if (result.isCancelled()) {
            } else {
                if (result.getExitCode() != 0 || !result.getStderr().isEmpty()) {
//                    error = "ExitCode=" + result.getExitCode() + "; " + result.getStderr();
                    error = result.getStderr();
                }
            }
        }
        catch (Exception e) {
            error = e.toString();
            e.printStackTrace();
        }

        if (error != null) {
            ErrorDialogBuilder.showMessage(error);
            return false;
        }

        authorDir = gitPodsDir.findChild(dependencyModel.getAuthor());
        repoDir = authorDir == null ? null : authorDir.findChild(dependencyModel.getRepositoryName());

        if (dependencyModel.getAuthor() == null
                || dependencyModel.getRepositoryName() == null) {
            repoDir = gitPodsDir.findChild(dependencyModel.getGitUrl());
        }

        if (repoDir == null) {
            ErrorDialogBuilder.showMessage("Unknown error: pods dir for '" + dependencyModel.getGitUrl()  + "' not found!");
            return false;
        }

        GeneralCommandLine checkoutCommandLine = new GeneralCommandLine();
        checkoutCommandLine.setExePath(GitVcsApplicationSettings.getInstance().getPathToGit());
        checkoutCommandLine.setCharset(CharsetToolkit.getDefaultSystemCharset());
        checkoutCommandLine.setWorkDirectory(repoDir.getPath());
        checkoutCommandLine.addParameter("checkout");
        checkoutCommandLine.addParameter("tags/" + dependencyModel.getTag());

        error = null;
        try {
            CapturingProcessHandler handler = new CapturingProcessHandler(checkoutCommandLine);
            ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
            ProcessOutput result = handler.runProcessWithProgressIndicator(indicator);

            if (result.isTimeout()) {
                error = "Run command timeout.";
            } else if (result.isCancelled()) {
            } else {
                if (result.getExitCode() != 0 || !result.getStderr().isEmpty()) {
//                    error = "ExitCode=" + result.getExitCode() + "; " + result.getStderr();
                    error = result.getStderr();
                }
            }
        }
        catch (Exception e) {
            error = e.toString();
            e.printStackTrace();
        }

        if (error != null) {
            ErrorDialogBuilder.showMessage(error);
            return false;
        }

        final VirtualFile finalRepoDir1 = repoDir;
        WriteActionUtil.runWriteAction(new Runnable() {
            @Override
            public void run() {
                VirtualFile gitDir = finalRepoDir1.findChild(Configure.GitDir);
                try {
                    gitDir.rename(null, Configure.GitMaskDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return true;
    }
}
