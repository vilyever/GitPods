package UI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Vilyever on 2016/4/1.
 */
public class ErrorDialogBuilder {

    public static void showMessage(String message) {
        showMessage("Error", message);
    }
    public static void showMessage(String title, String message) {
//        JFrame frame = new JFrame("Error Frame");
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showScrollableMessage(String message, Dimension messageSize) {
        showScrollableMessage("Error", message, messageSize);
    }

    public static void showScrollableMessage(String title, String message, final Dimension messageSize) {
        JTextArea textArea = new JTextArea(message);
        JScrollPane scrollPane = new JScrollPane(textArea){
            @Override
            public Dimension getPreferredSize() {
                return messageSize;
            }
        };
        JOptionPane.showMessageDialog(
                null,
                scrollPane,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}
