package utils;

import utils.Vector3;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FileHandler {
    private ArrayList<Vector3> points;
    private Vector3 axis;
    private ArrayList<DoublePoint> startingPoints;
    private double zn;

    public FileHandler(ArrayList<Vector3> points, Vector3 axis, double zn, ArrayList<DoublePoint> curvePoints) {
        this.points = points;
        this.axis = axis;
        this.zn = zn;
        this.startingPoints = curvePoints;
    }

    public ArrayList<Vector3> getPoints() {
        return points;
    }

    public Vector3 getAxis() {
        return axis;
    }

    public ArrayList<DoublePoint> getStartingPoints(){
        return startingPoints;
    }

    public double getZn() {
        return zn;
    }

    public void dumpInfoIntoFile(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(String.valueOf(points.size()));
        fileWriter.write("\n");
        for (Vector3 vector3 : points) {
            fileWriter.write(vector3.x + " ");
            fileWriter.write(vector3.y + " ");
            fileWriter.write(vector3.z + " ");
            fileWriter.write("\n");
        }
        fileWriter.write("Start");
        fileWriter.write("\n");
        for(DoublePoint point: startingPoints) {
            fileWriter.write((point.x + " "));
            fileWriter.write(point.y + " ");
            fileWriter.write("\n");
        }
        fileWriter.write("AXIS");
        fileWriter.write("\n");
        fileWriter.write(axis.x + " ");
        fileWriter.write(axis.y + " ");
        fileWriter.write(axis.z + " ");

        fileWriter.write("\n");
        fileWriter.write("Zn");
        fileWriter.write("\n");
        fileWriter.write(String.valueOf(zn));

        fileWriter.close();

    }

    public void readInfoFromFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int size = Integer.parseInt(bufferedReader.readLine());
        points.clear();

        for (int i = 0; i < size; i++) {
            String string = bufferedReader.readLine();
            String[] coords = string.split(" ");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            if(Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1) throw new NumberFormatException();
            Vector3 vector3 = new Vector3(x,y,z);
            points.add(vector3);
        }

        String startPointsFlag = bufferedReader.readLine();
        if(!startPointsFlag.equals("Start")) throw new NumberFormatException();
        for(int i = 0;i < size;i++) {
            String string = bufferedReader.readLine();
            String[] coords = string.split(" ");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            if(Math.abs(x) > 1 || Math.abs(y) > 1) throw new NumberFormatException();
            DoublePoint point = new DoublePoint(x,y);
            startingPoints.add(point);
        }

        String axisFlag = bufferedReader.readLine();
        if (!axisFlag.equals("AXIS")) throw new NumberFormatException();
        String string = bufferedReader.readLine();
        String[] coords = string.split(" ");
        axis = new Vector3(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));

        String znFlag = bufferedReader.readLine();
        if (!znFlag.equals("Zn")) throw new NumberFormatException();
        zn = Double.parseDouble(bufferedReader.readLine());

    }


}
