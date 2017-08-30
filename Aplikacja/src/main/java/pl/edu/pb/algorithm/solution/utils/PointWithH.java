package pl.edu.pb.algorithm.solution.utils;

import pl.edu.pb.graph.Point;

/**
 * Created by Mateusz on 11.05.2017.
 * Util container used to store temporary heuristic value of point
 */
public class PointWithH {
    public Point point;
    public double h;

    public PointWithH(Point point, double h) {
        this.point = point;
        this.h = h;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }
}
