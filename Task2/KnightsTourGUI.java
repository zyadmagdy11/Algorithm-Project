import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import javax.swing.Timer;

public class KnightsTourGUI extends JFrame {
    private static final int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};
    private static final Color[] COLORS = {
        new Color(255, 255, 255),  // White (unvisited)
        new Color(100, 200, 100),  // Green (visited)
        new Color(200, 100, 100),  // Red (current)
        new Color(100, 100, 255)   // Blue (starting position)
    };
    
    private int[][] board;
    private int currentX, currentY;
    private int startX, startY;
    private int moveNumber;
    private int boardSize;
    private Timer timer;
    private JPanel boardPanel;
    private JLabel statusLabel;

    public KnightsTourGUI(int size, int startX, int startY) {
        setTitle("Knight's Tour Visualizer");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        this.boardSize = size;
        this.startX = startX;
        this.startY = startY;
        this.currentX = startX;
        this.currentY = startY;
        this.board = new int[boardSize][boardSize];
        this.board[startX][startY] = 1; // Starting position
        this.moveNumber = 1;
        
        setupUI();
    }

    private void setupUI() {
        // Board panel
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(700, 700));
        
        // Control panel
        JPanel controlPanel = new JPanel();
        statusLabel = new JLabel("Move: 1 - Starting at (" + startX + "," + startY + ")");
        JButton stepButton = new JButton("Step");
        JButton autoButton = new JButton("Auto (100ms)");
        JButton resetButton = new JButton("Reset");
        
        stepButton.addActionListener(e -> performMove());
        autoButton.addActionListener(e -> startAutoMove());
        resetButton.addActionListener(e -> resetBoard());
        
        controlPanel.add(statusLabel);
        controlPanel.add(stepButton);
        controlPanel.add(autoButton);
        controlPanel.add(resetButton);
        
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Timer for automatic moves
        timer = new Timer(500, e -> performMove());
    }

    private void drawBoard(Graphics g) {
        int cellSize = Math.min(
            boardPanel.getWidth() / boardSize,
            boardPanel.getHeight() / boardSize
        );
        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Determine cell color based on state
                Color cellColor;
                if (i == currentX && j == currentY) {
                    cellColor = COLORS[2]; // Current position (red)
                } else if (i == startX && j == startY) {
                    cellColor = COLORS[3]; // Starting position (blue)
                } else if (board[i][j] > 0) {
                    cellColor = COLORS[1]; // Visited (green)
                } else {
                    cellColor = COLORS[0]; // Unvisited (white)
                }
                
                // Draw cell background
                g.setColor(cellColor);
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                
                // Draw cell border
                g.setColor(Color.BLACK);
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                
                // Draw move number if visited
                if (board[i][j] > 0) {
                    g.setColor(Color.BLACK);
                    String text = String.valueOf(board[i][j]);
                    FontMetrics fm = g.getFontMetrics();
                    int x = j * cellSize + (cellSize - fm.stringWidth(text)) / 2;
                    int y = i * cellSize + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
                    g.drawString(text, x, y);
                }
            }
        }
    }

    private void performMove() {
        if (moveNumber >= boardSize * boardSize) {
            timer.stop();
            statusLabel.setText("Tour completed successfully in " + (moveNumber-1) + " moves!");
            return;
        }

        int nextX = -1, nextY = -1;
        int minDegree = Integer.MAX_VALUE;

        // Find next move using Warnsdorff's algorithm
        for (int i = 0; i < 8; i++) {
            int newX = currentX + dx[i];
            int newY = currentY + dy[i];

            if (isValidMove(newX, newY)) {
                int degree = countAvailableMoves(newX, newY);
                if (degree < minDegree) {
                    minDegree = degree;
                    nextX = newX;
                    nextY = newY;
                }
            }
        }

        if (nextX == -1 || nextY == -1) {
            timer.stop();
            statusLabel.setText("No valid moves left! Tour incomplete after " + (moveNumber-1) + " moves.");
            return;
        }

        // Make the move
        currentX = nextX;
        currentY = nextY;
        board[currentX][currentY] = ++moveNumber;
        statusLabel.setText("Move: " + moveNumber + " - Now at (" + currentX + "," + currentY + ")");
        boardPanel.repaint();
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize && board[x][y] == 0;
    }

    private int countAvailableMoves(int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY)) {
                count++;
            }
        }
        return count;
    }

    private void startAutoMove() {
        if (moveNumber >= boardSize * boardSize) {
            resetBoard();
        }
        timer.start();
    }

    private void resetBoard() {
        timer.stop();
        for (int i = 0; i < boardSize; i++) {
            Arrays.fill(board[i], 0);
        }
        currentX = startX;
        currentY = startY;
        board[startX][startY] = 1;
        moveNumber = 1;
        statusLabel.setText("Move: 1 - Starting at (" + startX + "," + startY + ")");
        boardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int size = 8; // Default board size
            int startX = 0, startY = 0; // Default starting position
            
            // Get user input
            String input = JOptionPane.showInputDialog("Enter board size (default 8):");
            if (input != null && !input.isEmpty()) {
                size = Integer.parseInt(input);
            }
            
            input = JOptionPane.showInputDialog("Enter starting row (0-" + (size-1) + ", default 0):");
            if (input != null && !input.isEmpty()) {
                startX = Integer.parseInt(input);
            }
            
            input = JOptionPane.showInputDialog("Enter starting column (0-" + (size-1) + ", default 0):");
            if (input != null && !input.isEmpty()) {
                startY = Integer.parseInt(input);
            }
            
            // Validate input
            if (startX < 0 || startX >= size || startY < 0 || startY >= size) {
                JOptionPane.showMessageDialog(null, "Invalid starting position! Using default (0,0)");
                startX = 0;
                startY = 0;
            }
            
            KnightsTourGUI gui = new KnightsTourGUI(size, startX, startY);
            gui.setVisible(true);
        });
    }
}