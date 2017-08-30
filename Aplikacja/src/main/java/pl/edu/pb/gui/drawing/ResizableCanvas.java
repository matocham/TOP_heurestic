package pl.edu.pb.gui.drawing;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Mateusz on 14.05.2017.
 */
public class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        // Redraw canvas when size changes.
    }

    public void addResizeListener(ChangeListener listener) {
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
    }


    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
