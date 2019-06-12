package minesweeper_javafx;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

class GameLayout {
    private static int SQUARE_SIZE = 24;
    private ArrayList<Square> field = new ArrayList<>();
    private int columns, rows, mines;
    private ArrayList<Integer> mineLoc = new ArrayList<>();

    GameLayout(int rows, int columns, int mines) {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;
    }

    //destroy the field, delete every square
    void destroy() {
        for (Square square : field) {
            square.erase();
        }
    }

    void initialize() {
        fillSquares();
        fillNumbers();
    }

    //process clicking on some square
    boolean clickSquare(int index, boolean doubleClick) {
        if (field.get(index).getMine()) {
            if (field.get(index).click()) {
                for (int i : mineLoc) {
                    field.get(i).showMine();
                }
                return true;
            }
        } else {
            field.get(index).click();
            if (field.get(index).getCount() == 0) {
                getNeighbours(index / columns, index % columns, false, false);
            } else if (doubleClick) {
                if (field.get(index).getCount() == getNeighbours(index / columns, index % columns, true, true)) {
                    return getNeighbours(index / columns, index % columns, false, true) == -1;
                }
            }
        }
        return false;
    }

    int markMine(int index) {
        return field.get(index).mark();
    }

    //recursion, used to count mines around, used to go through the field
    private int getNeighbours(int i, int y, boolean mines, boolean dbl) {
        int count = 0;
        boolean goOn;
        int index;
        for (int j = i > 0 ? 0 : 3; j < (i < rows - 1 ? 9 : 6); j++) {
            //skip if out of bounds
            if (j == 4 || y == 0 && j % 3 == 0 || y == columns - 1 && j % 3 == 2) continue;
            index = (j / 3) * columns - columns + i * columns + y + j % 3 - 1;
            if (field.get(index).getMine()) {
                if (mines && !dbl) {
                    count++;
                }
                if (dbl && !mines && !field.get(index).getMarked()) {
                    field.get(index).click();
                    return -1;
                }
            }
            if (!mines) {
                goOn = field.get(index).click();
                if (goOn && field.get(index).getCount() == 0) {
                    getNeighbours((j / 3) - 1 + i, y + j % 3 - 1, false, false);
                }
            }
            if (field.get(index).getMarked() && dbl && mines) {
                count++;
            }
        }
        return count;
    }

    void hoverMine(int index, boolean hover) {
        field.get(index).hover(hover);
    }

    //fill the field with square
    private void fillSquares() {
        Random rand = new Random();
        int max = columns * rows;
        int pos = 0;
        int num;
        boolean isMine;
        for (int i = 0; i < mines; i++) {
            do {
                num = rand.nextInt(max);
            } while (mineLoc.contains(num));
            mineLoc.add(num);
        }
        Collections.sort(mineLoc);
        for (int i = 0; i < rows; i++) {
            for (int y = 0; y < columns; y++) {
                if (mineLoc.size() > pos && mineLoc.get(pos) == (i * columns + y)) {
                    isMine = true;
                    pos++;
                } else {
                    isMine = false;
                }
                field.add(new Square((y + 1) * SQUARE_SIZE, 40 + (i + 1) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, isMine));
            }
        }
    }

    //fill numbers into square according to count of mines around
    private void fillNumbers() {
        int count;
        for (int i = 0; i < rows; i++) {
            for (int y = 0; y < columns; y++) {
                if (!field.get(i * columns + y).getMine()) {
                    count = getNeighbours(i, y, true, false);
                    if (count > 0)
                        field.get(i * columns + y).setCount(count);
                }
            }
        }
    }
}
