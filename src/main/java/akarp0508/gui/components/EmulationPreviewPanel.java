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

        int imgHeight = image.getHeight(null);
        int imgWidth = image.getWidth(null);


        int size1 = panelHeight/imgHeight;
        int size2 = panelWidth/imgWidth;
        pixelSize = Math.min(size1,size2);
        Image scaledImage = image.getScaledInstance(pixelSize*imgWidth,pixelSize*imgHeight,Image.SCALE_FAST);
        g.drawImage(scaledImage,panelWidth/2-imgWidth*pixelSize/2,panelHeight/2-imgHeight*pixelSize/2,null);

    }
}
