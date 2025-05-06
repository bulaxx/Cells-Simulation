import javax.swing.*;
import java.awt.*;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


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
        int pointSize = (int)(cellSpacing*0.8) ;

        int centerX = getWidth() / 2 ;
        int centerY = getHeight() / 2 - cellSpacing*5;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    String status = organism[i][j][k].status;
                    Color baseColor = getColorForStatus(status);

                    int drawX = centerX + (j - n / 2) * cellSpacing + i * 15;
                    int drawY = centerY + (k - n / 2) * cellSpacing + i * 15;

                    float[] distance = {0.0f,1.0f};
                    Color[] colors = {baseColor.darker(), baseColor.brighter()};

                    RadialGradientPaint gradient = new RadialGradientPaint(
                            new Point2D.Float(drawX + pointSize / 2f + 4f, drawY + pointSize / 2f +4f),
                            pointSize / 2f,
                            distance,
                            colors
                    );
                    g2.setColor(Color.black);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(drawX, drawY, pointSize, pointSize);

                    g2.setPaint(gradient);
                    g2.fillOval(drawX, drawY, pointSize, pointSize);

                    g2.setColor(new Color(255, 255, 255, 80));
                    int highlightSize = (int) (pointSize * 0.4);
                    int highlightX = drawX + pointSize / 4 - 4;
                    int highlightY = drawY + pointSize / 4 - 4;

                    g2.fillOval(highlightX, highlightY, highlightSize, highlightSize);
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

    public void saveIMG(){
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        this.paint(g2);
        g2.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image As...");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".png")) {
                fileToSave = new File(path + ".png");
            }

            try {
                boolean result = ImageIO.write(image, "png", fileToSave);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


