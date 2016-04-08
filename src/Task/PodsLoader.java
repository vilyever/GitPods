package Task;

import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import Util.Configure;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vilyever on 2016/3/31.
 */
public class PodsLoader {
    public static void loadExistedPods(Project project, final LoaderDelegate readerDelegate) {
        ProgressManager.getInstance().run(new Task.Modal(project, "Loading existed pods", false) {

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                ArrayList<ModuleModel> moduleModels = new ArrayList<ModuleModel>();

                Module projectModule = ModuleUtil.findModuleForFile(getProject().getBaseDir(), getProject());

                Module[] modules = ModuleManager.getInstance(getProject()).getModules();
                for (Module module : modules) {
                    if (!module.equals(projectModule)) {

                        ModuleModel moduleModel = new ModuleModel();
                        moduleModel.setModule(module);
                        moduleModel.setName(module.getName());

                        VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(ModuleUtilCore.getModuleDirPath(module));
                        VirtualFile podsFile = moduleDir.findChild(Configure.ModuleGitPodFileName);
                        if (podsFile != null) {
                            try {
                                String content = VfsUtil.loadText(podsFile);
                                JSONArray jsonArray = new JSONArray(content);

                                ArrayList<DependencyModel> dependencyModels = new ArrayList<DependencyModel>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    DependencyModel dependencyModel = new DependencyModel();
                                    dependencyModel.setGitUrl(jsonObject.getString(Configure.GitPodsJsonKeyForGitUrl));
                                    dependencyModel.setTag(jsonObject.getString(Configure.GitPodsJsonKeyForTag));

                                    dependencyModels.add(dependencyModel);
                                }

                                moduleModel.setDependencyModels(dependencyModels);
                            } catch (Exception e) {
                            }
                        }

                        moduleModels.add(moduleModel);
                    }
                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        readerDelegate.onPodsLoaded(moduleModels);
                    }
                });
            }
        });
    }

    public interface LoaderDelegate {
        void onPodsLoaded(ArrayList<ModuleModel> moduleModels);

        class SimpleReaderDelegate implements LoaderDelegate {
            @Override
            public void onPodsLoaded(ArrayList<ModuleModel> moduleModels) {

            }
        }
    }
}
