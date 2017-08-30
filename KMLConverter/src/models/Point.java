package models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Mateusz on 23.04.2017.
 */
public class Point {
    String coordinates;

    @XmlElement(name="coordinates")
    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
