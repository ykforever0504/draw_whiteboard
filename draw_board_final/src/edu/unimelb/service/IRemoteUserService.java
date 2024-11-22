package edu.unimelb.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote users operate service logic
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public interface IRemoteUserService extends Remote {

    /**
     * add a user
     *
     * @param username
     * @throws RemoteException
     */
    void addUser(String username) throws RemoteException;

    /**
     * Gets a collection of remote connected users
     *
     * @return user list
     */
    List<String> listUsername() throws RemoteException;

    /**
     * Get manager name
     *
     * @return Share the whiteboard manager name
     */
    String getManagerUsername() throws RemoteException;

    /**
     * set manager username
     *
     * @param username
     */
    void setManagerUsername(String username) throws RemoteException;
    
}
