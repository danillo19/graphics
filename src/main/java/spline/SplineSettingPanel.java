package spline;

import draw.DrawPanel;
import render.RenderHandler;
import render.RenderPanel;
import utils.Vector3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SplineSettingPanel extends JPanel {
    public JTextField nField;
    public JTextField currentPointNumberField;
    public JTextField mField;
    public JTextField m1Field;
    public JTextField xField;
    public JTextField yField;
    public JButton deletePoint;
    public JButton addPoint;
    public JButton draw3D;


    public SplineSettingPanel(DrawPanel panel, RenderPanel renderPanel) {
        JLabel nLabel = new JLabel("n");
        nField = new JTextField("2");
        JLabel currentPointLabel = new JLabel("№");
        currentPointNumberField = new JTextField("0");
        JLabel mLabel = new JLabel("m");
        mField = new JTextField("5");
        JLabel m1Label = new JLabel("m1");
        m1Field = new JTextField("10");
        JLabel xLabel = new JLabel("x");
        xField = new JTextField("0");
        JLabel yLabel = new JLabel("y");
        yField = new JTextField("0");

        deletePoint = new JButton("Delete");
        draw3D = new JButton("Draw 3D");
        addPoint = new JButton("Add");
        GridLayout gridLayout = new GridLayout(4, 4);
        gridLayout.setVgap(5);
        gridLayout.setHgap(5);
        setLayout(gridLayout);
        add(currentPointLabel);
        add(currentPointNumberField);
        add(nLabel);
        add(nField);
        add(mLabel);
        add(mField);
        add(m1Label);
        add(m1Field);
        add(xLabel);
        add(xField);
        add(yLabel);
        add(yField);

        add(deletePoint);
        add(addPoint);
        add(draw3D);

        addListeners(panel, renderPanel);
    }

    private void addListeners(DrawPanel panel, RenderPanel renderPanel) {
        nField.addActionListener(e -> {
            JTextField field = (JTextField) e.getSource();
            int n = Integer.parseInt(field.getText());
            panel.getSplineHandler().setN(n);
            panel.getSplineHandler().redrawSpline();
            panel.repaint();
        });
        currentPointNumberField.addActionListener(e -> {
            JTextField field = (JTextField) e.getSource();
            int currentIndex = Integer.parseInt(field.getText());
            if (currentIndex >= panel.getSplineHandler().getAnchorPoints().size()) return;
            panel.getSplineHandler().setCurrentPointIndex(currentIndex);
            panel.getSplineHandler().redrawSpline();
            panel.repaint();
        });

        deletePoint.addActionListener(e -> {
            panel.getSplineHandler().deleteCurrentPoint();
            panel.getSplineHandler().redrawSpline();
            panel.repaint();
        });

        addPoint.addActionListener(e -> {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());

            panel.getSplineHandler().addPoint(new Point(x, y));
            panel.getSplineHandler().redrawSpline();
            panel.repaint();
        });

        draw3D.addActionListener(e -> {
            ArrayList<Point> curvePoints = panel.getSplineHandler().getCurvePoints();
            ArrayList<Point> points = new ArrayList<>();
            for(Point point: curvePoints) {
                points.add((Point) point.clone());
            }
            int m = Integer.parseInt(mField.getText());
            int m1 = Integer.parseInt(m1Field.getText());
            renderPanel.setM(m);
            renderPanel.setM1(m1);
            RenderHandler renderHandler = renderPanel.getRenderHandler();
            renderHandler.calcLocalPoints(points);
            renderHandler.getStartingCurve(0);
            renderHandler.setAxis(new Vector3(1, 0, 0));
            renderHandler.setZn(10);
            renderPanel.repaint();

        });
    }
}
