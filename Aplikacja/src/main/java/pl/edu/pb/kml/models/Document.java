package pl.edu.pb.kml.models;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by Mateusz on 23.04.2017.
 */
public class Document {
    List<Placemark> locations;
    Style style;

    @XmlElement(name = "Placemark")
    public List<Placemark> getLocations() {
        return locations;
    }

    public void setLocations(List<Placemark> locations) {
        this.locations = locations;
    }

    @XmlElement(name = "Style")
    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
}
