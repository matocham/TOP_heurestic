package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mateusz on 23.04.2017.
 */
@XmlRootElement(name = "kml")
public class Kml{
    Document document;

    @XmlElement(name="Document")
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
