package ModuleTable;

/**
 * Created by Vilyever on 2016/3/30.
 */
public enum ModuleColumnType {
    Name;

    public String title() {
        switch (this) {
            case Name:
                return "Module Name";
        }

        return "";
    }

    public Class cellClass() {
        switch (this) {
            case Name:
                return String.class;
        }

        return String.class;
    }
}
