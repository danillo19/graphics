package render;

import render.RenderHandler;
import rotation.RotationHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RenderPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {
    private final RenderHandler renderHandler;
    private final RotationHandler rotationHandler;
    private Point startPoint;
    private int m;
    private int m1;
    private final double[][] I;
    private boolean isMouseWasDragged = false;
    private boolean isResetAngles = false;

    public RenderPanel() {
        this.I = new double[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };
        rotationHandler = new RotationHandler();
        renderHandler = new RenderHandler(rotationHandler);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

    }

    public void setM(int m) {
        this.m = m;
    }

    public void setM1(int m1) {
        this.m1 = m1;
    }

    public RenderHandler getRenderHandler() {
        return renderHandler;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage image = renderHandler.getImage();

        if (getHeight() != image.getHeight() || getWidth() != image.getWidth()) {
            image = new BufferedImage(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            renderHandler.setImage(image);
        }
        Graphics graphics = renderHandler.getImage().getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, renderHandler.getImage().getWidth(), renderHandler.getImage().getHeight());

        double[][] rotationMatrix = isMouseWasDragged | isResetAngles ? rotationHandler.getRotationMatrix() : I;
        isResetAngles = false;

        for (int i = 0; i < 360; i += 360 / m) {
            renderHandler.drawProjection(rotationMatrix, i);
        }
        renderHandler.drawCircles(m1 * m, rotationMatrix);

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
        if (isMouseWasDragged) {
            rotationHandler.setVectors(startPoint, new Point(e.getX(), e.getY()), renderHandler.getImage().getWidth(),
                    renderHandler.getImage().getHeight());
            rotationHandler.updateAxis();
            rotationHandler.updateMatrix();
            renderHandler.updateAxis(rotationHandler.getRotationMatrix());
            renderHandler.updateObjectPoints();
        }

        isMouseWasDragged = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        rotationHandler.setVectors(startPoint, new Point(e.getX(), e.getY()), renderHandler.getImage().getWidth(),
                renderHandler.getImage().getHeight());
        rotationHandler.updateAxis();
        rotationHandler.updateMatrix();

        isMouseWasDragged = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double delta = e.getPreciseWheelRotation() * 0.1;
        renderHandler.updateZn(delta);
        repaint();
    }

}
