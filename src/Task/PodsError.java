package Task;

import UI.ErrorDialogBuilder;
import com.intellij.openapi.application.ApplicationManager;

/**
 * Created by vilyever on 2016/4/5.
 */
public class PodsError {

    public static void showFailReason(String reason) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ErrorDialogBuilder.showMessage(reason);
            }
        });
    }
}
