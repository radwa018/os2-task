import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatInMaze extends JFrame {
    private static final int N = 8; // Maze size
    private static final int[][] maze = new int[N][N];
    private static final JPanel[][] cells = new JPanel[N][N];
    private static ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public RatInMaze() {
        setTitle("Rat in a Maze");
        setSize(600, 600);
        setLayout(new GridLayout(N, N));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeMaze();
        setVisible(true);
    }

    // Initialize the maze and GUI grid
    private void initializeMaze() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                maze[i][j] = 1; // Default all cells as paths
                cells[i][j] = new JPanel();
                cells[i][j].setBackground(Color.WHITE);
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                add(cells[i][j]);
            }
        }

        // Add obstacles manually
        maze[1][3] = maze[2][3] = maze[3][3] = maze[4][3] = maze[4][2] = maze[4][1] = 0;
        updateMazeGUI();
    }

    // Update GUI to reflect maze structure
    private void updateMazeGUI() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (maze[i][j] == 0) {
                    cells[i][j].setBackground(Color.BLACK); // Obstacle
                }
            }
        }
    }

    // Solve the maze using multithreading
    private void solveMaze() {
        threadPool.execute(() -> findPath(0, 0)); // Start from top-left corner
        threadPool.shutdown();
    }

    private boolean findPath(int x, int y) {
        if (x == N - 1 && y == N - 1) { // Reached destination
            cells[x][y].setBackground(Color.YELLOW);
            return true;
        }

        if (isSafe(x, y)) {
            cells[x][y].setBackground(Color.GREEN); // Mark as visited
            try {
                Thread.sleep(200); // Simulate real-time visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Explore next steps
            boolean right = threadPool.submit(() -> findPath(x, y + 1)).isDone();
            boolean down = threadPool.submit(() -> findPath(x + 1, y)).isDone();

            if (right || down) return true;

            // Backtrack
            cells[x][y].setBackground(Color.RED);
        }
        return false;
    }

    private boolean isSafe(int x, int y) {
        return x >= 0 && y >= 0 && x < N && y < N && maze[x][y] == 1 && cells[x][y].getBackground() != Color.GREEN;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RatInMaze frame = new RatInMaze();
            frame.solveMaze();
        });
    }
}
