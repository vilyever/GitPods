package Dependency;

/**
 * Created by Vilyever on 2016/3/30.
 */
public enum DependencyColumnType {
    Name, UseGlobalRepository, Delete;

    public String title() {
        switch (this) {
            case Name:
                return "name";
            case UseGlobalRepository:
                return "UseGlobalRepository";
            case Delete:
                return "Delete";
        }

        return "";
    }

    public Class cellClass() {
        switch (this) {
            case Name:
                return String.class;
            case UseGlobalRepository:
                return Boolean.class;
            case Delete:
                return String.class;
        }

        return String.class;
    }
}
