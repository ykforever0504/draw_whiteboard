package edu.unimelb.thread;

import edu.unimelb.common.util.UserManagerUtils;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.common.constant.ConnectionConstant;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * User request thread
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class ClientRequestThread extends Thread {

    /**
     * client Socket connection
     */
    private ConnectionSocket connectionSocket;

    /**
     * The manager operates the whiteboard tool class
     */
    private UserManagerUtils userManagerUtils;

    public ClientRequestThread (ConnectionSocket connectionSocket, UserManagerUtils userManagerUtils) {
        this.connectionSocket = connectionSocket;
        this.userManagerUtils = userManagerUtils;
    }

    @Override
    public void run() {
        super.run();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();

        String request;
        while (true) {
            try {
                // Receiving a JSON message from a socket
                request = connectionSocket.receive();
                try {
                    jsonObject = (JSONObject) parser.parse(request);
                } catch (ParseException e) {
                    interrupt();
                }

                String requestType = (String) jsonObject.get(ConnectionConstant.REQUEST);
                // send response
                switch (requestType) {
                    // Create a shared whiteboard
                    case ConnectionConstant.CREATE_DRAW_BOARD:
                        // has manager
                        if (userManagerUtils.hasManager()) {
                            connectionSocket.responseHasManager();
                        } else {
                            // get unique value
                            String traceId = userManagerUtils.addUser((String) jsonObject.get(ConnectionConstant.USERNAME));
                            userManagerUtils.setManagerTraceId(traceId);
                            userManagerUtils.addUserSocket(traceId, connectionSocket);
                            // create successfully
                            connectionSocket.successCreateDrawBoard(traceId);
                        }
                        break;
                    // request to join
                    case ConnectionConstant.JOIN_DRAW_BOARD_REQUEST:
                        ConnectionSocket managerSocket = userManagerUtils.getManagerSocket();
                        // If the current user is not a manager
                        if (!userManagerUtils.hasManager()) {
                            connectionSocket.responseNoManager();
                            break;
                        }
                        // Gets the unique value of the waiting user
                        String traceId = userManagerUtils.addWaitUser((String) jsonObject.get(ConnectionConstant.USERNAME));
                        userManagerUtils.addUserSocket(traceId, connectionSocket);
                        // Request permission from the manager to join the shared whiteboard
                        managerSocket.requestManagerAccept(traceId);
                        break;
                    // Result of applying to join a shared whiteboard
                    case ConnectionConstant.JOIN_DRAW_BOARD_RESULT:
                        // Gets the value waiting to be tracked
                        String waitTraceId = (String) jsonObject.get(ConnectionConstant.TRACE_ID);
                        boolean result = Boolean.parseBoolean((String) jsonObject.get(ConnectionConstant.RESULT));
                        ConnectionSocket waitUserConnectionSocket = userManagerUtils.getConnectionSocket(waitTraceId);
                        if (result) {
                            waitUserConnectionSocket.getJoinRequestResult(ConnectionConstant.RESPONSE, ConnectionConstant.JOIN_DRAW_BOARD_SUCCESS, result, waitTraceId);
                            userManagerUtils.acceptWaitUser(waitTraceId);
                            userManagerUtils.sendDrawSyncRequest();
                        } else {
                            waitUserConnectionSocket.getJoinRequestResult(ConnectionConstant.RESPONSE, ConnectionConstant.MANAGER_REJECTED_REQUEST, result, waitTraceId);
                            userManagerUtils.rejectWaitUser(waitTraceId);
                        }
                        break;
                    // The user actively closes the shared whiteboard
                    case ConnectionConstant.LEAVE_DRAW_BOARD:
                        String userTraceId = (String) jsonObject.get(ConnectionConstant.TRACE_ID);
                        userManagerUtils.removeUser(userTraceId);
                        break;
                    // manager close whiteboard
                    case ConnectionConstant.CLOSE_DRAW_BOARD:
                        String userMTraceId = (String) jsonObject.get(ConnectionConstant.TRACE_ID);
                        userManagerUtils.removeManager(userMTraceId);
                        userManagerUtils.broadcastManagerOperate(ConnectionConstant.MANAGER_CLOSE_BOARD);
                        userManagerUtils.clear();
                        break;
                    // manager kick out user
                    case ConnectionConstant.USER_KICK_OUT:
                        String kickOutTraceId = (String) jsonObject.get(ConnectionConstant.TRACE_ID);
                        ConnectionSocket kickOutSocket = userManagerUtils.getConnectionSocket(kickOutTraceId);
                        if (kickOutSocket != null) {
                            System.out.println("    |send kick out request to " + ConnectionConstant.TRACE_ID);
                            kickOutSocket.sendKickOutRequest();
                        } else {
                            System.out.println("    |kick out non-existing uer: " + ConnectionConstant.TRACE_ID);
                        }
                        userManagerUtils.removeUser(kickOutTraceId);
                        break;
                    // manager create a new panel
                    case ConnectionConstant.MANAGER_CREATE_NEW_BOARD:
                        userManagerUtils.broadcastManagerOperate(ConnectionConstant.MANAGER_CREATE_NEW_BOARD);
                        break;
                    // manager open panel
                    case ConnectionConstant.MANAGER_OPEN_BOARD:
                        userManagerUtils.broadcastManagerOperate(ConnectionConstant.MANAGER_OPEN_BOARD);
                        break;
                    // The manager synchronizes the user drawing after drawing
                    case ConnectionConstant.OPERATE_DRAW:
                        // Broadcast manager actions for all users
                        userManagerUtils.broadcastManagerDraw(jsonObject);
                        break;
                }
            } catch (IOException e) {

                break;
            }
        }
    }
}
