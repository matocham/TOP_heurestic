package pl.edu.pb.algorithm.solution;

import pl.edu.pb.graph.Route;

/**
 * Created by Mateusz on 11.05.2017.
 */
public interface LocalOperations {
    boolean remove();
    boolean insert();
    boolean local2OPT(boolean findBest);
    boolean replace();
    void disturb2OPT();
    void distrubRemove();
    boolean localSwap(Route route);
    void doubleBridgePerturbation();
}
