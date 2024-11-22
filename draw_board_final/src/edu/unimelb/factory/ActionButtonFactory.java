package edu.unimelb.factory;

import edu.unimelb.common.enums.ActionTypeEnum;
import edu.unimelb.template.impl.*;

import javax.swing.*;

/**
 * operation button factory
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/11
 */
public class ActionButtonFactory {

    /**
     * create the corresponding button based on the type of operation
     *
     * @param actionTypeEnum Operation button enumeration
     * @return Action button instance
     */
    public static JButton getInstance(ActionTypeEnum actionTypeEnum) {
        switch (actionTypeEnum) {

            case BRUSH: return new BrushTypeButton().getButton(actionTypeEnum);

            case TEXT: return new TextTypeButton().getButton(actionTypeEnum);

            case LINE: return new LineTypeButton().getButton(actionTypeEnum);

            case RECTANGLE: return new RectangleTypeButton().getButton(actionTypeEnum);

            case ERASERS: return new EraserSTypeButton().getButton(actionTypeEnum);

            case ERASERL: return new EraserLTypeButton().getButton(actionTypeEnum);

            case ELLIPSE: return new EllipseTypeButton().getButton(actionTypeEnum);

            case COLOR: return new ColorTypeButton().getButton(actionTypeEnum);

            case CIRCLE: return new CircleTypeButton().getButton(actionTypeEnum);

            default: throw new IllegalStateException("Invalid enumeration: " + actionTypeEnum);
        }
    }
}
