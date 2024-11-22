package edu.unimelb.board;

import edu.unimelb.common.enums.ActionTypeEnum;
import edu.unimelb.common.util.ClientConnectionUtils;
import edu.unimelb.entity.DrawPosition;
import edu.unimelb.factory.ActionButtonFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.Vector;

import static edu.unimelb.common.constant.ActionNameConstant.*;

/**
 * whiteboard component
 * 
 * @author Zhuoya Zhou 1366573
 */
public class DrawPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    public void setClientConnectionUtils(String traceId, ClientConnectionUtils clientConnectionUtils) {
        this.traceId = traceId;
        this.clientConnectionUtils = clientConnectionUtils;
    }

    private String traceId;

    private ClientConnectionUtils clientConnectionUtils;

    private boolean isManager;

    /**
     * topbar
     */
    JMenuBar menuBar;

    /**
     * File submenu
     */
    JMenu fileMenu;

    /**
     * Open subitem
     */
    JMenuItem openItem;

    /**
     * Save subitem
     */
    JMenuItem saveItem;

    /**
     * Saveas subitem
     */
    JMenuItem saveAsItem;

    /**
     * Exit subitem
     */
    JMenuItem exitItem;

    /**
     * Clear subitem
     */
    JMenuItem clearItem;

    /**
     * Tools
     */
    JPanel toolPanel;

    JButton brushButton;

    JButton textButton;

    JButton lineButton;

    JButton rectangleButton;

    JButton ellipseButton;

    JButton colorButton;

    JButton erasersButton;

    JButton eraserlButton;

    JButton cycleButton;

    /**
     * Save the drawing trace vector array
     */
    Vector<DrawPosition> drawTrack = new Vector<>();

    /**
     * Save the drawing type, which defaults to brush
     */
    int style = ActionTypeEnum.BRUSH.getCode();
    
    /**
     * save the point location
     */
    int x1 = 0;
    int x2 = 0;
    int y1 = 0;
    int y2 = 0;

    /**
     * Default input text
     */
    String input = "";

    /**
     * Default brush color: Black
     */
    Color lineColor = Color.BLACK;

    public DrawPanel(boolean isManager) {
        this.isManager = isManager;
        // Background color
        setBackground(Color.WHITE);
        // BorderLayout
        setLayout(new BorderLayout());
        // Mouse Control
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        // Only manager can initialize the top menu
        if (isManager) {
            initTopMenu();
            initMenuItemActionListener();
        }
        
        // Initializes the side menu bar
        initSidebarMenu();

        // Initialize event listening
        initActionListener();
    }

    /**
     * Initialize the top menu
     */
    private void initTopMenu() {
        // Top menu bar
        menuBar = new JMenuBar();
        menuBar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        add(menuBar, BorderLayout.NORTH);

        // Initializes the top menu options
        fileMenu = new JMenu(FILE);

        clearItem = new JMenuItem(CLEAR);
        // Initializes the menu item
        openItem = new JMenuItem(OPEN);
        saveItem = new JMenuItem(SAVE);
        saveAsItem = new JMenuItem(SAVEAS);
        exitItem = new JMenuItem(EXIT);

        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);


        menuBar.add(fileMenu);

    }

    /**
     * Initializes the side menu bar
     */
    private void initSidebarMenu() {
        toolPanel = new JPanel();
        // The background color of the side toolbar
        toolPanel.setBackground(Color.LIGHT_GRAY);
        // Vertical layout of the side menu bar
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        // The shape is the default when the mouse moves to the corresponding position
        toolPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        add(toolPanel, BorderLayout.WEST);

        // Example Initialize button information
        brushButton = ActionButtonFactory.getInstance(ActionTypeEnum.BRUSH);
        textButton = ActionButtonFactory.getInstance(ActionTypeEnum.TEXT);
        lineButton = ActionButtonFactory.getInstance(ActionTypeEnum.LINE);
        rectangleButton = ActionButtonFactory.getInstance(ActionTypeEnum.RECTANGLE);
        ellipseButton = ActionButtonFactory.getInstance(ActionTypeEnum.ELLIPSE);
        colorButton = ActionButtonFactory.getInstance(ActionTypeEnum.COLOR);
        cycleButton = ActionButtonFactory.getInstance(ActionTypeEnum.CIRCLE);
        erasersButton = ActionButtonFactory.getInstance(ActionTypeEnum.ERASERS);
        eraserlButton = ActionButtonFactory.getInstance(ActionTypeEnum.ERASERL);

        // Add button to whiteboard
        toolPanel.add(brushButton);
        toolPanel.add(textButton);
        toolPanel.add(lineButton);
        toolPanel.add(rectangleButton);
        toolPanel.add(ellipseButton);
        toolPanel.add(colorButton);
        toolPanel.add(cycleButton);
        toolPanel.add(erasersButton);
        toolPanel.add(eraserlButton);

    }

    /**
     * Initialize event listening
     */
    private void initActionListener() {
        //Button event listening
        brushButton.addActionListener(this);
        textButton.addActionListener(this);
        lineButton.addActionListener(this);
        rectangleButton.addActionListener(this);
        ellipseButton.addActionListener(this);
        colorButton.addActionListener(this);
        cycleButton.addActionListener(this);
        erasersButton.addActionListener(this);
        eraserlButton.addActionListener(this);

        // Mouse event listening
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void initMenuItemActionListener() {
        // Top menu item event listening
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(this);
        exitItem.addActionListener(this);
        clearItem.addActionListener(this);
    }


    // The ability to record mouse selections
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == brushButton) {
            style = ActionTypeEnum.BRUSH.getCode();
        } else if (e.getSource() == textButton) {
            style = ActionTypeEnum.TEXT.getCode();
            input = JOptionPane.showInputDialog("Enter text and click the place on the whiteboard");
        } else if (e.getSource() == lineButton) {
            style = ActionTypeEnum.LINE.getCode();
        } else if (e.getSource() == rectangleButton) {
            style = ActionTypeEnum.RECTANGLE.getCode();
        } else if (e.getSource() == cycleButton) {
            style = ActionTypeEnum.CIRCLE.getCode();
        } else if (e.getSource() == ellipseButton) {
            style = ActionTypeEnum.ELLIPSE.getCode();
        } else if (e.getSource() == erasersButton) {
            style = ActionTypeEnum.ERASERS.getCode();
        } else if (e.getSource() == eraserlButton) {
            style = ActionTypeEnum.ERASERL.getCode();
        } else if (e.getSource() == colorButton) {
            lineColor = JColorChooser.showDialog(null, "choose the color", Color.BLACK);
        } else if (e.getActionCommand().equals(CLEAR)) {
            drawTrack.removeAllElements();
            syncDrawBoard();
        } else if (e.getActionCommand().equals(EXIT)){
            clientConnectionUtils.disconnect(true, traceId);
            System.exit(0);
        }else if (e.getActionCommand().equals(SAVEAS)){
            savePanelAsImage();
        } else if (e.getActionCommand().equals(SAVE)) {
            JFileChooser sfc = new JFileChooser();
            int flag = -1;
            // A dialog box to save the file is displayed
            try {
                flag = sfc.showSaveDialog(this);
            } catch (HeadlessException he) {
                System.out.println("Save File Dialog Exception!");
            }
            // Gets the path to the selected file
            if (flag == JFileChooser.APPROVE_OPTION) {
                // File full path
                String filePath = sfc.getSelectedFile().getPath();
                try {
                    FileOutputStream fos = new FileOutputStream(filePath);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(drawTrack);
                    oos.close();
                } catch (Exception es) {
                    System.out.println("save file failed");
                }
            }
        } else if (e.getActionCommand().equals(OPEN)) {
            JFileChooser ofc = new JFileChooser();
            int flag = -1;
            try {
                flag = ofc.showOpenDialog(this);
            } catch (HeadlessException he) {
                System.out.println("Open File Dialog Exception!");
            }

            if (flag == JFileChooser.APPROVE_OPTION) {

                String filePath = ofc.getSelectedFile().getPath();
                try {
                    FileInputStream fis = new FileInputStream(filePath);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    // Restore the saved track
                    drawTrack = (Vector<DrawPosition>) ois.readObject();
                    ois.close();
                } catch (Exception es) {
                    System.out.println("Save File Dialog Exception!");
                }
            }
            syncDrawBoard();
        }

        repaint();
    }

    // paintComponent Method calls the draw method to draw inside the container without going beyond it
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    // Take a point out of the array and draw it
    public void draw(Graphics2D g) {
        int n = drawTrack.size();
        DrawPosition p;
        for (int i = 0; i < n; i++) {
            try {
                p = drawTrack.get(i);
                if (p.type == ActionTypeEnum.BRUSH.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = x2 = p.x;
                    y1 = y2 = p.y;
                    while (p.type == ActionTypeEnum.BRUSH.getCode()) {
                        x2 = p.x;
                        y2 = p.y;
                        Line2D t = new Line2D.Double(x1, y1, x2, y2);
                        g.setColor(p.color);
                        // Recursively drawing
                        g.draw(t);
                        i++;
                        if (i == n) {
                            i--;
                            break;
                        }
                        p = drawTrack.get(i);
                        x1 = x2;
                        y1 = y2;
                    }
                }
                //eraser
                if (p.type == ActionTypeEnum.ERASERS.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = x2 = p.x;
                    y1 = y2 = p.y;

                    while (p.type == ActionTypeEnum.ERASERS.getCode()) {
                        x2 = p.x;
                        y2 = p.y;
                        Line2D t = new Line2D.Double(x1, y1, x2, y2);
                        g.setColor(Color.white);
                        g.draw(t);
                        i++;
                        if (i == n) {
                            i--;
                            break;
                        }
                        p = drawTrack.get(i);
                        x1 = x2;
                        y1 = y2;
                    }
                }
                //eraser
                if (p.type == ActionTypeEnum.ERASERL.getCode()) {
                    float penWidth = 15.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = x2 = p.x;
                    y1 = y2 = p.y;

                    while (p.type == ActionTypeEnum.ERASERL.getCode()) {
                        x2 = p.x;
                        y2 = p.y;
                        Line2D t = new Line2D.Double(x1, y1, x2, y2);
                        g.setColor(Color.white);
                        g.draw(t);
                        i++;
                        if (i == n) {
                            i--;
                            break;
                        }
                        p = drawTrack.get(i);
                        x1 = x2;
                        y1 = y2;
                    }
                }
                // text
                if (p.type == ActionTypeEnum.TEXT.getCode()) {
                    while (p.type == ActionTypeEnum.TEXT.getCode()) {
                        g.setColor(p.color);
                        g.drawString(p.s, p.x, p.y);
                        i++;
                        if (i == n) {
                            i--;
                            break;
                        }
                        p = drawTrack.get(i);
                    }
                }
                // line
                if (p.type == ActionTypeEnum.LINE.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = p.x;
                    y1 = p.y;
                    i++;
                    p = drawTrack.get(i);
                    x2 = p.x;
                    y2 = p.y;
                    // There is no flip problem, so there is no need to switch coordinates
                    if (p.type == ActionTypeEnum.LINE.getCode()) {
                        Line2D t = new Line2D.Double(x1, y1, x2, y2);
                        g.setColor(p.color);
                        g.draw(t);
                        drawTrack.remove(i);
                    } else if (p.type == ActionTypeEnum.NONE.getCode()) {
                        Line2D t = new Line2D.Double(x1, y1, x2, y2);
                        g.setColor(p.color);
                        g.draw(t);
                    } else {
                        i--;
                    }
                }
                // rectangle
                if (p.type == ActionTypeEnum.RECTANGLE.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = p.x;
                    y1 = p.y;
                    i++;
                    p = drawTrack.get(i);
                    x2 = p.x;
                    y2 = p.y;
                    // Swapping coordinates allows the graph to flip up, down, left and right
                    if (x2 < x1) {
                        int temp;
                        temp = x1;
                        x1 = x2;
                        x2 = temp;
                    }
                    if (y2 < y1) {
                        int temp;
                        temp = y1;
                        y1 = y2;
                        y2 = temp;
                    }
                    // The mouse press changes dynamically
                    if (p.type == ActionTypeEnum.RECTANGLE.getCode()) {
                        Rectangle2D t = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
                        g.setColor(p.color);
                        g.draw(t);
                        drawTrack.remove(i);
                    // Release the mouse to fix the drawing
                    } else if (p.type == ActionTypeEnum.NONE.getCode()) {
                        Rectangle2D t = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
                        g.setColor(p.color);
                        g.draw(t);
                    } else {
                        i--;
                    }
                }

                // oval
                if (p.type == ActionTypeEnum.ELLIPSE.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = p.x;
                    y1 = p.y;
                    i++;
                    p = drawTrack.get(i);
                    x2 = p.x;
                    y2 = p.y;
                    if (x2 < x1) {
                        int temp;
                        temp = x1;
                        x1 = x2;
                        x2 = temp;
                    }
                    if (y2 < y1) {
                        int temp;
                        temp = y1;
                        y1 = y2;
                        y2 = temp;
                    }
                    if (p.type == ActionTypeEnum.ELLIPSE.getCode()) {
                        Ellipse2D t = new Ellipse2D.Double(x1, y1, x2 - x1, y2 - y1);
                        g.setColor(p.color);
                        g.draw(t);
                        drawTrack.remove(i);
                    } else if (p.type == ActionTypeEnum.NONE.getCode()) {
                        Ellipse2D t = new Ellipse2D.Double(x1, y1, x2 - x1, y2 - y1);
                        g.setColor(p.color);
                        g.draw(t);
                    } else {
                        i--;
                    }
                }

                //circle
                if (p.type == ActionTypeEnum.CIRCLE.getCode()) {
                    float penWidth = 5.0f;
                    g.setStroke(new BasicStroke(penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    x1 = p.x;
                    y1 = p.y;
                    i++;
                    p = drawTrack.get(i);
                    x2 = p.x;
                    y2 = p.y;

                    // 确保 x1, y1 是左上角
                    if (x2 < x1) {
                        int temp = x1;
                        x1 = x2;
                        x2 = temp;
                    }
                    if (y2 < y1) {
                        int temp = y1;
                        y1 = y2;
                        y2 = temp;
                    }

                    int width = x2 - x1;
                    int height = y2 - y1;
                    int diameter = Math.min(width, height);  // The diameter of the circle is the smaller of the rectangular width

                    if (p.type == ActionTypeEnum.CIRCLE.getCode()) {
                        Ellipse2D circle = new Ellipse2D.Double(x1, y1, diameter, diameter);
                        g.setColor(p.color);
                        g.draw(circle);
                        drawTrack.remove(i);
                    } else if (p.type == ActionTypeEnum.NONE.getCode()) {
                        Ellipse2D circle = new Ellipse2D.Double(x1, y1, diameter, diameter);
                        g.setColor(p.color);
                        g.draw(circle);
                    } else {
                        i--;
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    // Mouse click to record the drawing track
    @Override
    public void mousePressed(MouseEvent e) {
        DrawPosition p = new DrawPosition();
        p.x = e.getX();
        p.y = e.getY();
        p.type = style;
        p.s = input;
        p.color = lineColor;
        drawTrack.add(p);
        try {
            clientConnectionUtils.sendDrawOperate(drawTrack, isManager);
        } catch (IOException ie) {
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // When the mouse is released, type = -1 stops drawing, but still records the track
    @Override
    public void mouseReleased(MouseEvent e) {
        DrawPosition p = new DrawPosition();
        p.x = e.getX();
        p.y = e.getY();
        p.type = ActionTypeEnum.NONE.getCode();
        p.s = input;
        p.color = lineColor;
        drawTrack.add(p);
        repaint();
        if (Objects.nonNull(drawTrack) && !drawTrack.isEmpty()) {
            try {
                clientConnectionUtils.sendDrawOperate(drawTrack, isManager);
            } catch (IOException ie) {
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // Mouse drag record drawing track
    @Override
    public void mouseDragged(MouseEvent e) {
        DrawPosition p = new DrawPosition();
        p.x = e.getX();
        p.y = e.getY();
        if (style == ActionTypeEnum.TEXT.getCode()) {
            // Disable text drag
            p.type = ActionTypeEnum.NONE.getCode();
        } else {
            p.type = style;
        }
        p.s = input;
        p.color = lineColor;
        drawTrack.add(p);
        repaint();
        if (Objects.nonNull(drawTrack) && !drawTrack.isEmpty()) {
            try {
                clientConnectionUtils.sendDrawOperate(drawTrack, isManager);
            } catch (IOException ie) {
            }
        }
    }

    public void repaintDraw(Vector<DrawPosition> drawTrack) {
        this.drawTrack = drawTrack;
        repaint();
    }

    public void syncDrawBoard() {
            try {
                clientConnectionUtils.sendDrawOperate(drawTrack, isManager);
            } catch (IOException ie) {
            }
    }

    public void savePanelAsImage() {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name:");
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        String[] options = {"png", "jpg"};
        int fileType = JOptionPane.showOptionDialog(this, "Choose file type:", "File Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (fileType == -1) {
            return;
        }

        String fileExtension = options[fileType];
        File file = new File(fileName + "." + fileExtension);

        Dimension size = this.getSize();
        Insets insets = this.getInsets();

        int topBarHeight = menuBar.getHeight();
        int toolBarWidth = toolPanel.getWidth();

        // Calculate the size and position of the drawing area
        int drawingAreaWidth = size.width - toolBarWidth - insets.left - insets.right;
        int drawingAreaHeight = size.height - topBarHeight - insets.top - insets.bottom;

        // Creates an image of the drawing area
        BufferedImage image = new BufferedImage(drawingAreaWidth, drawingAreaHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Draws the contents of the drawing area onto the image
        g2d.translate(-toolBarWidth - insets.left, -(topBarHeight + insets.top));
        this.paint(g2d); // Draws the entire panel
        g2d.dispose();

        try {
            ImageIO.write(image, "png", file);
            System.out.println("Drawing area saved as Image: " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error saving drawing area as image: " + e.getMessage());
        }
    }
}