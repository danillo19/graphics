import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SplineHandler {
    private ArrayList<Point> anchorPoints;
    private ArrayList<Point> curvePoints;

    private int currentPointIndex = 0;

    private int n = 3;
    private BufferedImage image;

    public SplineHandler() {
        anchorPoints = new ArrayList<>(4);
        curvePoints = new ArrayList<>(4);
        image = new BufferedImage(800, 600, Image.SCALE_SMOOTH);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.drawLine(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight() - 1);
        graphics2D.drawLine(0, image.getHeight() / 2, image.getWidth() - 1, image.getHeight() / 2);
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void addPoint(Point point) {
        anchorPoints.add(point);
        currentPointIndex = anchorPoints.size() - 1;
    }

    public void deleteCurrentPoint() {
        anchorPoints.remove(currentPointIndex);
        currentPointIndex = anchorPoints.size() - 1;
    }

    public int getCurrentPointIndex() {
        return currentPointIndex;
    }

    public void setCurrentPointIndex(int newCurrentPointIndex) {
        this.currentPointIndex = newCurrentPointIndex;
    }

    public void colorPoint(Point point, Color color) {
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.drawRect(point.x - 5,
                point.y - 5, 10, 10);
    }

    public Point findClickedPoint(int x, int y) {
        for (Point point : anchorPoints) {
            if (Math.abs(x - point.x) < 5 && Math.abs(y - point.y) < 5) {
                return point;
            }
        }
        return null;
    }

    public void changePoint(Point point) {
        anchorPoints.set(currentPointIndex, point);
    }

    public void drawPoint(int x, int y) {
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setColor(Color.MAGENTA);
        graphics2D.drawRect(x - 5, y - 5, 10, 10);

        if (anchorPoints.size() != 0) {
            Point lastAddedPoint = anchorPoints.get(anchorPoints.size() - 1);
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawRect(anchorPoints.get(currentPointIndex).x - 5, anchorPoints.get(currentPointIndex).y - 5, 10, 10);
            graphics2D.drawLine(x, y, lastAddedPoint.x, lastAddedPoint.y);
        }
    }

    public void drawPoints() {
        Graphics graphics = image.getGraphics();
        for (Point point : anchorPoints) {
            graphics.drawRect(point.x - 5, point.y - 5, 10, 10);
        }
    }


    public ArrayList<Point> getAnchorPoints() {
        return anchorPoints;
    }

    public ArrayList<Point> getCurvePoints() {
        return curvePoints;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void redrawSpline() {
        BufferedImage newImage = new BufferedImage(image.getWidth(),
                image.getHeight(), Image.SCALE_SMOOTH);
        Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();
        graphics2D.drawLine(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight() - 1);
        graphics2D.drawLine(0, image.getHeight() / 2, image.getWidth() - 1, image.getHeight() / 2);
        setImage(newImage);

        drawPoints();
        drawSpline();
        drawAnchorLine();
        colorPoint(anchorPoints.get(currentPointIndex), Color.MAGENTA);

    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    private ArrayList<Point> calcSpline(int startIndex) {
        ArrayList<Point> spline = new ArrayList<>();

        double[][] M = {
                {-1, 3, -3, 1},
                {3, -6, 3, 0},
                {-3, 0, 3, 0},
                {1, 4, 1, 0}};

        double[] T = new double[4];

        for (double t = 0; t < 1.0; t += 0.01) {
            double tau = 1;
            for (int i = 3; i >= 0; i--) {
                T[i] = tau;
                tau *= t;
            }

            double[] TM = multStringOnMatrix(T, M);
            double[] point = multStringOnPointsVector(TM, startIndex);
            spline.add(new Point((int) point[0], (int) point[1]));

        }
        return spline;
    }

    public void drawCurvePart(int startIndex) {
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setColor(Color.blue);
        for (int i = startIndex; i < curvePoints.size() - 1; i++) {
            Point curPoint = curvePoints.get(i);
            Point nextPoint = curvePoints.get(i + 1);
            graphics2D.drawLine(curPoint.x, curPoint.y, nextPoint.x, nextPoint.y);
        }
    }

    private void calcCurve(ArrayList<Point> spline) {
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setColor(Color.red);

        if (curvePoints.size() == 0) {
            curvePoints.add(spline.get(0));
            graphics2D.drawOval(spline.get(0).x - 5, spline.get(0).y - 5, 10, 10);
        }

        int startIndex = curvePoints.size() - 1;

        for (int i = spline.size() / n; i <= spline.size() - spline.size() / n; i += spline.size() / n) {
            Point point = spline.get(i);
            curvePoints.add(point);
            graphics2D.drawOval(point.x - 5, point.y - 5, 10, 10);
        }

        curvePoints.add(spline.get(spline.size() - 1));
        graphics2D.drawOval(spline.get(spline.size() - 1).x - 5, spline.get(spline.size() - 1).y - 5, 10, 10);

        drawCurvePart(startIndex);
    }

    public void drawSplinePart(int startIndex) {

        ArrayList<Point> spline = calcSpline(startIndex);

        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        for (int i = 0; i < spline.size() - 1; i++) {
            graphics2D.drawLine(spline.get(i).x, spline.get(i).y, spline.get(i + 1).x, spline.get(i + 1).y);
        }

        calcCurve(spline);
    }

    public void drawSpline() {
        ArrayList<Point> splinePart;
        Graphics graphics = image.getGraphics();
        curvePoints.clear();
        for (int i = 0; i < anchorPoints.size() - 3; i++) {
            splinePart = calcSpline(i);
            calcCurve(splinePart);
            for (int j = 0; j < splinePart.size() - 1; j++) {
                graphics.drawLine(splinePart.get(j).x, splinePart.get(j).y, splinePart.get(j + 1).x, splinePart.get(j + 1).y);
            }
        }
    }

    public void drawAnchorLine() {
        Graphics graphics = image.getGraphics();
        for (int i = 0; i < anchorPoints.size() - 1; i++) {
            graphics.drawLine(anchorPoints.get(i).x, anchorPoints.get(i).y, anchorPoints.get(i + 1).x, anchorPoints.get(i + 1).y);
        }
    }

    private double[] multStringOnMatrix(double[] string, double[][] matrix) {
        double[] result = new double[matrix[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < string.length; j++) {
                result[i] += matrix[j][i] * string[j] / 6;
            }
        }

        return result;
    }


    private double[] multStringOnPointsVector(double[] string, int startIndex) {
        double x = 0;
        double y = 0;
        double[] result = new double[2];
        for (int i = 0; i < string.length; i++) {
            x += string[i] * anchorPoints.get(i + startIndex).x;
            y += string[i] * anchorPoints.get(i + startIndex).y;
        }

        result[0] = x;
        result[1] = y;
        return result;
    }

}
