import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrominoTilingNoGaps extends JFrame {

    private static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE};
    private int size;
    private int missingX, missingY;
    private int[][] board; // Stores the color index of each cell (-1 for empty, -2 for missing)
    private JPanel gridPanel;
    private JLabel[][] labels;
    private List<TilingStep> steps;
    private int currentStep = 0;
    private ScheduledExecutorService executor;
    private JButton stepButton, runButton;

    private class TilingStep {
        int r1, c1, r2, c2, r3, c3;
        int colorIndex;

        public TilingStep(int r1, int c1, int r2, int c2, int r3, int c3, int colorIndex) {
            this.r1 = r1;
            this.c1 = c1;
            this.r2 = r2;
            this.c2 = c2;
            this.r3 = r3;
            this.c3 = c3;
            this.colorIndex = colorIndex;
        }
    }

    public TrominoTilingNoGaps(int size, int missingX, int missingY) {
        this.size = size;
        this.missingX = missingX;
        this.missingY = missingY;
        this.board = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = -1; // Initialize board with -1 (empty)
            }
        }
        if (missingX >= 0 && missingX < size && missingY >= 0 && missingY < size) {
            board[missingX][missingY] = -2; // Mark missing square with -2
        } else {
            JOptionPane.showMessageDialog(this, "Missing square coordinates are out of bounds.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        this.steps = new ArrayList<>();

        setTitle("Tromino Tiling (No Gaps) - Size: " + size + "x" + size);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeGrid();
        solveTiling(0, 0, missingX, missingY, size);

        createControlPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeGrid() {
        gridPanel = new JPanel(new GridLayout(size, size, 1, 1));
        labels = new JLabel[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                labels[i][j] = new JLabel("", SwingConstants.CENTER);
                labels[i][j].setOpaque(true);
                labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                labels[i][j].setFont(new Font("Arial", Font.BOLD, 14));
                if (i == missingX && j == missingY) {
                    labels[i][j].setText("M");
                    labels[i][j].setBackground(Color.LIGHT_GRAY);
                }
                gridPanel.add(labels[i][j]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel();

        stepButton = new JButton("Step");
        stepButton.addActionListener(e -> showNextStep());

        runButton = new JButton("Run All");
        runButton.addActionListener(e -> runAllSteps());

        controlPanel.add(stepButton);
        controlPanel.add(runButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void solveTiling(int rowStart, int colStart, int holeRow, int holeCol, int currentSize) {
        if (currentSize == 2) {
            placeTromino(rowStart, colStart, holeRow, holeCol);
            return;
        }

        int halfSize = currentSize / 2;
        int centerRow = rowStart + halfSize;
        int centerCol = colStart + halfSize;

        int colorIndex = findValidColorForCentralTromino(centerRow - 1, centerCol - 1, centerRow - 1, centerCol, centerRow, centerCol - 1);

        // Determine which quadrant the hole is in and place the central tromino accordingly
        if (holeRow < centerRow && holeCol < centerCol) { // Top-left quadrant
            placeCentralTromino(centerRow - 1, centerCol, centerRow, centerCol - 1, centerRow, centerCol, colorIndex);
            solveTiling(rowStart, colStart, holeRow, holeCol, halfSize);
            solveTiling(rowStart, centerCol, centerRow - 1, centerCol, halfSize);
            solveTiling(centerRow, colStart, centerRow, centerCol - 1, halfSize);
            solveTiling(centerRow, centerCol, centerRow, centerCol, halfSize);
        } else if (holeRow < centerRow && holeCol >= centerCol) { // Top-right quadrant
            placeCentralTromino(centerRow - 1, centerCol - 1, centerRow, centerCol - 1, centerRow, centerCol, colorIndex);
            solveTiling(rowStart, colStart, centerRow - 1, centerCol - 1, halfSize);
            solveTiling(rowStart, centerCol, holeRow, holeCol, halfSize);
            solveTiling(centerRow, colStart, centerRow, centerCol - 1, halfSize);
            solveTiling(centerRow, centerCol, centerRow, centerCol, halfSize);
        } else if (holeRow >= centerRow && holeCol < centerCol) { // Bottom-left quadrant
            placeCentralTromino(centerRow - 1, centerCol - 1, centerRow - 1, centerCol, centerRow, centerCol, colorIndex);
            solveTiling(rowStart, colStart, centerRow - 1, centerCol - 1, halfSize);
            solveTiling(rowStart, centerCol, centerRow - 1, centerCol, halfSize);
            solveTiling(centerRow, colStart, holeRow, holeCol, halfSize);
            solveTiling(centerRow, centerCol, centerRow, centerCol, halfSize);
        } else { // Bottom-right quadrant
            placeCentralTromino(centerRow - 1, centerCol - 1, centerRow, centerCol - 1, centerRow - 1, centerCol, colorIndex);
            solveTiling(rowStart, colStart, centerRow - 1, centerCol - 1, halfSize);
            solveTiling(rowStart, centerCol, centerRow - 1, centerCol, halfSize);
            solveTiling(centerRow, colStart, centerRow, centerCol - 1, halfSize);
            solveTiling(centerRow, centerCol, holeRow, holeCol, halfSize);
        }
    }

    private void placeCentralTromino(int r1, int c1, int r2, int c2, int r3, int c3, int colorIndex) {
        steps.add(new TilingStep(r1, c1, r2, c2, r3, c3, colorIndex));
        board[r1][c1] = colorIndex;
        board[r2][c2] = colorIndex;
        board[r3][c3] = colorIndex;
    }

    private void placeTromino(int r, int c, int holeR, int holeC) {
        for (int colorIndex = 0; colorIndex < COLORS.length; colorIndex++) {
            List<int[]> possibleTromino = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (r + i != holeR || c + j != holeC) {
                        possibleTromino.add(new int[]{r + i, c + j});
                    }
                }
            }

            if (possibleTromino.size() == 3) {
                int r1 = possibleTromino.get(0)[0];
                int c1 = possibleTromino.get(0)[1];
                int r2 = possibleTromino.get(1)[0];
                int c2 = possibleTromino.get(1)[1];
                int r3 = possibleTromino.get(2)[0];
                int c3 = possibleTromino.get(2)[1];

                if (isValidColor(r1, c1, colorIndex) && isValidColor(r2, c2, colorIndex) && isValidColor(r3, c3, colorIndex)) {
                    steps.add(new TilingStep(r1, c1, r2, c2, r3, c3, colorIndex));
                    board[r1][c1] = colorIndex;
                    board[r2][c2] = colorIndex;
                    board[r3][c3] = colorIndex;
                    return;
                }
            }
        }
        System.err.println("Error: Could not place tromino at " + r + "," + c + " around hole " + holeR + "," + holeC);
    }

    private int findValidColorForCentralTromino(int r1, int c1, int r2, int c2, int r3, int c3) {
        for (int colorIndex = 0; colorIndex < COLORS.length; colorIndex++) {
            if (isValidColor(r1, c1, colorIndex) && isValidColor(r2, c2, colorIndex) && isValidColor(r3, c3, colorIndex)) {
                return colorIndex;
            }
        }
        System.err.println("Error: Could not find valid color for central tromino at " + r1 + "," + c1);
        return 0; // Default color in case of error
    }

    private boolean isValidColor(int r, int c, int colorIndex) {
        if (r < 0 || r >= size || c < 0 || c >= size || board[r][c] != -1) {
            return false; // Out of bounds or already filled
        }
        int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] neighbor : neighbors) {
            int nr = r + neighbor[0];
            int nc = c + neighbor[1];
            if (nr >= 0 && nr < size && nc >= 0 && nc < size && board[nr][nc] == colorIndex) {
                return false;
            }
        }
        return true;
    }

    private void showNextStep() {
        if (currentStep >= steps.size()) {
            stepButton.setEnabled(false);
            runButton.setEnabled(false);
            return;
        }

        TilingStep step = steps.get(currentStep++);
        labels[step.r1][step.c1].setBackground(COLORS[step.colorIndex]);
        labels[step.r2][step.c2].setBackground(COLORS[step.colorIndex]);
        labels[step.r3][step.c3].setBackground(COLORS[step.colorIndex]);

        labels[step.r1][step.c1].setText(String.valueOf(currentStep));
        labels[step.r2][step.c2].setText(String.valueOf(currentStep));
        labels[step.r3][step.c3].setText(String.valueOf(currentStep));
    }

    private void runAllSteps() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (currentStep < steps.size()) {
                SwingUtilities.invokeLater(this::showNextStep);
            } else {
                executor.shutdown();
                stepButton.setEnabled(false);
                runButton.setEnabled(false);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String sizeStr = JOptionPane.showInputDialog(null,
                    "Enter board size (power of 2, e.g., 2,4,8,16):", "8");
            if (sizeStr == null) return;

            int size;
            try {
                size = Integer.parseInt(sizeStr);
                if ((size & (size - 1)) != 0 || size < 2) {
                    JOptionPane.showMessageDialog(null,
                            "Size must be a power of 2 (2,4,8,16,...)", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid size", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String missingXStr = JOptionPane.showInputDialog(null,
                    "Enter row of missing point (0-" + (size - 1) + "):", "0");
            if (missingXStr == null) return;

            String missingYStr = JOptionPane.showInputDialog(null,
                    "Enter column of missing point (0-" + (size - 1) + "):", "0");
            if (missingYStr == null) return;

            int missingX, missingY;
            try {
                missingX = Integer.parseInt(missingXStr);
                missingY = Integer.parseInt(missingYStr);
                if (missingX < 0 || missingX >= size || missingY < 0 || missingY >= size) {
                    JOptionPane.showMessageDialog(null,
                            "Coordinates must be between 0 and " + (size - 1), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid coordinates", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new TrominoTilingNoGaps(size, missingX, missingY);
        });
    }
}