package pl.edu.pb.kml.exporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.pb.algorithm.solution.Solution;
import pl.edu.pb.graph.Point;
import pl.edu.pb.graph.Route;
import pl.edu.pb.kml.models.Document;
import pl.edu.pb.kml.models.Kml;
import pl.edu.pb.kml.models.LineString;
import pl.edu.pb.kml.models.LineStyle;
import pl.edu.pb.kml.models.Placemark;
import pl.edu.pb.kml.models.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created by matocham on 15.05.2017.
 */
public class KmlExporter {
    private static final Logger logger = LogManager.getLogger(KmlExporter.class);

    public static void saveToFile(Solution solution, String fileName){
        try {
            File file = new File(fileName);

            JAXBContext jaxbContext = JAXBContext.newInstance(Kml.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Kml solutionInXml = getXmlModel(solution);
            jaxbMarshaller.marshal(solutionInXml, file);
            logger.warn("Data exported to file {}", fileName);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private static Kml getXmlModel(Solution solution) {
        Kml kml = new Kml();
        Document doc = new Document();
        kml.setDocument(doc);
        List<Placemark> locations = new ArrayList<>();
        for(Route r : solution.getRoutes()){
            Placemark placemark = new Placemark();
            LineString lineString = new LineString();
            StringBuilder pathString = new StringBuilder("\n");
            for(Point p : r){
                pathString.append(p.getX()).append(", ").append(p.getY()).append(", 0.\n");
            }
            lineString.setCoordinates(pathString.toString());
            placemark.setLineString(lineString);
            locations.add(placemark);
        }
        doc.setLocations(locations);
        Style s = new Style();
        LineStyle  lineStyle= new LineStyle();
        s.setLineStyle(lineStyle);
        lineStyle.setColor("#ff0000ff");
        return  kml;
    }
}
