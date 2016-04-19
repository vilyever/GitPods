package Task;

import Util.Configure;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileVisitor;

import java.util.ArrayList;

/**
 * Created by vilyever on 2016/4/19.
 */
public class PodsCompilerFindModuleDir {
    public static ArrayList<VirtualFile> findDependencyModuleDirs(VirtualFile rootDir) {
        ArrayList<VirtualFile> dirs = new ArrayList<>();

        if (rootDir.findChild(Configure.ModuleGitPodFileName) != null) {
            dirs.add(rootDir);
            return dirs;
        }

        for (VirtualFile dir : rootDir.getChildren()) {
            if (dir.isDirectory()) {
                dirs.addAll(findDependencyModuleDirs(dir));
            }
        }

        return dirs;
    }
}
