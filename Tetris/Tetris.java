
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Tetris extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int CELL_SIZE = 30;
    private final Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int currentX = 0;
    private int currentY = 0;
    private Shape currentPiece;
    private Tetrominoes[][] board;

    public Tetris() {
        setFocusable(true);
        board = new Tetrominoes[BOARD_WIDTH][BOARD_HEIGHT];
        addKeyListener(new TAdapter());
        timer = new Timer(400, this);
        start();
    }

    public void start() {
        isStarted = true;
        clearBoard();
        newPiece();
        timer.start();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = Tetrominoes.NoShape;
            }
        }
    }

    private void checkFullRows() {
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
                j++; // Recheck the same row after shifting
            }
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
        currentX = BOARD_WIDTH / 2 - 1;
        currentY = 0;
        if (!tryMove(currentPiece, currentX, currentY)) {
            isStarted = false;
            timer.stop();
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (Point p : newPiece.getShape()) {
            int x = newX + p.x;
            int y = newY + p.y;
            if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT || (y >= 0 && board[x][y] != Tetrominoes.NoShape)) {
                return false;
            }
        }
        currentPiece = newPiece;
        currentX = newX;
        currentY = newY;
        repaint();
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
        if (!tryMove(currentPiece, currentX, currentY + 1)) {
            pieceDropped();
        }
    }

    private void dropInstantly() {
        while (tryMove(currentPiece, currentX, currentY + 1)) {
            currentY++;
        }
        pieceDropped();
    }

    private void pieceDropped() {
        for (Point p : currentPiece.getShape()) {
            board[currentX + p.x][currentY + p.y] = currentPiece.getTetromino();
        }
        isFallingFinished = true;
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

        if (currentPiece != null) {
            g.setColor(Color.RED);
            for (Point p : currentPiece.getShape()) {
                int x = (currentX + p.x) * CELL_SIZE;
                int y = (currentY + p.y) * CELL_SIZE;
                drawSquare(g, x, y, currentPiece.getTetromino());
            }
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color[] colors = {Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE};
        g.setColor(colors[shape.ordinal()]);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
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
                    tryMove(currentPiece, currentX - 1, currentY);
                    checkFullRows();
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentPiece, currentX + 1, currentY);
                    checkFullRows();
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    currentPiece.rotate(currentX, currentY, board);
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    dropInstantly();
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris game = new Tetris();
        frame.add(game);
        frame.setSize(300, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
