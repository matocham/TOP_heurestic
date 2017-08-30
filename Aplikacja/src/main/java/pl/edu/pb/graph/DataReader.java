package pl.edu.pb.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Dominik on 2017-05-07.
 */
public class DataReader {
    public Point[] readTxtFormat(String path) throws FileNotFoundException {
        Point[] dataReaded = null;
        File file = new File(path);

            Scanner scanner = new Scanner(file);
            int size = Integer.parseInt(scanner.nextLine());
            dataReaded = new Point[size];
            double x, y, value;
            String bufor;
            for (int i = 0; i < size; i++) {
                bufor = scanner.nextLine();
                String s[] = bufor.split(" ");
                // second is X coordinate -> longitude -> szerokość geograficzna
                x = Double.parseDouble(s[0]);
                // second is Y coordinate -> latitude -> długość geograficzna
                y = Double.parseDouble(s[1]);
                value = Double.parseDouble(s[2]);
                dataReaded[i] = new Point(x, y, value, "null", i+1);
            }
        return dataReaded;
    }
}
