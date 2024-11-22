package edu.unimelb;

import edu.unimelb.common.constant.RegistryConstant;
import edu.unimelb.common.util.DialogUtils;
import edu.unimelb.common.util.UserManagerUtils;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.service.IRemoteUserService;
import edu.unimelb.service.impl.RemoteUserServiceImpl;
import edu.unimelb.thread.ClientRequestThread;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Service initiation class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class ServerApplication {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ServerApplication <port>");
            System.exit(1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65535) {
                System.out.println("Error: Port must be between 1 and 65535.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Port must be an integer.");
            System.exit(1);
        }


        UserManagerUtils userManagerUtils = new UserManagerUtils();
        try {
            IRemoteUserService remoteUserService = new RemoteUserServiceImpl();

            Registry registry = LocateRegistry.createRegistry(8888);

            // Bind the remote user action class
            registry.bind(RegistryConstant.REMOTE_USER_SERVICE, remoteUserService);
            userManagerUtils.setRemoteUserService(remoteUserService);

            // Start the user management page
            new UserListApplication(remoteUserService);
        } catch (AlreadyBoundException e) {
            DialogUtils.showErrorDialog("RMI have already registered");
            System.exit(1);
        } catch (AccessException e) {
            DialogUtils.showErrorDialog("RMI register failed");
            System.exit(1);
        } catch (RemoteException e) {
            DialogUtils.showErrorDialog("RMI connect failed");
            System.exit(1);
        }

        // start service
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try(ServerSocket server = factory.createServerSocket(port)) {
            while (true) {
                try{
                    // Start a new thread
                    Socket client = server.accept();

                    ClientRequestThread clientRequestsThread = new ClientRequestThread(new ConnectionSocket(client), userManagerUtils);
                    clientRequestsThread.start();
                }catch (IOException e){
                    System.out.println("Receive connection failure: ");
                    break;
                }

            }
        } catch (IOException e) {
            System.out.println("create Socket service failed");
        }
    }
}
