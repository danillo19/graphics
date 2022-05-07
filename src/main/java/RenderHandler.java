import Rotation.RotationHandler;
import utils.DoublePoint;
import utils.Vector3;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RenderHandler {
    private final BufferedImage image;
    private final ArrayList<DoublePoint> curvePoints;
    private final ArrayList<Vector3> currentObjectsPoints;
    private ArrayList<Vector3> objectPointsBeforeRotation;
    private RotationHandler rotationHandler;
    private Vector3 axis;
    private static final int imageWidth = 800;
    private static final int imageHeight = 600;
    private double zn = 12;

    public RenderHandler(ArrayList<Point> points, RotationHandler rotationHandler) {
        image = new BufferedImage(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        this.rotationHandler = rotationHandler;
        curvePoints = new ArrayList<>();
        calcLocalPoints(points);
        currentObjectsPoints = new ArrayList<>();
        objectPointsBeforeRotation = new ArrayList<>();

        axis = new Vector3(1, 0, 0);
        getStartingCurve(0);
    }


    public BufferedImage getImage() {
        return image;
    }

    public void updateAxis(double[][] rotationMatrix) {
        this.axis = new Vector3(multMatrixOnVector(rotationMatrix, axis.toDouble()));
    }

    public void updateZn(double delta) {
        zn += delta;
        if(zn < 0) {
            zn = 0;
        }
    }

    public void drawProjection(double[][] rotationMatrix, int alpha) {
        getFigureOfRotation(alpha);
        rotateObjectWithMatrix(rotationMatrix);

        ArrayList<Vector3> cameraPoints = getCameraCoordinates(currentObjectsPoints);
        ArrayList<Point> points = getScreenProjection(cameraPoints);

        Graphics graphics = image.getGraphics();

        for (int i = 0; i < points.size() - 1; i++) {
            graphics.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            graphics.setColor(Color.WHITE);
        }
    }

    public void calcLocalPoints(ArrayList<Point> points) {

        for (Point point : points) {
            point.x -= imageWidth / 2;
            point.y = imageHeight / 2 - point.y;
            curvePoints.add(new DoublePoint((double) 2 * point.x / imageWidth, (double) 2 * point.y / imageHeight));
        }
    }

    public void updateObjectPoints() {
        objectPointsBeforeRotation.clear();
        objectPointsBeforeRotation.addAll(currentObjectsPoints);
    }

    public void getStartingCurve(int alpha) {
        double radian = alpha * Math.PI / 180;
        double[][] rotationMatrix = rotationHandler.calcRotationMatrix(axis, radian);
        for (DoublePoint curvePoint : curvePoints) {
            double[] coordinates = {curvePoint.x, curvePoint.y, 0};
            objectPointsBeforeRotation.add(new Vector3(multMatrixOnVector(rotationMatrix, coordinates)));
        }
    }

    public void getFigureOfRotation(int alpha) {
        currentObjectsPoints.clear();
        double radian = alpha * Math.PI / 180;
        double[][] rotationMatrix = rotationHandler.calcRotationMatrix(axis, radian);
        for (Vector3 vector : objectPointsBeforeRotation) {
            Vector3 vector3 = new Vector3(multMatrixOnVector(rotationMatrix, vector.toDouble()));
            currentObjectsPoints.add(vector3);
        }
    }

    public void rotateObjectWithMatrix(double[][] rotationMatrix) {
        for (Vector3 point : currentObjectsPoints) {
            Vector3 rotated = new Vector3(multMatrixOnVector(rotationMatrix, point.toDouble()));
            currentObjectsPoints.set(currentObjectsPoints.indexOf(point), rotated);
        }
    }

    //REWRITE!!!
    public ArrayList<Vector3> getCircleApproximation(int pointNumber, Vector3 levelPoint, double[][] rotationMatrixForAxis) {
        double radian = 2 * Math.PI / pointNumber;
        Vector3 rotatedAxis = new Vector3(multMatrixOnVector(rotationMatrixForAxis,axis.toDouble()));
        double[][] rotationMatrix = rotationHandler.calcRotationMatrix(rotatedAxis,radian);
        ArrayList<Vector3> points = new ArrayList<>(pointNumber);
        Vector3 point = levelPoint;
        for (int i = 0; i < pointNumber; i++) {
            point = new Vector3(multMatrixOnVector(rotationMatrix, point.toDouble()));
            points.add(point);
        }

        return points;
    }

    public void drawCircle(int pointNumber, Vector3 levelPoint, double[][] rotationMatrix) {
        ArrayList<Vector3> circlePoints = getCircleApproximation(pointNumber, levelPoint, rotationMatrix);
        ArrayList<Vector3> cameraPoints = getCameraCoordinates(circlePoints);
        ArrayList<Point> points = getScreenProjection(cameraPoints);

        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.CYAN);

        for (int i = 0; i < points.size(); i++) {
            Point curPoint = points.get(i);
            Point nextPoint = points.get((i + 1) % points.size());
            graphics.drawLine(curPoint.x, curPoint.y, nextPoint.x, nextPoint.y);
        }

    }

    public void drawCircles(int pointNumber,double[][] rotationMatrix) {
        for (Vector3 point : currentObjectsPoints) {
            drawCircle(pointNumber, point,rotationMatrix);
        }
    }

    public ArrayList<Vector3> getCameraCoordinates(ArrayList<Vector3> points) {
        Vector3 cameraShift = new Vector3(0, 0, 10);
        ArrayList<Vector3> cameraPoints = new ArrayList<>();
        for (Vector3 point : points) {
            cameraPoints.add(new Vector3(point.x + cameraShift.x, point.y + cameraShift.y, point.z + cameraShift.z));
        }
        return cameraPoints;
    }


    public ArrayList<Point> getScreenProjection(ArrayList<Vector3> cameraPoints) {
        ArrayList<DoublePoint> cubeProjection = new ArrayList<>(cameraPoints.size());

        for (Vector3 point : cameraPoints) {
            cubeProjection.add(new DoublePoint(point.x / (point.z / zn), point.y / (point.z / zn)));
        }

        ArrayList<Point> intPoints = new ArrayList<>(cubeProjection.size());


        for (DoublePoint point : cubeProjection) {
            intPoints.add(new Point((int) (point.x * (imageWidth / 2)) + (imageWidth / 2),
                    -(int) (point.y * (imageHeight / 2)) + (imageHeight / 2)));
        }
        return intPoints;
    }


    private double[] multMatrixOnVector(double[][] matrix, double[] vector) {
        double[] result = new double[vector.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += (matrix[i][j] * vector[j]);
            }
        }

        return result;
    }
}
