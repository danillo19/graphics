import Rotation.RotationHandler;
import utils.DoublePoint;
import utils.Vector3;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RenderHandler {
    private BufferedImage image;
    private final ArrayList<DoublePoint> curvePoints;
    private final ArrayList<Vector3> currentObjectsPoints;
    private ArrayList<Vector3> objectPointsBeforeRotation;
    private RotationHandler rotationHandler;
    private Vector3 axis;
    private static final int imageWidth = 800;
    private static final int imageHeight = 600;
    private double zn = 10;

    public RenderHandler(RotationHandler rotationHandler) {
        image = new BufferedImage(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        this.rotationHandler = rotationHandler;
        curvePoints = new ArrayList<>();
        currentObjectsPoints = new ArrayList<>();
        objectPointsBeforeRotation = new ArrayList<>();

        axis = new Vector3(1, 0, 0);
    }

    public ArrayList<Vector3> getCurrentObjectsPoints() {
        return currentObjectsPoints;
    }

    public Vector3 getAxis() {
        return axis;
    }

    public double getZn() {
        return zn;
    }

    public void setObjectPointsBeforeRotation(ArrayList<Vector3> objectPointsBeforeRotation) {
        this.objectPointsBeforeRotation.clear();
        this.objectPointsBeforeRotation.addAll(objectPointsBeforeRotation);
    }

    public void setAxis(Vector3 axis) {
        this.axis = axis;
    }

    public void setZn(double zn) {
        this.zn = zn;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void updateAxis(double[][] rotationMatrix) {
        this.axis = new Vector3(multMatrixOnVector(rotationMatrix, axis.toDouble()));
    }

    public void updateZn(double delta) {
        zn += delta;
        System.out.println(zn);
        if (zn < 0) {
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
            point.x -= image.getWidth() / 2;
            point.y = image.getHeight() / 2 - point.y;
            curvePoints.add(new DoublePoint((double) 2 * point.x / image.getWidth(), (double) 2 * point.y / image.getHeight()));
        }
    }

    public void updateObjectPoints() {
        objectPointsBeforeRotation.clear();
        objectPointsBeforeRotation.addAll(currentObjectsPoints);
    }

    public void getStartingCurve(ArrayList <Point> points, int alpha) {
        curvePoints.clear();
        calcLocalPoints(points);
        objectPointsBeforeRotation.clear();
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
        Vector3 rotatedAxis = new Vector3(multMatrixOnVector(rotationMatrixForAxis, axis.toDouble()));
        double[][] rotationMatrix = rotationHandler.calcRotationMatrix(rotatedAxis, radian);
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

    private double getObjectCurveLength() {
        double length = 0;
        for (int i = 0; i < currentObjectsPoints.size() - 1; i++) {
            length += getDistanceBetweenPoints(currentObjectsPoints.get(i), currentObjectsPoints.get(i + 1));
        }
        return length;
    }

    public ArrayList<Vector3> getEvenCurvePoints() {
        double requiredShift = getObjectCurveLength() / (currentObjectsPoints.size() - 1);
        double remainingDistance = requiredShift;
        int checkedPointIndex = 1;
        Vector3 checkedPoint = currentObjectsPoints.get(checkedPointIndex);
        Vector3 currentPoint = currentObjectsPoints.get(0);
        ArrayList<Vector3> curveEvenPoints = new ArrayList<>();
        curveEvenPoints.add(currentPoint);
        while (curveEvenPoints.size() < currentObjectsPoints.size() - 1) {
            double curveSegmentLength = getDistanceBetweenPoints(currentPoint, checkedPoint);
            if (curveSegmentLength > remainingDistance) {
                double coeff = remainingDistance / curveSegmentLength;
                Vector3 newPoint = new Vector3(
                        currentPoint.x + (checkedPoint.x - currentPoint.x) * coeff,
                        currentPoint.y + (checkedPoint.y - currentPoint.y) * coeff,
                        currentPoint.z + (checkedPoint.z - currentPoint.z) * coeff);
                curveEvenPoints.add(newPoint);

                currentPoint = newPoint;
                remainingDistance = requiredShift;
            } else {
                remainingDistance -= curveSegmentLength;
                currentPoint = checkedPoint;
                checkedPointIndex++;
                if(checkedPointIndex == currentObjectsPoints.size()) break;
                checkedPoint = currentObjectsPoints.get(checkedPointIndex);
            }
        }
        curveEvenPoints.add(currentObjectsPoints.get(currentObjectsPoints.size() - 1));
        return curveEvenPoints;
    }

    public void drawCircles(int pointNumber, double[][] rotationMatrix) {
        ArrayList<Vector3> evenPoints = getEvenCurvePoints();
        for (Vector3 point : evenPoints) {
            drawCircle(pointNumber, point, rotationMatrix);
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
            intPoints.add(new Point((int) (point.x * (image.getWidth() / 2)) + (image.getWidth() / 2),
                    -(int) (point.y * (image.getHeight() / 2)) + (image.getHeight() / 2)));
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

    private double getDistanceBetweenPoints(Vector3 firstPoint, Vector3 secondPoint) {
        double x = firstPoint.x - secondPoint.x;
        double y = firstPoint.y - secondPoint.y;
        double z = firstPoint.z - secondPoint.z;
        return Math.sqrt(x * x + y * y + z * z);
    }
}
