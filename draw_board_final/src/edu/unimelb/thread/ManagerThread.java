package edu.unimelb.thread;

import edu.unimelb.board.DrawFrameApplication;
import edu.unimelb.entity.ConnectionSocket;
import edu.unimelb.entity.DrawPosition;
import edu.unimelb.common.constant.ConnectionConstant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.Vector;

/**
 * manager operation thread
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class ManagerThread extends Thread {

    private final DrawFrameApplication drawFrameApplication;

    private final String request;

    private final ConnectionSocket connectionSocket;

    public ManagerThread (DrawFrameApplication app, String request, ConnectionSocket connectionSocket) {
        this.drawFrameApplication = app;
        this.request = request;
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        super.run();

        JSONObject jsonObject;
        JSONParser jsonParser = new JSONParser();

        // send responses
        try {
            jsonObject = (JSONObject) jsonParser.parse(request);

            String requestType = (String) jsonObject.get(ConnectionConstant.REQUEST);

            if (requestType.equals(ConnectionConstant.JOIN_DRAW_BOARD_REQUEST)) {
                String waitTraceId = (String) jsonObject.get(ConnectionConstant.TRACE_ID);
                boolean result = drawFrameApplication.askAcceptWait(waitTraceId);
                connectionSocket.getJoinRequestResult(ConnectionConstant.REQUEST, ConnectionConstant.JOIN_DRAW_BOARD_RESULT, result, waitTraceId);
            } else if (requestType.equals(ConnectionConstant.OPERATE_DRAW)) {
                // drawback
                String data = jsonObject.get("data").toString();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(data);
                Vector<DrawPosition> drawTrack = new Vector<>();
                for (Object object : jsonArray) {
                    JSONObject json = (JSONObject) object;
                    DrawPosition drawPosition = new DrawPosition();
                    drawPosition.x = ((Long) json.get("x")).intValue();
                    drawPosition.y = ((Long) json.get("y")).intValue();
                    drawPosition.type = ((Long) json.get("type")).intValue();
                    drawPosition.s = json.get("s").toString();
                    drawPosition.color = getColorFromJson(((Long) json.get("color_red")).intValue(), ((Long) json.get("color_green")).intValue(), ((Long) json.get("color_blue")).intValue());
                    drawTrack.add(drawPosition);
                }
                drawFrameApplication.getDrawPanel().repaintDraw(drawTrack);
            } else if (requestType.equals(ConnectionConstant.SYNC_DRAW_BOARD)) {
                // Synchronous primary operation
                drawFrameApplication.getDrawPanel().syncDrawBoard();
            } else {

            }
        } catch (ParseException e) {

        } catch (IOException e) {

        }

    }

    private Color getColorFromJson(int red, int green, int blue) {
        return new Color(red, green, blue);
    }
}
