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
import com.intellij.openapi.util.text.StringUtil;
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

        if (dependencyModel.getRepositoryName() == null) {
            ErrorDialogBuilder.showMessage("Unknown protocol of git url: " + dependencyModel.getGitUrl());
            return false;
        }

        VirtualFile repoDir = gitPodsDir.findChild(dependencyModel.getRepositoryName());

        if (repoDir != null) {
            if (repoDir.findChild(Configure.GitMaskDir) != null) {
                final VirtualFile finalRepoGitDir = repoDir.findChild(Configure.GitMaskDir);

                WriteActionUtil.runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalRepoGitDir.rename(null, Configure.GitDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (repoDir.findChild(Configure.GitDir) == null) {
                final VirtualFile finalRepoDir = repoDir;
                WriteActionUtil.runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalRepoDir.delete(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                repoDir = null;
            }
        }

        GeneralCommandLine fetchCommandLine = new GeneralCommandLine();
        fetchCommandLine.setExePath(GitVcsApplicationSettings.getInstance().getPathToGit());
        fetchCommandLine.setCharset(CharsetToolkit.getDefaultSystemCharset());

        String fetchGitCommand;
        if (repoDir == null) {
            fetchGitCommand = "clone";
            fetchCommandLine.setWorkDirectory(gitPodsDir.getPath());
            fetchCommandLine.addParameter(fetchGitCommand);
            fetchCommandLine.addParameter(dependencyModel.getGitRepoUrl());
            fetchCommandLine.addParameter(dependencyModel.getRepositoryName());
        }
        else {
            fetchGitCommand = "fetch";
            fetchCommandLine.setWorkDirectory(repoDir.getPath());
            fetchCommandLine.addParameter(fetchGitCommand);
            fetchCommandLine.addParameter("origin");
        }

        String error = null;
        try {
            CapturingProcessHandler handler = new CapturingProcessHandler(fetchCommandLine);
            ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
            ProcessOutput result = handler.runProcessWithProgressIndicator(indicator);

            if (result.isTimeout()) {
                error = "Run git " + fetchGitCommand + " timeout.";
            } else if (result.isCancelled()) {
            } else {
                if (result.getExitCode() != 0) {
                    if (!result.getStderr().isEmpty()) {
                        error = result.getStderr();
                    }
                    else {
                        error = "ExitCode: " + result.getExitCode();
                    }
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

        /* checkout tag */
        WriteActionUtil.runWriteAction(new Runnable() {
            @Override
            public void run() {
                VirtualFileManager.getInstance().syncRefresh();
            }
        });

        repoDir = gitPodsDir.findChild(dependencyModel.getRepositoryName());

        System.out.println("repodir1 " + repoDir);

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
                error = "Run git checkout timeout.";
            } else if (result.isCancelled()) {
            } else {
                if (result.getExitCode() != 0) {
                    if (!result.getStderr().isEmpty()) {
                        error = result.getStderr();
                    }
                    else {
                        error = "ExitCode: " + result.getExitCode();
                    }
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

        final VirtualFile finalRepoGitDir = repoDir.findChild(Configure.GitDir);
        WriteActionUtil.runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    finalRepoGitDir.rename(null, Configure.GitMaskDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return true;
    }
}
