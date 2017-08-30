package pl.edu.pb.algorithm.solution;

import org.apache.logging.log4j.Logger;
import pl.edu.pb.algorithm.solution.utils.PointWithH;
import pl.edu.pb.algorithm.utils.PointComparator;
import pl.edu.pb.algorithm.utils.ReversePointComparator;
import pl.edu.pb.graph.Graph;
import pl.edu.pb.graph.Point;
import pl.edu.pb.graph.Route;

import java.util.*;

/**
 * Created by matocham on 11.05.2017.
 * Contains set of routes that represent one solution containing multiple routes that starts from the same point
 * and do not contain points duplicates
 */
public class Solution extends LocalOperationBased implements LocalOperations {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(Solution.class);
    public static final int REMOVE_LENGTH = 1;

    int startPoint;
    int capacity;
    int size;
    Route[] routes;
    Point[] allPoints;

    /**
     * Initializes solution
     *
     * @param points     pl.edu.pb.util of points that will create route
     * @param startPoint index of starting point contained in points table
     * @param maxLength  max single route length
     * @param maxSize    max number of routes
     */
    public Solution(Point[] points, int startPoint, double maxLength, int maxSize) {
        super(points, maxLength);
        this.startPoint = startPoint;
        this.capacity = maxSize;
        this.allPoints = points;
        size = 0; // size is used to determine real number of routes added to solution. Just in case
        routes = new Route[maxSize];

    }

    public void addRoute(Route route) {
        if (size < capacity) {
            routes[size] = route;
            Point[] tableRoute = route.getAsTable();
            unusedPoints.remove(tableRoute[0]);
            for (int j = 1; j < tableRoute.length - 1; j++) {
                for (int i = 0; i < size; i++) {
                    if (routes[i].contains(tableRoute[j])) {
                        throw new RuntimeException("Can not add new route\n" + route + "\nbecause point " + tableRoute[i] + " is already used in route\n" + routes[i]);
                    }
                }
                unusedPoints.remove(tableRoute[j]);
            }
            size++;
        } else {
            throw new ArrayIndexOutOfBoundsException("Can not insert more than " + capacity + " elements.");
        }
    }

    public void isValid() {
        for (int i = 0; i < size; i++) {
            Point[] table = routes[i].getAsTable();
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    for (int k = 1; k < table.length - 1; k++) {
                        if (routes[j].contains(table[k])) {
                            throw new RuntimeException("Found duplicates on routes " + routes[i] + " on position " + k + " and route " + routes[j]);
                        }
                    }
                }
            }
        }
        for (Route r : routes) {
            if (r != null) {
                for (Point p : unusedPoints) {
                    if (r.contains(p)) {
                        throw new RuntimeException("route contains point that should be unused " + p);
                    }
                }
                r.validateRoute();
            }
        }
        if (copmuteToalDistance() > lengthLimit * size) {
            throw new RuntimeException("Path is too long");
        }
        double totalDistance = 0;
        double totalProfit = 0;
        for (Route r : routes) {
            Point[] points2 = r.getAsTable();
            for (int i = 0; i < points2.length - 1; i++) {
                totalDistance += Graph.distanceBetween(points2[i], points2[i + 1]);
                totalProfit += points2[i].getProfit();
            }
        }
        if (totalDistance != getTotalLength()) {
            throw new RuntimeException("Computed path length is different from saved in paths");
        }
        if (totalProfit != getToalProfit()) {
            throw new RuntimeException("Computed path profit is different from saved in paths");
        }
    }

    public double copmuteToalDistance() {
        double totalProfit = 0;
        for (int i = 0; i < size; i++) {
            totalProfit += routes[i].computeTotalDistance();
        }
        return totalProfit;
    }

    public void generateRandomSolution() {
        generateGreedyRandomizedSolution(1);
    }

    public void generateGreedySolution() {
        generateGreedyRandomizedSolution(0);
    }

    /**
     * generates reedy randomized routes adding one point to each route in single iteration
     *
     * @param alpha randomness factor: 1 - works like random. 0 - works like greedy
     */
    public void generateGreedyRandomizedSolution(double alpha) {
        Random random = new Random(System.currentTimeMillis());
        boolean[] canAddMore = new boolean[capacity];
        prepareRoutes(canAddMore);

        do {
            for (int i = 0; i < capacity; i++) { // add one element to one route
                if (canAddMore[i]) { // add elements if route could be improved
                    List<Point> rcl = getRCL(routes[i], alpha);
                    if (rcl.isEmpty()) {
                        canAddMore[i] = false; // no elements matching found - exit
                    } else {
                        int pointToAdd = random.nextInt(rcl.size());
                        Point point = rcl.get(pointToAdd);
                        routes[i].addBeforeLast(point);
                        unusedPoints.remove(point);
                    }
                }
            }
        } while (atLeastOneCanBeAdded(canAddMore));
    }

    private void prepareRoutes(boolean[] canAddMore) {
        for (int i = 0; i < canAddMore.length; i++) {
            canAddMore[i] = true;
            routes[i] = new Route();
            routes[i].addStart(allPoints[startPoint]);
        }
        unusedPoints.remove(allPoints[startPoint]);
        size = routes.length;
    }

    /**
     * the bigger alpha is the more random values will be produced
     *
     * @param route route, in which new element will be added
     * @param alpha randomness factor 1 - works random, 0 -works like greedy
     * @return pl.edu.pb.util of possible values
     */
    private List<Point> getRCL(Route route, double alpha) {
        List<PointWithH> hResults = new ArrayList<>();
        double minH = Double.MAX_VALUE, maxH = Double.MIN_VALUE;
        for (Point point : unusedPoints) {
            double distanceIncrease = route.getLengthIncreaseAfterAddBeforeLast(point);
            if (isAcceptable(distanceIncrease + route.getTotalDistance())) { // ad as candidate only if can really be added to route
                double h = point.getProfit() / distanceIncrease;
                if (Double.isInfinite(h)) {
                    h = Double.MAX_VALUE;
                }
                PointWithH candidate = new PointWithH(point, h);
                hResults.add(candidate);
                if (candidate.h > maxH) {
                    maxH = candidate.h;
                }
                if (candidate.h < minH) {
                    minH = candidate.h;
                }
            } else {
                logger.info("Path is too long");
            }
        }
        if (!hResults.isEmpty()) {
            double minVal = maxH - alpha * (maxH - minH);
            List<Point> rcl = new ArrayList<>();
            for (PointWithH candidate : hResults) {
                if (candidate.h >= minVal) {
                    rcl.add(candidate.point);
                }
            }
            return rcl;
        }
        return Collections.emptyList();
    }

    private boolean atLeastOneCanBeAdded(boolean[] canAddMore) {
        for (int i = 0; i < canAddMore.length; i++) {
            if (canAddMore[i] == true) {
                return true;
            }
        }
        return false;
    }

    public boolean moveBetweenRoutes() {
        boolean overallResult = false;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    if (random.nextBoolean()) {
                        overallResult = moveBetweenRoutes(routes[i], routes[j]);
                    } else {
                        overallResult = moveBetweenRoutes(routes[j], routes[i]);
                    }
                }
            }
        }
        return overallResult;
    }

    /**
     * move will be performed only if profit/route ratio is grater in insertion pl.edu.pb.util and length limit is not exceeded
     *
     * @param routeA remove route
     * @param routeB insert route
     * @return if operation was performed
     */
    private boolean moveBetweenRoutes(Route routeA, Route routeB) {
        boolean improved = false;
        double hBest = Double.MIN_VALUE;
        Point bestPointToInsert = null;
        int insertAfterIndex = -1;

        Point[] tableA = routeA.getAsTable();
        Point[] tableB = routeB.getAsTable();

        for (int i = 1; i < tableA.length - 1; i++) { // do not touch start/stop points
            Point pointToInsert = tableA[i];
            // o ile trasa została skrócona
            double removeDistanceLoose = (Graph.distanceBetween(tableA[i - 1], pointToInsert) + Graph.distanceBetween(pointToInsert, tableA[i + 1])) - Graph.distanceBetween(tableA[i - 1], tableA[i + 1]);
            double pointProfit = pointToInsert.getProfit();
            double removeH = pointProfit / removeDistanceLoose; // heuristic cost of adding element into place
            for (int j = 0; j < tableB.length - 1; j++) { // do not insert after last element
                // heuristic cost of adding element after j
                double insertDistanceChange = Graph.distanceBetween(tableB[j], pointToInsert) + Graph.distanceBetween(pointToInsert, tableB[j + 1]) - Graph.distanceBetween(tableB[j], tableB[j + 1]);
                if (isAcceptable(insertDistanceChange + routeB.getTotalDistance())) {
                    double insertH = pointProfit / insertDistanceChange;
                    double totalH = insertH - removeH;
                    if (totalH > hBest && totalH > 0) { // if there is improvement and its better than the best already found
                        improved = true;
                        bestPointToInsert = pointToInsert;
                        insertAfterIndex = tableB[j].getId(); // TODO check if correct
                        hBest = totalH;
                    }
                }
            }
        }

        if (improved) {
            logger.info("Moved point {} from route \n{}\n to route \n{}\n after point {}", bestPointToInsert, routeA, routeB, insertAfterIndex);
            routeA.remove(bestPointToInsert);
            routeB.addAfter(bestPointToInsert, insertAfterIndex);
        }
        return improved;
    }

    public double getTotalLength() {
        double totalLength = 0;
        for (int i = 0; i < size; i++) {
            totalLength += routes[i].getTotalDistance();
        }

        return totalLength;
    }

    public double getToalProfit() {
        double totalProfit = 0;
        for (int i = 0; i < size; i++) {
            totalProfit += routes[i].getTotalProfit();
        }
        return totalProfit;
    }

    public Solution relink(Solution other) {
        Solution result = new Solution(allPoints, startPoint, lengthLimit, capacity);
        for (int i = 0; i < size; i++) {
            result.unusedPoints.removeAll(Arrays.asList(routes[i].getAsTable()));
        }
        for (int i = 0; i < size; i++) { // should have same size, every route is relinked only once
            Route p = this.routes[i].copy();
            Route q = other.routes[i].copy();
            Route best = pathRelinking(p, q, result.unusedPoints); // in first iteration full pl.edu.pb.util will change nothing. In second and so on pl.edu.pb.util will not contain points added to previous route
            logger.debug("Best route from path relinking of {} and {} is {}" + p, q, best);
            result.addRoute(best);
        }
        return result;
    }

    private Route pathRelinking(Route p, Route q, List<Point> unusedPoints) {
        LinkedList<Point> pointsToRemove = new LinkedList<>(); // points that are present in p and absent in q
        LinkedList<Point> pointsToAdd = new LinkedList<>(); // points that are present in q and absent in p
        populateLists(p, q, pointsToRemove, pointsToAdd);

        Route bestIntermidiateRoute = p.copy();
        pointsToAdd.removeIf(x -> !unusedPoints.contains(x)); //remove points that are already used in this solution

        while (!pointsToAdd.isEmpty()) { // while there are elements to add
            Point point = pointsToAdd.poll();
            Point[] pTable = p.getAsTable();
            int bestInsertPoint = -1;
            double lowestCostIncrease = Double.MAX_VALUE;
            for (int k = 0; k < pTable.length - 1; k++) { //find best place to add element
                double costIncrease = Graph.distanceBetween(pTable[k], point) + Graph.distanceBetween(point, pTable[k + 1]) - Graph.distanceBetween(pTable[k], pTable[k + 1]);
                if (costIncrease <= lowestCostIncrease) {
                    lowestCostIncrease = costIncrease;
                    bestInsertPoint = pTable[k].getId();
                }
            }
            logger.debug("Inserting point {} after {} in route {}", point, bestInsertPoint, p);
            p.addAfter(point, bestInsertPoint);
            while (!isAcceptable(p.getTotalDistance())) { // remove elements till route is too long
                Point pointToRemove = pointsToRemove.poll();
                if (pointToRemove == null) {
                    break;
                }
                logger.debug("Route {} is too long. Removing {}", p, pointToRemove);
                p.remove(pointToRemove);
            }
            if (isAcceptable(p.getTotalDistance()) && p.getTotalProfit() > bestIntermidiateRoute.getTotalProfit()) {
                logger.info("New route is better than previous one. New best route is {}", p);
                bestIntermidiateRoute = p.copy();
            }
        }
        return bestIntermidiateRoute;
    }

    private void populateLists(Route p, Route q, LinkedList<Point> vPQ, LinkedList<Point> vQP) {
        Point[] pTable = p.getAsTable();
        Point[] qTable = q.getAsTable();

        for (int k = 0; k < pTable.length || k < qTable.length; k++) {
            if (k < pTable.length) {
                if (!contains(qTable, pTable[k])) {
                    vPQ.add(pTable[k]); // candidates to delete from P
                }
            }
            if (k < qTable.length) {
                if (!contains(pTable, qTable[k])) {
                    vQP.add(qTable[k]); // candidates to insert to P
                }
            }
        }
        vPQ.sort(new PointComparator());
        vQP.sort(new ReversePointComparator());
    }

    private boolean contains(Point[] table, Point point) {
        for (Point p : table) {
            if (p.equals(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove() {
        boolean overallResult = false;
        for (Route r : routes) {
            boolean localResult = localRemove(r);
            if (localResult) {
                overallResult = localResult;
            }
        }
        return overallResult;
    }

    @Override
    public boolean insert() {
        boolean overallResult = false;

        for (Route r : routes) {
            boolean localResult = localInsert(r);
            if (localResult) {
                overallResult = localResult;
            }
        }
        return overallResult;
    }

    @Override
    public boolean local2OPT(boolean toFirstHit) {
        boolean overallResult = false;
        for (Route r : routes) {
            boolean localResult = local2OPT(r, toFirstHit);
            if (localResult) {
                overallResult = localResult;
            }
            //isValid();
        }
        return overallResult;
    }

    @Override
    public boolean replace() {
        boolean overallResult = false;
        for (Route r : routes) {
            boolean localResult = localReplace(r);
            if (localResult) {
                overallResult = localResult;
            }
        }
        return overallResult;
    }

    @Override
    public void disturb2OPT() {
        for (Route r : routes) {
            random2Opt(r);
        }
    }

    @Override
    public void distrubRemove() {
        for (Route r : routes) {
            randomRemove(r, REMOVE_LENGTH);
        }
    }

    @Override
    public void doubleBridgePerturbation() {
        for (int i = 0; i < size; i++) {
            routes[i] = doubleBridgePerturbation(routes[i]);
        }

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(routes[i].toString()).append("\n");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("\n").append("total distance: ").append(getTotalLength());
        stringBuilder.append(" total profit: ").append(getToalProfit());
        return stringBuilder.toString();
    }

    public Route[] getRoutes() {
        return routes;
    }

    public Solution copy() {
        Solution other = new Solution(allPoints, startPoint, lengthLimit, capacity);
        other.unusedPoints.clear();
        for (Route r : routes) {
            other.addRoute(r.copy());
        }
        return other;
    }

    /**
     * Zwraca trasę odpowiednią do sprawdzarki p. Ostrowskiego
     *
     * @return
     */
    public String getCheckableRoutes() {
        StringBuilder solution = new StringBuilder();
        for (Route r : routes) {
            for (Point p : r) {
                solution.append(p.getId() + 1).append(" ");
            }
            solution.replace(solution.lastIndexOf(" "), solution.length(), "\n");
        }
        return solution.toString();
    }
    public void generateRandomizedSolution() {
        boolean[] canAddMore = new boolean[capacity];
        prepareRoutes(canAddMore);
        double distance;
        do {
            for (int i = 0; i < capacity; i++) { // add one element to one route
                if (canAddMore[i]) { // add elements if route could be improved
                    Integer randomInt=null;
                    if(unusedPoints.size()>0)
                    randomInt = randomizePoint(unusedPoints.size());
                    Point toAdd = unusedPoints.get(randomInt);
                    distance = routes[i].getLengthIncreaseAfterAddBeforeLast(toAdd);
                    if (isAcceptable(distance + routes[i].getTotalDistance())) {
                        routes[i].addBeforeLast(toAdd);
                        unusedPoints.remove(toAdd);
                    } else {
                        canAddMore[i] = false;
                    }
                }
            }
        } while (atLeastOneCanBeAdded(canAddMore));
    }

    public Integer randomizePoint(Integer size) {
        Random generator = new Random(System.currentTimeMillis());
        Integer a = generator.nextInt(size);
        return a;

    }
}
