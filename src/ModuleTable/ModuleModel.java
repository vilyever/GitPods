package ModuleTable;

import DependencyTable.DependencyModel;
import com.intellij.openapi.module.Module;

import java.util.ArrayList;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class ModuleModel {
    private String name;
    public ModuleModel setName(String name) {
        this.name = name;
        return this;
    }
    public String getName() {
        return this.name;
    }

    private Module module;
    public ModuleModel setModule(Module module) {
        this.module = module;
        return this;
    }
    public Module getModule() {
        return this.module;
    }

    private ArrayList<DependencyModel> dependencyModels;
    public ModuleModel setDependencyModels(ArrayList<DependencyModel> dependencyModels) {
        this.dependencyModels = dependencyModels;
        return this;
    }
    public ArrayList<DependencyModel> getDependencyModels() {
        if (this.dependencyModels == null) {
            this.dependencyModels = new ArrayList<>();
        }
        return this.dependencyModels;
    }
}
