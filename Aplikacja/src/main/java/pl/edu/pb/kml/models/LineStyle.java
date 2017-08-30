package pl.edu.pb.kml.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by matocham on 15.05.2017.
 */
public class LineStyle {
    String color;

    @XmlElement(name= "color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
