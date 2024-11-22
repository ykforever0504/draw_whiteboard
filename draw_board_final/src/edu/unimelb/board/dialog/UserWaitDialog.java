package edu.unimelb.board.dialog;

import edu.unimelb.common.constant.DrawBoardConstant;

import javax.swing.*;
import java.awt.*;

/**
 * The user waits for the administrator to approve the loading box
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class UserWaitDialog {

    private int seconds = 0;

    /**
     * label to be displayed
     */
    private JLabel label = new JLabel();

    /**
     * java dialog
     */
    private JDialog userWaitDialog = null;

    /**
     * user's selection result for the userWaitDialog
     */
    private boolean result = DrawBoardConstant.REJECTED;

    /**
     * @param parent parent frame
     * @param message message to be displayed
     * @param sec seconds to be count down
     * @return true if user accept, false otherwise
     */
    public boolean showDialog(JFrame parent, String message, int sec) {
        seconds = sec;

        label.setText(message);
        label.setBounds(80,6, 400, 40);

        JButton confirm = new JButton("Approve");
        confirm.setBounds(100,50,80,30);
        confirm.addActionListener(e -> {
            result = DrawBoardConstant.ACCEPTED;
            parent.setEnabled(true);
            UserWaitDialog.this.userWaitDialog.dispose();
        });

        JButton cancel = new JButton("Reject");
        cancel.setBounds(270,50,80,30);
        cancel.addActionListener(e -> {
            result = DrawBoardConstant.REJECTED;
            parent.setEnabled(true);
            UserWaitDialog.this.userWaitDialog.dispose();
        });

        userWaitDialog = new JDialog(parent, true);
        parent.setEnabled(false);
        userWaitDialog.setTitle("Someone wants to share your whiteboard.");
        userWaitDialog.setLayout(null);
        userWaitDialog.add(label);
        userWaitDialog.add(confirm);
        userWaitDialog.add(cancel);

        userWaitDialog.pack();
        userWaitDialog.setSize(new Dimension(500,150));
        userWaitDialog.setLocationRelativeTo(parent);
        userWaitDialog.setVisible(true);

        return result;
    }
}
