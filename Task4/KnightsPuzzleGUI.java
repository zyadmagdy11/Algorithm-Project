import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class KnightsPuzzleGUI {
    private static final int V = 12;
    private String[] type = {"B", "B", "B", "E", "E", "E", "E", "E", "E", "W", "W", "W"};
    private final int[][][] edges = {
        {{0, 7}, {4, 9}},
        {{2, 7}, {4, 11}},
        {{2, 3}, {6, 11}},
        {{3, 8}, {5, 6}},
        {{8, 9}, {0, 5}},
        {{5, 10}, {8, 1}},
        {{10, 3}, {6, 1}}
    };

    private JFrame frame;
    private JPanel boardPanel;
    private JLabel[] tileLabels;
    private JTextArea logArea;
    private JButton startButton;
    private int currentStep = 0;

    public KnightsPuzzleGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Knights Puzzle Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLayout(new BorderLayout());

        // Board Panel
        boardPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tileLabels = new JLabel[V];
        
        for (int i = 0; i < V; i++) {
            tileLabels[i] = new JLabel(type[i], SwingConstants.CENTER);
            tileLabels[i].setOpaque(true);
            tileLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            tileLabels[i].setFont(new Font("Arial", Font.BOLD, 24));
            updateTileColor(i);
            boardPanel.add(tileLabels[i]);
        }

        // Control Panel
        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Solving");
        startButton.addActionListener(e -> new Thread(this::solvePuzzle).start());
        controlPanel.add(startButton);

        // Log Panel
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Add components to frame
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void updateTileColor(int index) {
        if (type[index].charAt(0) == 'B') {
            tileLabels[index].setBackground(new Color(200, 200, 255)); // Light blue for black knights
        } else if (type[index].charAt(0) == 'W') {
            tileLabels[index].setBackground(new Color(255, 200, 200)); // Light red for white knights
        } else {
            tileLabels[index].setBackground(Color.WHITE); // White for empty
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void swapTypeElements(int[][] arr) 
    {
        // First knight move
        int index1 = arr[0][0];
        int index2 = arr[0][1];
        int index3 = arr[1][0];
        int index4 = arr[1][1];
        if ((type[index1].equals("E") && type[index2].equals("E")) || (type[index3].equals("E") && type[index4].equals("E"))) return;
        if ((type[index1].equals("E") || type[index2].equals("E")) && (type[index3].equals("E") || type[index4].equals("E")))
        {

            { 
                // First knight move
                highlightTiles(new int[]{index1, index2}, Color.YELLOW);
                
                String temp = type[index1];
                type[index1] = type[index2];
                type[index2] = temp;
                
                updateGUI(); 
                log("Step " + (++currentStep) + ": Swap " + "( "+type[index1] + ", " + type[index2]  + " )"+ "of position " + "( " + index1 + ", " + index2  + " )");
                
                sleep(500); // Delay after first knight move
                resetTileColors();
            }
            {
                // Second knight move
                highlightTiles(new int[]{index3, index4}, Color.YELLOW);
                
                String temp = type[index3];
                type[index3] = type[index4];
                type[index4] = temp;
                
                updateGUI();
                log("Step " + (++currentStep) + ": Swap " + "( "+type[index3] + ", " + type[index4]  + " )"+ "of position " + "( " + index3 + ", " + index4  + " )");

                
                sleep(500); // Delay after second knight move
                resetTileColors();
            }
        }
        
    }

    private void highlightTiles(int[] indices, Color color) {
        SwingUtilities.invokeLater(() -> {
            for (int index : indices) {
                tileLabels[index].setBackground(color);
            }
        });
    }

    private void resetTileColors() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < V; i++) {
                updateTileColor(i);
            }
        });
    }

    private void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < V; i++) {
                tileLabels[i].setText(type[i]);
                updateTileColor(i);
            }
        });
    }

    private void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean goalState() {
        return type[0].charAt(0) == 'W' && 
               type[1].charAt(0) == 'W' && 
               type[2].charAt(0) == 'W' && 
               type[9].charAt(0) == 'B' && 
               type[10].charAt(0) == 'B' && 
               type[11].charAt(0) == 'B';
    }

    private void solvePuzzle() {
        SwingUtilities.invokeLater(() -> startButton.setEnabled(false));
        currentStep = 0;
        log("Starting puzzle solution...");
        
        int i = 5;
        while (!goalState()) {
            swapTypeElements(edges[i]);
            i = (i + 1) % 7;
        }
        
        log("Puzzle solved successfully in " + currentStep + " steps!");
        SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KnightsPuzzleGUI::new);
    }
}