package Task;

import Util.Configure;
import Util.WriteActionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vilyever on 2016/4/5.
 */
public class PodsCompilerCreateDir {

    public static VirtualFile createGitPodsDir(Project project) {
        if (project.getBaseDir().findChild(Configure.GitPodsRepositoriesDirName) == null) {
            WriteActionUtil.runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        project.getBaseDir().createChildDirectory(null, Configure.GitPodsRepositoriesDirName);
                        VirtualFile gitignoreFile = project.getBaseDir().findChild(Configure.GitIgnoreFileName);
                        if (gitignoreFile == null) {
                            gitignoreFile = project.getBaseDir().createChildData(null, Configure.GitIgnoreFileName);
                        }

                        FileInputStream fileInputStream = new FileInputStream(gitignoreFile.getPath());
                        String fileContent = IOUtils.toString(fileInputStream, "UTF-8");
                        fileInputStream.close();

                        if (!fileContent.contains(Configure.GitPodsIgnore)) {
                            fileContent = Configure.GitPodsIgnoreComment + "\n" + Configure.GitPodsIgnore + "\n\n" + fileContent;
                            FileOutputStream fileOutputStream = new FileOutputStream(gitignoreFile.getPath());
                            fileOutputStream.write(fileContent.getBytes("UTF-8"));
                            fileOutputStream.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (project.getBaseDir().findChild(Configure.GitPodsRepositoriesDirName) == null) {
                PodsError.showFailReason("Fail to create dir '" + Configure.GitPodsRepositoriesDirName + "' in path '" + project.getBaseDir().getPath() + "'");
                return null;
            }
        }

        return project.getBaseDir().findChild(Configure.GitPodsRepositoriesDirName);
    }
}
