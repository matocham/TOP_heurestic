package pl.edu.pb.kml.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Mateusz on 23.04.2017.
 */
public class Placemark {
    LineString lineString;

    @XmlElement(name = "LineString")
    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
    }
}
