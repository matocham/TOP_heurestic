package pl.edu.pb.algorithm.solution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.pb.graph.Graph;
import pl.edu.pb.graph.Point;
import pl.edu.pb.graph.Route;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by matocham on 11.05.2017.
 */
public abstract class LocalOperationBased {
    private static final Logger logger = LogManager.getLogger(LocalOperationBased.class);
    protected static final double APPROXIMATION_ERROR = 0.00005;
    public static final int MAX_ATTEMPTS = 100;
    protected LinkedList<Point> unusedPoints;
    protected double lengthLimit;

    public LocalOperationBased(Point[] data, double lengthLimit) {
        unusedPoints = new LinkedList<>();
        unusedPoints.addAll(Arrays.asList(data));
        this.lengthLimit = lengthLimit;
    }

    /**
     * Wstawia do trasy nowy wierzchołek. Wstawiane jest najlepsze możliwe aktualne dopasowanie,
     * czyli wstawiany jest wierzchołek, który zapewni najlepszy stosunek profit/przyrost odległości
     *
     * @param route
     * @return czy udało się poprawić trasę
     */
    protected boolean localInsert(Route route) {
        logger.info("Starting localInsert");
        double hBest = Double.MIN_VALUE;
        boolean improved = false;
        int insertPointId = -1;
        double totalDistance = route.computeTotalDistance();
        Point[] table = route.getAsTable();
        Point pointToInsert = null;

        for (Point freePoint : unusedPoints) {
            logger.debug("Trying to insert point {} into route {}", freePoint, route);
            for (int i = 0; i < table.length - 1; i++) {
                double newDistance = totalDistance;
                newDistance -= Graph.distanceBetween(table[i], table[i + 1]);
                newDistance += Graph.distanceBetween(table[i], freePoint) + Graph.distanceBetween(freePoint, table[i + 1]);
                double lengthIncrease = newDistance - totalDistance;
                if (freePoint.getProfit() / lengthIncrease >= hBest && isAcceptable(newDistance)) {
                    logger.debug("New best point to insert after: {}", insertPointId);
                    hBest = freePoint.getProfit() / lengthIncrease;
                    insertPointId = table[i].getId();
                    improved = true;
                    pointToInsert = freePoint;
                }
            }
        }
        if (improved) {
            logger.info("Inserting {} after point with id {}", pointToInsert, insertPointId);
            route.addAfter(pointToInsert, insertPointId);
            unusedPoints.remove(pointToInsert);
            logger.info("Route after insert : {}", route);
        }
        return improved;
    }

    /**
     * usuwa punkt, którego usunięcie spowoduje najmniejszą szkodę, np. wierzchołek o małym proficie położony
     * w dużej odległości od pozostałych
     *
     * @param route
     * @return czy udało się poprawić trasę
     */
    protected boolean localRemove(Route route) {
        logger.info("Starting localRemove");
        double hBest = Double.MIN_VALUE;
        boolean improved = false;
        double totalDistance = route.getTotalDistance();
        Point[] table = route.getAsTable();
        Point pointToRemove = null;

        for (int i = 1; i < table.length - 1; i++) {
            double newDistance = totalDistance + (Graph.distanceBetween(table[i - 1], table[i + 1]) - (Graph.distanceBetween(table[i], table[i + 1]) + Graph.distanceBetween(table[i - 1], table[i])));
            double lengthDecrease = totalDistance - newDistance;
            if (lengthDecrease > 0 && lengthDecrease / table[i].getProfit() >= hBest) {
                hBest = lengthDecrease / table[i].getProfit();
                improved = true;
                pointToRemove = table[i];
            }
        }
        if (improved) {
            logger.info("Removing {}", pointToRemove);
            route.remove(pointToRemove);
            unusedPoints.add(pointToRemove);
            logger.info("Route after remove: {}", route);
        }
        return improved;
    }

    /**
     * zamienia dwie krawędzie znajdujące się na trasie na 2 inne, które powodują skrócenie trasy
     * Techniczna sprawa: wymiana krawędzi polega na odwróceniu fragmentu trasy pomiędzy punktami i+1 i j
     * np z trasy a -> b -> c -> d -> e -> f, jeśli i+1=b a e=j, to w wyniku 2opt dostaniemy a -> e -> d -> c -> b -> f
     *
     * @param route
     * @param toFirstHit czy funkcja ma znaleźć pierwsze poprawiające trasę dopasowanie czy też takie, które da najlepsze rezultaty
     * @return czy udało się poprawić trasę
     */
    protected boolean local2OPT(Route route, boolean toFirstHit) {
        logger.info("Starting local2OPT - {}", route);
        double totalDistance = route.getTotalDistance();
        Point[] table = route.getAsTable();
        int bestI = -1, bestJ = -1;
        double bestDistance = totalDistance;
        for (int i = 0; i < table.length - 3; i++) {
            logger.debug("First edge is ({}, {})", table[i], table[i + 1]);
            for (int j = i + 2; j < table.length - 1; j++) {
                logger.debug("Second edge is ({},{})", table[j], table[j + 1]);
                double tempDistance = totalDistance - (Graph.distanceBetween(table[i].getId(), table[i + 1].getId()) + Graph.distanceBetween(table[j].getId(), table[j + 1].getId()));
                tempDistance += Graph.distanceBetween(table[i].getId(), table[j].getId()) + Graph.distanceBetween(table[i + 1].getId(), table[j + 1].getId());
                if (tempDistance + APPROXIMATION_ERROR < bestDistance) {
                    logger.debug(tempDistance);
                    bestDistance = tempDistance;
                    if (toFirstHit) {
                        route.reverseDirection(i + 1, j);
                        logger.info("replacing edges: ({},{}), ({},{})", table[i], table[(i + 1)], table[j], table[(j + 1)]);
                        logger.info("Route after operation: {}", route);
                        return true;
                    } else {
                        bestI = i;
                        bestJ = j;
                    }

                }
            }
        }
        if (bestI != -1 && bestJ != -1 && !toFirstHit) {
            route.reverseDirection(bestI + 1, bestJ);
            logger.info("replacing edges: ({},{}), ({},{})", table[bestI], table[(bestI + 1)], table[bestJ], table[(bestJ + 1)]);
            logger.info("Route after operation: {}", route);
            return true;
        }
        return false;
    }

    /**
     * Z póli wolnych punktów wybiera kolejne wierzchołki i próbuje zamienić je z innymi znajdującymi się na trasie
     * Nowy wierzchołek nie jest wstawiany na miejsce starego ale w najlepsze możliwe miejsce
     *
     * @param route
     * @return czy udało się poprawić trasę
     */
    protected boolean localReplace(Route route) {
        logger.info("Starting localReplace - {}", route);
        boolean improved = false;
        boolean zeroOrLessResult = false;
        Point pointToRemove = null, pointToInsert = null;
        double totalDistance = route.getTotalDistance();
        double bestProfitGain = Double.MIN_VALUE;
        double bestH = Double.MIN_VALUE;
        int insertAfterId = -1;
        Point[] table = route.getAsTable();

        for (Point freePoint : unusedPoints) {
            logger.debug("\n\nChecking point to insert: {}", freePoint);
            int posInTable = getBestInsertPos(freePoint, route); // position after which element will be placed
            logger.debug("Insert after index: {}", posInTable);
            for (int i = 1; i < table.length - 1; i++) {
                double profitGain = freePoint.getProfit() - table[i].getProfit();
                int tempPosInTable = posInTable;
                logger.debug("\nPoint to check: {}, Profit gain: {}", table[i], profitGain);
                if (profitGain <= 0) { // skip replace if there is no profit gain
                    continue;
                }
                // route change after remove
                double newDistance = totalDistance;
                if (i == tempPosInTable/*delete from left*/ || i - 1 == tempPosInTable/*delete from right*/) { // insert point neighbourhood
                    logger.debug("Remove point {} is in neighbourhood of insert point {}", table[i], freePoint);
                    Route tempRoute = route.copy();
                    tempRoute.remove(table[i]);
                    //tempRoute.setTotalDistance(newDistance);
                    tempPosInTable = getBestInsertPos(freePoint, tempRoute);
                    logger.debug("New position after: {}", tempPosInTable);
                    if (tempPosInTable >= i) {
                        logger.debug("Position is higher or equal delete pos {} and will be increased", i);
                        tempPosInTable++;
                    }
                    if (i == tempPosInTable/*insert from right*/) { //normal replace! i is removed and new point is put there
                        //remove old point values
                        newDistance -= (Graph.distanceBetween(table[tempPosInTable - 1], table[tempPosInTable]) + Graph.distanceBetween(table[tempPosInTable], table[tempPosInTable + 1]));
                        //add new point in same place
                        newDistance += Graph.distanceBetween(table[tempPosInTable - 1], freePoint) + Graph.distanceBetween(freePoint, table[tempPosInTable + 1]);
                        logger.debug("Insert after removed element - new distnace: {}", newDistance);
                    } else if (i - 1 == tempPosInTable/*insert from left*/) { //normal replace!
                        //remove old point values - removed value is from right so +2 should be used to skip it
                        newDistance -= (Graph.distanceBetween(table[tempPosInTable], table[tempPosInTable + 1]) + Graph.distanceBetween(table[tempPosInTable + 1], table[tempPosInTable + 2]));
                        //add new point in same place
                        newDistance += Graph.distanceBetween(table[tempPosInTable], freePoint) + Graph.distanceBetween(freePoint, table[tempPosInTable + 2]);
                        logger.debug("Insert before removed element - new distance: {}", newDistance);
                    } else { // new insert point is not neighbour of deleted point - normal calculation should be performed
                        // remove point
                        newDistance += (Graph.distanceBetween(table[i - 1], table[i + 1]) - (Graph.distanceBetween(table[i], table[i + 1]) + Graph.distanceBetween(table[i - 1], table[i])));
                        // add new point
                        newDistance += Graph.distanceBetween(table[tempPosInTable], freePoint) + Graph.distanceBetween(freePoint, table[tempPosInTable + 1]) - Graph.distanceBetween(table[tempPosInTable], table[tempPosInTable + 1]);
                        logger.debug("Insert away from removed element - new distance: {}", newDistance);
                    }
                } else { // normal situation
                    // remove point
                    newDistance += (Graph.distanceBetween(table[i - 1], table[i + 1]) - (Graph.distanceBetween(table[i], table[i + 1]) + Graph.distanceBetween(table[i - 1], table[i])));
                    // add new point
                    newDistance += Graph.distanceBetween(table[tempPosInTable], freePoint) + Graph.distanceBetween(freePoint, table[tempPosInTable + 1]) - Graph.distanceBetween(table[tempPosInTable], table[tempPosInTable + 1]);
                    logger.debug("Points away from each other - new distance: {}", newDistance);
                }
                double distanceIncrease = newDistance - totalDistance;
                logger.debug("Distance increase: {}", distanceIncrease);
                if (distanceIncrease > 0 && !zeroOrLessResult) { // insert with distance increase
                    logger.debug("Standard h check");
                    if (isAcceptable(newDistance)) {
                        logger.debug("Length is acceptable");
                        double h = profitGain / distanceIncrease;
                        logger.debug("New h: {}, old h: {}", h, bestH);
                        if (h >= bestH) {
                            improved = true;
                            bestH = h;
                            pointToInsert = freePoint;
                            pointToRemove = table[i];
                            insertAfterId = table[tempPosInTable].getId();
                            logger.debug("New candidates: insert - {} after {} remove - {}", pointToInsert, insertAfterId, pointToRemove);
                        }
                    } else {
                        logger.debug("Path is too long: {}", newDistance);
                    }
                } else if (distanceIncrease <= 0) { // proffered insert with length decrease
                    logger.debug("Profit based check");
                    if (bestProfitGain <= profitGain) {
                        improved = true;
                        zeroOrLessResult = true;
                        bestProfitGain = profitGain;
                        pointToInsert = freePoint;
                        pointToRemove = table[i];
                        insertAfterId = table[tempPosInTable].getId();
                        logger.debug("New candidates: insert - {} after {} remove - {}" + pointToInsert, insertAfterId, pointToRemove);
                    }
                }
            }
        }
        if (improved) {
            route.addAfter(pointToInsert, insertAfterId);
            route.remove(pointToRemove);
            unusedPoints.remove(pointToInsert);
            unusedPoints.add(pointToRemove);
            logger.info("Replacing {} with {} inserted after {}", pointToRemove, pointToInsert, insertAfterId);
        }
        logger.info("Route after local replace {}\tcomputed distance: {}", route, route.computeTotalDistance());
        return improved;
    }

    private int getBestInsertPos(Point pointToCheck, Route route) {
        logger.debug("\tLooking for insert place");
        double hBest = Double.MIN_VALUE;
        int insertAfter = -1;
        double totalDistance = route.getTotalDistance();
        Point[] table = route.getAsTable();

        for (int i = 0; i < table.length - 1; i++) {
            double newDistance = totalDistance;
            newDistance -= Graph.distanceBetween(table[i], table[i + 1]);
            newDistance += Graph.distanceBetween(table[i], pointToCheck) + Graph.distanceBetween(pointToCheck, table[i + 1]);
            double lengthIncrease = newDistance - totalDistance;
            if (pointToCheck.getProfit() / lengthIncrease >= hBest) {
                logger.debug("\tNew best insert after: {} old h: {}, new h: {} length increase: {}", table[i], hBest, pointToCheck.getProfit() / lengthIncrease, lengthIncrease);
                hBest = pointToCheck.getProfit() / lengthIncrease;
                insertAfter = i;
            }
        }
        return insertAfter;
    }

    /**
     * Wymienia 2 wierzchołki na trasie. Nie testowane
     *
     * @param route
     * @return czy trasa się poprawiła
     */
    public boolean localSwap(Route route) {
        logger.info("Starting localSwap");
        double minLengthIncrease = Double.MAX_VALUE;
        boolean improved = false;
        double totalDistance = route.getTotalDistance();
        Point[] table = route.getAsTable();
        Point pointToReplace = null, secondPointToReplace = null;

        for (int i = 1; i < table.length - 2; i++) {
            for (int j = i + 1; j < table.length - 1; j++) {
                //remove old distances
                double newDistance = totalDistance;
                if (i - j == 1) { // points are neighbours
                    newDistance -= Graph.distanceBetween(table[i - 1], table[i]) + Graph.distanceBetween(table[j], table[j + 1]);
                    //add new distances
                    newDistance += Graph.distanceBetween(table[i - 1], table[j]) + Graph.distanceBetween(table[i], table[j + 1]);
                } else {
                    newDistance -= Graph.distanceBetween(table[i - 1], table[i]) + Graph.distanceBetween(table[i], table[i + 1]);
                    newDistance -= Graph.distanceBetween(table[j - 1], table[j]) + Graph.distanceBetween(table[j], table[j + 1]);
                    //add new distances
                    newDistance += Graph.distanceBetween(table[i - 1], table[j]) + Graph.distanceBetween(table[j], table[i + 1]);
                    newDistance += Graph.distanceBetween(table[j - 1], table[i]) + Graph.distanceBetween(table[i], table[j + 1]);
                }

                double lengthIncrease = newDistance - totalDistance;
                if (lengthIncrease <= minLengthIncrease) {
                    minLengthIncrease = lengthIncrease;
                    improved = true;
                    pointToReplace = table[i];
                    secondPointToReplace = table[j];
                }
            }
        }
        if (improved) {
            logger.info("Replacing {} and {}", pointToReplace.toString(), secondPointToReplace);
            logger.info("Route after replace: {}", route);
            route.replace(pointToReplace, secondPointToReplace);
        }
        return improved;
    }

    /**
     * metoda zaburzania trasy. Dobrze działa
     *
     * @param route
     * @return nowa trasa
     */
    protected Route doubleBridgePerturbation(Route route) {
        Random rand = new Random(System.currentTimeMillis());
        int size = route.size();
        int pos1 = 1 + rand.nextInt(size / 4);
        int pos2 = pos1 + 1 + rand.nextInt(size / 4);
        int pos3 = pos2 + 1 + rand.nextInt(size / 4);
        Point[] table = route.getAsTable();
        Route newRoute = new Route();
        for (int i = 0; i < pos1; i++) {
            newRoute.add(table[i]);
        }
        for (int i = pos3; i < table.length - 1; i++) { // route is a cycle so last element is the same as first and should be added at the end
            newRoute.add(table[i]);
        }
        for (int i = pos2; i < pos3; i++) {
            newRoute.add(table[i]);
        }
        for (int i = pos1; i < pos2; i++) {
            newRoute.add(table[i]);
        }
        newRoute.add(table[table.length - 1]);
        while (!isAcceptable(newRoute.getTotalDistance())) {
            randomRemove(newRoute, 1);
        }
        return newRoute;
    }

    /**
     * W testach słabo wypada
     *
     * @param route
     * @param length
     */
    protected void randomRemove(Route route, int length) {
        if (route.size() > 2 + length) {
            Point[] table = route.getAsTable();
            int randomValue;
            Random random = new Random(System.currentTimeMillis());
            do {
                randomValue = random.nextInt(table.length - 2) + 1;
            } while (randomValue + length > table.length - 1);

            for (int i = randomValue; i < length + randomValue; i++) {
                route.remove(table[i]);
                unusedPoints.add(table[i]);
            }
        }
    }

    /**
     * 2opt na losowych krawędziach
     *
     * @param route
     */
    protected void random2Opt(Route route) {
        if (route.size() < 3) {
            return;
        }
        Random random = new Random(System.currentTimeMillis());
        int[] randomPoints = new int[2];
        Integer counter = 0;
        do {
            randomPoints[0] = random.nextInt(route.size() - 2) + 1; // TODO test!
            randomPoints[1] = random.nextInt(route.size() - 2) + 1;
            counter++;
        } while (Math.abs(randomPoints[0] - randomPoints[1]) < 2 && counter < MAX_ATTEMPTS);

        if (counter < MAX_ATTEMPTS) {
            if (randomPoints[0] > randomPoints[1]) {
                int temp = randomPoints[0];
                randomPoints[0] = randomPoints[1];
                randomPoints[1] = temp;
            }
            logger.debug("replacing edges: ({},{}), ({},{})", randomPoints[0], (randomPoints[0] + 1), randomPoints[1], (randomPoints[1] + 1));
            route.reverseDirection(randomPoints[0] + 1, randomPoints[1]);
            while (!isAcceptable(route.getTotalDistance())) {
                randomRemove(route, 1);
            }
        } else {
            logger.error("Random2Opt has failed! last random points: {}, {}\nroute:{}", randomPoints[0], randomPoints[1],route);
        }
    }

    protected boolean isAcceptable(double length) {
        return length <= lengthLimit;
    }
}
