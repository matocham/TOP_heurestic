package pl.edu.pb.algorithm;

import pl.edu.pb.algorithm.solution.Solution;
import pl.edu.pb.graph.Point;

/**
 * Created by Mateusz on 08.05.2017.
 */
public abstract class Algorithm {
    protected Point[] data;
    protected int startPoint;
    protected double lengthLimit;

    public Algorithm(Point[] data, int startPoint, double lengthLimit) {
        this.data = data;
        this.startPoint = startPoint;
        this.lengthLimit = lengthLimit;
    }

    public abstract Solution calculateSolution();


}
