package Task;

import DependencyTable.DependencyModel;
import ModuleTable.ModuleModel;
import Util.Configure;
import Util.WriteActionUtil;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vilyever on 2016/5/6.
 */
public class PodsCompilerGenerateBuild {
    private static boolean buildOK;

    public static boolean generateBuild(Project project, VirtualFile gitPodsDir, ArrayList<ModuleModel> moduleModels) {
        final VirtualFile settingFile = project.getBaseDir().findChild(Configure.ProjectSettingGradleFileName);
        if (settingFile == null) {
            PodsError.showFailReason("settings.gradle not found!");
            return false;
        }

        buildOK = true;
        WriteActionUtil.runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream settingFileInputStream = new FileInputStream(settingFile.getPath());
                    String settingFileContent = IOUtils.toString(settingFileInputStream, "UTF-8");
                    settingFileInputStream.close();

                    String appendSettingFileContent = Configure.SettingBeginMark + "\n";

                    VirtualFile[] repoDirs = gitPodsDir.getChildren();
                    for (VirtualFile dir : repoDirs) {
                        String libraryName = dir.getName();

                        boolean findLibrary = false;
                        ArrayList<VirtualFile> moduleDirs = findModuleDirs(dir);
                        for (VirtualFile moduleDir : moduleDirs) {
                            if (moduleDir.findChild(Configure.ModuleGitPodFileName) != null) {
                                libraryName += "_" + moduleDir.getName();
                                findLibrary = true;
                                break;
                            }
                        }

                        if (!findLibrary) {
                            PodsError.showFailReason("File .gitpod not found in any module on " + dir.getName() + "!");
                            buildOK = false;
                            return;
                        }

                        String libraryPath = String.format("%s/%s/%s'", Configure.GitPodsRepositoriesDirName, dir.getName(), libraryName);
                        appendSettingFileContent += String.format("include ':gitpods_%s'\n", libraryName);
                        appendSettingFileContent += String.format("project(':gitpods_%s').projectDir = new File(settingsDir, '%s')\n", libraryName, libraryPath);
                    }

                    appendSettingFileContent += Configure.SettingEndMark + "\n";

                    settingFileContent = settingFileContent.replaceAll("[\\s\\S]*gitpods_[\\s\\S]*\n", "");
                    settingFileContent = appendSettingFileContent + settingFileContent;
                    FileOutputStream settingFileOutputStream = new FileOutputStream(settingFile.getPath());
                    settingFileOutputStream.write(settingFileContent.getBytes("UTF-8"));
                    settingFileOutputStream.close();

                    for (ModuleModel moduleModel : moduleModels) {
                        VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(ModuleUtilCore.getModuleDirPath(moduleModel.getModule()));
                        VirtualFile buildFile = moduleDir.findChild("build.gradle");
                        if (buildFile == null) {
                            PodsError.showFailReason("File build.gradle not found in module " + moduleModel.getModule().getName() + "!");
                            buildOK = false;
                            return;
                        }

                        FileInputStream buildFileInputStream = new FileInputStream(buildFile.getPath());
                        String buildFileContent = IOUtils.toString(buildFileInputStream, "UTF-8");
                        buildFileInputStream.close();
                        if (!buildFileContent.contains("apply from: 'build_gitpods.gradle'")) {
                            String appendContent = "apply from: 'build_gitpods.gradle' // This is automatically generate by GitPods, please do not edit\n";

                            String preContent = "";
                            while (buildFileContent.substring(0, buildFileContent.indexOf("\n")).contains("apply plugin:")) {
                                preContent += buildFileContent.substring(0, buildFileContent.indexOf("\n") + 1);
                                buildFileContent = buildFileContent.replace(preContent, "");
                            }

                            buildFileContent = preContent + appendContent + buildFileContent;
                            FileOutputStream buildFileOutputStream = new FileOutputStream(buildFile.getPath());
                            buildFileOutputStream.write(buildFileContent.getBytes("UTF-8"));
                            buildFileOutputStream.close();
                        }

                        VirtualFile extBuildFile = moduleDir.createChildDirectory(null, Configure.GitPodsModuleBuildGradleExtFileName);
                        if (extBuildFile == null) {
                            PodsError.showFailReason("Fail to create file build.gradle in module " + moduleModel.getModule().getName() + "!");
                            buildOK = false;
                            return;
                        }

                        FileInputStream extBuildFileInputStream = new FileInputStream(extBuildFile.getPath());
                        String extBuildFileContent = IOUtils.toString(extBuildFileInputStream, "UTF-8");

                        for (DependencyModel dependencyModel : moduleModel.getDependencyModels()) {

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return buildOK;
    }

    private static ArrayList<VirtualFile> findModuleDirs(VirtualFile rootDir) {
        ArrayList<VirtualFile> result = new ArrayList<>();
        if (rootDir.isDirectory()) {
            if (rootDir.findChild("src") != null
                    && rootDir.findChild("build.gradle") != null) {
                result.add(rootDir);
            } else {
                for (VirtualFile child : rootDir.getChildren()) {
                    result.addAll(findModuleDirs(child));
                }
            }
        }
        return result;
    }

}
