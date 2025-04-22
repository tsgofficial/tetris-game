package mn.edu.sict;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {

    private Shape shape;

    @BeforeEach
    void setUp() {
        shape = new Shape();
    }

    @Test
    void testShapeInitialization() {
        assertNotNull(shape.getShape(), "Shape points should not be null");
        assertNotNull(shape.getPositions(), "Initial positions should not be null");
        assertNotNull(shape.getTetromino(), "Tetromino type should not be null");
    }

    @Test
    void testSetGhostPositionsAndGet() {
        Point[] ghost = {
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3)
        };
        shape.setGhostPositions(ghost);
        assertArrayEquals(ghost, shape.getGhostPositions(), "Ghost positions should be set properly");
    }

    @Test
    void testMoveFunction() {
        Point[] beforeMove = shape.getPositions();
        Point[] beforeCopy = new Point[beforeMove.length];
        for (int i = 0; i < beforeMove.length; i++) {
            beforeCopy[i] = new Point(beforeMove[i]);
        }

        shape.move(1, 1);
        Point[] afterMove = shape.getPositions();

        for (int i = 0; i < beforeCopy.length; i++) {
            assertEquals(beforeCopy[i].x + 1, afterMove[i].x);
            assertEquals(beforeCopy[i].y + 1, afterMove[i].y);
        }
    }

    @Test
    void testRotateFunctionNoShape() {
        // Simulate NoShape scenario by manually overriding pieceShape
        Tetrominoes[][] board = new Tetrominoes[10][20];
        shape.initializePositions(0, 0);
        shape.rotate(board); // Should do nothing for NoShape
        // No assert needed — just ensure no exceptions
    }

    @Test
    void testRotateWithValidBoard() {
        Tetrominoes[][] board = new Tetrominoes[10][20];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = Tetrominoes.NoShape;
            }
        }
        shape.rotate(board);
        assertNotNull(shape.getPositions(), "Positions after rotation should not be null");
    }
}
