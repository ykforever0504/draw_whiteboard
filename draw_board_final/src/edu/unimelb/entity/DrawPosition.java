package edu.unimelb.entity;

import java.awt.*;
import java.io.Serializable;

/**
 * Define the basic properties of the drawing point
 *
 * @author Zhuoya Zhou 1366573
 */
public class DrawPosition implements Serializable {

    /**
     * Brush X axis
     */
    public int x;

    /**
     * Brush Y axis
     */
    public int y;

    /**
     * operation type
     */
    public int type;

    /**
     * operation content
     */
    public String s;

    /**
     * operation color
     */
    public Color color;

}