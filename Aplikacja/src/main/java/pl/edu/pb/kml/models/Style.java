package pl.edu.pb.kml.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by matocham on 15.05.2017.
 */
public class Style {
   LineStyle lineStyle;

   @XmlElement(name = "LineStyle")
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }
}
