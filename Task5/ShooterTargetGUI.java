import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.border.LineBorder;

public class ShooterTargetGUI extends JFrame {

    private static int n;
    private static int target;
    private static int consecutiveShot;
    private static List<Integer> spots;
    private static JPanel boardPanel;
    private static Color[] cellColors;
    private static JTextArea outputTextArea;
    private static JButton runInstantlyButton;
    private static ScheduledExecutorService scheduler;
    private static volatile boolean isRunning = false; // Make volatile for thread safety
    private static int currentMid = -1; // To track the shooter's current position
    private static JLabel midTargetLabel; // Label to display currentMid and target

    private static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    private static final Color TARGET_COLOR = Color.RED;
    private static final Color SHOOTER_COLOR = Color.BLUE;
    private static final Color SEPARATOR_COLOR = Color.BLACK;

    public ShooterTargetGUI(int numSpots, int initialTarget) {
        n = numSpots;
        target = initialTarget % n; // Ensure target is within bounds
        consecutiveShot = 0;
        spots = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            spots.add(i);
        }
        cellColors = new Color[n];
        for (int i = 0; i < n; i++) {
            cellColors[i] = EMPTY_COLOR;
        }

        setTitle("Shooter Target Game (Size: " + n + ", Target: " + target + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(1, n)); // Single row
        for (int i = 0; i < n; i++) {
            JPanel cell = new JPanel();
            cell.setPreferredSize(new Dimension(30, 50)); // Adjust cell size
            cell.setBackground(cellColors[i]);
            cell.setBorder(new LineBorder(SEPARATOR_COLOR)); // Add thin black border
            boardPanel.add(cell);
        }
        updateBoardColorsOnEDT(); // Initial update
        add(boardPanel, BorderLayout.NORTH);

        midTargetLabel = new JLabel("Mid: - , Target: " + target); // Initialize the label
        add(midTargetLabel, BorderLayout.CENTER);

        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        add(scrollPane, BorderLayout.SOUTH);

        runInstantlyButton = new JButton("Run Instantly");
        runInstantlyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    isRunning = true;
                    new Thread(() -> { // Run instantly on a separate thread
                        runInstantly();
                        isRunning = false;
                    }).start();
                }
            }
        });
        add(runInstantlyButton, BorderLayout.EAST);

        scheduler = Executors.newSingleThreadScheduledExecutor();

        setSize(Math.max(300, n * 30), 450); // Adjust size based on cell width
        setLocationRelativeTo(null);
        setVisible(true);

        // Start the tracing at 0.25 sec intervals
        startTracing();
    }

    private void startTracing() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) return; // Don't trace if running instantly
            SwingUtilities.invokeLater(() -> {
                updateBoardColors();
                updateMidTargetLabel(); // Update the label during tracing
            });
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    private void updateBoardColors() {
        for (int i = 0; i < n; i++) {
            cellColors[i] = EMPTY_COLOR;
        }
        if (target >= 0 && target < n) {
            cellColors[target] = TARGET_COLOR;
        }
        if (currentMid >= 0 && currentMid < n) {
            cellColors[currentMid] = SHOOTER_COLOR;
        }
        for (int i = 0; i < n; i++) {
            ((JPanel) boardPanel.getComponent(i)).setBackground(cellColors[i]);
        }
        boardPanel.repaint();
    }

    private void updateMidTargetLabel() {
        midTargetLabel.setText("Mid: " + currentMid + " , Target: " + target);
    }

    private void updateBoardColorsOnEDT() {
        SwingUtilities.invokeLater(this::updateBoardColors);
    }

    private boolean shootAtTarget(int start, int end) {
        if (consecutiveShot == -1) {
            return true;
        } else if (start == end) {
            return false;
        }

        currentMid = (start + end) / 2;
        String shootMessage = "Shoot at hiding spot " + currentMid;
        logMessage(shootMessage);
        updateBoardColorsBasedOnLog(currentMid, target);
        updateMidTargetLabelOnEDT(); // Update immediately after setting currentMid

        if (spots.get(currentMid) == target) {
            logMessage("Shooter shot target at index: " + target);
            consecutiveShot = -1;
            updateBoardColorsOnEDT();
            updateMidTargetLabelOnEDT();
            return true;
        }

        consecutiveShot++;
        int prevTarget = target;
        if (consecutiveShot % 2 == 0) 
        {
            boolean flag = new Random().nextBoolean(); // randomly true or false
            if (flag) 
            {
                target = (target + 1) % spots.size();
            } 
            else 
            {
                target = (target - 1 + spots.size()) % spots.size(); // ensure positive modulo
            }
            if (prevTarget != target) {
                logMessage("Target moved from " + prevTarget + " to " + target);
            }
        }
        
        updateBoardColorsBasedOnLog(currentMid, target);
        updateMidTargetLabelOnEDT(); // Update after potential target move

        // Introduce a delay for tracing visibility (only if not running instantly)

        try {
            Thread.sleep(500); // Increased delay for better visibility
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        return shootAtTarget(start, currentMid) || shootAtTarget(currentMid + 1, end);
    }

    private void updateMidTargetLabelOnEDT() {
        SwingUtilities.invokeLater(this::updateMidTargetLabel);
    }

    private void updateBoardColorsBasedOnLog(int shooterPos, int targetPos) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < n; i++) {
                cellColors[i] = EMPTY_COLOR;
            }
            if (targetPos >= 0 && targetPos < n) {
                cellColors[targetPos] = TARGET_COLOR;
            }
            if (shooterPos >= 0 && shooterPos < n) {
                cellColors[shooterPos] = SHOOTER_COLOR;
            }
            for (int i = 0; i < n; i++) {
                ((JPanel) boardPanel.getComponent(i)).setBackground(cellColors[i]);
            }
            boardPanel.repaint();
        });
    }

    private void runInstantly() {
        logMessage("=============================================================");
        consecutiveShot = 0;
        while (!shootAtTarget(0, n - 1)) {
            logMessage("Failed Try again");
            logMessage("=============================================================");
            consecutiveShot = 0;
            Random random = new Random();
            int prevTarget = target;
            target = random.nextInt(n);
            String resetMessage = "Target reset to: " + target;
            logMessage(resetMessage);
            updateBoardColorsBasedOnLog(currentMid, target);
            updateMidTargetLabelOnEDT();
        }
        logMessage("===================== Simulation Ended ======================");
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            outputTextArea.append(message + "\n");
            outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String nStr = JOptionPane.showInputDialog("Enter the number of hiding spots:");
            if (nStr == null || nStr.isEmpty()) {
                return;
            }
            int numSpots;
            try {
                numSpots = Integer.parseInt(nStr);
                if (numSpots <= 0) {
                    JOptionPane.showMessageDialog(null, "Number of hiding spots must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number of hiding spots.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String targetStr = JOptionPane.showInputDialog("Enter the initial target spot (0 to " + (numSpots - 1) + "):");
            if (targetStr == null || targetStr.isEmpty()) {
                return;
            }
            int initialTarget;
            try {
                initialTarget = Integer.parseInt(targetStr);
                if (initialTarget < 0 || initialTarget >= numSpots) {
                    JOptionPane.showMessageDialog(null, "Target spot is out of bounds.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid target spot.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new ShooterTargetGUI(numSpots, initialTarget);
        });
    }
}
