package edu.unimelb;

import edu.unimelb.board.DrawFrameApplication;
import edu.unimelb.common.constant.RegistryConstant;
import edu.unimelb.common.util.ClientConnectionUtils;
import edu.unimelb.common.util.DialogUtils;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.service.IRemoteUserService;
import edu.unimelb.thread.ManagerThread;

import javax.swing.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Create a project launch class by manager
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class CreateFrameApplication {

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
            System.out.println("Usage: CreateWhiteBoard <serverIPAddress> <serverPort> <username>");
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
        // manager create thread
        DrawFrameApplication app = new DrawFrameApplication(true);
        try {
            // Connect to local RMI registration
            Registry registry = LocateRegistry.getRegistry("localhost", 8888);

            // create Socket connection
            ConnectionSocket socket = new ConnectionSocket(serverAddress, serverPort);

            // Get the remote user operation interface
            IRemoteUserService remoteUserService = (IRemoteUserService) registry.lookup(RegistryConstant.REMOTE_USER_SERVICE);
            app.setRemoteUserService(remoteUserService);

            ClientConnectionUtils connectionUtils = new ClientConnectionUtils(socket);
            app.setClientConnection(connectionUtils);
            connectionUtils.connect(app, username);
            app.showDrawBoard();

            while (true) {
                String request = socket.receive();
                ManagerThread joinRequestThread = new ManagerThread(app, request, socket);
                joinRequestThread.start();
            }
        } catch (IllegalArgumentException | RemoteException | NotBoundException e ) {
            DialogUtils.showNoServerConnectionDialog();
        } catch (IOException e) {
        }
    }

}

