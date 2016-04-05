package Task;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MultiLineLabelUI;
import git4idea.GitVcs;
import git4idea.config.GitVcsApplicationSettings;
import git4idea.config.GitVersion;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;

/**
 * Created by vilyever on 2016/4/5.
 */
public class PodsCompilerCheckGitAvailable {

    public static boolean checkGitAvailable(Project project) {
        GitVersion gitVersion = GitVersion.NULL;
        try {
            String executable = GitVcsApplicationSettings.getInstance().getPathToGit();
            gitVersion = GitVersion.identifyVersion(executable);
        } catch (Exception e) {
        }

        final boolean isGitSetup = !gitVersion.equals(GitVersion.NULL);
        final boolean isGitSupported = gitVersion.isSupported();
        final String gitVersionString = gitVersion.toString();

        if (!isGitSetup || !isGitSupported) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final DialogBuilder gitErrorDialogBuilder = new DialogBuilder(project);
                    gitErrorDialogBuilder.resizable(false);
                    gitErrorDialogBuilder.setTitle("Error");

                    gitErrorDialogBuilder.removeAllActions();
                    gitErrorDialogBuilder.addCancelAction();
                    gitErrorDialogBuilder.addAction(new TextAction(!isGitSetup ? "Install" : "Update") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            gitErrorDialogBuilder.getDialogWrapper().doCancelAction();
                            BrowserUtil.browse("http://git-scm.com");
                        }
                    });
                    gitErrorDialogBuilder.addOkAction().setText("Setting");

                    String reason = null;
                    if (!isGitSetup) {
                        reason = "Can't Start Git.\nProbably the path to Git executable is not valid.\nPlease set supported git";
                    }
                    else if (!isGitSupported) {
                        reason = "The configured version '" + gitVersionString + "' of Git is not supported.\nThe minimal supported version is '" + GitVersion.MIN.toString() + "'.\nPlease update or set another supported git.";
                    }

                    JLabel label = new JLabel(reason);
                    label.setUI(new MultiLineLabelUI());
                    gitErrorDialogBuilder.setCenterPanel(label);

                    if (gitErrorDialogBuilder.show() == DialogWrapper.OK_EXIT_CODE) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, GitVcs.getInstance(project).getConfigurable().getDisplayName());
                    }
                }
            });

            return false;
        }

        return true;
    }
}
