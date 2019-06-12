package minesweeper_javafx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CanvasPanel extends Pane {

    private final static Color DEFAULT_BACKGROUND_COLOR = Color.rgb(192, 192, 192);

    private Color colorOfBackground;

    final private Canvas canvas;
    private GraphicsContext graphicsContext;

    private static GameRun gr;

    private Text mines;
    private Text time;
    private ImageView optionsSmile;
    private ArrayList<Image> numbersImages = new ArrayList<>();
    private Image mineImage;
    private Image flagImage;
    private Map<Integer, ImageView> fieldImages = new HashMap<>();

    CanvasPanel(double width, double height) {
        super();
        setWidth(width);
        setHeight(height);
        canvas = new javafx.scene.canvas.Canvas(width, height);
        graphicsContext = canvas.getGraphicsContext2D();
        setColorOfBackground();
        getChildren().add(canvas);
        initializeResources();
    }

    private void initializeResources() {
        Font font = Font.font("roboto", 24);
        mines = new Text(100, 40, "0");
        time = new Text(690, 40, "0");
        Text minesText = new Text(24, 40, "Mines:");
        Text timeText = new Text(620, 40, "Time:");
        mines.setFont(font);
        time.setFont(font);
        minesText.setFont(font);
        timeText.setFont(font);
        optionsSmile = new ImageView();
        optionsSmile.setY(18);
        optionsSmile.setX(370);
        try {
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_1.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_2.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_3.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_4.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_5.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_6.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_7.png"), 18, 18, true, true));
            numbersImages.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine_8.png"), 18, 18, true, true));
            mineImage = new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\mine.png"), 18, 18, true, true);
            flagImage = new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\flag.png"), 20, 20, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        getChildren().addAll(mines, time, minesText, timeText, optionsSmile);
    }

    void setColorOfForeground(Color color) {
        graphicsContext.setFill(color);
    }

    private void setColorOfBackground() {
        colorOfBackground = CanvasPanel.DEFAULT_BACKGROUND_COLOR;
        erase();
    }

    private void erase() {
        erase(new Rectangle2D.Double(0, 0, canvas.getWidth(), canvas.getHeight()));
    }

    //erase shape from canvas
    void erase(Shape shape) {
        Paint original = graphicsContext.getFill();
        graphicsContext.setFill(colorOfBackground);
        fill(shape);
        graphicsContext.setFill(original);
    }

    //fill image in field on top of square (number, flag or mine)
    void fillImage(int x, int y, String what, int count) {
        ImageView image;
        int i = gr.getI(x);
        int j = gr.getJ(y);
        if (fieldImages.containsKey(j * gr.getCols() + i)) {
            image = fieldImages.get(j * gr.getCols() + i);
        } else {
            image = new ImageView();
            fieldImages.put(j * gr.getCols() + i, image);
            getChildren().add(image);
        }
        image.setY(y);
        image.setX(x);
        switch (what) {
            case "count":
                image.setImage(numbersImages.get(count - 1));
                break;
            case "mine":
                image.setImage(mineImage);
                break;
            case "mark":
                image.setImage(flagImage);
                break;
            case "unmark":
                image.setImage(null);
                break;
        }
    }

    //remove all images in field
    void removeFieldImages() {
        for (ImageView image : fieldImages.values()) {
            image.setImage(null);
        }
    }

    //paint shape into canvas
    void fill(Shape shape) {
        if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            graphicsContext.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
    }

    //initialize game run with mouse event handlers
    void initializeGR() {
        gr = GameRun.getInstance(time, mines, optionsSmile);
        setOnMousePressed(e -> gr.mousePressed(e));
        setOnMouseMoved(e -> gr.mouseMoved(e));
        setOnMouseReleased(e -> gr.mouseReleased());
    }
}
