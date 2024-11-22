package edu.unimelb.template;

import edu.unimelb.common.enums.ActionTypeEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

/**
 * Operation button template
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/11
 */
public abstract class AbstractTypeButton {

    /**
     * init button
     *
     * @param actionType Button type enumeration
     * @return button instance
     */
    protected JButton initButton(ActionTypeEnum actionType) {
        // Only action buttons can be instantiated
        if (ActionTypeEnum.isChooseButton(actionType)) {
            // Set basic button information
            JButton button = new JButton();
            // settings icon
            String iconPath = setButtonIcon(button, actionType);
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                button.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        ImageIcon scaledIcon = scaleIconToButton(button, icon);
                        button.setIcon(scaledIcon);
                    }
                });
            }
            // Defines the fixed size of the button
            Dimension buttonSize = new Dimension( 66, 50);
            // Applied fixed size
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);

            return button;
        }
        return null;
    }

    /**
     * Set button icon
     *
     * @param button     Button instance
     * @param actionType Operation type enumeration
     */
    public abstract String setButtonIcon(JButton button, ActionTypeEnum actionType);

    /**
     * Get button object
     *
     * @param actionType button type
     * @return button object
     */
    public abstract JButton getButton(ActionTypeEnum actionType);

    public ImageIcon scaleIconToButton(JButton button, ImageIcon icon) {
        int buttonWidth = button.getWidth();
        int buttonHeight = button.getHeight();
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();

        // Calculated scaling
        float scale = Math.min((float) buttonWidth / iconWidth, (float) buttonHeight / iconHeight) * 0.8f;

        // Maintain image aspect ratio
        int newIconWidth = Math.round(iconWidth * scale);
        int newIconHeight = Math.round(iconHeight * scale);

        // Generate a new zoom image
        Image scaledImage = icon.getImage().getScaledInstance(newIconWidth, newIconHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

}
