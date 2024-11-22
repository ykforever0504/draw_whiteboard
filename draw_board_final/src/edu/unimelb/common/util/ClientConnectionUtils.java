package edu.unimelb.common.util;

import edu.unimelb.board.DrawFrameApplication;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.entity.DrawPosition;
import edu.unimelb.common.constant.ConnectionConstant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Vector;

/**
 * Client connection tool class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class ClientConnectionUtils {

    private ConnectionSocket socket;

    public ClientConnectionUtils(ConnectionSocket socket) {
        this.socket = socket;
    }

    /**
     * @param app Share whiteboard form
     * @param username
     */
    public void connect(DrawFrameApplication app, String username) {
        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();

        if (app.isManager()) {
            // Create an whiteboard if the current user is a manager
            jsonObject.put(ConnectionConstant.REQUEST, ConnectionConstant.CREATE_DRAW_BOARD);
            jsonObject.put(ConnectionConstant.USERNAME, username);
        } else {
            // apply to join the drawing board
            jsonObject.put(ConnectionConstant.REQUEST, ConnectionConstant.JOIN_DRAW_BOARD_REQUEST);
            jsonObject.put(ConnectionConstant.USERNAME, username);
        }

        String response = "";
        try {
            socket.send(jsonObject.toJSONString());
            // Permission to share board or refuse to share board
            response = socket.receive();
            jsonObject = (JSONObject) parser.parse(response);

            String responseType = (String) jsonObject.get(ConnectionConstant.RESPONSE);

            switch (responseType) {
                // is manager
                case ConnectionConstant.HAS_MANAGER: app.error(responseType); break;
                // create board successfully
                case ConnectionConstant.CREATE_DRAW_BOARD_SUCCESS: app.start((String) jsonObject.get(ConnectionConstant.TRACE_ID)); break;
                // manager rejected request
                case ConnectionConstant.MANAGER_REJECTED_REQUEST: app.error("The manager refused your request."); System.exit(0); break;
                // join the board successfully
                case ConnectionConstant.JOIN_DRAW_BOARD_SUCCESS: app.start((String) jsonObject.get(ConnectionConstant.TRACE_ID)); break;
                // Does not have manager rights
                case ConnectionConstant.NO_MANAGER: app.error("There is no manager-opened board"); break;
                default:
                    break;
            }
        } catch (IOException e) {
            DialogUtils.showNoServerConnectionDialog();
        } catch (ParseException e) {
            System.out.println("Wrong response: " + response);
        }
    }

    /**
     * @param isManager
     * @param traceId
     */
    public void disconnect(boolean isManager, String traceId) {

        JSONObject jsonObject = new JSONObject();

        if (isManager) {
            // The manager closes the shared whiteboard.
            jsonObject.put(ConnectionConstant.REQUEST, ConnectionConstant.CLOSE_DRAW_BOARD);
            jsonObject.put(ConnectionConstant.TRACE_ID, traceId);

        } else {
            // user-initiated departure
            jsonObject.put(ConnectionConstant.REQUEST, ConnectionConstant.LEAVE_DRAW_BOARD);
            jsonObject.put(ConnectionConstant.TRACE_ID, traceId);
        }

        try {
            socket.send(jsonObject.toJSONString());
            socket.close();
        } catch (IOException e) {
            DialogUtils.showNoServerConnectionDialog();
        }
    }

    /**
     * @param traceId
     */
    public void kickOut(String traceId) {
        JSONObject obj = new JSONObject();

        obj.put(ConnectionConstant.REQUEST, ConnectionConstant.USER_KICK_OUT);
        obj.put(ConnectionConstant.TRACE_ID, traceId);

        try {
            socket.send(obj.toJSONString());
        } catch (IOException e) {
            DialogUtils.showNoServerConnectionDialog();
        }
    }


    public void sendDrawOperate(Vector<DrawPosition> drawTrack, boolean isManage) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ConnectionConstant.REQUEST, ConnectionConstant.OPERATE_DRAW);
        JSONArray jsonArray = new JSONArray();
        if(!drawTrack.isEmpty()){
            for (int i = 0; i < drawTrack.size(); i++) {
                JSONObject entity = new JSONObject();
                DrawPosition drawPosition = drawTrack.get(i);
                entity.put("x", drawPosition.x);
                entity.put("y", drawPosition.y);
                entity.put("type", drawPosition.type);
                entity.put("s", drawPosition.s);
                entity.put("color_red", drawPosition.color.getRed());
                entity.put("color_green", drawPosition.color.getGreen());
                entity.put("color_blue", drawPosition.color.getBlue());
                jsonArray.add(entity);
            }
        }
        jsonObject.put("data", jsonArray.toJSONString());
        jsonObject.put("isManager", isManage);
        socket.send(jsonObject.toString());
    }

}
