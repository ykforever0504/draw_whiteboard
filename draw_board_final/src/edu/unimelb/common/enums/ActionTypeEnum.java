package edu.unimelb.common.enums;

/**
 * Operation type enumeration class
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/11
 */
public enum ActionTypeEnum {

    NONE(-1),

    COLOR(-2),

    BRUSH(0),

    TEXT(1),

    LINE(2),

    RECTANGLE(3),

    ELLIPSE(5),

    ERASERL(6),

    ERASERS(7),

    CIRCLE(8);


    /**
     * operation code
     */
    private final int code;

    public Integer getCode() {
        return code;
    }


    ActionTypeEnum(Integer code) {
        this.code = code;
    }

    /**
     * Check whether it is an operation button type
     *
     * @param actionType Type of button to determine
     * @return true-Need to operate button，false-no need to operate button
     */
    public static Boolean isChooseButton(ActionTypeEnum actionType) {
        return !actionType.equals(ActionTypeEnum.NONE);
    }


}
