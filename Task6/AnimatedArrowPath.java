import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatedArrowPath extends JPanel {

    private final List<Point> points;
    private int currentIndex = 1;
    private javax.swing.Timer timer;

    public AnimatedArrowPath(List<Point> points) {
        this.points = points;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);

        // Animation timer
        timer = new javax.swing.Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex++;
                if (currentIndex >= points.size()) {
                    timer.stop();
                }
                repaint();
            }
        });
        timer.setInitialDelay(500);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPath((Graphics2D) g);
    }

    private void drawPath(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margin = 50;
        int gridSize = 500;
        int n = getGridSize(points);
        int step = gridSize / n;

        // Draw lattice points with coordinates
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = margin + i * step;
                int y = margin + j * step;
                g2.fillOval(x - 2, y - 2, 4, 4);
                g2.drawString("(" + i + "," + j + ")", x + 4, y - 4);
            }
        }

        // Draw path with arrows
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        for (int i = 1; i < currentIndex && i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            int x1 = margin + p1.x * step;
            int y1 = margin + p1.y * step;
            int x2 = margin + p2.x * step;
            int y2 = margin + p2.y * step;

            g2.drawLine(x1, y1, x2, y2);
            drawArrowHead(g2, x1, y1, x2, y2);
        }
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(30);
        int barb = 10;

        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);
        double rho = theta + phi;

        for (int j = 0; j < 2; j++) {
            double angle = rho - j * 2 * phi;
            int x = (int) (x2 - barb * Math.cos(angle));
            int y = (int) (y2 - barb * Math.sin(angle));
            g2.drawLine(x2, y2, x, y);
        }
    }

    private int getGridSize(List<Point> points) {
        int maxX = 0, maxY = 0;
        for (Point p : points) {
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }
        return Math.max(maxX, maxY) + 1;
    }

    public static void main(String[] args) {
        int n = 9;  // Try even or odd values

        List<Point> points = generateCornerPoints(n);

        // Add extra line depending on parity of n
        if (n % 2 == 1) { // Odd n → add line from last point to right
            Point last = points.get(points.size() - 1);
            points.add(new Point(n - 1, last.y));
        } else { // Even n → add line from second-last point to left
            Point secondLast = points.get(points.size() - 2);
            points.add(new Point(0, secondLast.y));
        }


        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Animated Arrow Path");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new AnimatedArrowPath(points));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Generates spiral corner points starting right
    public static List<Point> generateCornerPoints(int n) {
        List<Point> result = new ArrayList<>();
        result.add(new Point(0, 0)); // Start at top-left

        int layers = (n + 1) / 2;
        int x1, y1, x2 = 0, y2 = 0;

        for (int i = 0; i < layers * 4 - 1; ++i) {
            int layer = i / 4;

            switch (i % 4) {
                case 0: // right
                    x1 = layer;
                    y1 = layer;
                    x2 = n - 1 - layer;
                    y2 = layer;
                    break;
                case 1: // down
                    x1 = n - 1 - layer;
                    y1 = layer;
                    x2 = n - 1 - layer;
                    y2 = n - 1 - layer;
                    break;
                case 2: // left
                    x1 = n - 1 - layer;
                    y1 = n - 1 - layer;
                    x2 = layer;
                    y2 = n - 1 - layer;
                    break;
                case 3: // up
                    x1 = layer;
                    y1 = n - 1 - layer;
                    x2 = layer;
                    y2 = layer + 1;
                    break;
                default:
                    continue;
            }

            if (x1 == x2 && y1 == y2) break;
            result.add(new Point(x2, y2));
        }

        return result;
    }
}
