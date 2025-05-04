import javax.swing.*;
import java.awt.*;

public class PaintPanel extends JPanel {

    private Cell[][][] organism;
    private int n;

    public PaintPanel(Cell[][][] organism, int n) {
        this.organism = organism;
        this.n = n;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cellSize = getWidth() / n;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                String status = getTopState(i, j);
                g.setColor(getColorForStatus(status));
                g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    private String getTopState(int x, int y) {
        for (int z = 0; z < n; z++) {
            if (organism[x][y][z].status.equals("cancer")) return "cancer";
            if (organism[x][y][z].status.equals("mutated")) return "mutated";
            if (organism[x][y][z].status.equals("damaged")) return "damaged";
            if (organism[x][y][z].status.equals("healthy")) return "healthy";
        }
        return "empty";
    }

    private Color getColorForStatus(String status) {
        switch (status) {
            case "healthy": return Color.GREEN;
            case "damaged": return Color.YELLOW;
            case "mutated": return Color.ORANGE;
            case "cancer": return Color.RED;
            case "dead": return Color.BLACK;
            case "empty": return Color.WHITE;
            default: return Color.GRAY;
        }
    }

    public void updateOrganism(Cell[][][] newOrganism) {
        this.organism = newOrganism;
        repaint();
    }
}
