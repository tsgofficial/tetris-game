
import java.awt.Point;
import java.util.Random;

class Shape {

    private Tetrominoes pieceShape;
    private Point[] shape;
    private static final Point[][] TetrominoShapes = {
        {new Point(0, 0)},
        {new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(1, 1)}, // L Shape
        {new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(-1, 1)}, // J Shape
        {new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0)}, // I Shape
        {new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(0, 1)}, // T Shape
        {new Point(-1, 0), new Point(0, 0), new Point(0, 1), new Point(1, 1)}, // S Shape
        {new Point(-1, 1), new Point(0, 1), new Point(0, 0), new Point(1, 0)} // Z Shape
    };

    public Shape() {
        setRandomShape();
    }

    public void setRandomShape() {
        int x = new Random().nextInt(TetrominoShapes.length - 1) + 1;
        shape = TetrominoShapes[x];
        pieceShape = Tetrominoes.values()[x];
    }

    public Point[] getShape() {
        return shape;
    }

    public Tetrominoes getTetromino() {
        return pieceShape;
    }

    public void rotate(int currentX, int currentY, Tetrominoes[][] board) {
        Point[] rotatedShape = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            rotatedShape[i] = new Point(-shape[i].y, shape[i].x);
        }

        int shiftX = 0;
        while (!canRotate(rotatedShape, shiftX, currentX, currentY, board)) {
            shiftX--;
            if (shiftX < -2) {
                return; // Prevent infinite shifting

            }
        }

        for (int i = 0; i < rotatedShape.length; i++) {
            rotatedShape[i] = new Point(rotatedShape[i].x + shiftX, rotatedShape[i].y);
        }
        shape = rotatedShape;
    }

    private boolean canRotate(Point[] rotatedShape, int shiftX, int currentX, int currentY, Tetrominoes[][] board) {
        for (Point p : rotatedShape) {
            int x = currentX + p.x + shiftX;
            int y = currentY + p.y;
            if (x < 0 || x >= 10 || y >= 20 || (y >= 0 && board[x][y] != Tetrominoes.NoShape)) {
                return false;
            }
        }
        return true;
    }
}
