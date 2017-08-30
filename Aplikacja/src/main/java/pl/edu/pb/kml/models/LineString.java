package pl.edu.pb.kml.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by matocham on 15.05.2017.
 */
public class LineString {
    String coordinates;

    @XmlElement(name = "coordinates")
    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
