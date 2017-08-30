package pl.edu.pb.graph.util;

import pl.edu.pb.graph.Point;

/**
 * Created by Mateusz on 08.05.2017.
 */
public class Element {
    private Point point;
    private Element prev;
    private Element next;

    public Element(Point p){
        point = p;
        prev = next = null;
    }
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Element getPrev() {
        return prev;
    }

    public void setPrev(Element prev) {
        this.prev = prev;
    }

    public Element getNext() {
        return next;
    }

    public void setNext(Element next) {
        this.next = next;
    }
}
