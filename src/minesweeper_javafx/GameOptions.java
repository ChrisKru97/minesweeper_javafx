package minesweeper_javafx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class GameOptions {
    private static final Canvas CANVAS = Canvas.getInstance();
    private static ImageView optionsSmile;
    private ArrayList<Image> smiles = new ArrayList<>();
    private int x, y, size;

    GameOptions(ImageView optionsSmile) {
        this(368, 16, 32, optionsSmile);
    }

    private GameOptions(int x, int y, int size, ImageView optionsSmile) {
        this.x = x;
        this.y = y;
        this.size = size;
        GameOptions.optionsSmile = optionsSmile;
        try {
            smiles.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\happy.png"), 28, 28, true, true));
            smiles.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\sad.png"), 28, 28, true, true));
            smiles.add(new Image(new FileInputStream("src\\minesweeper_javafx\\resources\\worry.png"), 28, 28, true, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CANVAS.setColorOfForeground(Color.BLACK);
        CANVAS.fill(new Rectangle2D.Double(x, y, size, size));
        CANVAS.setColorOfForeground(Color.rgb(192, 192, 192));
        CANVAS.fill(new Rectangle2D.Double(x + 1, y + 1, size - 2, size - 2));
        setSmile("happy");
    }

    //change appearance of smile according to action
    void setSmile(String what) {
        switch (what) {
            case "happy":
                optionsSmile.setImage(smiles.get(0));
                break;
            case "sad":
                optionsSmile.setImage(smiles.get(1));
                break;
            case "worry":
                optionsSmile.setImage(smiles.get(2));
                break;
        }
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getSize() {
        return size;
    }

    //show window for user input to new game
    int[] showSettings(boolean options, boolean won, boolean newRecord, int time) {
        String selected;
        String record = newRecord ? "set a new record" : "won";
        if (won) {
            JOptionPane.showMessageDialog(null, "Congratulations, you have " + record + "!!! Your time is: " + time);
        } else if (!options) {
            JOptionPane.showMessageDialog(null, "I am sorry, you have lost. Your time is: " + time);
        }
        try {
            selected = JOptionPane.showInputDialog(null, "Select difficulty", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Easy", "Medium", "Hard", "Custom"}, 0).toString();
        } catch (Exception e) {
            selected = null;
        }
        if (selected != null) {
            switch (selected) {
                case "Easy":
                    return new int[]{9, 9, 10, 1};
                case "Medium":
                    return new int[]{16, 16, 40, 2};
                case "Hard":
                    return new int[]{16, 30, 99, 3};
            }
            if (selected.equals("Custom")) {
                int countY, countX, mines;
                countY = countX = mines = 5;
                JTextField xField = new JTextField(5);
                JTextField yField = new JTextField(5);
                JTextField mineField = new JTextField(5);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Columns:"));
                myPanel.add(xField);
                myPanel.add(Box.createHorizontalStrut(15));
                myPanel.add(new JLabel("Rows:"));
                myPanel.add(yField);
                myPanel.add(Box.createHorizontalStrut(15));
                myPanel.add(new JLabel("mines:"));
                myPanel.add(mineField);
                do {
                    try {
                        JOptionPane.showConfirmDialog(null, myPanel, "Input your own settings", JOptionPane.OK_CANCEL_OPTION);
                        countX = Integer.parseInt(xField.getText());
                        countY = Integer.parseInt(yField.getText());
                        mines = Integer.parseInt(mineField.getText());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                while (countY < 5 || countY > 16 || countX < 5 || countX > 30 || mines < 1 || mines > ((countX * countY) / 4));
                return new int[]{countX, countY, mines, 0};
            }
        }
        return null;
    }
}
