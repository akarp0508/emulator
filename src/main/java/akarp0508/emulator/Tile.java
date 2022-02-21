package akarp0508.emulator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    private byte tileTextureNumber;
    private boolean isLetter;
    private boolean flipX;
    private boolean flipY;
    private GPUEmulationEngine gpu;

    public Tile(int constructor, GPUEmulationEngine parent) {
        tileTextureNumber = (byte)(constructor);
        isLetter = (constructor>>8)%2 == 1;
        flipX = (constructor>>9)%2 == 1;
        flipY = (constructor>>10)%2 == 1;
        gpu = parent;
    }

    public byte getTileTextureNumber() {
        return tileTextureNumber;
    }

    public void setTileTextureNumber(byte tileTextureNumber) {
        this.tileTextureNumber = tileTextureNumber;
    }

    public boolean isLetter() {
        return isLetter;
    }

    public void setLetter(boolean letter) {
        isLetter = letter;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public Image getImage(byte bg){
        BufferedImage bi = new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        byte[] texture = isLetter ? gpu.getLetterTexture(tileTextureNumber) : gpu.getTileTexture(tileTextureNumber);


        for(byte xx = 0; xx<8 ; xx++){
            for(byte yy = 0;yy<8;yy++){
                Color c = gpu.getBGColor(texture[yy*8+xx],bg);
                g.setColor(c);
                byte px = flipX ? (byte) (8 - xx) : xx;
                byte py = flipX ? (byte) (8 - yy) : yy;
                g.fillRect(px,py,1,1);
            }
        }
        return bi;
    }


    /*
    public static void main(String[] args) {
        Tile tile = new Tile(1152);
        System.out.println(tile.getTileNumber());
        System.out.println(tile.isLetter);
        System.out.println(tile.isFlipX());
        System.out.println(tile.isFlipY());
    }
    */
}
