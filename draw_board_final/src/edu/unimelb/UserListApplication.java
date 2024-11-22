package edu.unimelb;

import edu.unimelb.service.IRemoteUserService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

/**
 * User management page
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class UserListApplication {

    private JFrame mainFrame;
    private Thread uiUpdater;
    private JTextArea managerTextArea, userTextArea;
    private IRemoteUserService userService;

    public UserListApplication(IRemoteUserService userService) {
        this.userService = userService;
        initUI();
        setupUIUpdateThread();
        mainFrame.setVisible(true);
    }

    private void initUI() {
        mainFrame = new JFrame("Distributed Whiteboard Server");
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(200, 350);
        mainFrame.setResizable(false);

        setupLabelsAndTextAreas();
        setupWindowListener();
    }

    private void setupLabelsAndTextAreas() {
        JLabel managerLabel = new JLabel("Manager: ");
        managerLabel.setBounds(0, 0, 70, 20);
        mainFrame.add(managerLabel);

        managerTextArea = new JTextArea();
        managerTextArea.setEditable(false);
        managerTextArea.setBounds(70, 5, 120, 20);
        mainFrame.add(managerTextArea);

        JLabel userLabel = new JLabel("Users: ");
        userLabel.setBounds(0, 30, 70, 20);
        mainFrame.add(userLabel);

        userTextArea = new JTextArea();
        userTextArea.setEditable(false);
        userTextArea.setBounds(70, 30, 120, 250);
        mainFrame.add(userTextArea);
    }

    private void setupWindowListener() {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (uiUpdater != null) {
                    uiUpdater.interrupt();
                }
                System.exit(0);
            }
        });
    }

    private void setupUIUpdateThread() {
        uiUpdater = new Thread(() -> updateUIPeriodically());
        uiUpdater.start();
    }

    private void updateUIPeriodically() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                updateManagerName();
                updateUserList();
                Thread.sleep(300);
            } catch (InterruptedException e) {
                break;
            } catch (RemoteException e) {
                System.out.println("Failed to retrieve manager username" );
            }
        }
    }

    private void updateManagerName() throws RemoteException {
        String managerName = userService.getManagerUsername();
        if (managerName != null && !managerName.equals(managerTextArea.getText())) {
            managerTextArea.setText(managerName);
        }
    }

    private void updateUserList() throws RemoteException {
        StringBuilder newUserText = new StringBuilder();
        for (String username : userService.listUsername()) {
            newUserText.append(username).append("\n");
        }
        if (!newUserText.toString().equals(userTextArea.getText())) {
            userTextArea.setText(newUserText.toString());
        }
    }
}
