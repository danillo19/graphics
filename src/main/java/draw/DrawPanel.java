package draw;

import spline.SplineHandler;
import spline.SplineSettingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {
    private SplineHandler splineHandler;
    private SplineSettingPanel settingPanel;
    private boolean isClickOnPoint;

    public DrawPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        this.splineHandler = new SplineHandler();
    }

    public SplineHandler getSplineHandler() {
        return splineHandler;
    }


    public void setSettingPanel(SplineSettingPanel settingPanel) {
        this.settingPanel = settingPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(splineHandler.getImage(), 0, 0, null);
        settingPanel.currentPointNumberField.setText(String.valueOf(splineHandler.getCurrentPointIndex()));
        if(splineHandler.getAnchorPoints().size() > 0) {
            Point point = splineHandler.getAnchorPoints().get(splineHandler.getCurrentPointIndex());
            settingPanel.xField.setText(String.valueOf(point.x));
            settingPanel.yField.setText(String.valueOf(point.y));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point clickedPoint = splineHandler.findClickedPoint(e.getX(), e.getY());
        if(clickedPoint != null) {
            splineHandler.colorPoint(splineHandler.getAnchorPoints().get(splineHandler.getCurrentPointIndex()),Color.WHITE);
            splineHandler.colorPoint(clickedPoint,Color.MAGENTA);
            int newCurrentIndex = splineHandler.getAnchorPoints().indexOf(clickedPoint);
            splineHandler.setCurrentPointIndex(newCurrentIndex);
            isClickOnPoint = true;
            getGraphics().drawImage(splineHandler.getImage(),0,0,null);
        }
        else {
            isClickOnPoint = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isClickOnPoint) {
            splineHandler.changePoint(new Point(e.getX(), e.getY()));
            splineHandler.redrawSpline();
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clickedPoint = splineHandler.findClickedPoint(e.getX(), e.getY());
        if (clickedPoint == null) {

            splineHandler.drawPoint(e.getX(), e.getY());
            splineHandler.addPoint(new Point(e.getX(), e.getY()));

            splineHandler.redrawSpline();
            splineHandler.setCurrentPointIndex(splineHandler.getAnchorPoints().size() - 1);
            settingPanel.currentPointNumberField.setText(String.valueOf(splineHandler.getCurrentPointIndex()));

        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
