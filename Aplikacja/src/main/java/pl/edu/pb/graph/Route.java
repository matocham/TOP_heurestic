package pl.edu.pb.graph;


import pl.edu.pb.graph.util.Element;

import java.util.Iterator;

/**
 * Created by Dominik on 2017-05-07.
 */
public class Route implements Iterable<Point> {
    int totalProfit;
    double totalDistance;

    public Route() {
        totalProfit = 0;
    }

    Element start;
    Element end;
    int size = 0;

    public void addStart(Point p) {
        add(p);
        add(p);
    }

    public void add(Point p) {
        if (start == null) {
            start = new Element(p);
            end = start;
        } else if (start.equals(end)) {
            Element element = new Element(p);
            element.setPrev(start);
            start.setNext(element);
            end = element;
            totalDistance += Graph.distanceBetween(start.getPoint(), p);
        } else {
            Element element = new Element(p);
            element.setPrev(end);
            end.setNext(element);
            end = element;
            totalDistance += Graph.distanceBetween(end.getPrev().getPoint(), p);
        }
        size++;
        totalProfit += p.getProfit();
    }

    public void addBefore(Point p, int followingPointId) {
        Element beforeElement = null;
        Element walker = start;
        while (walker != null) {
            if (walker.getPoint().getId() == followingPointId) {
                beforeElement = walker;
                break;
            }
            walker = walker.getNext();
        }
        addBefore(p, beforeElement);
    }

    public void addBeforeLast(Point p) {
        Element beforeElement = end;
        addBefore(p, beforeElement);
    }

    /**
     * adds p before followingElement
     *
     * @param p
     * @param followingElement
     */
    private void addBefore(Point p, Element followingElement) {
        if (followingElement != null) {
            Element newElement = new Element(p);
            if (followingElement.equals(start)) {
                totalDistance += Graph.distanceBetween(p, followingElement.getPoint());
                newElement.setNext(start);
                start.setPrev(newElement);
                start = newElement;
            } else {
                totalDistance += Graph.distanceBetween(followingElement.getPrev().getPoint(), p) + Graph.distanceBetween(p, followingElement.getPoint());
                totalDistance -= Graph.distanceBetween(followingElement.getPoint(), followingElement.getPrev().getPoint());
                newElement.setPrev(followingElement.getPrev());
                newElement.setNext(followingElement);
                followingElement.setPrev(newElement);
                newElement.getPrev().setNext(newElement);
            }
            size++;
            totalProfit += p.getProfit();
        }
    }

    public void addAfter(Point p, int precedingPointId) {
        Element precedingElement = null;
        Element walker = start;
        while (walker != null) {
            if (walker.getPoint().getId() == precedingPointId) {
                precedingElement = walker;
                break;
            }
            walker = walker.getNext();
        }
        if (precedingElement != null) {
            Element newElement = new Element(p);
            if (precedingElement.equals(end)) {
                totalDistance += Graph.distanceBetween(end.getPoint(), p);
                newElement.setPrev(end);
                end.setNext(newElement);
                end = newElement;
            } else {
                totalDistance += Graph.distanceBetween(precedingElement.getPoint(), p) + Graph.distanceBetween(p, precedingElement.getNext().getPoint());
                totalDistance -= Graph.distanceBetween(precedingElement.getPoint(), precedingElement.getNext().getPoint());
                newElement.setPrev(precedingElement);
                newElement.setNext(precedingElement.getNext());
                precedingElement.setNext(newElement);
                newElement.getNext().setPrev(newElement);
            }
            size++;
            totalProfit += p.getProfit();
        }
    }

    public void replace(Point p, int pointIdToBeReplaced) {
        Element foundElement = null;
        Element walker = start;
        while (walker != null) {
            if (walker.getPoint().getId() == pointIdToBeReplaced) {
                foundElement = walker;
                break;
            }
            walker = walker.getNext();
        }
        if (foundElement != null) {
            Element newElement = new Element(p);
            if (foundElement.equals(start)) {
                newElement.setNext(start.getNext());
                newElement.getNext().setPrev(newElement);
                start = newElement;
                totalDistance -= Graph.distanceBetween(foundElement.getPoint(), foundElement.getNext().getPoint());
                totalDistance += Graph.distanceBetween(newElement.getPoint(), newElement.getNext().getPoint());
            } else if (foundElement.equals(end)) {
                newElement.setPrev(end.getPrev());
                newElement.getPrev().setNext(newElement);
                end = newElement;
                totalDistance -= Graph.distanceBetween(foundElement.getPoint(), foundElement.getPrev().getPoint());
                totalDistance += Graph.distanceBetween(newElement.getPoint(), newElement.getPrev().getPoint());
            } else {
                newElement.setPrev(foundElement.getPrev());
                newElement.setNext(foundElement.getNext());
                newElement.getPrev().setNext(newElement);
                newElement.getNext().setPrev(newElement);
                totalDistance -= Graph.distanceBetween(foundElement.getPoint(), foundElement.getNext().getPoint()) + Graph.distanceBetween(foundElement.getPoint(), foundElement.getPrev().getPoint());
                totalDistance += Graph.distanceBetween(newElement.getPoint(), newElement.getNext().getPoint()) + Graph.distanceBetween(newElement.getPoint(), newElement.getPrev().getPoint());
            }
            totalProfit += p.getProfit() - foundElement.getPoint().getProfit();
        }
    }

    public void replace(int idA, int idB) {
        if (idA == idB) {
            return;
        }
        Element elementA = null, elementB = null;
        Element walker = start;
        while (walker != null) {
            if (walker.getPoint().getId() == idA) {
                elementA = walker;
            } else if (walker.getPoint().getId() == idB) {
                elementB = walker;
            }
            if (elementA != null && elementB != null) {
                break;
            }
            walker = walker.getNext();
        }

        if (elementA != null && elementB != null) {
            if (elementA.equals(start)) {
                start = elementB;
            }
            if (elementA.equals(end)) {
                end = elementB;
            }

            if (elementB.equals(start)) {
                start = elementA;
            }
            if (elementB.equals(end)) {
                end = elementA;
            }
            Element aPrev = elementA.getPrev();
            Element bPrev = elementB.getPrev();
            Element aNext = elementA.getNext();
            Element bNext = elementB.getNext();

            if (aPrev != null) {
                totalDistance -= Graph.distanceBetween(elementA.getPoint(), aPrev.getPoint());
            }
            if (bPrev != null) {
                totalDistance -= Graph.distanceBetween(elementB.getPoint(), bPrev.getPoint());
            }
            if (aNext != null && !aNext.equals(elementB)) {
                totalDistance -= Graph.distanceBetween(elementA.getPoint(), aNext.getPoint());
            }
            if (bNext != null && !bNext.equals(elementA)) {
                totalDistance -= Graph.distanceBetween(elementB.getPoint(), bNext.getPoint());
            }
            replaceLinksNext(elementA, elementB, bNext);
            replaceLinksNext(elementB, elementA, aNext);
            replaceLinksPrev(elementA, elementB, bPrev);
            replaceLinksPrev(elementB, elementA, aPrev);
            if (elementA.getPrev() != null) {
                totalDistance += Graph.distanceBetween(elementA.getPoint(), elementA.getPrev().getPoint());
            }
            if (elementB.getPrev() != null) {
                totalDistance += Graph.distanceBetween(elementB.getPoint(), elementB.getPrev().getPoint());
            }
            if (elementA.getNext() != null && !elementA.getNext().equals(elementB)) {
                totalDistance += Graph.distanceBetween(elementA.getPoint(), elementA.getNext().getPoint());
            }
            if (elementB.getNext() != null && !elementB.getNext().equals(elementA)) {
                totalDistance += Graph.distanceBetween(elementB.getPoint(), elementB.getNext().getPoint());
            }
        }
    }

    private void replaceLinksPrev(Element elementA, Element elementB, Element bPrev) {
        if (elementA.equals(bPrev)) {
            elementA.setPrev(elementB);
        } else {
            elementA.setPrev(bPrev);
            if (bPrev != null) {
                bPrev.setNext(elementA);
            }
        }
    }

    private void replaceLinksNext(Element elementA, Element elementB, Element bNext) {
        if (elementA.equals(bNext)) {
            elementA.setNext(elementB);
        } else {
            elementA.setNext(bNext);
            if (bNext != null) {
                bNext.setPrev(elementA);
            }
        }
    }

    public void replace(Point pointA, Point pointB) {
        replace(pointA.getId(), pointB.getId());
    }

    public void remove(int pointId) {
        Element foundElement = null;
        Element walker = start;
        while (walker != null) {
            if (walker.getPoint().getId() == pointId) {
                foundElement = walker;
                break;
            }
            walker = walker.getNext();
        }
        if (foundElement != null) {
            if (foundElement.equals(start)) {
                totalDistance -= Graph.distanceBetween(start.getPoint(), start.getNext().getPoint());
                start = start.getNext();
            } else if (foundElement.equals(end)) {
                totalDistance -= Graph.distanceBetween(end.getPoint(), end.getNext().getPoint());
                end = end.getPrev();
            } else {
                totalDistance -= Graph.distanceBetween(foundElement.getPoint(), foundElement.getNext().getPoint()) + Graph.distanceBetween(foundElement.getPoint(), foundElement.getPrev().getPoint());
                totalDistance += Graph.distanceBetween(foundElement.getPrev().getPoint(), foundElement.getNext().getPoint());
                foundElement.getNext().setPrev(foundElement.getPrev());
                foundElement.getPrev().setNext(foundElement.getNext());
            }
            size--;
            totalProfit -= foundElement.getPoint().getProfit();
        } else {
            throw new RuntimeException("Element to remove not found!" +pointId);
        }
    }

    public Point get(int index) {
        if (index > size()) {
            return null;
        }

        for (Point p : this) {
            if (index == 0) {
                return p;
            }
            index--;
        }
        return null;
    }

    public Point[] getAsTable() {
        Point[] table = new Point[size()];
        int index = 0;
        for (Point p : this) {
            table[index] = p;
            index++;
        }
        return table;
    }

    /**
     * reverse direction from position fromIndex to position toIndex
     *
     * @param fromIndex
     * @param toIndex
     */
    public void reverseDirection(int fromIndex, int toIndex) {
        Point[] table = getAsTable();
        for (int i = 0; i <= (toIndex - fromIndex) / 2; i++) { // TODO improve performance if required
            replace(table[fromIndex + i].getId(), table[toIndex - i].getId());
        }
    }

    public Route copy() {
        Route copy = new Route();
        for (Point p : this) {
            copy.add(p);
        }
        return copy;
    }

    public double getLengthIncreaseAfterAddBeforeLast(Point pointToInsert) {
        if (end != null && end.getPrev() != null) {
            double lengthIncrease = Graph.distanceBetween(end.getPrev().getPoint(), pointToInsert) + Graph.distanceBetween(pointToInsert, end.getPoint()) - Graph.distanceBetween(end.getPrev().getPoint(), end.getPoint());
            return lengthIncrease;
        }
        return 0;
    }

    public void validateRoute() {
        if (size() < 3) {
            throw new RuntimeException("Route is too short");
        }
        if (computeTotalDistance() != getTotalDistance()) {
            throw new RuntimeException("Route has improper distance!");
        }

        if (computeTotalProfit() != getTotalProfit()) {
            throw new RuntimeException("Route has improper profit!");
        }
        Point[] table = getAsTable();
        for (int i = 1; i < table.length - 1; i++) {
            for (int j = i + 1; j < table.length - 1; j++) {
                if (table[i].equals(table[j])) {
                    throw new RuntimeException("Route has repeting elements! "+table[i]+" on position "+i+" and "+j);
                }
            }
        }
    }

    public double computeTotalDistance() {
        Point[] table = getAsTable();
        double totalDistance = 0;
        for (int i = 0; i < table.length - 1; i++) {
            totalDistance += Graph.distanceBetween(table[i].getId(), table[i + 1].getId());
        }
        return totalDistance;
    }

    private int computeTotalProfit() {
        int totalProfit = 0;
        for (Point p : this) {
            totalProfit += p.getProfit();
        }
        return totalProfit;
    }

    public boolean contains(int pointId) {
        for (Point p : this) {
            if (p.getId() == pointId) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return start == null && end == null;
    }

    public int size() {
        return size;
    }

    public boolean contains(Point p) {
        return contains(p.getId());
    }

    public void remove(Point p) {
        remove(p.getId());
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public Iterator<Point> iterator() {
        return new Iterator() {
            Element current = start;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Point next() {
                Element cur = current;
                current = current.getNext();
                return cur.getPoint();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Point p : this) {
            builder.append(p.getId()).append(", ");
        }
        builder.replace(builder.lastIndexOf(","), builder.length(), "");
        builder.append(" profit:").append(totalProfit).append(", distance:").append(totalDistance);
        return builder.toString();
    }
}
