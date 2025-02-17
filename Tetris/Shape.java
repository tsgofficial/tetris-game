
import java.awt.Point;
import java.util.Random;

class Shape {

    private Tetrominoes pieceShape;
    private Point[] shape;
    private Point[] positions;
    private Point[] ghostPositions; // Stores the ghost piece position

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
        initializePositions(4, 0);
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

    public Point[] getPositions() {
        return positions;
    }

    public Point[] getGhostPositions() {
        return ghostPositions;
    }

    public void setGhostPositions(Point[] ghostPositions) {
        this.ghostPositions = ghostPositions;
    }

    public void initializePositions(int x, int y) {
        positions = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            positions[i] = new Point(x + shape[i].x, y + shape[i].y);
        }
    }

    public void move(int dx, int dy) {
        for (Point p : positions) {
            p.translate(dx, dy);
        }
    }

    public void rotate(Tetrominoes[][] board) {
        if (pieceShape == Tetrominoes.NoShape) {
            return;
        }
        Point pivot = positions[1];
        Point[] rotatedPositions = new Point[shape.length];

        for (int i = 0; i < shape.length; i++) {
            int newX = pivot.x - (positions[i].y - pivot.y);
            int newY = pivot.y + (positions[i].x - pivot.x);
            rotatedPositions[i] = new Point(newX, newY);
        }

        if (canRotate(rotatedPositions, board)) {
            positions = rotatedPositions;
        }
    }

    private boolean canRotate(Point[] rotatedPositions, Tetrominoes[][] board) {
        for (Point p : rotatedPositions) {
            if (p.x < 0 || p.x >= 10 || p.y >= 20 || (p.y >= 0 && board[p.x][p.y] != Tetrominoes.NoShape)) {
                return false;
            }
        }
        return true;
    }
}
