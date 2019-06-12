package pj1_2018_dp_kru0144;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GameRun {
    private static final Canvas canvas = Canvas.getInstance();
    private static int SQUARE_SIZE = 24;
    private static GameRun gameRun;
    private static Text minesLeftDisplay;
    private static Text timeDisplay;
    private static ImageView optionsSmile;
    private GameLayout gameLayout;
    private GameOptions gameOptions;
    private int level = 3;
    //used to repaint after hovering over different square
    private int lastSquare = 0;
    private int cols = 30;
    private int rows = 16;
    private int mines = 99;
    private int minesFound = 0;
    private int width;
    private int height;
    private int time;
    //timecounter
    private Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            actionEvent -> {
                time++;
                timeDisplay.setText(String.valueOf(time));
            }
    ));

    private GameRun() {
        gameLayout = new GameLayout(rows, cols, mines);
        gameLayout.initialize();
        gameOptions = new GameOptions(optionsSmile);
        width = SQUARE_SIZE * cols;
        height = SQUARE_SIZE * rows;
        time = 0;
        minesLeftDisplay.setText(String.valueOf(mines));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    static GameRun getInstance(Text time, Text mines, ImageView options) {
        timeDisplay = time;
        minesLeftDisplay = mines;
        optionsSmile = options;
        if (gameRun == null) {
            gameRun = new GameRun();
        }
        return gameRun;
    }

    //process clicking the mouse
    void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        boolean doubleClick = false;
        if (e.getClickCount() == 2) {
            doubleClick = true;
        }
        if (e.isPrimaryButtonDown()) {
            gameOptions.setSmile("worry");
            if (x > gameOptions.getX() && x < gameOptions.getX() + gameOptions.getSize() && y > gameOptions.getY() && y < gameOptions.getY() + gameOptions.getSize()) {
                gameEnded(false, true, false);
            } else {
                int i = getI(x);
                int j = getJ(y);
                if (checkPos(i, j)) {
                    if (gameLayout.clickSquare(i + cols * j, doubleClick)) {
                        gameOptions.setSmile("sad");
                        gameEnded(false, false, false);
                    }
                }
            }
        } else {
            int i = getI(x);
            int j = getJ(y);
            if (checkPos(i, j)) {
                minesFound = minesFound + gameLayout.markMine(i + cols * j);
                minesLeftDisplay.setText(String.valueOf(mines - minesFound));
                if (minesFound == mines) {
                    gameEnded(true, false, submitScore(time, level));
                }
            }
        }
    }

    void mouseReleased() {
        gameOptions.setSmile("happy");
    }

    private void gameEnded(boolean won, boolean once, boolean newRecord) {
        int[] settings;
        do {
            settings = gameOptions.showSettings(once, won, newRecord, time);
        } while (settings == null && !once);
        if (settings != null) {
            gameLayout.destroy();
            timeline.stop();
            rows = settings[0];
            cols = settings[1];
            mines = settings[2];
            level = settings[3];
            width = SQUARE_SIZE * cols;
            height = SQUARE_SIZE * rows;
            minesFound = lastSquare = 0;
            minesLeftDisplay.setText(String.valueOf(mines));
            canvas.removeFieldImages();
            gameLayout = new GameLayout(settings[0], settings[1], settings[2]);
            gameLayout.initialize();
            gameOptions.setSmile("happy");
            time = 0;
            timeDisplay.setText(String.valueOf(time));
            timeline.play();
        }
    }

    private boolean submitScore(int time, int level) {
        if (level > 0) {
            BufferedReader in = null;
            PrintWriter out = null;
            boolean recordSet = false;
            String levelString = "";
            switch (level) {
                case 1:
                    levelString = "Easy: ";
                    break;
                case 2:
                    levelString = "Medium: ";
                    break;
                case 3:
                    levelString = "Hard: ";
                    break;
            }
            try {
                in = new BufferedReader(new FileReader("stats.txt"));
                Pattern p = Pattern.compile("\\d+");
                Matcher m;
                String line;
                int recordTime = 0;
                int lineCount = 1;
                ArrayList<String> lines = new ArrayList<>();
                while ((line = in.readLine()) != null) {
                    if (lineCount == level) {
                        m = p.matcher(line);
                        if (m.find()) {
                            recordTime = Integer.parseInt(m.group(0));
                        }
                        if (time < recordTime || recordTime == 0) {
                            line = levelString + Integer.toString(time);
                            recordSet = true;
                        }
                    }
                    lines.add(line);
                    lineCount++;
                }
                in.close();
                out = new PrintWriter(new FileWriter("stats.txt"));
                for (String outLine : lines) {
                    out.println(outLine);
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (out != null) {
                    out.close();
                }
                try {
                    out = new PrintWriter(new FileWriter("stats.txt"));
                    out.println("Easy: " + (level == 1 ? Integer.toString(time) : "0"));
                    out.println("Medium: " + (level == 2 ? Integer.toString(time) : "0"));
                    out.println("Hard: " + (level == 3 ? Integer.toString(time) : "0"));
                    out.close();
                } catch (IOException e2) {
                    e.printStackTrace();
                    if (out != null) {
                        out.close();
                    }
                }
                recordSet = true;
            }
            return recordSet;
        }
        return false;
    }

    void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        int i = getI(x);
        int j = getJ(y);
        if (checkPos(i, j)) {
            gameLayout.hoverMine(lastSquare, false);
            lastSquare = i + cols * j;
            gameLayout.hoverMine(lastSquare, true);
        } else {
            gameLayout.hoverMine(lastSquare, false);
        }
    }

    int getCols() {
        return cols;
    }

    //check if position is valid in the field
    private boolean checkPos(int i, int j) {
        return i < cols && j < rows && i >= 0 && j >= 0;
    }

    //get position from mouse coordinates
    int getI(double x) {
        return (int) Math.floor(((x - 24) / (width)) * cols);
    }

    int getJ(double y) {
        return (int) Math.floor(((y - 64) / (height)) * rows);
    }
}