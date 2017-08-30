package models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Mateusz on 23.04.2017.
 */
public class Placemark {
    String name;
    String description;
    Point point;

    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name="Point")
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return name+" profit:"+description;
    }
}
