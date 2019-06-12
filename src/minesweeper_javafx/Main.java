package minesweeper_javafx;

public class Main {

    public static void main(String[] args) {

        Canvas canvas = Canvas.getInstance();
        canvas.getCanvasPanel().initializeGR();
    }
}

// doubleclick cannot work when blank squares are around, interface