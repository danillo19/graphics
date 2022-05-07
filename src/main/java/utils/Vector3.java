package utils;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3(double[] vector) {
        x = vector[0];
        y = vector[1];
        z = vector[2];
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getLength() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double[] toDouble() {
        return new double[]{x,y,z};
    }

    @Override
    public String toString() {
        return "x = " + x + " ; " + "y = " + y + " ; " + "z = " + z;
    }
}
