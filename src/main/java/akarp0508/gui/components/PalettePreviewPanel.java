package akarp0508.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PalettePreviewPanel extends JPanel {
    private final static int TILE_GAP=3;
    private int selectedIndex = -1;



    @Override
    public void paint(Graphics g) {
        super.paint(g);
        double h = getSize().getHeight();
        double w = getSize().getWidth();
        int size = (int)Math.min(h,w);
        int tileSize = size/16;
        for(int i=0;i<256;i++){
            g.setColor(Color.BLACK);
            int tx = i%16;
            int ty = i/16;
            g.fillRect(tileSize*tx+TILE_GAP,tileSize*ty+TILE_GAP,tileSize-2*TILE_GAP,tileSize-2*TILE_GAP);
        }
        if(selectedIndex>0) {
            g.setColor(Color.WHITE);
            int sx = selectedIndex % 16;
            int sy = selectedIndex / 16;
            g.drawRect(tileSize * sx, tileSize * sy, tileSize, tileSize);
        }
    }

    public int changeSelectedColor(MouseEvent e){
        double h = getSize().getHeight();
        double w = getSize().getWidth();
        int size = (int)Math.min(h,w);
        int tileSize = size/16;
        int x = e.getX()/tileSize;
        int y = e.getY()/tileSize;
        if(x+y==0 || x>15 ||y>15) {
            selectedIndex = -1;
            return -1;
        }
        selectedIndex = x+y*16;
        repaint();
        return selectedIndex;
    }
}
