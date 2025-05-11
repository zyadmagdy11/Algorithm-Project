import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.Timer;

public class HanoiTowerGUI extends JFrame {
    private static final int PEG_COUNT = 4;
    private static final int DISK_HEIGHT = 20;
    private static final int[] DISK_WIDTHS = {60, 80, 100, 120, 140, 160, 180, 200}; // Fixed sizes for up to 8 disks
    private static final int PEG_WIDTH = 20;
    private static final int BASE_HEIGHT = 30;
    
    private List<Stack<Integer>> pegs;
    private int moveCount;
    private Timer timer;
    private int animationDelay = 500; // milliseconds
    private List<Move> moves;
    private int currentMove;
    private JLabel moveLabel;
    private JButton startButton;
    private JButton stepButton;
    private JPanel drawingPanel;

    public HanoiTowerGUI(int disks) {
        setTitle("Tower of Hanoi - 4 Pegs");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pegs = new ArrayList<>();
        for (int i = 0; i < PEG_COUNT; i++) {
            pegs.add(new Stack<>());
        }
        
        // Initialize first peg with disks
        for (int i = disks; i > 0; i--) {
            pegs.get(0).push(i);
        }
        
        moves = new ArrayList<>();
        moveCount = 0;
        currentMove = 0;
        
        setupUI();
        generateMoves(disks, 0, 3, 1, 2);
    }
    
    private void setupUI() {
        JPanel controlPanel = new JPanel();
        moveLabel = new JLabel("Moves: 0");
        startButton = new JButton("Start Animation");
        stepButton = new JButton("Step");
        
        startButton.addActionListener(e -> startAnimation());
        stepButton.addActionListener(e -> performMove());
        
        controlPanel.add(moveLabel);
        controlPanel.add(startButton);
        controlPanel.add(stepButton);
        
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTowers(g);
            }
        };
        drawingPanel.setBackground(new Color(240, 240, 240));
        
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        
        timer = new Timer(animationDelay, e -> {
            if (currentMove < moves.size()) {
                performMove();
            } else {
                timer.stop();
                startButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Puzzle solved in " + moveCount + " moves!");
            }
        });
    }
    
    private void drawTowers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelHeight = drawingPanel.getHeight();
        int panelWidth = drawingPanel.getWidth();
        int pegSpacing = panelWidth / (PEG_COUNT + 1);
        
        // Draw base
        g2d.setColor(new Color(139, 69, 19)); // Brown color
        g2d.fillRect(50, panelHeight - BASE_HEIGHT, panelWidth - 100, BASE_HEIGHT);
        
        // Draw pegs
        g2d.setColor(new Color(101, 67, 33)); // Darker brown
        for (int i = 0; i < PEG_COUNT; i++) {
            int x = pegSpacing * (i + 1);
            int yBase = panelHeight - BASE_HEIGHT;
            g2d.fillRect(x - PEG_WIDTH/2, 100, PEG_WIDTH, yBase - 100);
        }
        
        // Draw disks
        for (int peg = 0; peg < PEG_COUNT; peg++) {
            int xCenter = pegSpacing * (peg + 1);
            Stack<Integer> disks = pegs.get(peg);
            
            for (int i = 0; i < disks.size(); i++) {
                int diskSize = disks.get(i);
                int width = DISK_WIDTHS[diskSize - 1]; // Use fixed size from array
                int x = xCenter - width/2;
                int y = panelHeight - BASE_HEIGHT - (i + 1) * DISK_HEIGHT;
                
                // Gradient paint for disks
                GradientPaint gp = new GradientPaint(
                    x, y, new Color(70, 130, 180), 
                    x + width, y + DISK_HEIGHT, new Color(0, 191, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(x, y, width, DISK_HEIGHT, 10, 10);
                
                // Disk border
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(x, y, width, DISK_HEIGHT, 10, 10);
                
                // Disk number
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(diskSize), xCenter - 5, y + DISK_HEIGHT/2 + 5);
            }
        }
    }
    
    private void generateMoves(int k, int src, int dest, int aux1, int aux2) {
        if (k == 0) return;
        
        generateMoves(k-1, src, aux2, aux1, dest);
        moves.add(new Move(k, src, dest));
        generateMoves(k-1, aux2, dest, src, aux1);
    }
    
    private void performMove() {
        if (currentMove >= moves.size()) return;
        
        Move move = moves.get(currentMove);
        pegs.get(move.to).push(pegs.get(move.from).pop());
        moveCount++;
        currentMove++;
        
        moveLabel.setText("Moves: " + moveCount + " - Moving disk " + move.diskSize + 
                         " from Peg " + (move.from+1) + " to Peg " + (move.to+1));
        drawingPanel.repaint();
    }
    
    private void startAnimation() {
        startButton.setEnabled(false);
        timer.start();
    }
    
    private static class Move {
        int diskSize;
        int from;
        int to;
        
        public Move(int diskSize, int from, int to) {
            this.diskSize = diskSize;
            this.from = from;
            this.to = to;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HanoiTowerGUI hanoi = new HanoiTowerGUI(8); // 8 disks
            hanoi.setVisible(true);
        });
    }
}