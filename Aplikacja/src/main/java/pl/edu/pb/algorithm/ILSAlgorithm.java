package pl.edu.pb.algorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.pb.algorithm.solution.Solution;
import pl.edu.pb.graph.Point;
import pl.edu.pb.gui.drawing.DrawingVisitor;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Dominik on 02.05.2017.
 */
public class ILSAlgorithm extends Algorithm {
    DrawingVisitor visitor;
    private static final Logger logger = LogManager.getLogger(ILSAlgorithm.class);
    Integer days;
    Integer solutionN;
    Integer maxInterations;
    public ILSAlgorithm(Point[] data, int startPoint, double lengthLimit, Integer days,Integer maxInterations,Integer solutionN, DrawingVisitor visitor) {
        super(data, startPoint, lengthLimit);
        this.days = days;
        this.visitor = visitor;
        this.maxInterations=maxInterations;
        this.solutionN=solutionN;
    }

    public Integer randomizePoint(Integer size) {
        Random generator = new Random();
        Integer a = generator.nextInt(size);
        return a;

    }
    @Override
    public Solution calculateSolution() {
        logger.warn("Starting alghoritm!");

        LinkedList<Point> problem = new LinkedList<>();
        for (int i = 0; i < data.length; i++) {
            problem.add(data[i]);
        }
        problem.remove(1);
        Solution solution;
        int k;
        Solution bestSolution = null;
        for (int solutionNumbers = 0; solutionNumbers < maxInterations; solutionNumbers++) {
            logger.warn("Iteration: " + solutionNumbers);
            solution = new Solution(data, startPoint, lengthLimit, days);
            solution.generateRandomizedSolution();
            boolean improvmentInsert;
            boolean improvment2Opt;
            boolean improvmentReplace;
            k = 0;
            for (int n = 0; n < solutionN; n++) {
                 do{
                    improvment2Opt = solution.local2OPT(false);
                }while(improvment2Opt);
                k++;
                if(k%2==0) {
                    solution.remove();
                }
                do{
                    improvmentInsert = solution.insert();
                }while (improvmentInsert);
             /*  do{
                    improvmentReplace=solution.replace();
                }while(improvmentReplace);*/
               if(bestSolution==null||solution.getToalProfit()>bestSolution.getToalProfit()){
                    logger.warn("New best solution: {}", solution.getToalProfit());
                    if (bestSolution != null) {
                        logger.warn(" old is {}", bestSolution.getToalProfit());
                    }
                    bestSolution=solution.copy();
                }
                visitor.drawSolution(solution,bestSolution);
                solution.disturb2OPT();
                solution.disturb2OPT();
            }
        }
        visitor.drawSolution(null, bestSolution);
        logger.warn("End of iterations. The best solution is:");
        logger.warn("\n{}", bestSolution);
        return bestSolution;
    }

}