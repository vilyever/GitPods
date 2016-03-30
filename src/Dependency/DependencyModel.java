package Dependency;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyModel {
    private String name;
    public DependencyModel setName(String name) {
        this.name = name;
        return this;
    }
    public String getName() {
        return this.name;
    }
    
    private boolean useGlobalRepository;
    public DependencyModel setUseGlobalRepository(boolean useGlobalRepository) {
        this.useGlobalRepository = useGlobalRepository;
        return this; 
    }
    public boolean isUseGlobalRepository() {
        return this.useGlobalRepository;
    }
}
