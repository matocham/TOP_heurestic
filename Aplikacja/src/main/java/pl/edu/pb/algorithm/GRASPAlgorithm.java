package pl.edu.pb.algorithm;

import pl.edu.pb.algorithm.solution.Solution;
import pl.edu.pb.graph.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.pb.algorithm.utils.ReversedSolutionComparator;
import pl.edu.pb.gui.drawing.DrawingVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 08.05.2017.
 */
public class GRASPAlgorithm extends Algorithm {
    private static final Logger logger = LogManager.getLogger(GRASPAlgorithm.class);
    private static final int RANDOM_GENERATE_INTERVAL = Integer.MAX_VALUE;
    private static int NUMBER_OF_SOLUTIONS = 2;
    private static int NUMBER_OF_ITERATIONS = 250;
    private static int NUMBER_OF_ROUTES_IN_SOLUTION = 2;
    private static final double RANDOMNESS_FACTOR = 0.4;
    private static final int NUMBER_OF_REMOVALS = 1;
    private static final boolean MOVE_IN_SOLUTION = true;

    private DrawingVisitor visitor;

    public GRASPAlgorithm(Point[] data, int startPoint, double lengthLimit, int days, int maxIterations, int solutionsCount, DrawingVisitor visitor) {
        super(data, startPoint, lengthLimit);
        NUMBER_OF_ITERATIONS = maxIterations;
        NUMBER_OF_ROUTES_IN_SOLUTION = days;
        NUMBER_OF_SOLUTIONS = solutionsCount;
        this.visitor = visitor;
    }

    @Override
    public Solution calculateSolution() {
        List<Solution> currentSolutions = new ArrayList<>();
        Solution theBestSolution = null;
        Solution localBest;
        logger.warn("Starting alghoritm!");
        logger.warn("Initial solutions:");
        for (int i = 0; i < NUMBER_OF_SOLUTIONS; i++) {
            Solution solution = new Solution(data, startPoint, lengthLimit, NUMBER_OF_ROUTES_IN_SOLUTION);
            solution.generateGreedyRandomizedSolution(RANDOMNESS_FACTOR);
            currentSolutions.add(solution);
            logger.warn(solution);
        }

        visitor.drawSolution(getTheBestCurrentSolution(currentSolutions), theBestSolution);
        for (int iteration = 0; iteration < NUMBER_OF_ITERATIONS; iteration++) {
            logger.warn("Iteration: " + iteration);
        /*
        1. obsolete: generate greedy-random N solutions
            now perturbations are computed
        */
            if (iteration % RANDOM_GENERATE_INTERVAL == 0) {
                if (iteration != 0) {
                    logger.warn("Generating new greedy random solutions set");
                    currentSolutions.clear();
                    for (int j = 0; j < NUMBER_OF_SOLUTIONS; j++) {
                        Solution solution = new Solution(data, startPoint, lengthLimit, NUMBER_OF_ROUTES_IN_SOLUTION);
                        solution.generateGreedyRandomizedSolution(RANDOMNESS_FACTOR);
                        currentSolutions.add(solution);
                    }
                }
            } else {
                for (int i = 0; i < NUMBER_OF_SOLUTIONS; i++) {
                    logger.info("Genereting new disturbed solutions");
//                    currentSolutions.get(i).doubleBridgePerturbation();
//                    currentSolutions.get(i).doubleBridgePerturbation();
                    //currentSolutions.get(i).disturb2OPT();
                    currentSolutions.get(i).disturb2OPT();
                    currentSolutions.get(1).distrubRemove();
                    currentSolutions.get(i).disturb2OPT();
                }
                logger.debug("New solution disturb created");
            }


            visitor.drawSolution(getTheBestCurrentSolution(currentSolutions), theBestSolution);
        /*
        2. using 2opt, insert, replace and optionally swap improve solutions
        */
            localSearch(currentSolutions);
            logger.debug("Local search performed");
            if (MOVE_IN_SOLUTION) {
                movePointsInSolution(currentSolutions);
            }
            visitor.drawSolution(getTheBestCurrentSolution(currentSolutions), theBestSolution);
        /*
        3. perform path relinking on solutions getting NxN solutions
        */
            List<Solution> intermediateSolutions = pathRelinking(currentSolutions);
            logger.debug("Path relinking performed");
        /*
        4. get N best solutions and improve using local operations
        */
            if (!intermediateSolutions.isEmpty()) {
                intermediateSolutions.sort(new ReversedSolutionComparator());
                currentSolutions.clear();
                for (int i = 0; i < NUMBER_OF_SOLUTIONS; i++) {
                    currentSolutions.add(intermediateSolutions.get(i));
                }
                localSearch(currentSolutions);
                if (MOVE_IN_SOLUTION) {
                    movePointsInSolution(currentSolutions);
                }
            }
            visitor.drawSolution(getTheBestCurrentSolution(currentSolutions), theBestSolution);
            logger.debug("Local search after parth relinking performed");
        /*
        5. check if there is any better solution
        */
            localBest = getTheBestCurrentSolution(currentSolutions);
            if (theBestSolution == null || localBest.getToalProfit() > theBestSolution.getToalProfit()) {
                logger.warn("New best solution: {}", localBest.getToalProfit());
                if (theBestSolution != null) {
                    logger.warn(" old is {}", theBestSolution.getToalProfit());
                }
                theBestSolution = localBest.copy();
            }
            visitor.drawSolution(getTheBestCurrentSolution(currentSolutions), theBestSolution);
        /*
        6. performs steps 1-5 M times or set some variable indicating solution improvement,
         */
        }
        logger.warn("End of iterations. The best solution is:");
        logger.warn("\n{}", theBestSolution);
        visitor.drawSolution(null, theBestSolution);
        return theBestSolution;
    }

    private void movePointsInSolution(List<Solution> currentSolutions) {
        for (int i = 0; i < currentSolutions.size(); i++) {
            boolean improved;
            do {
                improved = currentSolutions.get(i).moveBetweenRoutes();
            } while (improved);
        }
    }

    private void localSearch(List<Solution> solutions) {
        for (Solution solution : solutions) {
            boolean pathIsImproved;
            do {
                pathIsImproved = solution.local2OPT(false);
            } while (pathIsImproved);
            for (int i = 0; i < NUMBER_OF_REMOVALS; i++) {
                solution.remove();
            }
            do {
                pathIsImproved = solution.insert();
            } while (pathIsImproved);
            do {
                pathIsImproved = solution.replace();
            } while (pathIsImproved);
        }
    }

    private List<Solution> pathRelinking(List<Solution> solutions) {
        List<Solution> intermediateSolutions = new ArrayList<>();
        for (int i = 0; i < solutions.size(); i++) {
            for (int j = 0; j < solutions.size(); j++) {
                if (i != j) {
                    Solution intermediate = solutions.get(i).relink(solutions.get(j));
                    intermediateSolutions.add(intermediate);
                }
            }
        }
        return intermediateSolutions;
    }

    private Solution getTheBestCurrentSolution(List<Solution> currentSolutions) {
        Solution theBestSolution = null;
        for (Solution solution : currentSolutions) {
            if (theBestSolution == null || theBestSolution.getToalProfit() < solution.getToalProfit()) {
                theBestSolution = solution;
            }
        }
        logger.info("Best created solution: " + theBestSolution.getToalProfit() + " " + theBestSolution.getTotalLength());
        return theBestSolution;
    }

    @Override
    public String toString() {
        return "GRASP";
    }
}
