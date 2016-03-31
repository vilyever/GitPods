package UI;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorDialog extends BaseDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel infoLabel;

    public ErrorDialog(Project project, String info) {
        super(project);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setTitle("Error");
        this.infoLabel.setText(info);
        setLocationRelativeTo(null);
    }

    private void onOK() {
// add your code here
        dispose();
    }
}
