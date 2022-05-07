package Rotation;

import utils.Vector3;

import java.awt.*;

public class RotationHandler {
    private Vector3 axis;
    private double[][] rotationMatrix;
    private Vector3 startArcPoint;
    private Vector3 finishArcPoint;

    public RotationHandler() {
        axis = new Vector3(1,0,0);
        rotationMatrix = new double[][]{{1,0,0},{0,1,0},{0,0,1}};
        startArcPoint = new Vector3(0,0,1);
        finishArcPoint = new Vector3(0,1,0);
    }

    public void updateMatrix() {
        rotationMatrix = calcRotationMatrix(axis,getRotationAngle());
    }

    public double[][] getRotationMatrix() {
        return rotationMatrix;
    }

    public void setVectors(Point startPoint, Point finishPoint, int width, int height) {
        double xStart = startPoint.x - (double) width / 2;
        double yStart = (double) height / 2 - startPoint.y;

        xStart /= (double) width / 2;
        yStart /= (double) height / 2;

        double xFinish = finishPoint.x - (double) width / 2;
        double yFinish = (double) height / 2 - finishPoint.y;

        xFinish /= (double) width / 2;
        yFinish /= (double) height / 2;

        int radius = 1;
        double zStartSquared = radius * radius - xStart * xStart - yStart * yStart;
        double zFinishSquared = radius * radius - xFinish * xFinish - yFinish * yFinish;
        double zStart = zStartSquared > 0 ? Math.sqrt(zStartSquared) : 0;
        double zFinish = zFinishSquared > 0 ? Math.sqrt(zFinishSquared) : 0;


        startArcPoint = new Vector3(xStart, yStart, zStart);
        finishArcPoint = new Vector3(xFinish, yFinish, zFinish);
    }

    public void updateAxis() {
        this.axis = calcRotationAxis();
    }

    public Vector3 getAxis() {
        return axis;
    }

    public Vector3 calcRotationAxis() {
        double x = startArcPoint.z * finishArcPoint.y - finishArcPoint.z * startArcPoint.y;
        double y = startArcPoint.x * finishArcPoint.z - finishArcPoint.x * startArcPoint.z;
        double z = startArcPoint.y * finishArcPoint.x - startArcPoint.x * finishArcPoint.y;

        double t = 1 / Math.sqrt(x * x + y * y + z * z);
        return new Vector3(x * t, y * t, z * t);
    }


    public double getRotationAngle() {
        double scalar = startArcPoint.x * finishArcPoint.x + startArcPoint.y * finishArcPoint.y + startArcPoint.z * finishArcPoint.z;
        return Math.acos(scalar / (startArcPoint.getLength() * finishArcPoint.getLength()));
    }

    private double[][] multiplyVectorOnString(double[] vector, double[] string) {
        double[][] matrix = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = vector[i] * string[j];
            }
        }
        return matrix;
    }

    private double[][] sumMatrix(double[][] first, double[][] second, int firstCoeff, int secondCoeff) {
        double[][] matrix = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = first[i][j] * firstCoeff + secondCoeff * second[i][j];
            }
        }

        return matrix;
    }

    private void multiplyMatrixByNumber(double[][] matrix, double number) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] *= number;
            }
        }
    }


    public double[][] calcRotationMatrix(Vector3 axis, double angle) {
        double[][] axisAndAxisT = multiplyVectorOnString(axis.toDouble(), axis.toDouble());
        double[][] matrixForVectorMult =
                {
                        {0, -axis.z, axis.y},
                        {axis.z, 0, -axis.x},
                        {-axis.y, axis.x, 0}
                };

        double[][] I = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        double[][] IminusAxisandAxisT = sumMatrix(I, axisAndAxisT, 1, -1);

        multiplyMatrixByNumber(IminusAxisandAxisT, Math.cos(angle));
        multiplyMatrixByNumber(matrixForVectorMult, Math.sin(angle));

        double[][] firstSum = sumMatrix(axisAndAxisT, IminusAxisandAxisT, 1, 1);
        return sumMatrix(firstSum, matrixForVectorMult, 1, 1);
    }

    public double getDeterminant(double[][] matrix) {
        return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) -
                matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]) +
                matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
    }
}
