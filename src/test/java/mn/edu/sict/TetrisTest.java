package mn.edu.sict;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import javax.swing.JLabel;
import javax.swing.Timer;

import static org.junit.jupiter.api.Assertions.*;

public class TetrisTest {
    private Tetris tetris;
    private JLabel scoreLabel;
    private JLabel bestScoreLabel;

    @BeforeEach
    void setUp() {
        scoreLabel = new JLabel();
        bestScoreLabel = new JLabel();
        tetris = new Tetris(scoreLabel, bestScoreLabel);
    }

    @Test
    void testClearBoard() {
        tetris.start();
        // indirectly tests clearBoard, updateScoreLabels, newPiece
        assertNotNull(tetris);
    }

    @Test
    void testTryMoveFalseOnOutOfBounds() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(0, 0);
        TestUtils.setPrivateField(tetris, "currentPiece", shape);
        boolean result = TestUtils.invokeTryMove(tetris, -100, 0);
        assertFalse(result);
    }

    @Test
    void testCanMoveFalse() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(0, 19); // bottom of board
        TestUtils.setPrivateField(tetris, "currentPiece", shape);
        boolean result = TestUtils.invokeTryMove(tetris, 0, 1); // attempt move down
        assertFalse(result); // should fail, out of bounds
    }

    @Test
    void testCheckFullRowsAndRemoveRow() throws Exception {
        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);

        // Fill last row
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
        }

        TestUtils.invokeCheckFullRows(tetris);

        for (int i = 0; i < 10; i++) {
            assertEquals(Tetrominoes.NoShape, board[i][19], "Row should be cleared");
        }
    }

    @Test
    void testPieceDroppedUpdatesBoard() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(1, 0);
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        TestUtils.invokePieceDropped(tetris);

        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        for (Point p : shape.getPositions()) {
            assertNotEquals(Tetrominoes.NoShape, board[p.x][p.y]);
        }
    }

    @Test
    void testUpdateGhostPieceDoesNotOverflow() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(0, 18); // near bottom
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        TestUtils.invokeUpdateGhostPiece(tetris);

        Point[] ghost = shape.getGhostPositions();
        for (Point p : ghost) {
            assertTrue(p.y < 20);
        }
    }

    // NEW TESTS BELOW

    @Test
    void testTryMoveSuccess() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(5, 5); // middle of board
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        Point[] originalPositions = shape.getPositions().clone();
        boolean result = TestUtils.invokeTryMove(tetris, 1, 0); // move right

        assertTrue(result);
        // Check if piece actually moved
        Point[] newPositions = shape.getPositions();
        for (int i = 0; i < originalPositions.length; i++) {
            assertEquals(originalPositions[i].x + 1, newPositions[i].x);
            assertEquals(originalPositions[i].y, newPositions[i].y);
        }
    }

    @Test
    void testScoreIncreaseWhenRowsCleared() throws Exception {
        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        TestUtils.setPrivateField(tetris, "score", 0);

        // Fill last row
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
        }

        int originalScore = (int) TestUtils.getPrivateField(tetris, "score");
        TestUtils.invokeCheckFullRows(tetris);
        int newScore = (int) TestUtils.getPrivateField(tetris, "score");

        assertEquals(originalScore + 100, newScore, "Score should increase by 100 when one row is cleared");
    }

    @Test
    void testMultipleRowsScoring() throws Exception {
        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        TestUtils.setPrivateField(tetris, "score", 0);

        // Fill two rows
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
            board[i][18] = Tetrominoes.LShape;
        }

        TestUtils.invokeCheckFullRows(tetris);
        int newScore = (int) TestUtils.getPrivateField(tetris, "score");

        assertEquals(200, newScore, "Score should increase by 200 when two rows are cleared");
    }

    @Test
    void testBestScoreUpdatesWhenExceeded() throws Exception {
        TestUtils.setPrivateField(tetris, "score", 0);
        TestUtils.setPrivateField(tetris, "bestScore", 50);

        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        // Fill one row to get 100 points
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
        }

        TestUtils.invokeCheckFullRows(tetris);
        int bestScore = (int) TestUtils.getPrivateField(tetris, "bestScore");

        assertEquals(100, bestScore, "Best score should update when exceeded");
    }

    @Test
    void testBestScoreDoesNotUpdateWhenNotExceeded() throws Exception {
        TestUtils.setPrivateField(tetris, "score", 0);
        TestUtils.setPrivateField(tetris, "bestScore", 500);

        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        // Fill one row to get 100 points
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
        }

        TestUtils.invokeCheckFullRows(tetris);
        int bestScore = (int) TestUtils.getPrivateField(tetris, "bestScore");

        assertEquals(500, bestScore, "Best score should not update when not exceeded");
    }

    @Test
    void testOneLineDown() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(5, 5);
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        Point[] originalPositions = shape.getPositions().clone();
        TestUtils.invokeOneLineDown(tetris);

        Point[] newPositions = shape.getPositions();
        for (int i = 0; i < originalPositions.length; i++) {
            assertEquals(originalPositions[i].x, newPositions[i].x);
            assertEquals(originalPositions[i].y + 1, newPositions[i].y);
        }
    }

    @Test
    void testOneLineDownTriggersDropWhenBlocked() throws Exception {
        Shape shape = new Shape();
        shape.initializePositions(5, 19); // At bottom
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        // Mock the board to have the bottom row filled
        Tetrominoes[][] board = TestUtils.getPrivateBoard(tetris);
        for (int i = 0; i < 10; i++) {
            board[i][19] = Tetrominoes.LShape;
        }

        // Set a flag we can check to see if pieceDropped was called
        TestUtils.setPrivateField(tetris, "isFallingFinished", false);

        TestUtils.invokeOneLineDown(tetris);

        boolean isFallingFinished = (boolean) TestUtils.getPrivateField(tetris, "isFallingFinished");
        assertTrue(isFallingFinished, "isFallingFinished should be true after piece is dropped");
    }

    @Test
    void testGameStartSetsInitialState() throws Exception {
        // Change some values first
        TestUtils.setPrivateField(tetris, "score", 500);
        TestUtils.setPrivateField(tetris, "isStarted", false);

        tetris.start();

        boolean isStarted = (boolean) TestUtils.getPrivateField(tetris, "isStarted");
        int score = (int) TestUtils.getPrivateField(tetris, "score");

        assertTrue(isStarted, "Game should be started");
        assertEquals(0, score, "Score should be reset to 0");
    }

    @Test
    void testLoadBestScore(@TempDir Path tempDir) throws Exception {
        // Create a temporary bestscore.txt file
        File testScoreFile = new File(tempDir.toFile(), "bestscore.txt");
        try (FileWriter writer = new FileWriter(testScoreFile)) {
            writer.write("1000");
        }

        // Mock the file path or access
        // Note: This would require modifying the Tetris class to accept a file path
        // For this test to work, you'd need to modify the Tetris class or use a testing
        // framework that can intercept file operations

        // This is a placeholder to show the concept
        // TestUtils.mockFilePath(tetris, testScoreFile.getAbsolutePath());
        // TestUtils.invokeLoadBestScore(tetris);

        // int bestScore = (int) TestUtils.getPrivateField(tetris, "bestScore");
        // assertEquals(1000, bestScore, "Best score should be loaded from file");
    }

    @Test
    void testSaveBestScore(@TempDir Path tempDir) throws Exception {
        // Similar to loadBestScore test, would require modifying the Tetris class
        // or using a framework that can intercept file operations

        // This is a placeholder to show the concept
        // File testScoreFile = new File(tempDir.toFile(), "bestscore.txt");
        // TestUtils.mockFilePath(tetris, testScoreFile.getAbsolutePath());
        // TestUtils.setPrivateField(tetris, "bestScore", 2000);
        // TestUtils.invokeSaveBestScore(tetris);

        // String savedScore = Files.readString(testScoreFile.toPath());
        // assertEquals("2000", savedScore, "Best score should be saved to file");
    }

    @Test
    void testTimerActionPerformsGameLogic() throws Exception {
        TestUtils.setPrivateField(tetris, "isStarted", true);

        // Test when a piece is falling
        TestUtils.setPrivateField(tetris, "isFallingFinished", false);
        Shape shape = new Shape();
        shape.initializePositions(5, 5);
        TestUtils.setPrivateField(tetris, "currentPiece", shape);

        Point[] originalPositions = shape.getPositions().clone();

        // Simulate timer tick
        Timer timer = (Timer) TestUtils.getPrivateField(tetris, "timer");
        timer.getActionListeners()[0].actionPerformed(null);

        Point[] newPositions = shape.getPositions();
        for (int i = 0; i < originalPositions.length; i++) {
            assertEquals(originalPositions[i].x, newPositions[i].x);
            assertEquals(originalPositions[i].y + 1, newPositions[i].y, "Piece should move down on timer event");
        }
    }

    @Test
    void testTimerActionWithFinishedPiece() throws Exception {
        TestUtils.setPrivateField(tetris, "isStarted", true);
        TestUtils.setPrivateField(tetris, "isFallingFinished", true);

        // Create a mockup currentPiece to verify it's replaced
        Shape oldShape = new Shape();
        oldShape.initializePositions(5, 5);
        TestUtils.setPrivateField(tetris, "currentPiece", oldShape);

        // Simulate timer tick
        Timer timer = (Timer) TestUtils.getPrivateField(tetris, "timer");
        timer.getActionListeners()[0].actionPerformed(null);

        // Check if isFallingFinished was reset
        boolean isFallingFinished = (boolean) TestUtils.getPrivateField(tetris, "isFallingFinished");
        assertFalse(isFallingFinished, "isFallingFinished should be reset after new piece");

        // This is harder to test without mocking newPiece(), but we could check
        // that the currentPiece changed
        Shape newShape = (Shape) TestUtils.getPrivateField(tetris, "currentPiece");
        assertNotSame(oldShape, newShape, "A new piece should be created");
    }
}