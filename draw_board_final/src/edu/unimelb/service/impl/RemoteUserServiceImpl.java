package edu.unimelb.service.impl;

import edu.unimelb.service.IRemoteUserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Remote user operation service implementation class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class RemoteUserServiceImpl extends UnicastRemoteObject implements IRemoteUserService {

    /**
     *  manager
     */
    private String manageUsername;

    /**
     * Connection userlist
     */
    private List<String> usernames;

    public RemoteUserServiceImpl() throws RemoteException {
        usernames = new ArrayList<>();
    }

    /**
     * add a user
     *
     * @param username
     * @throws RemoteException
     */
    @Override
    public void addUser(String username) throws RemoteException {
        usernames.add(username);
    }

    /**
     * Gets a collection of remote connected users
     *
     * @return remote connected user lists
     */
    @Override
    public List<String> listUsername() throws RemoteException {
        return usernames;
    }

    /**
     * Get manager name
     *
     * @return Share the whiteboard manager name
     */
    @Override
    public String getManagerUsername() throws RemoteException {
        return manageUsername;
    }

    /**
     * set manager username
     *
     * @param username
     */
    @Override
    public void setManagerUsername(String username) throws RemoteException {
        this.manageUsername = username;
    }
}
