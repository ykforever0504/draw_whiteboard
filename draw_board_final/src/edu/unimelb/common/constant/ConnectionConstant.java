package edu.unimelb.common.constant;

/**
 * Joins related global static constants
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class ConnectionConstant {

    /**
     * User unique tracking value
     */
    public static final String TRACE_ID = "trace_id";

    /**
     * Request-response
     */
    public static final String RESPONSE = "response";

    /**
     * user request
     */
    public static final String REQUEST = "request";

    /**
     * username
     */
    public static final String USERNAME = "username";

    /**
     * operating result
     */
    public static final String RESULT = "result";

    /**
     * has a existed manager
     */
    public static final String HAS_MANAGER = "There is already a manager.";

    /**
     * no manager rights
     */
    public static final String NO_MANAGER = "There is no manager.";

    /**
     * Apply for a shared whiteboard connection
     */
    public static final String JOIN_DRAW_BOARD_REQUEST = "join_draw_board_request";

    /**
     * apply to join
     */
    public static final String JOIN_DRAW_BOARD_RESULT = "join_draw_board_result";

    /**
     * create board successfully
     */
    public static final String CREATE_DRAW_BOARD_SUCCESS = "create_draw_board_success";

    /**
     * join successfully
     */
    public static final String JOIN_DRAW_BOARD_SUCCESS = "join_draw_board_success";

    /**
     * close board
     */
    public static final String CLOSE_DRAW_BOARD = "close_draw_board";

    /**
     * user close actively
     */
    public static final String LEAVE_DRAW_BOARD = "leave_draw_board";

    /**
     * manager reject user join
     */
    public static final String MANAGER_REJECTED_REQUEST = "manager_rejected_request";

    /**
     * kickout user
     */
    public static final String USER_KICK_OUT = "user_kick_out";

    /**
     * create board
     */
    public static final String CREATE_DRAW_BOARD = "create_draw_board";

    /**
     * create a new board
     */
    public static final String MANAGER_CREATE_NEW_BOARD = "manager_create_new_board";

    /**
     * create open a new board
     */
    public static final String MANAGER_OPEN_BOARD = "manager_open_board";

    /**
     * close the board
     */
    public static final String MANAGER_CLOSE_BOARD = "manager_create_close_board";

    /**
     * draw operation
     */
    public static final String OPERATE_DRAW = "operate_draw";

    /**
     * sync the draw operation
     */
    public static final String SYNC_DRAW_BOARD = "sync_draw_board";

}
