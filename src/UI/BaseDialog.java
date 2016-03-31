package UI;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Vilyever on 2016/3/30.
 */
public class BaseDialog extends JDialog {

    public BaseDialog(@NotNull Project project) {
        this.project = project;
    }


    private final Project project;
    protected Project getProject() {
        return this.project;
    }
}
