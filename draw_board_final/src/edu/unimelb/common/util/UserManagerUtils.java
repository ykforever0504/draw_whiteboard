package edu.unimelb.common.util;

import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.service.IRemoteUserService;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * User management tools
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class UserManagerUtils {

    private int userCount = 0;

    /**
     * User Socket Connection Pool
     */
    private Map<String, ConnectionSocket> usersSocket;

    /**
     * manager trace unique value
     */
    private String managerTraceId;

    /**
     * Remotely connect the user service logic
     */
    private IRemoteUserService remoteUserService;

    /**
     * Waiting to connect user collection
     */
    private List<String> waitUserList;

    public UserManagerUtils() {
        this.usersSocket = new HashMap<>();
        this.waitUserList = new ArrayList<>();
    }

    /**
     * Add a new user
     *
     * @param username
     * @return User unique identification
     */
    public synchronized String addUser(String username) {
        String traceId = String.format("%s [%d]", username, userCount);

        try {
            // If the manager ID is empty, indicating that there is no manager, set the current user as the manager
            if (Objects.isNull(managerTraceId)) {
                managerTraceId = traceId;
                remoteUserService.setManagerUsername(traceId);
            } else {
                // add a common user
                remoteUserService.addUser(traceId);
            }
        } catch (RemoteException e) {
            System.out.println("something");
        }
        // total number of users + 1
        userCount++;
        return traceId;
    }

    /**
     * Add waiting user
     * 
     * @param username wait username
     * @return an unique id for candidate user
     */
    public synchronized String addWaitUser(String username) {
        String traceId = String.format("%s [%d]", username, userCount);
        waitUserList.add(traceId);
        userCount++;
        return traceId;
    }

    /**
     * Accept the current waiting user
     *
     * @param traceId User unique tracking value
     */
    public synchronized void acceptWaitUser(String traceId) {
        // If the current user exists in the user wait set
        if (waitUserList.contains(traceId)) {
            // remove current user
            waitUserList.remove(traceId);
            try {
                // add online user
                remoteUserService.addUser(traceId);
            } catch (RemoteException e) {
                System.out.println("something");
            }
        }
    }

    /**
     * Refuse to wait user
     *
     * @param traceId
     */
    public synchronized void rejectWaitUser(String traceId) {
        if (waitUserList.contains(traceId)) {
            waitUserList.remove(traceId);
            // Remove the current user connection
            usersSocket.remove(traceId);
        }
    }

    public void setRemoteUserService(IRemoteUserService remoteUserService) {
        this.remoteUserService = remoteUserService;
    }

    /**
     * remove user
     *
     * @param traceId 
     */
    public synchronized void removeUser(String traceId) {
        try {
            // Removes the current user based on a unique value
            remoteUserService.listUsername().remove(traceId);
        } catch (RemoteException e) {
            System.out.println("something");
        }
        // Close the Socket connection based on the unique value
        if (usersSocket.containsKey(traceId)) {
            try {
                usersSocket.get(traceId).close();
            } catch (IOException e) {
                System.out.println("something");
            }
            usersSocket.remove(traceId);
        }
    }
    /**
     * remove manager
     *
     * @param traceId 
     */
    public synchronized void removeManager(String traceId) {
        try {
            // Removes the current user based on a unique value
            remoteUserService.setManagerUsername("");
        } catch (RemoteException e) {
            System.out.println("something");
        }
        // Close the Socket connection based on the unique value
        if (usersSocket.containsKey(traceId)) {
            try {
                usersSocket.get(traceId).close();
            } catch (IOException e) {
                System.out.println("something");
            }
            usersSocket.remove(traceId);
        }
    }
    /**
     * Adding User Socket Connection
     *
     * @param traceId
     * @param socket
     */
    public synchronized void addUserSocket(String traceId, ConnectionSocket socket) {
        // If the current connection does not exist, add it
        if (!usersSocket.containsKey(traceId)) {
            usersSocket.put(traceId, socket);
        } else {
            System.out.println("The current user connection already exists: " + traceId);
        }
    }

    /**
     * Get Manager Connection Socket
     *
     * @return Manager Socket Connection
     */
    public synchronized ConnectionSocket getManagerSocket() {
        return usersSocket.get(managerTraceId);
    }

    /**
     * Gets the user connection entity class
     *
     * @param traceId
     * @return User Connection Object
     */
    public synchronized ConnectionSocket getConnectionSocket(String traceId) {
        return usersSocket.getOrDefault(traceId, null);
    }

    public synchronized void setManagerTraceId(String managerTraceId) {
        this.managerTraceId = managerTraceId;
    }

    public synchronized boolean hasManager() {
        return managerTraceId != null;
    }

    /**
     * clear users
     */
    public synchronized void clear() {
        userCount = 0;
        usersSocket.clear();
        managerTraceId = null;
        try {
            remoteUserService.listUsername().clear();
        } catch (RemoteException e) {
            System.out.println("something wrong");
        }
        waitUserList.clear();
    }

    /**
     * Broadcast manager operation
     *
     * @param operate manager operation
     */
    public synchronized void broadcastManagerOperate(String operate) {
        for (Map.Entry<String, ConnectionSocket> entry: usersSocket.entrySet()) {
            // get the unique id of current user
            String traceId = entry.getKey();
            ConnectionSocket connectionSocket = entry.getValue();
            // if the connection is closed
            if (connectionSocket.isClosed()) {
                System.out.println(traceId + " socket has closed");
            } else if (!traceId.equals(managerTraceId)) {
                 try {
                    //Send manager operation data
                    connectionSocket.sendManagerOperate(operate);
                } catch (IOException e) {
                    System.out.println("Socket happens error.");
                }
            }
        }
    }

    public synchronized void broadcastManagerDraw(JSONObject jsonObject) throws IOException {
        for (Map.Entry<String, ConnectionSocket> entry: usersSocket.entrySet()) {
            // Gets the current user unique value
            String traceId = entry.getKey();
            ConnectionSocket connectionSocket = entry.getValue();
            // If the current connection is closed
            if (connectionSocket.isClosed()) {
                System.out.println(traceId + " socket has closed");
            // Broadcast only when the current connection is not an administrator connection
            } else {
                // Broadcast all operations
                connectionSocket.sendDrawRequest(jsonObject);
            }
        }
    }

    public synchronized void sendDrawSyncRequest() throws IOException {
        for (Map.Entry<String, ConnectionSocket> entry: usersSocket.entrySet()) {
            // Gets the current user unique value
            String traceId = entry.getKey();
            ConnectionSocket connectionSocket = entry.getValue();
            // If the current connection is closed
            if (connectionSocket.isClosed()) {
                System.out.println(traceId + " socket has closed");
                // Broadcast only when the current connection is not an administrator connection
            } else {
                // Broadcast a synchronization operation for all users
                connectionSocket.sendDrawSyncRequest();
            }
        }
    }

}
