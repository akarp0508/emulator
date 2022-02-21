package akarp0508.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EmulationPreviewPanel extends JPanel {
    private Image image = new BufferedImage(256,192,BufferedImage.TYPE_INT_RGB);

    public EmulationPreviewPanel() {
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0,0,256,192);
        graphics.dispose();
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int pixelSize;

        int size1 = panelHeight/192;
        int size2 = panelWidth/256;
        pixelSize = Math.min(size1,size2);
        Image scaledImage = image.getScaledInstance(pixelSize*256,pixelSize*192,Image.SCALE_FAST);
        g.drawImage(scaledImage,panelWidth/2-128*pixelSize,panelHeight/2-96*pixelSize,null);

    }
}
