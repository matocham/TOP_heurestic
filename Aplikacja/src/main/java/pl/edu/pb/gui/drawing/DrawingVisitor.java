package pl.edu.pb.gui.drawing;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.pb.algorithm.solution.Solution;
import pl.edu.pb.graph.Point;
import pl.edu.pb.graph.Route;

/**
 * Created by Mateusz on 14.05.2017.
 */
public class DrawingVisitor implements ChangeListener {
    private static final Color START_POINT_COLOR = Color.DARKMAGENTA;
    private static final Color UNVISITED_POINT_COLOR = Color.BLUE;
    private static final Color[] BEST_POINT_COLORS = new Color[]{Color.RED, Color.GREEN, Color.AQUA, Color.CHOCOLATE};
    private static final Color[] BEST_EDGE_COLOR = new Color[]{Color.RED, Color.GREEN, Color.AQUA, Color.CHOCOLATE};
    private static final Color[] CURRENT_EDGE_COLOR = new Color[]{Color.YELLOW, Color.PINK, Color.DEEPPINK, Color.WHITE};
    private static final Color[] CURRENT_POINTS_COLOR = new Color[]{Color.YELLOW, Color.PINK, Color.DEEPPINK, Color.WHITE};
    private static final int FULL_PROFIT_CIRCLE_SIZE = 20;
    private static final int LINE_WIDTH = 1;
    private static double CANVAS_TO_POINTS_RATIO;
    private static double MARGIN = 20;
    Canvas canvas;
    GraphicsContext gc;
    Point[] points;
    double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;

    /**
     * Ważna uwaga. Na komputerze wartości Y rosną w dół, zaś na półkuli północnej równoleżniki rosną do góry ku biegunowi. Przez ten efekt
     * rysowana trasa będzie odbita wokół osi X. Można to odwrócić, odejmująć każdą pozycję y od 90, zakładając, że jesteśmy na półkuli północnej
     *
     * @param canvas
     */
    public DrawingVisitor(ResizableCanvas canvas) {
        this.canvas = canvas;
        canvas.addResizeListener(this);
        gc = canvas.getGraphicsContext2D();
    }

    public void setPoints(Point[] points) {
        this.points = points;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;

        for (int i = 0; i < points.length; i++) {
            if (points[i].getX() > maxX) {
                maxX = points[i].getX();
            }
            if (points[i].getX() < minX) {
                minX = points[i].getX();
            }
            if (points[i].getY() > maxY) {
                maxY = points[i].getY();
            }
            if (points[i].getY() < minY) {
                minY = points[i].getY();
            }
        }
        recalculateRatio();
    }

    private void recalculateRatio() {
        double width = maxX - minX;
        double height = maxY - minY;

        double canvasWidth = canvas.getWidth() - MARGIN * 2;
        double canvasHeight = canvas.getHeight() - MARGIN * 2;
        double widthRatio = canvasWidth / width;
        double heightRatio = canvasHeight / height;
        double minRatio = widthRatio;
        if (heightRatio < minRatio) {
            minRatio = heightRatio;
        }
        CANVAS_TO_POINTS_RATIO = minRatio;
    }

    public void drawSolution(Solution current, Solution bestSolution) {
        Platform.runLater(() -> {
            clear();
            draw(bestSolution, current);
        });
    }

    private void draw(Solution bestSolution, Solution current) {
        if (points != null) {
            for (Point p : points) {
                drawFilledCircle(p, UNVISITED_POINT_COLOR);
            }
        }
        if (current != null) {
            drawSolution(current.copy(), CURRENT_POINTS_COLOR, CURRENT_EDGE_COLOR);
        }
        if (bestSolution != null) {
            drawSolution(bestSolution.copy(), BEST_POINT_COLORS, BEST_EDGE_COLOR);
        }
    }

    private void drawSolution(Solution current, Color[] pointColors, Color[] edgeColors) {
        Route[] routes = current.getRoutes();
        for (int i = 0; i < routes.length; i++) {
            Point[] points = routes[i].getAsTable();
            for (int j = 1; j < points.length; j++) {
                drawLineBetween(points[j - 1], points[j], edgeColors[i]);
                if (j - 1 == 0) {
                    drawFilledCircle(points[j - 1], START_POINT_COLOR);
                } else {
                    drawFilledCircle(points[j - 1], pointColors[i]);
                }
            }
            drawFilledCircle(points[points.length - 1], START_POINT_COLOR);
        }
    }

    private void drawFilledCircle(Point p, Color color) {
        double circleSize;
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(LINE_WIDTH);
        if (p.getProfit() == 0) {
            circleSize = FULL_PROFIT_CIRCLE_SIZE / 2;
        } else {
            circleSize = (p.getProfit() / 100) * FULL_PROFIT_CIRCLE_SIZE;
        }
        double drawingX = getDrawingX(p.getX());
        double drawingY = getDrawingY(p.getY());
        double radius = circleSize / 2;
        gc.fillOval(drawingX - radius, drawingY - radius, circleSize, circleSize);
    }

    private void drawLineBetween(Point p, Point q, Color color) {
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(LINE_WIDTH);
        double drawingX = getDrawingX(p.getX());
        double drawingY = getDrawingY(p.getY());
        double drawing2X = getDrawingX(q.getX());
        double drawing2Y = getDrawingY(q.getY());
        gc.strokeLine(drawingX, drawingY, drawing2X, drawing2Y);
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private double getDrawingX(double realPosition) {
        double canvasPosition = MARGIN + (realPosition - minX) * CANVAS_TO_POINTS_RATIO;
        return canvasPosition;
    }

    private double getDrawingY(double realPosition) {
        double canvasPosition = MARGIN + (realPosition - minY) * CANVAS_TO_POINTS_RATIO;
        return canvasPosition;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        recalculateRatio();
    }
}
