package pl.edu.pb.algorithm.utils;

import pl.edu.pb.algorithm.solution.Solution;

import java.util.Comparator;

/**
 * Created by Mateusz on 12.05.2017.
 */
public class ReversedSolutionComparator implements Comparator<Solution> {
    @Override
    public int compare(Solution o1, Solution o2) {
        return -Double.compare(o1.getToalProfit(), o2.getToalProfit());
    }
}
