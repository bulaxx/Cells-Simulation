import javax.swing.*;
import java.awt.*;

public class PaintPanel extends JPanel {

    private Cell[][][] organism;
    private int n;

    public PaintPanel(Cell[][][] organism, int newN) {
        this.organism = organism;
        this.n = newN;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //smooths the edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelSize = Math.min(getWidth(), getHeight());
        int cellSpacing = panelSize / (n+12) ; //  to leave margins
        int pointSize = (int) (cellSpacing*0.8) ;

        int centerX = getWidth() / 2 ;
        int centerY = getHeight() / 2 - cellSpacing*5;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    String status = organism[i][j][k].status;
                    g2.setColor(getColorForStatus(status));

                    int drawX = centerX + (j - n / 2) * cellSpacing + i * 15;
                    int drawY = centerY + (k - n / 2) * cellSpacing + i * 15;

                    g2.fillOval(drawX, drawY, pointSize, pointSize);
                }
            }
        }
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

    public void updateOrganism(Cell[][][] newOrganism, int newN) {
        this.organism = newOrganism;
        this.n = newN;
        repaint();
    }
}
