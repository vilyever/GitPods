package Task;

import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import Util.WriteActionUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vilyever on 2016/5/6.
 */
public class PodsCompilerRemoveRedundant {
    public static void removeRedundantDependencies(VirtualFile gitPodsDir, ArrayList<ModuleModel> moduleModels) {
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
    }
}
