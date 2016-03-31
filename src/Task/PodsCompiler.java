package Task;

import ModuleTable.ModuleModel;
import Util.Configure;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import UI.ErrorDialog;

/**
 * Created by Vilyever on 2016/3/31.
 */
public class PodsCompiler {
    private static Object lock = new Object();

    public static void compilePods(Project project, final ArrayList<ModuleModel> moduleModels, final CompilerDelegate compileDelegate) {
        ProgressManager.getInstance().run(new Task.Modal(project, "Compiling pods", false) {

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                if (getProject().getBaseDir().findChild(Configure.GitPodsRepositoriesDirName) == null) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getProject().getBaseDir().createChildDirectory(null, Configure.GitPodsRepositoriesDirName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finally {
                                        synchronized (lock) {
                                            lock.notifyAll();
                                        }
                                    }
                                }
                            });
                        }
                    });

                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (getProject().getBaseDir().findChild(Configure.GitPodsRepositoriesDirName) == null) {

                        return;
                    }
                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        compileDelegate.onCompileFail("Fail to create dir " + Configure.GitPodsRepositoriesDirName + " in path " + getProject().getBaseDir().getPath());
                    }
                });

                // TODO: 2016/3/31 check git downloaded

                // TODO: 2016/3/31 update all git and checkout specify tag

                // TODO: 2016/3/31 find out library modules and import to project

                // TODO: 2016/3/31 modify build.gradle to make module as dependency

                // TODO: 2016/3/31 sync gradle

                // TODO: 2016/3/31 write .gitpods
                System.out.println("compile voer");
            }
        });
    }

    public interface CompilerDelegate {
        void onCompileSuccess();
        void onCompileFail(String reason);

        class SimpleCompilerDelegate implements CompilerDelegate {
            @Override
            public void onCompileSuccess() {

            }

            @Override
            public void onCompileFail(String reason) {

            }
        }
    }
}
