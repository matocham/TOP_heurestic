package pl.edu.pb.gui;

import pl.edu.pb.algorithm.Algorithm;
import pl.edu.pb.algorithm.solution.Solution;
import org.apache.logging.log4j.Logger;
import pl.edu.pb.kml.exporter.KmlExporter;

/**
 * Created by Mateusz on 14.05.2017.
 */
public class AlgorithmRunner extends Thread {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(AlgorithmRunner.class);
    private static final String DEFAULT_FILENAME = "kmlSolution.kml";

    Algorithm algorithm;
    boolean exportToKml;

    public AlgorithmRunner(Algorithm algorithm, boolean exportToKml) {
        this.algorithm = algorithm;
        this.exportToKml =exportToKml;
    }

    @Override
    public void run() {
        if (algorithm != null) {
            long startTime = System.currentTimeMillis();
            Solution solution = algorithm.calculateSolution();
            long endTime = System.currentTimeMillis();
            solution.isValid();
            logger.warn("Compute time: {}s",((double)(endTime-startTime))/1000.0);
            logger.warn("Solution to validate:\n{}",solution.getCheckableRoutes());
            if(exportToKml){
                KmlExporter.saveToFile(solution, DEFAULT_FILENAME);
            }
        }
    }
}
