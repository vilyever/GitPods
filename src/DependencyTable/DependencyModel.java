package DependencyTable;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class DependencyModel {
    private String gitUrl;
    public DependencyModel setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }
    public String getGitUrl() {
        return this.gitUrl;
    }

    private String tag;
    public DependencyModel setTag(String tag) {
        this.tag = tag;
        return this;
    }
    public String getTag() {
        return this.tag;
    }
}
