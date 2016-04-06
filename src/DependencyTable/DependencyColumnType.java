package DependencyTable;

/**
 * Created by Vilyever on 2016/3/30.
 */
public enum DependencyColumnType {
    GitUrl, Tag, Alias, UserName, Password;

    public String title() {
        switch (this) {
            case GitUrl:
                return "Git URL";
            case Tag:
                return "Tag";
            case Alias:
                return "Alias(Resolve duplication repo name)";
            case UserName:
                return "UserName";
            case Password:
                return "Password";
        }

        return "";
    }

    public Class cellClass() {
        switch (this) {
            case GitUrl:
                return String.class;
            case Tag:
                return String.class;
            case Alias:
                return String.class;
            case UserName:
                return String.class;
            case Password:
                return String.class;
        }

        return String.class;
    }
}
