package pj1_2018_dp_kru0144;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.awt.*;

public class Canvas {

    private static Canvas cm;

    private CanvasPanel canvasPanel;

    public static class JavaFXApplication extends Application {

        @Override
        public void start(Stage primaryStage) {
            synchronized (Canvas.class) {
                cm = new Canvas(primaryStage);
                Canvas.class.notifyAll();
            }
        }

        static void launchAsync(String... args) {

            new Thread(() -> launch(args)).start();
            synchronized (Canvas.class) {
                while (cm == null) {
                    try {
                        Canvas.class.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }

    }

    private Canvas(Stage primaryStage) {
        canvasPanel = new CanvasPanel(768, 472);
        BorderPane root = new BorderPane();
        root.setCenter(canvasPanel);
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static Canvas getInstance() {
        if (cm == null) {
            JavaFXApplication.launchAsync();
        }
        return cm;
    }

    void setColorOfForeground(Color color) {
        canvasPanel.setColorOfForeground(color);
    }

    CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }

    void fill(Shape shape) {
        canvasPanel.fill(shape);
    }


    void fillImage(int x, int y, String what, int count) {
        canvasPanel.fillImage(x, y, what, count);
    }

    void removeFieldImages() {
        canvasPanel.removeFieldImages();
    }

    void erase(Shape shape) {
        canvasPanel.erase(shape);
    }

}
