package edu.unimelb.entity;

import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static edu.unimelb.common.constant.ConnectionConstant.*;
import static edu.unimelb.common.util.DialogUtils.showNoServerConnectionDialog;

/**
 * Connect communication entity class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class ConnectionSocket {

    /**
     * Socket connection to the server
     */
    private Socket socket;

    /**
     * An input stream of data received from the server
     */
    private DataInputStream inputStream;

    /**
     * An output stream of data sent to the server
     */
    private DataOutputStream outputStream;

    public ConnectionSocket(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // Throw no connection exception
            showNoServerConnectionDialog();
        }
    }

    public ConnectionSocket(Socket socket) {
        try {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to create a connection");
        }
    }

    /**
     * send message
     *
     * @param message content
     * @throws IOException
     */
    public void send(String message) throws IOException {
        outputStream.writeUTF(message);
        // Refresh cache
        outputStream.flush();
    }

    /**
     * Receives messages from a Socket connection
     *
     * @return return message
     * @throws IOException io exception
     */
    public String receive() throws IOException {
        // Read the message from the input stream
        String receive = inputStream.readUTF();
        return receive;
    }

    /**
     * close Socket connection
     *
     * @throws IOException io exception
     */
    public void close() throws IOException {
        socket.close();
        inputStream.close();
        outputStream.close();
    }

    /**
     * Send an administrator response message
     *
     * @throws IOException
     */
    public void responseHasManager() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESPONSE, HAS_MANAGER);
        send(jsonObject.toString());
    }

    /**
     * Sends non-administrator response messages
     */
    public void responseNoManager() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESPONSE, NO_MANAGER);
        send(jsonObject.toString());
    }

    /**
     * Whiteboard successfully created
     *
     * @param traceId
     * @throws IOException
     */
    public void successCreateDrawBoard(String traceId) throws IOException {
        JSONObject jsonObject = new JSONObject();
        // Add whiteboard request and user tracking values in JSON
        jsonObject.put(RESPONSE, CREATE_DRAW_BOARD_SUCCESS);
        jsonObject.put(TRACE_ID, traceId);
        // send response
        send(jsonObject.toString());
    }

    /**
     * Request permission from the manager to join the shared whiteboard
     *
     * @param traceId
     * @throws IOException
     */
    public void requestManagerAccept(String traceId) throws IOException {
        JSONObject jsonObject = new JSONObject();
        // Request to join a shared whiteboard
        jsonObject.put(REQUEST, JOIN_DRAW_BOARD_REQUEST);
        jsonObject.put(TRACE_ID, traceId);

        send(jsonObject.toString());
    }

    /**
     * Send added whiteboard results
     *
     * @param typeKey JSON Message Key
     * @param type message type
     * @param result message result
     * @param traceId
     */
    public void getJoinRequestResult(String typeKey, String type, boolean result, String traceId) throws IOException {
        JSONObject jsonObject = new JSONObject();
        // Package operation result
        jsonObject.put(typeKey, type);
        jsonObject.put(TRACE_ID, traceId);
        jsonObject.put(RESULT, Boolean.toString(result));

        send(jsonObject.toString());
    }

    /**
     * Send manager operation
     *
     * @param operate manager
     */
    public void sendManagerOperate(String operate) throws IOException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(REQUEST, operate);

        send(jsonObject.toString());
    }

    /**
     * Send a kick request
     *
     * @throws IOException
     */
    public void sendKickOutRequest() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REQUEST, USER_KICK_OUT);
        send(jsonObject.toJSONString());
    }

    public void sendDrawRequest(JSONObject jsonObject) throws IOException {
        send(jsonObject.toJSONString());
    }

    public void sendDrawSyncRequest() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REQUEST, SYNC_DRAW_BOARD);
        send(jsonObject.toJSONString());
    }

    /**
     * Whether the current connection is closed
     *
     * @return true-closed
     */
    public boolean isClosed() {
        return socket.isClosed();
    }
}
