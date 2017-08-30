import models.Kml;
import models.Placemark;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            File file = new File("dane.kml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Kml.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Kml kml = (Kml) jaxbUnmarshaller.unmarshal(file);
            List<Placemark> locations = kml.getDocument().getLocations();

            File out = new File("dane.txt");
            FileWriter writer = new FileWriter(out);
            writer.write(locations.size()+"\n");
            for(Placemark p : locations){
                String coordinates = p.getPoint().getCoordinates();
                String[] coords = coordinates.split(", ");
                double a;
                double b;
                try{
                    a = Double.parseDouble(coords[0]);
                    b = Double.parseDouble(coords[1]);
                } catch (NumberFormatException e){
                    System.out.println("Location "+p.getName()+" has invalid coordinates");
                    throw e;
                }
                writer.write(a+" "+b+" "+p.getDescription()+"\n");
            }
            writer.close();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
