
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;

public class Tetris extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int CELL_SIZE = 30;
    private final Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int score = 0;
    private int bestScore = 0;
    private Shape currentPiece;
    private Tetrominoes[][] board;

    private JLabel scoreLabel;
    private JLabel bestScoreLabel;

    public Tetris(JLabel scoreLabel, JLabel bestScoreLabel) {
        this.scoreLabel = scoreLabel;
        this.bestScoreLabel = bestScoreLabel;

        setFocusable(true);
        setPreferredSize(new Dimension(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE));
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        board = new Tetrominoes[BOARD_WIDTH][BOARD_HEIGHT];
        addKeyListener(new TAdapter());
        timer = new Timer(400, this);
        loadBestScore();
        start();
    }

    public void start() {
        isStarted = true;
        score = 0;
        updateScoreLabels();
        clearBoard();
        newPiece();
        timer.start();
        repaint();
    }

    private void updateScoreLabels() {
        scoreLabel.setText("Score: " + score);
        bestScoreLabel.setText("Best: " + bestScore);
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = Tetrominoes.NoShape;
            }
        }
    }

    private void checkFullRows() {
        int rowsCleared = 0;

        for (int j = BOARD_HEIGHT - 1; j >= 0; j--) {
            boolean isFull = true;
            for (int i = 0; i < BOARD_WIDTH; i++) {
                if (board[i][j] == Tetrominoes.NoShape) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                removeRow(j);
                rowsCleared++;
                j++;
            }
        }

        if (rowsCleared > 0) {
            score += (rowsCleared * 100);
            if (score > bestScore) {
                bestScore = score;
                saveBestScore();
            }
            updateScoreLabels();
        }
    }

    private void removeRow(int row) {
        for (int j = row; j > 0; j--) {
            for (int i = 0; i < BOARD_WIDTH; i++) {
                board[i][j] = board[i][j - 1];
            }
        }
        for (int i = 0; i < BOARD_WIDTH; i++) {
            board[i][0] = Tetrominoes.NoShape;
        }
    }

    private void newPiece() {
        currentPiece = new Shape();
        updateGhostPiece();

        if (!tryMove(0, 0)) {
            isStarted = false;
            timer.stop();

            JOptionPane.showMessageDialog(null, "Game Over!\nYour Score: " + score + "\nBest Score: " + bestScore, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            start();
        }
    }

    private boolean tryMove(int dx, int dy) {
        Point[] originalPositions = currentPiece.getPositions();
        Point[] newPositions = new Point[originalPositions.length];

        for (int i = 0; i < originalPositions.length; i++) {
            newPositions[i] = new Point(originalPositions[i].x + dx, originalPositions[i].y + dy);
            if (newPositions[i].x < 0 || newPositions[i].x >= BOARD_WIDTH || newPositions[i].y >= BOARD_HEIGHT || (newPositions[i].y >= 0 && board[newPositions[i].x][newPositions[i].y] != Tetrominoes.NoShape)) {
                return false;
            }
        }
        currentPiece.move(dx, dy);
        updateGhostPiece();
        repaint();
        return true;
    }

    private void updateGhostPiece() {
        if (currentPiece == null) {
            return;
        }

        Point[] ghost = new Point[currentPiece.getPositions().length];

        for (int i = 0; i < ghost.length; i++) {
            ghost[i] = new Point(currentPiece.getPositions()[i]);
        }

        while (canMove(ghost, 0, 1)) {
            for (Point p : ghost) {
                p.y += 1;
            }
        }

        currentPiece.setGhostPositions(ghost);
    }

    private boolean canMove(Point[] positions, int dx, int dy) {
        for (Point p : positions) {
            int newX = p.x + dx;
            int newY = p.y + dy;

            if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT || (newY >= 0 && board[newX][newY] != Tetrominoes.NoShape)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private void oneLineDown() {
        if (!tryMove(0, 1)) {
            pieceDropped();
        }
    }

    private void pieceDropped() {
        for (Point p : currentPiece.getPositions()) {
            board[p.x][p.y] = currentPiece.getTetromino();
        }
        isFallingFinished = true;
        checkFullRows();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[i][j] != Tetrominoes.NoShape) {
                    drawSquare(g, i * CELL_SIZE, j * CELL_SIZE, board[i][j]);
                }
            }
        }

        if (currentPiece != null && currentPiece.getGhostPositions() != null) {
            g.setColor(Color.GRAY);
            for (Point p : currentPiece.getGhostPositions()) {
                drawSquare(g, p.x * CELL_SIZE, p.y * CELL_SIZE, Tetrominoes.NoShape);
            }
        }

        if (currentPiece != null) {
            g.setColor(Color.RED);
            for (Point p : currentPiece.getPositions()) {
                drawSquare(g, p.x * CELL_SIZE, p.y * CELL_SIZE, currentPiece.getTetromino());
            }
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color[] colors = {Color.WHITE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE};
        g.setColor(colors[shape.ordinal()]);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
    }

    private void loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("bestscore.txt"))) {
            bestScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            bestScore = 0;
        }
    }

    private void saveBestScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bestscore.txt"))) {
            writer.write(String.valueOf(bestScore));
        } catch (IOException e) {
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || currentPiece == null) {
                return;
            }
            int keycode = e.getKeyCode();
            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(1, 0);
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    currentPiece.rotate(board);
                    updateGhostPiece();
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    while (tryMove(0, 1)) {
                    }
                    pieceDropped();
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");

            JPanel scorePanel = new JPanel();
            scorePanel.setLayout(new FlowLayout());
            scorePanel.setBackground(Color.BLACK);

            JLabel scoreLabel = new JLabel("Score: 0");
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

            JLabel bestScoreLabel = new JLabel("Best: 0");
            bestScoreLabel.setForeground(Color.WHITE);
            bestScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

            scorePanel.add(scoreLabel);
            scorePanel.add(Box.createHorizontalStrut(20));
            scorePanel.add(bestScoreLabel);

            Tetris game = new Tetris(scoreLabel, bestScoreLabel);

            frame.setLayout(new BorderLayout());
            frame.add(scorePanel, BorderLayout.NORTH);
            frame.add(game, BorderLayout.CENTER);

            frame.setSize(303, 670);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
