package pl.edu.pb.algorithm.utils;

import pl.edu.pb.graph.Point;
import java.util.Comparator;

/**
 * Created by Mateusz on 12.05.2017.
 * Sorts ascending
 */
public class PointComparator implements Comparator<Point> {
    @Override
    public int compare(Point o1, Point o2) {

        return Double.compare(o1.getProfit(), o2.getProfit());
    }
}
