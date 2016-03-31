package DependencyTable;

/**
 * Created by Vilyever on 2016/3/30.
 */
public enum DependencyColumnType {
    GitUrl, Tag;

    public String title() {
        switch (this) {
            case GitUrl:
                return "Git URL";
            case Tag:
                return "Tag";
        }

        return "";
    }

    public Class cellClass() {
        switch (this) {
            case GitUrl:
                return String.class;
            case Tag:
                return String.class;
        }

        return String.class;
    }
}
