package edu.unimelb.board;

import edu.unimelb.board.dialog.UserWaitDialog;
import edu.unimelb.common.constant.ApplicationNameConstant;
import edu.unimelb.common.util.ClientConnectionUtils;
import edu.unimelb.common.util.DialogUtils;
import edu.unimelb.service.IRemoteUserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import static edu.unimelb.common.constant.DrawBoardConstant.ASK_MANAGER_JOIN_TIMEOUT;
import static edu.unimelb.common.constant.DrawBoardConstant.SHARE_MESSAGE;

/**
 * frame of whiteboard
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class DrawFrameApplication {

    private boolean isManager;

    private JFrame drawFrame;

    public DrawPanel getDrawPanel() {
        return drawPanel;
    }

    private DrawPanel drawPanel;

    private Thread updateUserListThread;

    /**
     * User unique tracking value
     */
    private String traceId;

    /**
     * Client connection tool class
     */
    private ClientConnectionUtils clientConnectionUtils;

    private IRemoteUserService remoteUserService;

    private boolean isKickedOut = false;

    public DrawFrameApplication(boolean isManager) {
        this.isManager = isManager;

        drawFrame = new JFrame();
        // Close the form exit program
        drawFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame(e);
            }
        });

        // Draw whiteboard content
        this.drawPanel = new DrawPanel(isManager);
        drawFrame.setLayout(new BoxLayout(drawFrame.getContentPane(), BoxLayout.X_AXIS));
        // frame size
        drawFrame.setBounds(300, 300, 1200, 800);
        drawPanel.setPreferredSize(new Dimension(1000, 800));
        drawFrame.add(drawPanel);

        // If it is the manager to display the whiteboard
        if (this.isManager) {
            // Set manager title
            drawFrame.setTitle(ApplicationNameConstant.DRAW_APP_TITLE + "Manager");

            // kickout logic
            JPanel kickPanel = new JPanel();
            kickPanel.setLayout(new BoxLayout(kickPanel, BoxLayout.Y_AXIS));
            kickPanel.setPreferredSize(new Dimension(200, 800));
            drawFrame.add(kickPanel);

            // kickout user label
            JLabel kickOutLabel = new JLabel("Kickout User：");
            kickOutLabel.setSize(kickOutLabel.getPreferredSize());
            // UserID input field
            JTextField kickOutTextField = new JTextField();

            JButton kickOutButton = new JButton("Kickout");
            kickOutButton.addActionListener(e -> {
                // Get the ID of the kicked out user
                String kickOutTraceId = kickOutTextField.getText();
                try {
                    if (kickOutTraceId.equals(remoteUserService.getManagerUsername())) {
                        DialogUtils.showInfoDialog("Can't kick yourself out");
                    } else {
                        clientConnectionUtils.kickOut(kickOutTraceId);
                    }
                } catch (RemoteException e1) {
                    DialogUtils.showNoServerConnectionDialog();
                }
            });

            kickPanel.add(kickOutLabel);
            kickPanel.add(kickOutTextField);
            kickPanel.add(kickOutButton);

        } else {
            // set visitor title
            drawFrame.setTitle(ApplicationNameConstant.DRAW_APP_TITLE);
        }

    }

    public void setClientConnection(ClientConnectionUtils clientConnectionUtils) {
        this.clientConnectionUtils = clientConnectionUtils;
        this.drawPanel.setClientConnectionUtils(traceId, clientConnectionUtils);
    }

    public void setRemoteUserService(IRemoteUserService remoteUserService) {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setSize(80, 400);
        drawFrame.add(jTextArea);

        this.remoteUserService = remoteUserService;

        // Update the userList thread pool
        updateUserListThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    StringBuilder newText = new StringBuilder();
                    try {
                        // If it has a manager
                        if (remoteUserService.getManagerUsername() != null) {
                            newText.append("[*] ").append(remoteUserService.getManagerUsername()).append("\n");
                            for (String s : remoteUserService.listUsername()) {
                                newText.append("[-] ").append(s).append("\n");
                            }
                        }
                        if (!newText.toString().equals(jTextArea.getText())) {
                            jTextArea.setText(newText.toString());
                        }
                    } catch (RemoteException e) {
                        DialogUtils.showNoServerConnectionDialog();
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        updateUserListThread.start();
    }

    public boolean isManager() {
        return isManager;
    }

    public void stop() {
        updateUserListThread.interrupt();
    }

    public void start(String traceId) {
        this.traceId = traceId;
        drawFrame.setVisible(true);
    }

    public void error(String message) {
        DialogUtils.showInfoDialog(message);
        System.exit(0);
        stop();
    }

    public boolean askAcceptWait(String waitTraceId) {
        UserWaitDialog dialog = new UserWaitDialog();
        return dialog.showDialog(drawFrame, waitTraceId + SHARE_MESSAGE, ASK_MANAGER_JOIN_TIMEOUT);
    }

    public void kickedOut() {
        drawFrame.setTitle(drawFrame.getTitle() + " - [kick out]");
        stop();
        DialogUtils.showInfoDialog("You've been kicked out of the manager.");
        isKickedOut = true;
        closeFrame();
    }

    private void closeFrame(WindowEvent e) {
        if (!isKickedOut) {
            clientConnectionUtils.disconnect(isManager, traceId);
        }
        stop();
        e.getWindow().dispose();
        System.exit(0);
    }

    private void closeFrame() {
        if (!isKickedOut) {
            clientConnectionUtils.disconnect(isManager, traceId);
        }
        drawFrame.dispose();
        System.exit(0);
    }

    public void closeByManager() {
        drawFrame.setTitle(drawFrame.getTitle() + " - [closed]");
        stop();
        DialogUtils.showInfoDialog("The system has been closed by the manager.");
        isKickedOut = true;
        closeFrame();
    }

    public void notifyUser(String message) {
        DialogUtils.showInfoDialog(message);
    }


    public void showDrawBoard() {
        drawFrame.setVisible(true);
    }

}
