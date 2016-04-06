package DependencyTable;

import com.intellij.openapi.util.text.StringUtil;

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

    private String aliasName;
    public DependencyModel setAliasName(String aliasName) {
        this.aliasName = aliasName;
        return this;
    }
    public String getAliasName() {
        return this.aliasName;
    }

    private String userName;
    public DependencyModel setUserName(String userName) {
        this.userName = userName;
        return this;
    }
    public String getUserName() {
        return this.userName;
    }

    private String password;
    public DependencyModel setPassword(String password) {
        this.password = password;
        return this;
    }
    public String getPassword() {
        return this.password;
    }

    public boolean isLocal() {
        return (getGitUrl().indexOf("file://") == 0) || (getGitUrl().indexOf("/") == 0);
    }

    public boolean isHTTP() {
        return (getGitUrl().indexOf("http://") == 0) || isHTTPS() || isSmartHTTP();
    }

    public boolean isHTTPS() {
        return (getGitUrl().indexOf("https://") == 0);
    }

    public boolean isSmartHTTP() {
        return (getGitUrl().indexOf("git://") == 0);
    }

    public boolean isSSH() {
        return (getGitUrl().indexOf("ssh://") == 0) || getGitUrl().contains("@");
    }

    public String getGitRepoUrl() {
        if (isHTTP() && !StringUtil.isEmpty(getUserName()) && !StringUtil.isEmpty(getPassword())) {
            String protocol = getGitUrl().substring(0, getGitUrl().indexOf("://") + "://".length());
            return getGitUrl().replace(protocol, protocol + getUserName() + ":" + getPassword() + "@");
        }

        return getGitUrl();
    }

    public String getRepositoryName() {
        if (getGitUrl() != null) {
            if (!StringUtil.isEmpty(getAliasName())) {
                return getAliasName();
            }

            if (isLocal()) {
                String subUrl = getGitUrl().replaceAll("(?i).git", "");
                int lastBackSlashIndex = subUrl.lastIndexOf('\\');
                int lastSlashIndex = subUrl.lastIndexOf('/');
                subUrl = subUrl.substring(Math.max(lastBackSlashIndex, lastSlashIndex) + 1);

                return subUrl;
            }
            else if (isHTTP()) {
                String protocol = getGitUrl().substring(0, getGitUrl().indexOf("://") + "://".length());
                String subUrl = getGitUrl().replace(protocol, "");
                subUrl = subUrl.substring(subUrl.indexOf("/") + 1);
                subUrl = subUrl.replaceAll("(?i).git", "");

                int lastSlashIndex = subUrl.lastIndexOf('/');
                subUrl = subUrl.substring(lastSlashIndex + 1);

                return subUrl;
            }
            else if (isSSH()) {
                String subUrl = getGitUrl().substring(getGitRepoUrl().indexOf("@") + 1);
                if (subUrl.contains(":")) {
                    subUrl = subUrl.substring(subUrl.indexOf(":") + 1);
                }
                else {
                    subUrl = subUrl.substring(subUrl.indexOf("/") + 1);
                }
                subUrl = subUrl.replaceAll("(?i).git", "");

                int lastSlashIndex = subUrl.lastIndexOf('/');
                subUrl = subUrl.substring(lastSlashIndex + 1);

                return subUrl;
            }
        }
        return null;

    }

}
