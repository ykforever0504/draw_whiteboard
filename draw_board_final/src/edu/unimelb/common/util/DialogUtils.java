package edu.unimelb.common.util;

import javax.swing.*;

/**
 * Dialog tool class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class DialogUtils {

    /**
     * There is no service address error message dialog box
     */
    public static void showNoServerConnectionDialog() {
        showErrorDialog("No Server connection");
    }

    /**
     * Displays the error dialog box
     *
     * @param message Display message
     */
    public static void showErrorDialog(String message) {
        JOptionPane.showConfirmDialog(null, message, "Error happens", JOptionPane.OK_CANCEL_OPTION);
        System.exit(1);
    }

    /**
     * Display the message dialog box
     *
     * @param message Display message
     */
    public static void showInfoDialog(String message) {
        JOptionPane.showConfirmDialog(null, message, "Message", JOptionPane.OK_CANCEL_OPTION);
    }

    public static String showUsernameDialog(String username) {
        JFrame frame = new JFrame();
        String input = JOptionPane.showInputDialog(frame, "Enter your username:", username);
        // If the user clicks Cancel or close the dialog box, the program exits
        if (input == null) {
            System.exit(0);
        }
        if (input != null && !input.trim().isEmpty()) {
            input = input.trim();
        }
        // Assign the user name entered by the user to a local variable
        return input;
    }
}
