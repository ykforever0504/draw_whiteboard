package edu.unimelb.thread;

import edu.unimelb.board.DrawFrameApplication;
import edu.unimelb.entity.DrawPosition;
import edu.unimelb.common.constant.ConnectionConstant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.util.Vector;

/**
 * User action thread
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class UserThread extends Thread {

    private final DrawFrameApplication drawFrameApplication;

    private final String request;

    public UserThread (DrawFrameApplication app, String request) {
        this.drawFrameApplication = app;
        this.request = request;
    }

    @Override
    public void run() {
        super.run();

        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();

        // send responses
        try {
            jsonObject = (JSONObject) jsonParser.parse(request);

            String requestType = (String) jsonObject.get(ConnectionConstant.REQUEST);

            switch (requestType) {
                case ConnectionConstant.USER_KICK_OUT:
                    drawFrameApplication.kickedOut();
                    break;
                case ConnectionConstant.MANAGER_CLOSE_BOARD:
                    drawFrameApplication.closeByManager();
                    break;
                case ConnectionConstant.MANAGER_CREATE_NEW_BOARD:
                    drawFrameApplication.notifyUser(ConnectionConstant.MANAGER_CREATE_NEW_BOARD);
                    break;
                case ConnectionConstant.MANAGER_OPEN_BOARD:
                    drawFrameApplication.notifyUser(ConnectionConstant.MANAGER_OPEN_BOARD);
                    break;
                // update panel
                case ConnectionConstant.OPERATE_DRAW:
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
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {

        }
    }

    private Color getColorFromJson(int red, int green, int blue) {
        return new Color(red, green, blue);
    }
}
