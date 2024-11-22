package edu.unimelb.template.impl;

import edu.unimelb.common.enums.ActionTypeEnum;
import edu.unimelb.template.AbstractTypeButton;

import javax.swing.*;

/**
 * Color selection button
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/12
 */
public class ColorTypeButton extends AbstractTypeButton {

    /**
     * Set button icon
     *
     * @param button     Button instance
     * @param actionType Operation type enumeration
     */
    @Override
    public String setButtonIcon(JButton button, ActionTypeEnum actionType) {
        String iconPath = "/lib/icon/color.png";
        return iconPath;
    }

    /**
     * Get button object
     *
     * @param actionType button type
     * @return button object
     */
    @Override
    public JButton getButton(ActionTypeEnum actionType) {
        return initButton(actionType);
    }
}
