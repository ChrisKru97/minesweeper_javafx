package minesweeper_javafx;

import javafx.scene.paint.Color;

import java.awt.geom.Rectangle2D;

class Square implements IPaintable{

    private static final Canvas CANVAS = Canvas.getInstance();
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private Color fieldColor = Color.DARKGRAY;
    private Color color = Color.rgb(189, 189, 189);
    private boolean isMine;
    private boolean clicked = false;
    private boolean marked = false;
    private int count = 0;

    Square(int x, int y, int width, int height, boolean isMine) {
        xPos = x;
        yPos = y;
        this.isMine = isMine;
        this.width = width;
        this.height = height;
        paint();
    }

    boolean getMine() {
        return isMine;
    }

    boolean getMarked() {
        return marked;
    }

    boolean click() {
        if (clicked || marked) {
            return false;
        } else {
            clicked = true;
            paint();
            return true;
        }
    }

    void showMine() {
        clicked = true;
        paint();
    }

    //mark unclicked square as a mine with flag
    int mark() {
        if(clicked){
            return 0;
        }
        marked = !marked;
        paint();
        if (!clicked) {
            if (marked) {
                CANVAS.fillImage(xPos + 2, yPos + 2, "mark", 0);
            } else {
                CANVAS.fillImage(xPos + 2, yPos + 2, "unmark", 0);
            }
        }
        return isMine ? marked ? 1 : -1 : 0;
    }

    //process hovering over the square with mouse
    void hover(boolean hover) {
        if (!clicked && !marked && hover) {
            CANVAS.setColorOfForeground(Color.rgb(150, 150, 150));
            CANVAS.fill(new Rectangle2D.Double(xPos + 4, yPos + 4, width - 8, height - 8));
        } else {
            paint();
        }
    }

    void setCount(int count) {
        this.count = count;
    }

    int getCount() {
        return count;
    }

    //repaint appearance of square according to its state
    public void paint() {
        CANVAS.setColorOfForeground(Color.WHITE);
        CANVAS.fill(new Rectangle2D.Double(xPos, yPos, width, height));
        if (!clicked) {
            CANVAS.setColorOfForeground(Color.rgb(222, 222, 222));
            CANVAS.fill(new Rectangle2D.Double(xPos + 2, yPos + 2, width - 4, height - 2));
        }
        CANVAS.setColorOfForeground(Color.rgb(154, 154, 154));
        CANVAS.fill(new Rectangle2D.Double(xPos + 4, yPos + 4, width - 4, height - 4));
        if (!clicked) {
            CANVAS.setColorOfForeground(Color.rgb(123, 123, 123));
            CANVAS.fill(new Rectangle2D.Double(xPos + 6, yPos + 6, width - 6, height - 6));
        }
        CANVAS.setColorOfForeground(clicked ? fieldColor : color);
        CANVAS.fill(new Rectangle2D.Double(xPos + 4, yPos + 4, width - 8, height - 8));
        if (clicked && count > 0) {
            CANVAS.fillImage(xPos + 5, yPos + 5, "count", count);
        }
        if (clicked && isMine) {
            CANVAS.fillImage(xPos + 5, yPos + 5, "mine", 0);
        }
    }

    //delete square from canvas
    public void erase() {
        CANVAS.erase(new java.awt.geom.Rectangle2D.Double(xPos, yPos, width, height));
    }
}
