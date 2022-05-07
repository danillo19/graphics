import Rotation.RotationHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RenderPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {
    private final RenderHandler renderHandler;
    private final RotationHandler rotationHandler;
    private Point startPoint;
    private int m;
    private int m1;
    private boolean mouseWasDragged = false;

    public RenderPanel(ArrayList<Point> curvePoints, int m,int m1) {
        this.m = m;
        this.m1 = m1;
        rotationHandler = new RotationHandler();
        renderHandler = new RenderHandler(curvePoints,rotationHandler);
        double[][] I = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        for(int i = 0;i < 360;i += 360/m) {
            renderHandler.drawProjection(I, i);
        }
        renderHandler.drawCircles(m1,I);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(renderHandler.getImage(), 0, 0, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(mouseWasDragged) {
            rotationHandler.setVectors(startPoint, new Point(e.getX(), e.getY()), renderHandler.getImage().getWidth(),
                    renderHandler.getImage().getHeight());
            rotationHandler.updateAxis();
            double[][] rotationMatrix = rotationHandler.calcRotationMatrix(rotationHandler.getAxis(),
                    rotationHandler.getRotationAngle());
            renderHandler.updateAxis(rotationMatrix);

            renderHandler.updateObjectPoints();
        }

        mouseWasDragged = false;

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Graphics graphics = renderHandler.getImage().getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, renderHandler.getImage().getWidth(), renderHandler.getImage().getHeight());

        rotationHandler.setVectors(startPoint, new Point(e.getX(), e.getY()), renderHandler.getImage().getWidth(),
                renderHandler.getImage().getHeight());
        rotationHandler.updateAxis();
        double[][] rotationMatrix = rotationHandler.calcRotationMatrix(rotationHandler.getAxis(),
                rotationHandler.getRotationAngle());
        for(int i = 0;i < 360;i += 360/m) {
            renderHandler.drawProjection(rotationMatrix, i);
        }
        renderHandler.drawCircles(m1,rotationMatrix);

        repaint();
        mouseWasDragged = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double delta = e.getPreciseWheelRotation() * 0.1;
        renderHandler.updateZn(delta);

        Graphics graphics = renderHandler.getImage().getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, renderHandler.getImage().getWidth(), renderHandler.getImage().getHeight());

        double[][] I = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        for(int i = 0;i < 360;i += 360/m) {
            renderHandler.drawProjection(I, i);
        }
        renderHandler.drawCircles(m1,I);
        repaint();
    }
}
