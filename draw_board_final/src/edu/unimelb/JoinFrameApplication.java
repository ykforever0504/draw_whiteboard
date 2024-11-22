package edu.unimelb;

import edu.unimelb.board.DrawFrameApplication;
import edu.unimelb.common.constant.RegistryConstant;
import edu.unimelb.common.util.ClientConnectionUtils;
import edu.unimelb.common.util.DialogUtils;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.service.IRemoteUserService;
import edu.unimelb.thread.UserThread;

import javax.swing.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * join whiteboard launch class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class JoinFrameApplication {

    private static String serverAddress;

    private static int serverPort;

    private static String username;

    public static void main(String[] args) throws Exception {
        // Optimize UI effect
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        if (args.length != 3) {
            System.out.println("Usage: JoinWhiteBoard <serverIPAddress> <serverPort> <username>");
            System.exit(0);
        }
        try{
            serverAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
            username = args[2];
        } catch (NumberFormatException e){
            System.out.println("The port number must be an integer");
        }

        username = DialogUtils.showUsernameDialog(username);
        // Create a non-administrator whiteboard
        DrawFrameApplication app = new DrawFrameApplication(false);
        try {

            ConnectionSocket connectionSocket = new ConnectionSocket(serverAddress, serverPort);
            // Connect to the locally registered RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 8888);

            // Find a registered remote management class
            IRemoteUserService remoteUserService = (IRemoteUserService) registry.lookup(RegistryConstant.REMOTE_USER_SERVICE);
            app.setRemoteUserService(remoteUserService);

            ClientConnectionUtils connectionUtils = new ClientConnectionUtils(connectionSocket);
            app.setClientConnection(connectionUtils);
            Thread t = new Thread() {
                @Override
                public void run() {
                    super.run();
                    DialogUtils.showInfoDialog("Wait for the manager to approve your request to join");
                }
            };
            t.start();
            connectionUtils.connect(app, username);
            app.showDrawBoard();

            while (true) {
                String request = connectionSocket.receive();
                UserThread kickOutThread = new UserThread(app, request);
                kickOutThread.start();
            }
        } catch (RemoteException | NotBoundException e) {
            DialogUtils.showNoServerConnectionDialog();
        } catch (IOException e) {
            System.out.println("disconnect.");
        }
    }

}
